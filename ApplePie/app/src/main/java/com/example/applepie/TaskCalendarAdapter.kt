package com.example.applepie

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.applepie.model.Task
import com.example.applepie.model.TaskList
import java.text.SimpleDateFormat
import java.util.Locale

class TaskCalendarAdapter (context: Context, tasks: List<Task>, lists: List<TaskList>):
    RecyclerView.Adapter<TaskCalendarAdapter.ViewHolder>() {
    private val context: Context = context
    private var tasks: List<Task> = tasks
    private val lists: List<TaskList> = lists
    interface OnItemClickListener{
        fun onItemClick(task: Task)
    }
    var onItemClickListener: OnItemClickListener? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitleTextView: TextView = itemView.findViewById(R.id.taskTitleTextView)
        val dueDateTextView: TextView = itemView.findViewById(R.id.dueDateTextView)
        val listTextView: TextView = itemView.findViewById(R.id.listTextView)
        val taskStatus: ImageView = itemView.findViewById(R.id.taskStatus)
        val taskStatus_1: ImageView = itemView.findViewById(R.id.taskStatus_1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.event_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentTask = tasks[position]

        holder.taskTitleTextView.text = currentTask.title
        if (currentTask.isDone) {
            holder.taskTitleTextView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
//            holder.taskStatus.setImageResource(R.drawable.ic_circular_full)
//            holder.taskStatus_1.visibility = View.VISIBLE
        }
        else{
            holder.taskTitleTextView.paintFlags = holder.taskTitleTextView.paintFlags and (Paint.STRIKE_THRU_TEXT_FLAG.inv())
//            holder.taskStatus.setImageResource(R.drawable.ic_circular)
//            holder.taskStatus_1.visibility = View.GONE
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val formattedDate = sdf.parse(currentTask.due_datetime)
        val outputFormat = SimpleDateFormat("EEE'\n'dd MMM'\n'HH:mm", Locale.getDefault())
        val formattedDateString = outputFormat.format(formattedDate)
        holder.dueDateTextView.text = formattedDateString

        if (currentTask.priority == "low") {
            holder.dueDateTextView.setTextColor(context.getColorCompat(R.color.black))
        }

        val matchingList = lists.find { it.id_list == currentTask.id_list }
        val listName = matchingList?.list_name ?: "Unknown List"

        holder.listTextView.text = listName

        val priorityColor = getPriorityColor(currentTask.priority)
        holder.dueDateTextView.setBackgroundColor(context.getColorCompat(priorityColor))

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(currentTask)
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun getPercentageDone(): Float {
        val totalTasks = tasks.size
        val doneTasks = tasks.filter { it.isDone }.count()
        return if (totalTasks > 0) doneTasks.toFloat() / totalTasks * 100 else 0.0f
    }

    fun updateData(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    fun getPriorityColor(priority: String): Int {
        return when (priority) {
            "none" -> R.color.none_color
            "low" -> R.color.low_color
            "medium" -> R.color.medium_color
            "high" -> R.color.high_color
            else -> R.color.none_color
        }
    }
}