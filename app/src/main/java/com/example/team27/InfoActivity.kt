package com.example.team27

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration

class InfoActivity  : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var hamburgerImageButton: ImageButton
    private lateinit var button_click: Animation


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_info)

        button_click = AnimationUtils.loadAnimation(this, R.anim.button_click)

        hamburgerImageButton = findViewById<ImageButton>(R.id.hamburgerImageButton)

        hamburgerImageButton.setOnClickListener(View.OnClickListener {
            hamburgerImageButton.setAnimation(button_click)
            Handler().postDelayed({
                val intent = Intent(this@InfoActivity, MenuActivity::class.java)
                startActivity(intent)
                finish()
            },200)
        })
    }
}