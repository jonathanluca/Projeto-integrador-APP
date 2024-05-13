package br.com.myapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class calendario : AppCompatActivity() {
    private lateinit var className: EditText
    private lateinit var classTime: EditText
    private lateinit var addButton: Button
    private lateinit var classesRecyclerView: RecyclerView
    private lateinit var daysOfWeekCarousel: RecyclerView
    private lateinit var database: FirebaseDatabase
    private lateinit var lessonAdapter: LessonAdapter
    private var selectedDay: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario)

        database = FirebaseDatabase.getInstance()

        className = findViewById(R.id.className)
        classTime = findViewById(R.id.classTime)
        addButton = findViewById(R.id.addButton)
        classesRecyclerView = findViewById(R.id.classesRecyclerView)
        classesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Obtenha o UID do usuário atual
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Toast.makeText(this, "Não foi possível identificar o usuário logado.", Toast.LENGTH_LONG).show()
            return
        }

        // Instancia o LessonAdapter com o UID do usuário
        lessonAdapter = LessonAdapter(mutableListOf(), userId)
        classesRecyclerView.adapter = lessonAdapter

        daysOfWeekCarousel = findViewById(R.id.daysOfWeekCarousel)
        val days = listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb")
        val dayAdapter = DaysAdapter(days) { day ->
            selectedDay = day
            addButton.text = "Adicionar Aula para $selectedDay"
        }
        daysOfWeekCarousel.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        daysOfWeekCarousel.adapter = dayAdapter

        addButton.setOnClickListener { addLesson() }

        database.reference.child("users").child(userId).child("lessons").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lessons = mutableListOf<Pair<String, String>>()
                snapshot.children.forEach { child ->
                    val lesson = child.getValue(String::class.java)
                    lesson?.let {
                        lessons.add(Pair(it, child.key ?: ""))
                    }
                }
                lessonAdapter.updateLessons(lessons)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@calendario, "Erro ao carregar aulas: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addLesson() {
        val name = className.text.toString()
        val time = classTime.text.toString()
        if (name.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedDay.isEmpty()) {
            Toast.makeText(this, "Por favor, selecione um dia primeiro.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val lessonInfo = "$name - $time - $selectedDay"
        val lessonId = database.reference.child("users").child(userId).child("lessons").push().key ?: return
        database.reference.child("users").child(userId).child("lessons").child(lessonId).setValue(lessonInfo).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                className.text.clear()
                classTime.text.clear()
            } else {
                Toast.makeText(this, "Falha ao salvar a aula.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
