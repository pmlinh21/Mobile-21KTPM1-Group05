package com.example.applepie

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.applepie.database.FirebaseManager
import com.example.applepie.database.PreferenceManager
import com.example.applepie.model.Task
import es.dmoral.toasty.Toasty

class TaskRecyclerAdapter(private val context: Context, private var tasks: List<Task>): RecyclerView.Adapter<TaskRecyclerAdapter.ViewHolder>() {

    var onItemClick: ((Task) -> Unit)? = null

    inner class ViewHolder(listItemView: View): RecyclerView.ViewHolder(listItemView) {
        val preferenceManager: PreferenceManager = PreferenceManager(context)
        val taskStatusCB: CheckBox = listItemView.findViewById<CheckBox>(R.id.task_status_check_box)
        val taskTitleTV: TextView = listItemView.findViewById<TextView>(R.id.task_title_text_view)
        val listNameTV: TextView = listItemView.findViewById<TextView>(R.id.list_name_text_view)
        val listIconTV: TextView = listItemView.findViewById<TextView>(R.id.list_icon_text_view)
        val dueDateTV: TextView = listItemView.findViewById<TextView>(R.id.due_date_text_view)

        init {
            listItemView.setOnClickListener {
                onItemClick?.invoke(tasks[absoluteAdapterPosition])
                Toast.makeText(context, "Go to task details page", Toast.LENGTH_SHORT).show()
            }

            taskStatusCB.setOnClickListener {
                val task = tasks[absoluteAdapterPosition]
                Toasty.success(context, "Task completed", Toasty.LENGTH_SHORT).show()
                FirebaseManager.setTaskStatus(preferenceManager.getIndex(), task.id_task, !task.isDone)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskRecyclerAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val listLayout = inflater.inflate(R.layout.layout_task, parent, false)
        return ViewHolder(listLayout)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskStatusCB.isChecked = task.isDone
        holder.taskTitleTV.text = task.title
        holder.listNameTV.text = task.listName
        holder.dueDateTV.text = task.due_datetime

        if (task.list_color == -1){
            holder.listIconTV.setTextColor(context.getColor(R.color.green))
            holder.listNameTV.setTextColor(context.getColor(R.color.green))
        } else {
            holder.listIconTV.setTextColor(task.list_color - 0x1000000)
            holder.listNameTV.setTextColor(task.list_color - 0x1000000)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTasks(tasks: List<Task>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }
}