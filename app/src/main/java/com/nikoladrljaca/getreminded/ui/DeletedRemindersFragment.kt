package com.nikoladrljaca.getreminded.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Slide
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import com.nikoladrljaca.getreminded.R
import com.nikoladrljaca.getreminded.adapter.DeletedReminderAdapter
import com.nikoladrljaca.getreminded.databinding.FragmentDeletedRemindersBinding
import com.nikoladrljaca.getreminded.utils.ANIM_DURATION
import com.nikoladrljaca.getreminded.viewmodel.DeletedRemindersViewModel
import com.nikoladrljaca.getreminded.viewmodel.DeletedRemindersViewModel.DeletedRemindersEvents.ShowAllRestoredMessage
import kotlinx.coroutines.flow.collect

class DeletedRemindersFragment : Fragment(R.layout.fragment_deleted_reminders),
    DeletedReminderAdapter.OnItemClickListener {

    private val viewModel: DeletedRemindersViewModel by activityViewModels()
    private lateinit var adapter: DeletedReminderAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentDeletedRemindersBinding.bind(view)
        adapter = DeletedReminderAdapter(this)
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab_create_new_reminder)
        val bottomAppBar = requireActivity().findViewById<BottomAppBar>(R.id.bottomAppBar)

        enterTransition = MaterialElevationScale(true).apply {
            duration = ANIM_DURATION
        }

        returnTransition = Slide().apply {
            addTarget(R.id.deleted_reminders_card_view)
            duration = ANIM_DURATION
        }

        binding.rvDeletedList.adapter = adapter
        binding.apply {
            rvDeletedList.layoutManager = LinearLayoutManager(activity)

            viewModel.allDeletedReminders.observe(viewLifecycleOwner) { deletedReminders ->
                adapter.submitList(deletedReminders)
            }

            ivDeleteAll.setOnClickListener {
                //delete all reminders perm
                displayDeleteAllDialog()
            }

            ivRestoreAll.setOnClickListener {
                //restore all reminders
                viewModel.onRestoreAllClicked()
            }
        }

        //listen for viewModel events
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    is ShowAllRestoredMessage ->{
                        Snackbar.make(requireView(), "All reminders restored", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        fab.visibility = View.INVISIBLE
        bottomAppBar.visibility = View.INVISIBLE
    }

    override fun onItemDelete(position: Int) {
        displayDeleteReminderDialog(position)
    }

    override fun onItemRestore(position: Int) {
        viewModel.onRestoreReminderClicked(position)
    }

    private fun displayDeleteAllDialog() {
        if (adapter.currentList.isNotEmpty()) {
            val alertDialog = AlertDialog.Builder(requireActivity())
            alertDialog.setTitle(getString(R.string.delete_dialog_confirm_title))
                .setMessage("Reminders will be deleted permanently. Are you sure?")
                .setPositiveButton("Yes") { dialog, _ ->
                    viewModel.deleteAllReminders()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create().show()
        } else {
            Toast.makeText(requireContext(), getString(R.string.list_is_empty), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun displayDeleteReminderDialog(position: Int) {
        val alertDialog = AlertDialog.Builder(requireActivity())
        alertDialog.setTitle("Are you sure?")
            .setMessage("This will delete the reminder permanently.")
            .setPositiveButton("Yes") { dialog, _ ->
                viewModel.onDeleteReminderClicked(position)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create().show()
    }
}