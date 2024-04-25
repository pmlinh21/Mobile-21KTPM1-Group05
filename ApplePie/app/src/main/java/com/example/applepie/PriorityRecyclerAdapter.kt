package com.example.applepie

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.applepie.model.Task
import es.dmoral.toasty.Toasty

class PriorityRecyclerAdapter(private val context: Context, private var tasks: List<Task>): RecyclerView.Adapter<PriorityRecyclerAdapter.ViewHolder>() {

    var onItemClick: ((Task) -> Unit)? = null

    inner class ViewHolder(listItemView: View): RecyclerView.ViewHolder(listItemView) {
        val taskTitleTV = listItemView.findViewById<TextView>(R.id.task_title_text_view)
        val listNameTV = listItemView.findViewById<TextView>(R.id.list_name_text_view)
        val dueDateTV = listItemView.findViewById<TextView>(R.id.due_date_text_view)

        init {
            listItemView.setOnClickListener {
                onItemClick?.invoke(tasks[absoluteAdapterPosition])
                Toasty.info(context, "Task clicked", Toasty.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PriorityRecyclerAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val listLayout = inflater.inflate(R.layout.layout_task_priority, parent, false)
        return ViewHolder(listLayout)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskTitleTV.text = task.title
        holder.listNameTV.text = task.listName
        holder.dueDateTV.text = task.due_datetime

//        val color = try {
//            task.list_color.toColor()
//        } catch (e: IllegalArgumentException) {
//            // Handle potential conversion errors (e.g., invalid color value)
//            ContextCompat.getColor(context, R.color.green) // Use a default color
//        }

//        holder.iconTV.setTextColor(ContextCompat.getColor(context, R.color.green))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTasks(tasks: List<Task>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }
}