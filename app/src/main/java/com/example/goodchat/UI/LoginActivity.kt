package com.example.goodchat.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.goodchat.MainActivity
import com.example.goodchat.R
import com.example.goodchat.databinding.ActivityLoginBinding
import com.example.goodchat.models.UserDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        binding.signinbtn.setOnClickListener {
            UserSignIn()
        }

        NoAccount()
        forgotPassword()
    }

    private fun UserSignIn(){
        var email = binding.emailSignin.editText!!.text.toString()
        var password = binding.passwordSignin.editText!!.text.toString()

        if(email.isEmpty() && password.isEmpty()){
            return
        }
        if(password.length < 6 ){
            binding.passwordSignin.error = "Weak Password.."
        }else {
            password_signin.error = null
            binding.signinprogress.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    delay(2000)
                    mAuth.signInWithEmailAndPassword(email,password).await()
                    var database = FirebaseDatabase.getInstance()
                    database.getReference("Users").addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                            Timber.d("Error is " + p0.message)
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            for(snapshot in p0.children){
                                var userDetails = snapshot.getValue(UserDetails::class.java)
                                binding.signinprogress.visibility = View.INVISIBLE
                                Toast.makeText(this@LoginActivity,"User Successfully Logged", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@LoginActivity,MainActivity::class.java)
                                    .putExtra("user",userDetails!!.username))
                            }

                        }
                    })

                }catch (e : Exception){
                    withContext(Dispatchers.Main){
                        binding.signinprogress.visibility = View.INVISIBLE
                        Toast.makeText(this@LoginActivity,"No Such Account Found..",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        }
    private fun NoAccount(){
        binding.noaccounttext.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }
    }
    private fun forgotPassword(){
     binding.forgotpassword.setOnClickListener {
         startActivity(Intent(this,ForgetPaswordActivity::class.java))
     }
    }

    override fun onStart() {
        super.onStart()
        var currentUser = mAuth.currentUser
        if(currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
        }
    }



}