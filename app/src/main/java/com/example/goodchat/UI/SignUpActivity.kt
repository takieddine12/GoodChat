package com.example.goodchat.UI

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Base64.URL_SAFE
import android.util.Base64.encodeToString
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.goodchat.ChatUtility
import com.example.goodchat.R
import com.example.goodchat.databinding.ActivitySignUpBinding
import com.example.goodchat.models.UserDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.util.*

class SignUpActivity : AppCompatActivity() {
    private lateinit var mAuth : FirebaseAuth
    private lateinit var binding : ActivitySignUpBinding
    lateinit var storageReference : FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance()

        haveAccount()

        binding.signupimg.setOnClickListener {
            var intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(intent,ChatUtility.IMAGE_REQUEST_CODE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ChatUtility.IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Picasso.get().load(data!!.data).fit().into(binding.signupimg)
            binding.signupbtn.setOnClickListener {
                SignUpNewAccount(data.data!!)
            }
        }
    }
    private fun getFileExtension(uri : Uri) : String? {
        var contentResolver  = contentResolver
        var mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))

    }
    private fun SignUpNewAccount(uri : Uri){

        val email = binding.emailSignup.editText!!.text.toString()
        val password = binding.passwordSignup.editText!!.text.toString()
        val usrname = binding.usernameSignup.editText!!.text.toString()

        if(email.isEmpty() && password.isEmpty() && usrname.isEmpty()){ return }
        if(password.length < 6){ binding.passwordSignup.error = "Weak Password"
            return
        } else { password_signup.error = ""
            binding.signupprogress.visibility = View.VISIBLE

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    delay(2000)
                    mAuth.createUserWithEmailAndPassword(email,password).await()

                    //save usr image
                    var ref = storageReference.reference.child("images/ ${mAuth.currentUser!!.uid}"+"."+ getFileExtension(uri))
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                           ref.putFile(uri).await()
                            var downloadUrl = ref.downloadUrl.await()
                            FirebaseDatabase.getInstance().getReference("Users")
                                .child(mAuth.currentUser!!.uid).setValue(UserDetails(email,usrname,downloadUrl.toString())
                                ,mAuth.currentUser!!.uid).await()

                        }catch (e : Exception){
                            Timber.d("Error Storage ${e.message}")
                        }
                    }

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