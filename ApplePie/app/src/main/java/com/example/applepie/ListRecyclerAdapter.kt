package com.example.applepie

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.graphics.toColor
import androidx.recyclerview.widget.RecyclerView
import com.example.applepie.R
import com.example.applepie.model.TaskList
import com.google.common.io.Resources.getResource

class ListRecyclerAdapter(private val context: Context, private var taskLists: List<TaskList>): RecyclerView.Adapter<ListRecyclerAdapter.ViewHolder>() {
//    private val db: SMDatabase = SMDatabase.getInstance(context)

    var onItemClick: ((TaskList) -> Unit)? = null

    inner class ViewHolder(listItemView: View): RecyclerView.ViewHolder(listItemView) {
        val iconTV = listItemView.findViewById<TextView>(R.id.list_icon_text_view)
        val nameTV = listItemView.findViewById<TextView>(R.id.list_name_text_view)

        init {
            listItemView.setOnClickListener { onItemClick?.invoke(taskLists[absoluteAdapterPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListRecyclerAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val listLayout = inflater.inflate(R.layout.layout_list, parent, false)
        return ViewHolder(listLayout)
    }

    override fun getItemCount(): Int {
        return taskLists.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val taskList = taskLists[position]
        holder.nameTV.text = taskList.list_name
        holder.iconTV.text = taskList.list_icon

        holder.iconTV.setTextColor(taskList.list_color - 0x1000000)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setLists(taskLists: List<TaskList>) {
        this.taskLists = taskLists
        notifyDataSetChanged()
    }
}