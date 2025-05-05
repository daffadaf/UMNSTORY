package com.umn.story.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.umn.story.R
import com.umn.story.databinding.ActivityLoginBinding
import com.umn.story.databinding.ActivityMainBinding
import com.umn.story.models.User
import com.umn.story.utils.LoadingDialog
import com.umn.story.utils.SharedPrefUtils

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        SharedPrefUtils.getNim(this)?.let {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        loadingDialog = LoadingDialog(this)

        db = FirebaseFirestore.getInstance()

        with(binding){
            tvRegister.setOnClickListener{
                startActivity(Intent(this@LoginActivity,RegisterActivity::class.java))
                finish()
            }
            btnLogin.setOnClickListener {
                loadingDialog.show()

                val nim = etNim.text.toString()
                val password = etPassword.text.toString()

                db.collection("users")
                    .document(nim)
                    .get()
                    .addOnSuccessListener { document ->
                        if(document == null || !document.exists()){
                            Toast.makeText(this@LoginActivity,"NIM atau Password salah!",Toast.LENGTH_SHORT).show()
                            loadingDialog.dismiss()
                            return@addOnSuccessListener
                        }
                        val user = document.toObject(User::class.java)
                        if (user == null) {
                            Toast.makeText(this@LoginActivity,"Terjadi kesalahan, silahkan coba lagi",Toast.LENGTH_SHORT).show()
                            loadingDialog.dismiss()
                            return@addOnSuccessListener
                        }
                        if(user.password != password) {
                            Toast.makeText(
                                this@LoginActivity,
                                "NIM atau Password salah!",
                                Toast.LENGTH_SHORT
                            ).show()
                            loadingDialog.dismiss()
                            return@addOnSuccessListener
                        }
                        loadingDialog.dismiss()
                        SharedPrefUtils.saveNim(nim, this@LoginActivity)
                        startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@LoginActivity,"Terjadi kesalahan, silahkan coba lagi",Toast.LENGTH_SHORT).show()
                        loadingDialog.dismiss()
                    }
            }
        }
    }
}