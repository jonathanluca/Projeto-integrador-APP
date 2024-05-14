package br.com.myapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.myapp.databinding.ActivityRelatorios2Binding
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Relatorios : AppCompatActivity() {
    private lateinit var binding: ActivityRelatorios2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRelatorios2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Aplicando o listener ao container principal
        ViewCompat.setOnApplyWindowInsetsListener(binding.relatorios) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnHistorico.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                fetchAllRecords()
            }
        }
    }

    private suspend fun fetchAllRecords() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("pontos")
        try {
            val dataSnapshot = databaseReference.orderByKey().get().await()
            if (dataSnapshot.exists()) {
                val records = StringBuilder("Histórico de Registros:\n\n")
                dataSnapshot.children.forEach { snapshot ->
                    val userId = snapshot.child("userId").value?.toString() ?: "Usuário desconhecido"
                    val timestamp = snapshot.child("timestamp").value?.toString()?.toLongOrNull() ?: System.currentTimeMillis()
                    val latitude = snapshot.child("latitude").value?.toString() ?: "Latitude desconhecida"
                    val longitude = snapshot.child("longitude").value?.toString() ?: "Longitude desconhecida"
                    val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
                    records.append("Data: $formattedDate\nUsuário: $userId\nLatitude: $latitude\nLongitude: $longitude\n\n")
                }
                withContext(Dispatchers.Main) {
                    binding.txtHistorico.text = records.toString()
                }
            } else {
                withContext(Dispatchers.Main) {
                    binding.txtHistorico.text = "Nenhum registro encontrado"
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                binding.txtHistorico.text = "Erro ao recuperar dados"
                Toast.makeText(this@Relatorios, "Erro ao acessar dados: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                e.printStackTrace() // Log the stack trace for more detailed error information
            }
        }
    }
}
