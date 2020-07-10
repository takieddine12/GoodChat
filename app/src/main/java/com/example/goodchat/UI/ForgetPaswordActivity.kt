package com.example.goodchat.UI

import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.goodchat.ChatUtility
import com.example.goodchat.R
import com.example.goodchat.databinding.ActivityForgetPaswordBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class ForgetPaswordActivity : AppCompatActivity() {
    private lateinit var binding : ActivityForgetPaswordBinding
    private lateinit var mAuth  : FirebaseAuth
    private lateinit var counter : CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_forget_pasword)

        mAuth = FirebaseAuth.getInstance()
        binding.recoverbtn.setOnClickListener {
            recoverPassword()
        }
    }

    private fun recoverPassword(){
        var email = binding.forgotpasswordedit.text.toString()
        CoroutineScope(Dispatchers.IO).launch {
           try {
               mAuth.sendPasswordResetEmail(email).await()
           }catch ( e : Exception){

           }
        }
        counter = object : CountDownTimer(ChatUtility.COUNTER_TIMEINMILLIS,ChatUtility.COUNTER_INTERVAL){
            override fun onFinish() {
               binding.recoverbtn.isEnabled = true
                binding.textcounter.text = "00:00"
            }
            override fun onTick(millisUntilFinished: Long) {
                ChatUtility.COUNTER_TIMEINMILLIS = millisUntilFinished
                binding.recoverbtn.isEnabled = false
                val minutes = (ChatUtility.COUNTER_TIMEINMILLIS / 1000).toInt() / 60
                val seconds = (ChatUtility.COUNTER_TIMEINMILLIS / 1000).toInt() % 60
                val timeLeftFormatted: String = java.lang.String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                binding.textcounter.text = timeLeftFormatted


            }
        }.start()
    }
}