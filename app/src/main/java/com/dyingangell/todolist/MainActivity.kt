
package com.dyingangell.todolist

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    // Модель задачи

    private val tasks = mutableListOf<Task>()
    private var adapter: TaskAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loadTasks()

        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        // Передаем tasks и onItemClick в адаптер
        adapter = TaskAdapter(tasks) { _, position ->
            showDeleteTaskDialog(position)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            showTaskDialog()
        }
    }

    private fun showDeleteTaskDialog(position: Int) {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Удалить задачу?")
            .setView(layout)
            .setPositiveButton("Удалить") { _, _ ->
                tasks.removeAt(position)
                adapter?.notifyItemRemoved(position)
            }
            .setNegativeButton("Отмена", null)
            .create()

        dialog.show()
    }

    private fun showTaskDialog() {
        val editText = EditText(this)
        editText.hint = "Введите задачу"

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)
        layout.addView(editText)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Добавить задачу")
            .setView(layout)
            .setPositiveButton("Добавить") { _, _ ->
                val taskText = editText.text.toString()
                if (taskText.isNotBlank()) {
                    tasks.add(Task(taskText))
                    adapter?.notifyItemInserted(tasks.size - 1)
                }
            }
            .setNegativeButton("Отмена", null)
            .create()

        dialog.show()
    }

    // Сохраняем задачи в SharedPreferences
    private fun saveTasks() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("ToDoListPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(tasks)
        editor.putString("tasks", json)
        editor.apply()
    }
    private fun loadTasks() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("ToDoListPrefs", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("tasks", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<Task>>() {}.type
            try {
                tasks.clear()
                tasks.addAll(Gson().fromJson(json, type) ?: mutableListOf())
            } catch (e: Exception) {
                Log.e("ERROR", "Ошибка при парсинге JSON", e)
            }
        }
    }


    override fun onStop() {
        super.onStop()
        saveTasks()
    }
}
