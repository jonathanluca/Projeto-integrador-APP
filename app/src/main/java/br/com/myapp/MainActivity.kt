package br.com.myapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.myapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa o Firebase Auth
        auth = Firebase.auth

        // Verifica se o usuário já está logado
        if (auth.currentUser != null) {
            // Usuário já está logado, redireciona para a tela inicial
            val telaInicial = Intent(this, Tela_incial::class.java)
            startActivity(telaInicial)
            finish()
        } else {
            // Navegação entre telas quando não logado
            navEntreTelas()
        }
    }

    private fun navEntreTelas() {
        binding.buttonAcessar.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val senha = binding.editSenha.text.toString()

            if (email.isNotEmpty() && senha.isNotEmpty()) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    autenticar(email, senha)
                } else {
                    Toast.makeText(baseContext, "Por favor, insira um email válido.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(baseContext, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.RedefinirSenha.setOnClickListener {
            val redefinirSenha = Intent(this, Redefinir_senha::class.java)
            startActivity(redefinirSenha)
        }
        binding.NaoTemConta.setOnClickListener {
            val Criar_contas = Intent(this, Criar_contas::class.java)
            startActivity(Criar_contas)
        }
    }

    private fun autenticar(email: String, senha: String) {
        auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(baseContext, "Autenticado com sucesso!", Toast.LENGTH_LONG).show()
                val telaInicial = Intent(this, Tela_incial::class.java)
                startActivity(telaInicial)
                finish() // Finaliza a atividade de login após sucesso
            } else {
                Toast.makeText(baseContext, "Autenticação falhou, tente novamente.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
