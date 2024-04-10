package com.example.applepie

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.applepie.database.Task

class TaskListAdapter(context: Context, tasks: ArrayList<Task>):
    RecyclerView.Adapter<TaskListAdapter.ViewHolder>() {
        private val context: Context = context
        private val tasks: ArrayList<Task> = tasks

        interface OnItemClickListener{
            fun onItemClick(task: Task)
        }
    var onItemClickListener: OnItemClickListener? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitleTextView: TextView = itemView.findViewById(R.id.taskTitleTextView)
        val dueDateTextView: TextView = itemView.findViewById(R.id.dueDateTextView)
        val listTextView: TextView = itemView.findViewById(R.id.listTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentTask = tasks[position]
        holder.taskTitleTextView.text = currentTask.title
        holder.dueDateTextView.text = currentTask.due_datetime
        holder.listTextView.text = currentTask.id_list.toString()

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(currentTask)
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun updateStudentList(newList: List<Task>) {
        tasks.clear()
        tasks.addAll(newList)
        notifyDataSetChanged()
    }
}