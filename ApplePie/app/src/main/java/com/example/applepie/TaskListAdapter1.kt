package com.example.applepie

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.applepie.database.FirebaseManager
import com.example.applepie.database.PreferenceManager
import com.example.applepie.model.TaskList
import com.example.applepie.model.Task
import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.util.Locale

class TaskListAdapter1(context: Context, tasks: List<Task>, lists: List<TaskList>):
    RecyclerView.Adapter<TaskListAdapter1.ViewHolder>() {
    private val context: Context = context
    private var tasks: List<Task> = tasks
    private val lists: List<TaskList> = lists
    interface OnItemClickListener{
        fun onItemClick(task: Task)
    }
    private var onItemClickListener: OnItemClickListener? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitleTextView: TextView = itemView.findViewById(R.id.taskTitleTextView)
        val dueDateTextView: TextView = itemView.findViewById(R.id.dueDateTextView)
        val dueTimeTextView: TextView = itemView.findViewById(R.id.dueTimeTextView)
//        val listTextView: TextView = itemView.findViewById(R.id.listTextView)
        val taskStatus: ImageView = itemView.findViewById(R.id.taskStatus)
        val taskStatus_1: ImageView = itemView.findViewById(R.id.taskStatus_1)
        val taskDelete: ImageView = itemView.findViewById(R.id.taskDelete)
        val priorityImageView: ImageView = itemView.findViewById(R.id.priorityImageView)

        val preferenceManager = PreferenceManager(context)

        init {
            taskStatus.setOnClickListener {
                val task = tasks[absoluteAdapterPosition]
                Toasty.success(context, "Task completed", Toasty.LENGTH_SHORT).show()
                FirebaseManager.setTaskStatus(preferenceManager.getIndex(), task.id_task, !task.isDone)
            }
            taskDelete.setOnClickListener {
                val task = tasks[absoluteAdapterPosition]
                Toasty.error(context, "Task deleted", Toasty.LENGTH_SHORT).show()
                FirebaseManager.deleteTask(preferenceManager.getIndex(), task.id_task)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentTask = tasks[position]

        holder.taskTitleTextView.text = currentTask.title
        Log.i("isDone", currentTask.isDone.toString())
        if (currentTask.isDone) {
            holder.taskTitleTextView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.taskStatus.setImageResource(R.drawable.ic_circular_full)
            holder.taskStatus_1.visibility = View.VISIBLE
        } else {
            holder.taskTitleTextView.paintFlags = holder.taskTitleTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.taskStatus.setImageResource(R.drawable.ic_circular)
            holder.taskStatus_1.visibility = View.GONE
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val formattedDate = sdf.parse(currentTask.due_datetime)
        val formattedDateString = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(formattedDate)
        val formattedTimeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(formattedDate)
        holder.dueDateTextView.text = formattedDateString
        holder.dueTimeTextView.text = formattedTimeString

        if (currentTask.priority == "high") {
            holder.priorityImageView.setImageResource(R.drawable.ic_prio_high)
        } else if (currentTask.priority == "medium") {
            holder.priorityImageView.setImageResource(R.drawable.ic_prio_medium)
        } else if (currentTask.priority == "low") {
            holder.priorityImageView.setImageResource(R.drawable.ic_prio_low)
        }

        val matchingList = lists.find { it.id_list == currentTask.id_list }
        val listName = matchingList?.list_name ?: "Unknown List"

//        holder.listTextView.text = listName

        holder.itemView.setOnClickListener {
//            onItemClickListener?.onItemClick(currentTask)
            val fragment = TaskDetails.newInstance(currentTask.id_list, position, listName)
            (context as MainActivity).supportFragmentManager
            .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
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
}