package com.umn.story.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.umn.story.adapters.StoryAdapter
import com.umn.story.databinding.FragmentHomeBinding
import com.umn.story.models.Story
import com.umn.story.models.StoryUser
import com.umn.story.models.User
import com.umn.story.utils.SharedPrefUtils

class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding
    private lateinit var adapter: StoryAdapter
    private lateinit var db: FirebaseFirestore
    private val likeList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentHomeBinding.inflate(inflater,container,false)

        db = FirebaseFirestore.getInstance()

        val userRef = db.collection("users")
            .document(SharedPrefUtils.getNim(requireContext())!!)

        adapter = StoryAdapter(object :StoryAdapter.StoryAdapterListener{
            override fun onLike(story: StoryUser) {
                likeList.add(story.story.id)
                userRef.update("likes",
                    if(story.like) FieldValue.arrayUnion(story.story.id)
                    else FieldValue.arrayRemove(story.story.id)
                )
            }
        })

       userRef
            .get()
            .addOnSuccessListener { document ->
                if(document == null || !document.exists()){
                    Toast.makeText(requireContext(),"Terjadi kesalahan, silahkan coba lagi",Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                val user = document.toObject(User::class.java)
                if (user == null) {
                    Toast.makeText(requireContext(),"Terjadi kesalahan, silahkan coba lagi",Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                likeList.addAll(user.likes)

                fetchStories()
            }
            .addOnFailureListener { _ ->
                Toast.makeText(requireContext(),"Terjadi kesalahan, silahkan coba lagi",Toast.LENGTH_SHORT).show()
            }


        with(binding){
            rvStory.layoutManager = LinearLayoutManager(requireContext())
            rvStory.adapter = adapter
        }

        return binding.root
    }

    private fun isLiked(id:String):Boolean{
        for(like in likeList){
            if(like == id){
                return true
            }
        }
        return false
    }

    private fun fetchStories() {
        db.collection("stories")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(requireContext(), "Terjadi kesalahan, silahkan coba lagi", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                val stories = mutableListOf<StoryUser>()
                snapshots?.let {
                    for (document in it.documents) {
                        val id = document.getString("id")
                        val storyCerita = document.getString("cerita")
                        val storyFoto = document.getString("foto")
                        val storyCreatedAt = document.getDate("createdAt") // Retrieve as Date

                        val story = Story(
                            id = id ?: "",
                            cerita = storyCerita ?: "",
                            foto = storyFoto,
                            createdAt = storyCreatedAt
                        )

                        stories.add(StoryUser(story, isLiked(id?:"")))
                    }
                    val sortedStories = stories.sortedByDescending { it.like }
                    adapter.setList(sortedStories)
                }
            }
    }
}