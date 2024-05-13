package br.com.myapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class LessonAdapter(private var lessons: MutableList<Pair<String, String>>, private val userId: String) :
    RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {

    class LessonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lessonText: TextView = view.findViewById(R.id.lessonText)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lesson_item, parent, false)
        return LessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        val (lessonInfo, lessonId) = lessons[position]
        holder.lessonText.text = lessonInfo
        holder.deleteButton.setOnClickListener {
            // Use o UID do usuário ao excluir a aula
            FirebaseDatabase.getInstance().reference.child("users").child(userId).child("lessons").child(lessonId).removeValue()
        }
    }

    override fun getItemCount() = lessons.size

    fun updateLessons(newLessons: List<Pair<String, String>>) {
        lessons.clear()
        lessons.addAll(newLessons)
        notifyDataSetChanged()
    }
}
