package com.umn.story.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.umn.story.R
import com.umn.story.databinding.ActivityMainBinding
import com.umn.story.fragments.HomeFragment
import com.umn.story.fragments.PostStoryFragment
import com.umn.story.fragments.ProfileFragment

class       MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container, PostStoryFragment()
            ).commit()
        }

        with(binding){
            bottomNavigation.setOnItemSelectedListener { item ->
                val selectedFragment: Fragment = when (item.itemId) {
                    R.id.nav_post_story -> PostStoryFragment()
                    R.id.nav_home -> HomeFragment()
                    R.id.nav_profile -> ProfileFragment()
                    else -> PostStoryFragment()
                }
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container, selectedFragment
                ).commit()
                true
            }
        }
    }
}