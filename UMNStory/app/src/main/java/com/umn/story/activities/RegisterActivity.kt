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
import com.umn.story.databinding.ActivityMainBinding
import com.umn.story.databinding.ActivityRegisterBinding
import com.umn.story.models.User
import com.umn.story.utils.LoadingDialog
import com.umn.story.utils.SharedPrefUtils

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadingDialog = LoadingDialog(this)

        db = FirebaseFirestore.getInstance()

        with(binding){
            tvLogin.setOnClickListener{
                startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
                finish()
            }
            btnRegister.setOnClickListener{
                if(isValid()){
                    loadingDialog.show()
                    val user = User(
                        nim = etNim.text.toString(),
                        nama = etNama.text.toString(),
                        password = etPassword.text.toString()
                    )
                    db.collection("users")
                        .document(user.nim)
                        .get()
                        .addOnSuccessListener { document ->
                            if(document == null || !document.exists()){
                                db.collection("users")
                                    .document(user.nim)
                                    .set(user)
                                    .addOnSuccessListener {
                                        Toast.makeText(this@RegisterActivity,"Registrasi Berhasil!",
                                            Toast.LENGTH_SHORT).show()
                                        reset()
                                        loadingDialog.dismiss()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this@RegisterActivity,"Terjadi kesalahan, silahkan coba lagi",
                                            Toast.LENGTH_SHORT).show()
                                        loadingDialog.dismiss()
                                    }
                            }else{
                                Toast.makeText(this@RegisterActivity,"NIM pernah digunakan",
                                    Toast.LENGTH_SHORT).show()
                                loadingDialog.dismiss()
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this@RegisterActivity,"Terjadi kesalahan, silahkan coba lagi",
                                Toast.LENGTH_SHORT).show()
                            loadingDialog.dismiss()
                        }
                }
            }
        }
    }

    private fun isValid():Boolean{
        var valid = true
        with(binding){
            if(etNim.text.isEmpty()){
                tilNim.error = "Nama harus diisi"
                valid = false
            }else{
                tilNim.isErrorEnabled = false
            }

            if(etNama.text.isEmpty()){
                tilNama.error = "Nama harus diisi"
                valid = false
            }else{
                tilNama.isErrorEnabled = false
            }

            if(etPassword.text.isEmpty()){
                tilPassword.error = "Password harus diisi"
                valid = false
            }else{
                tilPassword.isErrorEnabled = false
            }

            if(etPasswordConfirmation.text.isEmpty()){
                tilPasswordConfirmation.error = "Konfirmasi Password harus diisi"
                valid = false
            }else if(etPasswordConfirmation.text.toString() != etPassword.text.toString()){
                tilPasswordConfirmation.error = "Konfirmasi Password tidak tepat"
                valid = false
            }else{
                tilPasswordConfirmation.isErrorEnabled = false
            }
        }
        return valid
    }

    private fun reset(){
        with(binding){
            etNim.setText(null)
            etNama.setText(null)
            etPassword.setText(null)
            etPasswordConfirmation.setText(null)
        }
    }
}