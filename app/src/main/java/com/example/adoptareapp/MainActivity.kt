package com.example.adoptareapp
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
    }

    private fun openRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
    private fun openLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
