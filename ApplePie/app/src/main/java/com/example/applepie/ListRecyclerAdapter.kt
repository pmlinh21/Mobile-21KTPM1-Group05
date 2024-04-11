//package com.example.studentmanagementv4
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.core.content.ContentProviderCompat.requireContext
//import androidx.recyclerview.widget.RecyclerView
//import com.example.applepie.R
//import com.example.studentmanagementv4.R
//import com.example.studentmanagementv4.model.Student
//
//class ListRecyclerAdapter(private val context: Context, private var students: List<Student>): RecyclerView.Adapter<StudentRecyclerAdapter.ViewHolder>() {
//    private val db: SMDatabase = SMDatabase.getInstance(context)
//
//    var onItemClick: ((Student) -> Unit)? = null
//
//    inner class ViewHolder(listItemView: View): RecyclerView.ViewHolder(listItemView) {
//        val nameTextView: TextView = listItemView.findViewById<TextView>(R.id.name_text_view)
//        val classTextView: TextView = listItemView.findViewById<TextView>(R.id.class_text_view)
//        val infoTextView: TextView = listItemView.findViewById<TextView>(R.id.info_text_view)
//        val iconTextView: TextView = listItemView.findViewById<TextView>(R.id.icon_text_view)
//
//        init {
//            listItemView.setOnClickListener { onItemClick?.invoke(students[absoluteAdapterPosition]) }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentRecyclerAdapter.ViewHolder {
//        val context = parent.context
//        val inflater = LayoutInflater.from(context)
//        val studentView = inflater.inflate(R.layout.student_recycler_item, parent, false)
//        return ViewHolder(studentView)
//    }
//
//    override fun getItemCount(): Int {
//        return students.size
//    }
//
//    @SuppressLint("SetTextI18n")
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val student = students[position]
//        holder.nameTextView.text = student.name
//        holder.classTextView.text = db.studentClassDAO().findById(student.classId).name
//        holder.infoTextView.text = "${student.birthday} - ${student.gender}"
//        holder.iconTextView.text = student.name.split(" ").map { it[0] }.joinToString("")
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    fun setStudents(students: List<Student>) {
//        this.students = students
//        notifyDataSetChanged()
//    }
//}