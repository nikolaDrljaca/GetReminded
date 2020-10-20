package com.nikoladrljaca.getreminded.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nikoladrljaca.getreminded.databinding.ListItemLayoutBinding
import com.nikoladrljaca.getreminded.viewmodel.Reminder
import java.text.SimpleDateFormat
import java.util.*

class ReminderListAdapter :
    RecyclerView.Adapter<ReminderListAdapter.ReminderViewHolder>() {

    interface OnReminderClickListener {
        fun onItemClickListener(position: Int, reminderId: Int)
    }

    private var listOfReminders = emptyList<Reminder>()
    private lateinit var listener: OnReminderClickListener

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReminderViewHolder {
        val binding = ListItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val current = listOfReminders[position]
        holder.bind(current)
        holder.itemView.setOnClickListener {
            val reminderId = current.id!!
            listener.onItemClickListener(position, reminderId)
        }
    }

    inner class ReminderViewHolder(private val binding: ListItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(reminder: Reminder) {
            binding.apply {
                tvReminderTitle.text = reminder.title
                tvReminderNote.text = reminder.note
                tvReminderDate.text = dateFromEpoch(reminder.date)

                if (tvReminderNote.text.toString().isEmpty()) {
                    tvReminderNote.visibility = View.GONE
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return listOfReminders.size
    }

    fun setListener(listener: OnReminderClickListener) {
        this.listener = listener
    }

    fun setReminderList(reminderList: List<Reminder>) {
        listOfReminders = reminderList
        notifyDataSetChanged()
    }

    fun isListEmpty(): Boolean {
        return listOfReminders.isEmpty()
    }

    companion object {
        fun dateFromEpoch(epoch: Long): String {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.US)
            val date = Date(epoch)
            return sdf.format(date)
        }
    }
}