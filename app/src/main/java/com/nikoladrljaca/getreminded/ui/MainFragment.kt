package com.nikoladrljaca.getreminded.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.*
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.nikoladrljaca.getreminded.MainActivity
import com.nikoladrljaca.getreminded.R
import com.nikoladrljaca.getreminded.adapter.ReminderListAdapter
import com.nikoladrljaca.getreminded.databinding.FragmentMainBinding
import com.nikoladrljaca.getreminded.viewmodel.Reminder
import com.nikoladrljaca.getreminded.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
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

        //check and get any shared text
        if (requireActivity().intent.hasExtra(Intent.EXTRA_TEXT)) {
            if (requireActivity().intent?.action == Intent.ACTION_SEND) {
                if (requireActivity().intent.type == "text/plain") {
                    val sharedText = requireActivity().intent.getStringExtra(Intent.EXTRA_TEXT)
                    val bundle = bundleOf("sharedString" to sharedText, "sharedItemSent" to true)
                    findNavController().navigate(R.id.action_mainFragment_to_reminderFragment, bundle)
                    requireActivity().intent.removeExtra(Intent.EXTRA_TEXT)
                }
            }
        }

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
            ItemTouchHelper.RIGHT + ItemTouchHelper.LEFT
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

                Snackbar.make(requireView(), getString(R.string.item_deleted), Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        undoPressed = true
                        adapter.notifyDataSetChanged()
                    }
                    .setAnchorView(fabCreateNewReminder)
                    .show()
                sharedViewModel.viewModelScope.launch {
                    delay(2750) //wait to see if the undo button will get pressed
                    sharedViewModel.deleteEntry(current, !undoPressed)
                }
            }
        }
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.rvReminderList)

        fabCreateNewReminder.setOnClickListener {
            sharedViewModel.setClearReminder(true)
            findNavController().navigate(R.id.action_mainFragment_to_reminderFragment)
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

    override fun onItemClickListener(position: Int) {
        fabCreateNewReminder.hide()
        bottomAppBar.performHide()

        val current = sharedViewModel.allReminders.value!![position]
        sharedViewModel.setDisplayReminder(current)
        sharedViewModel.setClearReminder(false)
        findNavController().navigate(R.id.action_mainFragment_to_reminderFragment)
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