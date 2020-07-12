package com.example.goodchat

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ActionProvider
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import com.example.goodchat.UI.SettingsActivity
import com.example.goodchat.databinding.ActivityMainBinding
import com.example.goodchat.models.UserDetails
import com.example.goodchat.viewPager.viewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var friendsRequestsDatabase : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        setSupportActionBar(binding.activitytoolbar)

        mAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        friendsRequestsDatabase = FirebaseDatabase.getInstance().getReference().child("FriendsRequest")

        SetUpViewPager()



    }

    private fun SetUpViewPager(){
        var viewPagerAdapter = viewPagerAdapter(this)
        binding.viewpager.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tablayout,binding.viewpager){ tab, position ->
            when(position){
                0 -> tab.text   = "History"
                1 -> tab.text  = "Friends"
                2 -> tab.text = "Posts"
                3 -> tab.text = "Profile"
            }
        }.attach()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        var menuItem = menu!!.findItem(R.id.search)
        var searchView  = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                var view = LayoutInflater.from(this@MainActivity).inflate(R.layout.searchfriends,null,false)
                var dialog = Dialog(this@MainActivity)
                dialog.setContentView(view)
                dialog.window!!.setLayout(600,1000)
                dialog.show()

                val userName = view.findViewById<TextView>(R.id.searchfriendtext)
                val button = view.findViewById<Button>(R.id.searchfriendsbtn)
                val userImg = view.findViewById<CircleImageView>(R.id.searchfriendsimg)

                firebaseDatabase.getReference("Users").addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(Error : DatabaseError) {
                        Timber.d("Error ${Error.message}")
                    }

                    override fun onDataChange(snapShot : DataSnapshot) {
                        for(snapshot in snapShot.children){
                            var userDetails = snapshot.getValue(UserDetails::class.java)
                            if(userDetails!!.username!!.toLowerCase().contains(query!!.toLowerCase())){
                                userName.text = userDetails.username
                                Picasso.get().load(userDetails.imgUrl).fit().into(userImg)
                                button.setOnClickListener {
                                 CoroutineScope(Dispatchers.IO).launch {
                                     try {

                                         //Invitation Sent
                                         friendsRequestsDatabase.child(mAuth.currentUser!!.uid)
                                             .child(userDetails.userUidd!!).setValue("Sent.").await()

                                         //Invitation Received
                                         friendsRequestsDatabase.child(userDetails.userUidd!!)
                                             .child(mAuth.currentUser!!.uid).setValue("Received.").await()

                                         withContext(Dispatchers.Main){
                                             button.isEnabled = false
                                             button.text = "Added"
                                         }

                                     }catch (e : Exception){
                                         Timber.d("Error ${e.message}")
                                     }
                                 }
                                }
                            }
                        }
                    }
                })
               return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                return false
            }
        })
        return true

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.settings ->{
            startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        return true
    }

}
