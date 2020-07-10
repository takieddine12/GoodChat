package com.example.goodchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.goodchat.UI.LoginActivity
import com.example.goodchat.databinding.ActivityMainBinding
import com.google.firebase.FirebaseAppLifecycleListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        setSupportActionBar(binding.activitytoolbar)

        var intent = intent
        intent?.let {
            binding.activitytoolbar.title = it.getStringExtra("user")
        }

        var mauth = FirebaseAuth.getInstance()
        binding.signout.setOnClickListener {
            mauth.signOut()
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }
}