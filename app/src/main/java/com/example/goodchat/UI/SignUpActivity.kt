package com.example.goodchat.UI

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.goodchat.R
import com.example.goodchat.databinding.ActivitySignUpBinding
import com.example.goodchat.models.UserDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class SignUpActivity : AppCompatActivity() {
    private lateinit var mAuth : FirebaseAuth
    private lateinit var binding : ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()

        binding.signupbtn.setOnClickListener {
            SignUpNewAccount()
        }

        haveAccount()
    }

    private fun SignUpNewAccount(){
        var email = binding.emailSignup.editText!!.text.toString()
        var password = binding.passwordSignup.editText!!.text.toString()
        var usrname = binding.usernameSignup.editText!!.text.toString()

        if(email.isEmpty() && password.isEmpty() && usrname.isEmpty()){
            return
        }
        if(password.length < 6 ){
            binding.passwordSignup.error = "Weak Password"
            return
        }else {
            password_signup.error = ""
            binding.signupprogress.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    delay(2000)
                    mAuth.createUserWithEmailAndPassword(email,password).await()

                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(mAuth.currentUser!!.uid).setValue(UserDetails(email,usrname,R.drawable.placerholder)).await()

                    withContext(Dispatchers.Main){
                        binding.signupprogress.visibility = View.INVISIBLE
                        Toast.makeText(this@SignUpActivity,"User Successfully Registered",Toast.LENGTH_SHORT).show()
                    }

                }catch (e : Exception){
                    withContext(Dispatchers.Main){
                        Timber.d("ErrorIS ${e.message}")
                        binding.signupprogress.visibility = View.INVISIBLE
                        Toast.makeText(this@SignUpActivity,"Error Creating Account..",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
    private fun haveAccount(){
        binding.haveaccount.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }
}