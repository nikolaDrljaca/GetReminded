package com.nikoladrljaca.getreminded.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.nikoladrljaca.getreminded.databinding.ListItemLayoutBinding
import com.nikoladrljaca.getreminded.utils.dateFromEpoch
import com.nikoladrljaca.getreminded.viewmodel.Reminder

class ReminderAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Reminder, ReminderAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ListItemLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        //this is for performance purposes, if the click was defined in the bind method
        //it would execute each time a new list item is passed, this way this is only done
        //when a new viewHolder for multiple items is created
        init {
            binding.apply {
                card.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        val reminderId = getItem(adapterPosition).id!!
                        listener.onItemClick(reminderId, binding.card)
                    }
                }

                card.setOnLongClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        val reminderId = getItem(adapterPosition).id!!
                        listener.onItemLongClick(reminderId)
                    }
                    true
                }
            }
        }

        fun bind(reminder: Reminder) {
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

    class DiffCallback: DiffUtil.ItemCallback<Reminder> () {
        override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder) = (oldItem.id) == (newItem.id)

        override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder) = oldItem == newItem
    }

    interface OnItemClickListener {
        fun onItemClick(reminderId: Int, card: MaterialCardView)
        fun onItemLongClick(reminderId: Int)
    }
}