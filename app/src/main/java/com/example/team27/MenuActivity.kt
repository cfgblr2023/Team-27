package com.example.team27

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.team27.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMenuBinding
    private lateinit var cross:ImageButton
    private lateinit var home:TextView
    private lateinit var info:TextView
    private lateinit var profile:TextView
    private lateinit var logout:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)
        cross=findViewById(R.id.crossButton)

        home=findViewById(R.id.home)
        info=findViewById(R.id.about)
        profile=findViewById(R.id.profile)
        logout=findViewById(R.id.signout)


        cross.setOnClickListener {
            val intent=Intent(this@MenuActivity,HomeActivity::class.java)
            startActivity(intent)
        }

        home.setOnClickListener {
            val intent=Intent(this@MenuActivity,HomeActivity::class.java)
            startActivity(intent)
        }
        info.setOnClickListener {
            val intent=Intent(this@MenuActivity,InfoActivity::class.java)
            startActivity(intent)
        }
        profile.setOnClickListener {
            val intent=Intent(this@MenuActivity,ProfileActivity::class.java)
            startActivity(intent)
        }
       logout.setOnClickListener {
            val intent=Intent(this@MenuActivity,HomeActivity::class.java)
            startActivity(intent)
        }


    }


}