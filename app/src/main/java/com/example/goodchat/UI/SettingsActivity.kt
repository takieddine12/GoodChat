package com.example.goodchat.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.goodchat.R
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.log

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportFragmentManager.beginTransaction().replace(
            R.id.framelayout,
            PreferencesSettings()
        ).commit()
    }

    class PreferencesSettings : PreferenceFragmentCompat(){
        private lateinit var mAuth : FirebaseAuth
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            mAuth = FirebaseAuth.getInstance()
        }
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferencessettings,rootKey)


            var logoutPrefernece = preferenceManager.findPreference<Preference>("logout")
            logoutPrefernece!!.setOnPreferenceClickListener(object : Preference.OnPreferenceClickListener {
                override fun onPreferenceClick(preference: Preference?): Boolean {
                    mAuth.signOut()
                    startActivity(Intent(requireContext(),LoginActivity::class.java))
                    return true
                }
            })
        }
    }
}