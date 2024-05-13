import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.myapp.R

class Relatorios : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_relatorios)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Recuperar a data e hora do SharedPreferences
        val sharedPreferences = getSharedPreferences("RegistroPonto", MODE_PRIVATE)
        val ultimoRegistro = sharedPreferences.getString("ultimoRegistro", "Nenhum registro encontrado")

        // Suponha que você tenha um TextView com id txtUltimoRegistro para mostrar a data/hora
        val txtUltimoRegistro = findViewById<TextView>(R.id.button_registrar_ponto)
        txtUltimoRegistro.text = ultimoRegistro
    }
}
