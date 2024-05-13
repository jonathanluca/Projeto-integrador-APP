package br.com.myapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.myapp.databinding.ActivityCriarContasBinding
import com.google.firebase.auth.FirebaseAuth

class Criar_contas : AppCompatActivity() {
    private lateinit var binding: ActivityCriarContasBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCriarContasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.button4.setOnClickListener {
            val email = binding.emailCadastro.text.toString().trim()
            val password = binding.senhaCadastro.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                criarUsuario(email, password)
            } else {
                Toast.makeText(this, "Email e senha não podem estar vazios", Toast.LENGTH_SHORT).show()
            }
        }

        binding.SairCadastro.setOnClickListener {
            finish()
        }
    }

    private fun criarUsuario(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java)) // Redirecionar para a tela principal ou de login
                finish()
            } else {
                Toast.makeText(this, "Falha no cadastro: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
