package com.example.adoptareapp
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.scoreboard.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val registerButton: Button = findViewById(R.id.signup)
        registerButton.setOnClickListener {
            openRegisterActivity()
        }

        val loginButton: Button = findViewById(R.id.login)
        loginButton.setOnClickListener {
            openLoginActivity()
        }

        val beforeAdoptButton: Button = findViewById(R.id.antes)
        beforeAdoptButton.setOnClickListener {
            showBeforeAdoptDialog()
        }

        val adoptButton: Button = findViewById(R.id.adoptar)
        adoptButton.setOnClickListener {
            showAdoptDialog()
        }
    }

    private fun openRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
    private fun openLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
    private fun showBeforeAdoptDialog() {
        AlertDialog.Builder(this)
            .setTitle("Antes de adoptar")
            .setMessage("Lo que debe evaluar antes de adoptar una mascota\n" +
                    "Compromiso a largo plazo. Un perro puede vivir de 12 a 17 años, según su tamaño, raza y cuidados. ...\n" +
                    "Los gastos que implica tener un animal de compañía. ...\n" +
                    "Dedicación y tiempo. ...\n" +
                    "Adaptar la casa a la mascota. ...\n" +
                    "Acuerdo familiar.")
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showAdoptDialog() {
        AlertDialog.Builder(this)
            .setTitle("Adoptar")
            .setMessage("Lo que debe evaluar antes de adoptar una mascota\n" +
                    "Registrase en la aplicación.\n" +

                    "Ponerse en contacto con el refugio para pedir informes sobre la mascota disponible por el refugio.")
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
