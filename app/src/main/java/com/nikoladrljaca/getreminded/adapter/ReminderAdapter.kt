package com.nikoladrljaca.getreminded.adapter

import android.content.Context

import android.view.*

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

import com.nikoladrljaca.getreminded.databinding.ListItemLayoutBinding
import com.nikoladrljaca.getreminded.utils.*
import com.nikoladrljaca.getreminded.viewmodel.Reminder

class ReminderAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Reminder, ReminderAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ListItemLayoutBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {
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
            }
        }

        fun bind(reminder: Reminder) {
            binding.apply {
                tvReminderTitle.text = reminder.title
                tvReminderNote.text = reminder.note
                tvReminderDate.text = dateFromEpoch(reminder.date)
                card.transitionName = reminder.id.toString()
                //change color of the layout, since the card wont do it
                //this color should be grabbed from the reminder itself
                //a raw color int will be provided here
                cardLayout.setBackgroundColor(reminder.color)
                card.setOnCreateContextMenuListener(this@ViewHolder)

                if (tvReminderNote.text.toString().isEmpty()) {
                    tvReminderNote.visibility = View.GONE
                }
            }
        }

        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            //first parameter is adapter position so that the frag can have access to it via groupId
            menu?.setHeaderTitle("Options")
            menu?.add(this.adapterPosition, SHARE_CONTEXT_MENU_ID, 0, "Share")
            menu?.add(this.adapterPosition, DELETE_CONTEXT_MENU_ID, 0, "Delete")
            menu?.add(this.adapterPosition, COPY_CONTEXT_MENU_ID, 0, "Make a copy")
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Reminder>() {
        override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder) = (oldItem.id) == (newItem.id)

        override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder) = oldItem == newItem
    }

    interface OnItemClickListener {
        fun onItemClick(reminderId: Int, card: MaterialCardView)
    }
}