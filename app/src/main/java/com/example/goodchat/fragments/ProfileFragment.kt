package com.example.goodchat.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.goodchat.R
import com.example.goodchat.databinding.ProfilelayoutBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.tasks.await
import okhttp3.Dispatcher
import timber.log.Timber


class ProfileFragment : Fragment() {

    private lateinit var storage: FirebaseStorage
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var binding = DataBindingUtil.inflate<ProfilelayoutBinding>(inflater, R.layout.profilelayout, container, false)

        mAuth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()


        populateEditTexts(binding)

        return binding.root

    }
    private fun populateEditTexts(binding : ProfilelayoutBinding){
        val userRef = firebaseDatabase.getReference("Users").child(mAuth.currentUser!!.uid)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(Error: DatabaseError) {
                Timber.d("Error ${Error.message}")
            }

            override fun onDataChange(snapShot: DataSnapshot) {

                var username = snapShot.child("username").value.toString()
                var email = snapShot.child("email").value.toString()
                var imgUrl = snapShot.child("imgUrl").value.toString()

                Picasso.get().load(imgUrl).fit().into(binding.profileimg)
                binding.profileemail.setText(email)
                binding.profileusername.setText(username)

                binding.profileusername.isEnabled = false
                binding.profileemail.isEnabled = false

            }
        })
    }

}