package br.com.myapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.myapp.databinding.ActivityRedefinirSenhaBinding
import com.google.firebase.auth.FirebaseAuth

class Redefinir_senha : AppCompatActivity() {
    private lateinit var binding: ActivityRedefinirSenhaBinding
    private lateinit var auth: FirebaseAuth  // Declarar o FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRedefinirSenhaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()  // Inicializar o FirebaseAuth

        // Configurando o listener do botão para redefinir a senha
        binding.buttonRedefinirSenha.setOnClickListener {
            val email = binding.campoEmail.text.toString()
            esqueceuSenha(email)
        }
        binding.textSairRedefinirSenha.setOnClickListener {
            auth.signOut() // Desconecta o usuário
            finishAffinity() // Fecha todas as atividades
        }
    }

    private fun esqueceuSenha(email: String) {
        if (email.isEmpty()) {
            Toast.makeText(baseContext, "Insira seu email no campo de email.", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(
                baseContext,
                "Por favor, insira um email válido para redefinir a senha.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    baseContext,
                    "Instruções para redefinir a senha foram enviadas para seu email.",
                    Toast.LENGTH_LONG
                ).show()
                navigateToMainScreen() // Navegar para a tela inicial
            } else {
                Toast.makeText(
                    baseContext,
                    "Falha ao enviar email de redefinição. Verifique se o email está correto e tente novamente.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun navigateToMainScreen() {
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        startActivity(mainActivityIntent)
        finish()  // Finaliza a atividade atual para que o usuário não retorne a ela ao pressionar o botão Voltar
    }

    override fun onBackPressed() {
        // Não fazer nada aqui para impedir que o usuário volte à tela anterior
    }
}
