package com.nikoladrljaca.getreminded.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.*
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import com.nikoladrljaca.getreminded.MainActivity
import com.nikoladrljaca.getreminded.R
import com.nikoladrljaca.getreminded.adapter.ReminderAdapter
import com.nikoladrljaca.getreminded.databinding.FragmentMainBinding
import com.nikoladrljaca.getreminded.utils.ANIM_DURATION
import com.nikoladrljaca.getreminded.utils.COPY_CONTEXT_MENU_ID
import com.nikoladrljaca.getreminded.utils.DELETE_CONTEXT_MENU_ID
import com.nikoladrljaca.getreminded.utils.SHARE_CONTEXT_MENU_ID
import com.nikoladrljaca.getreminded.viewmodel.Reminder
import com.nikoladrljaca.getreminded.viewmodel.SharedViewModel
import kotlinx.coroutines.flow.collect
import java.util.*


class MainFragment : Fragment(R.layout.fragment_main), ReminderAdapter.OnItemClickListener {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var fabCreateNewReminder: FloatingActionButton
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var adapter: ReminderAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)
        fabCreateNewReminder = requireActivity().findViewById(R.id.fab_create_new_reminder)
        bottomAppBar = requireActivity().findViewById(R.id.bottomAppBar)

        //transitions
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        exitTransition = MaterialElevationScale(false).apply {
            duration = ANIM_DURATION
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = ANIM_DURATION
        }

        adapter = ReminderAdapter(this)
        binding.apply {
            rvReminderList.setHasFixedSize(true)
            rvReminderList.adapter = adapter
            checkLayoutManager()
        }

        fabCreateNewReminder.show()
        fabCreateNewReminder.visibility = View.VISIBLE
        bottomAppBar.performShow()
        bottomAppBar.visibility = View.VISIBLE

        binding.etSearch.isCursorVisible = false

        binding.etSearch.setOnClickListener {
            binding.etSearch.isCursorVisible = true
        }

        binding.etSearch.addTextChangedListener {
            val text = it?.toString()?.toLowerCase(Locale.ROOT)!!
            val list = sharedViewModel.allReminders.value
            val filteredList = mutableListOf<Reminder>()
            if (list != null) {
                for (item in list) {
                    if (item.title.toLowerCase(Locale.ROOT).contains(text))
                        filteredList.add(item)
                }
                adapter.submitList(filteredList)
            }
        }

        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val current = sharedViewModel.allReminders.value!![viewHolder.adapterPosition]
                sharedViewModel.onReminderSwiped(current)
            }
        }
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.rvReminderList)

        sharedViewModel.allReminders.observe(viewLifecycleOwner) {
            adapter.submitList(it)

            binding.apply {
                if (it.isEmpty()) {
                    rvReminderList.isVisible = false
                    tvNothingToShow.isVisible = true
                } else {
                    rvReminderList.isVisible = true
                    tvNothingToShow.isVisible = false
                }
            }
        }

        //collect different events
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            sharedViewModel.reminderEvents.collect { event ->
                when (event) {
                    is SharedViewModel.MainEvents.ShowUndoReminderDeleteMessage -> {
                        Snackbar.make(requireView(), "Reminder deleted", Snackbar.LENGTH_LONG)
                            .setAnchorView(fabCreateNewReminder)
                            .setAction("UNDO") {
                                sharedViewModel.onUndoDeleteClick(event.reminder, event.deletedReminder)
                            }.show()
                    }
                    is SharedViewModel.MainEvents.ShowReminderDiscardedMessage -> {
                        Snackbar.make(requireView(), "Reminder discarded", Snackbar.LENGTH_SHORT)
                            .setAnchorView(fabCreateNewReminder)
                            .show()
                    }
                    is SharedViewModel.MainEvents.ShareReminderMenuItemClicked -> {
                        shareReminderIntent(event.reminder)
                    }
                }
            }
        }

        fabCreateNewReminder.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToReminderFragment()
            findNavController().navigate(action)
            fabCreateNewReminder.hide()
            bottomAppBar.performHide()
        }

        bottomAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.bap_delete_all -> {
                    showDeleteAllDialog()
                    true
                }
                R.id.bap_day_start -> {
                    (activity as MainActivity).showTimePicker()
                    true
                }

                else -> false
            }
        }

        bottomAppBar.setNavigationOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToBottomMenuFragment()
            findNavController().navigate(action)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //handle context clicks from rv, group id contains the adapter position of item clicked
    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            SHARE_CONTEXT_MENU_ID -> {
                sharedViewModel.onShareReminderClicked(item.groupId)
                true
            }
            DELETE_CONTEXT_MENU_ID -> {
                sharedViewModel.onDeleteReminderClicked(item.groupId)
                true
            }
            COPY_CONTEXT_MENU_ID -> {
                sharedViewModel.insert(item.groupId)
                true
            }
            else -> {
                super.onContextItemSelected(item)
            }
        }
    }

    override fun onItemClick(reminderId: Int, card: MaterialCardView) {
        fabCreateNewReminder.hide()
        bottomAppBar.performHide()

        val action = MainFragmentDirections.actionMainFragmentToReminderFragment(reminderId)
        val extras = FragmentNavigatorExtras(card to "container_card")
        findNavController().navigate(action, extras)
    }

    private fun shareReminderIntent(reminder: Reminder) {
        val textToShare = "${reminder.title}\n${reminder.note}"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textToShare)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun checkLayoutManager() {
        val shared = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val savedLayout = shared.getString("layout", "vertical")
        if (savedLayout == "grid")
            binding.rvReminderList.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        if (savedLayout == "vertical")
            binding.rvReminderList.layoutManager = LinearLayoutManager(activity)
    }

    private fun showDeleteAllDialog() {
        if (adapter.currentList.isNotEmpty()) {
            val alertDialog = AlertDialog.Builder(requireActivity())
            alertDialog.setTitle(getString(R.string.delete_dialog_confirm_title))
            alertDialog.setMessage(getString(R.string.delete_dialog_message))
            alertDialog.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                sharedViewModel.deleteAll()
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            }
            alertDialog.setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            alertDialog.create().show()
        } else {
            Toast.makeText(requireContext(), getString(R.string.list_is_empty), Toast.LENGTH_SHORT)
                .show()
        }
    }
}