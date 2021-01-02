package com.nikoladrljaca.getreminded.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.nikoladrljaca.getreminded.databinding.ListItemLayoutBinding
import com.nikoladrljaca.getreminded.viewmodel.Reminder
import java.text.SimpleDateFormat
import java.util.*

class ReminderListAdapter :
    RecyclerView.Adapter<ReminderListAdapter.ReminderViewHolder>() {

    interface OnReminderClickListener {
        fun onItemClickListener(reminderId: Int, cardView: MaterialCardView)
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
    }

    inner class ReminderViewHolder(private val binding: ListItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(reminder: Reminder) {
            binding.apply {
                tvReminderTitle.text = reminder.title
                tvReminderNote.text = reminder.note
                tvReminderDate.text = dateFromEpoch(reminder.date)
                card.transitionName = reminder.id.toString()

                if (tvReminderNote.text.toString().isEmpty()) {
                    tvReminderNote.visibility = View.GONE
                }

                card.setOnClickListener {
                    val reminderId = reminder.id!!
                    listener.onItemClickListener(reminderId, card)
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