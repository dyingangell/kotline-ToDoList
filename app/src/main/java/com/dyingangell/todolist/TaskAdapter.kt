package com.dyingangell.todolist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Task(val text: String, var isCompleted: Boolean = false)
class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onItemClick: (Task, Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskText: TextView = itemView.findViewById(R.id.taskText)
        val taskCheckBox: CheckBox = itemView.findViewById(R.id.taskCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskText.text = task.text
        holder.itemView.setBackgroundColor(Color.parseColor("#E3F2FD")) // Светло-синий

        holder.itemView.setOnClickListener {
            onItemClick(task, position)
        }

        holder.taskCheckBox.setOnCheckedChangeListener(null) // Очищаем старый listener, чтобы избежать зацикливания
        holder.taskCheckBox.isChecked = task.isCompleted // Устанавливаем правильное состояние чекбокса
        holder.taskText.paint.isStrikeThruText = task.isCompleted

        holder.taskCheckBox.setOnCheckedChangeListener { _, isChecked ->
            task.isCompleted = isChecked
            holder.taskText.paint.isStrikeThruText = isChecked
            holder.taskText.invalidate() // Перерисовываем текст
        }
        holder.taskCheckBox.isChecked = task.isCompleted
    }

    override fun getItemCount() = tasks.size
}


