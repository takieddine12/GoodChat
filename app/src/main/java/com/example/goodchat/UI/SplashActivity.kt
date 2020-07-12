package com.example.goodchat.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.databinding.DataBindingUtil
import com.example.goodchat.ChatUtility
import com.example.goodchat.R
import com.example.goodchat.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = DataBindingUtil.setContentView<ActivitySplashBinding>(this,R.layout.activity_splash)

        binding.shimmer.startShimmer()

        Handler().postDelayed(Runnable {
            binding.shimmer.stopShimmer()
            var intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        },ChatUtility.SHIMMER_TIME)
    }
}