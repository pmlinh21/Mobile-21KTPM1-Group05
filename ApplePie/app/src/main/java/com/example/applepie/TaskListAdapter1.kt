package com.example.applepie

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.applepie.model.TaskList
import com.example.applepie.model.Task

class TaskListAdapter1(context: Context, tasks: ArrayList<Task>, lists: ArrayList<TaskList>):
    RecyclerView.Adapter<TaskListAdapter1.ViewHolder>() {
    private val context: Context = context
    private val tasks: ArrayList<Task> = tasks
    private val lists: ArrayList<TaskList> = lists
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
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_task_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentTask = tasks[position]

        holder.taskTitleTextView.text = currentTask.title
        if (currentTask.isDone) {
            holder.taskTitleTextView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.taskStatus.setImageResource(R.drawable.ic_circular_full)
            holder.taskStatus_1.visibility = View.VISIBLE
        }

        holder.dueDateTextView.text = currentTask.due_datetime

        val matchingList = lists.find { it.id_list == currentTask.id_list }
        val listName = matchingList?.list_name ?: "Unknown List"

        holder.listTextView.text = listName

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
}