package com.nikoladrljaca.getreminded.ui

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
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
import com.nikoladrljaca.getreminded.adapter.ReminderListAdapter
import com.nikoladrljaca.getreminded.databinding.FragmentMainBinding
import com.nikoladrljaca.getreminded.viewmodel.Reminder
import com.nikoladrljaca.getreminded.viewmodel.SharedViewModel
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import java.util.*

class MainFragment : Fragment(R.layout.fragment_main), ReminderListAdapter.OnReminderClickListener {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel by activityViewModels<SharedViewModel>()

    private lateinit var fabCreateNewReminder: FloatingActionButton
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var adapter: ReminderListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)
        fabCreateNewReminder = requireActivity().findViewById(R.id.fab_create_new_reminder)
        bottomAppBar = requireActivity().findViewById(R.id.bottomAppBar)

        //transitions
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        exitTransition = MaterialElevationScale(false).apply {
            duration = 250
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = 250
        }

        adapter = ReminderListAdapter()
        adapter.setListener(this)
        binding.apply {
            rvReminderList.setHasFixedSize(true)
            rvReminderList.adapter = adapter
            checkLayoutManager()
        }

        fabCreateNewReminder.show()
        fabCreateNewReminder.visibility = View.VISIBLE
        bottomAppBar.performShow()
        bottomAppBar.visibility = View.VISIBLE

        sharedViewModel.allReminders.observe(viewLifecycleOwner, {
            adapter.setReminderList(it)

            if (adapter.isListEmpty()) {
                binding.rvReminderList.visibility = View.GONE
                binding.tvNothingToShow.visibility = View.VISIBLE
            } else {
                binding.rvReminderList.visibility = View.VISIBLE
                binding.tvNothingToShow.visibility = View.GONE
            }
        })

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
                adapter.setReminderList(filteredList)
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
                var undoPressed = false

                sharedViewModel.onReminderSwiped(current)
            }
        }
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.rvReminderList)

        //collect different events
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            sharedViewModel.reminderEvents.collect { event ->
                when (event) {
                    is SharedViewModel.MainEvents.ShowUndoReminderDeleteMessage -> {
                        Snackbar.make(requireView(), "Reminder deleted", Snackbar.LENGTH_LONG)
                            .setAnchorView(fabCreateNewReminder)
                            .setAction("UNDO") {
                                sharedViewModel.onUndoDeleteClick(event.reminder)
                            }.show()
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
                R.id.bap_settings -> {
                    fabCreateNewReminder.hide()
                    bottomAppBar.performHide()
                    findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
                    true
                }
                R.id.bap_day_start -> {
                    (activity as MainActivity).showTimePicker()
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClickListener(reminderId: Int, cardView: MaterialCardView) {
        fabCreateNewReminder.hide()
        bottomAppBar.performHide()

        val action = MainFragmentDirections.actionMainFragmentToReminderFragment(reminderId)
        val extras = FragmentNavigatorExtras(cardView to "container_card")
        findNavController().navigate(action, extras)
    }

    private fun checkLayoutManager() {
        val shared = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val savedLayout = shared.getString("layout", null)
        if (savedLayout == "grid")
            binding.rvReminderList.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        if (savedLayout == "vertical")
            binding.rvReminderList.layoutManager = LinearLayoutManager(activity)
    }

    private fun showDeleteAllDialog() {
        if (!adapter.isListEmpty()) {
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