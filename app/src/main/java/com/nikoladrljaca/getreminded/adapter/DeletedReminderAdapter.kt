package com.nikoladrljaca.getreminded.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nikoladrljaca.getreminded.databinding.ItemDeletedReminderBinding
import com.nikoladrljaca.getreminded.utils.dateFromEpoch
import com.nikoladrljaca.getreminded.viewmodel.DeletedReminder


class DeletedReminderAdapter(private val listener: OnItemClickListener) :
    ListAdapter<DeletedReminder, DeletedReminderAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDeletedReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemDeletedReminderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                btnDelete.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        listener.onItemDelete(adapterPosition)
                    }
                }

                btnRestore.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        listener.onItemRestore(adapterPosition)
                    }
                }


            }
        }

        fun bind(reminder: DeletedReminder) {
            binding.apply {
                tvReminderTitle.text = reminder.title
                tvReminderNote.text = reminder.note
                tvReminderDate.text = dateFromEpoch(reminder.date)
                card.transitionName = reminder.id.toString()

                if (tvReminderNote.text.toString().isEmpty()) {
                    tvReminderNote.visibility = View.GONE
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DeletedReminder>() {
        override fun areItemsTheSame(oldItem: DeletedReminder, newItem: DeletedReminder) =
            (oldItem.id) == (newItem.id)

        override fun areContentsTheSame(oldItem: DeletedReminder, newItem: DeletedReminder) =
            oldItem == newItem

    }

    interface OnItemClickListener {
        fun onItemDelete(position: Int)
        fun onItemRestore(position: Int)
    }
}