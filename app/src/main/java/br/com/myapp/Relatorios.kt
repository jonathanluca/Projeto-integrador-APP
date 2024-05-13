import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase
import br.com.myapp.R
import br.com.myapp.databinding.ActivityRelatorios2Binding

class Relatorios : AppCompatActivity() {
    private lateinit var binding: ActivityRelatorios2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityRelatorios2Binding.inflate(layoutInflater)
        setContentView(binding.root) 

        // Aplicando o listener ao container principal
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.relatorios)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btnHistorico).setOnClickListener {
            fetchAllRecords()
        }
    }

    private fun fetchAllRecords() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("pontos")
        databaseReference.orderByKey().get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val records = StringBuilder("Histórico de Registros:\n")
                dataSnapshot.children.forEach { snapshot ->
                    val userId = snapshot.child("userId").value.toString()
                    val timestamp = snapshot.child("timestamp").value.toString()
                    val latitude = snapshot.child("latitude").value.toString()
                    val longitude = snapshot.child("longitude").value.toString()
                    records.append("Registro: $timestamp por usuário $userId em latitude $latitude e longitude $longitude\n")
                }
                val txtHistorico = findViewById<TextView>(R.id.txtHistorico)
                txtHistorico.text = records.toString()
            } else {
                val txtHistorico = findViewById<TextView>(R.id.txtHistorico)
                txtHistorico.text = "Nenhum registro encontrado"
            }
        }.addOnFailureListener {
            val txtHistorico = findViewById<TextView>(R.id.txtHistorico)
            txtHistorico.text = "Erro ao recuperar dados"
        }
    }
}
