package com.umn.story.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.umn.story.databinding.FragmentPostStoryBinding
import com.umn.story.models.Story
import com.umn.story.utils.LoadingDialog

class PostStoryFragment : Fragment() {
    private lateinit var binding:FragmentPostStoryBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: StorageReference
    private var uriFoto: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentPostStoryBinding.inflate(inflater,container,false)
        loadingDialog = LoadingDialog(requireContext())

        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance().reference

        with(binding){
            btnImage.setOnClickListener{
                galleryLauncher.launch("image/*")
            }
            btnPost.setOnClickListener{
                if(etStory.text.isNotEmpty()){
                    tilStory.isErrorEnabled = false
                    loadingDialog.show()
                    val id = db.collection("stories").document().id
                    if(uriFoto!=null){
                        storage.child(id).putFile(uriFoto!!)
                            .addOnSuccessListener { taskSnapshot ->
                                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                                    postStory(id, uri.toString())
                                }
                            }
                            .addOnFailureListener{
                                Toast.makeText(requireContext(),"Terjadi kesalahan, coba lagi",Toast.LENGTH_SHORT).show()
                                loadingDialog.dismiss()
                            }
                    }else{
                        postStory(id)
                    }
                }else{
                    tilStory.error = "Mohon isi cerita anda"
                }

            }
        }
        return binding.root
    }

    private fun postStory(id:String, foto:String?=null){
        val story = Story(
            id = id,
            cerita = binding.etStory.text.toString(),
            foto = foto
        )
        val storyData = hashMapOf(
            "id" to story.id,
            "cerita" to story.cerita,
            "foto" to story.foto,
            "createdAt" to FieldValue.serverTimestamp() // Use server timestamp
        )
        db.collection("stories")
            .document(id)
            .set(storyData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(),"Post berhasil!",
                    Toast.LENGTH_SHORT).show()
                binding.etStory.setText(null)
                binding.ivStory.visibility = View.GONE
                uriFoto = null
                loadingDialog.dismiss()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(),"Terjadi kesalahan, silahkan coba lagi",
                    Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
            }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        try{
            it?.let {
                Glide.with(this)
                    .load(it)
                    .into(binding.ivStory)
                binding.ivStory.visibility = View.VISIBLE
                uriFoto = it
            }
        }catch(e:Exception){
            e.printStackTrace()
        }
    }
}