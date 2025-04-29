package com.example.zakazaka.Views

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.zakazaka.Data.Database.AppDatabase
import com.example.zakazaka.Models.UserEntity
import com.example.zakazaka.R
import com.example.zakazaka.Repository.UserRepository
import com.example.zakazaka.ViewModels.LoginRegistrationViewModel

class CreateAccount : AppCompatActivity() {
    lateinit var loginViewModel: LoginRegistrationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_account)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.transMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val db = AppDatabase.getDatabase(this)
        loginViewModel = LoginRegistrationViewModel(UserRepository(userDao =db.userDao()))


        val btnCreateAcc = findViewById<Button>(R.id.btn_create_account)
        btnCreateAcc.setOnClickListener {
            var username = findViewById<EditText>(R.id.edUsername).text.toString()
            var firstName = findViewById<EditText>(R.id.edFirstname).text.toString()
            var lastName = findViewById<EditText>(R.id.edSurname).text.toString()
            var email = findViewById<EditText>(R.id.edEmail).text.toString()
            var password = findViewById<EditText>(R.id.edPassword).text.toString()
            var retypePassword = findViewById<EditText>(R.id.edRetypePassword).text.toString()

            if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || retypePassword.isEmpty()) {
                val toast = Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT)
                toast.show()
            }else if(password != retypePassword){
                val toast = Toast.makeText(this, "Passwords do not match, please try again", Toast.LENGTH_SHORT)
                toast.show()
            }else{
                //create account
                val user = UserEntity(
                    username = username,
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    password = password
                )
                val uId = loginViewModel.registerUser(user)
                uId.observe(this){id ->
                    if(id != null) {
                        val toast = Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT)
                        toast.show()
                        val intent = Intent(this, LoginActivity::class.java)//user will be directed to log in page
                        startActivity(intent)
                    }else{
                        val toast = Toast.makeText(this, "Account Creation Failed", Toast.LENGTH_SHORT)
                        toast.show()
                    }
                 }
            }

        }
    }
}