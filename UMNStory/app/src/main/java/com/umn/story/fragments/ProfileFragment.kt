package com.umn.story.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.umn.story.activities.LoginActivity
import com.umn.story.databinding.FragmentProfileBinding
import com.umn.story.models.User
import com.umn.story.utils.SharedPrefUtils

class ProfileFragment : Fragment() {
    private lateinit var binding:FragmentProfileBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentProfileBinding.inflate(inflater,container,false)

        db = FirebaseFirestore.getInstance()

        val userRef = db.collection("users")
            .document(SharedPrefUtils.getNim(requireContext())!!)

        userRef
            .get()
            .addOnSuccessListener { document ->
                if(document == null || !document.exists()){
                    Toast.makeText(requireContext(),"Terjadi kesalahan, silahkan coba lagi", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                val user = document.toObject(User::class.java)
                if (user == null) {
                    Toast.makeText(requireContext(),"Terjadi kesalahan, silahkan coba lagi", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                with(binding){
                    etNim.setText(user.nim)
                    etNama.setText(user.nama)
                }
            }
            .addOnFailureListener { _ ->
                Toast.makeText(requireContext(),"Terjadi kesalahan, silahkan coba lagi", Toast.LENGTH_SHORT).show()
            }

        with(binding){
            btnLogout.setOnClickListener{
                SharedPrefUtils.removeNim(requireContext())
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }

            btnUpdate.setOnClickListener{
                val updates = hashMapOf<String, Any>(
                    "nama" to etNama.text.toString()
                )
                if(etPassword.text.isNotEmpty()){
                    updates["password"] = etPassword.text.toString()
                }
                userRef.update(updates)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Mengubah profil berhasil!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Terjadi kesalahan, silahkan coba lagi", Toast.LENGTH_SHORT).show()
                    }
            }

        }
        return binding.root
    }

    private fun isValid():Boolean{
        var valid = true
        with(binding){
            if(etNama.text.isEmpty()){
                tilNama.error = "Nama harus diisi"
                valid = false
            }else{
                tilNama.isErrorEnabled = false
            }

            if(etPassword.text.isNotEmpty()){
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
        }
        return valid
    }
}