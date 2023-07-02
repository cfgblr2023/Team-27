package com.example.team27

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.team27.databinding.ActivityUploadBinding
import com.google.android.gms.auth.api.signin.GoogleSignInClient

import java.util.*

class UploadActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val REQUEST_CODE_SIGN_IN = 1

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivityUploadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_upload)

        val imageView: ImageView = findViewById(R.id.imageView)
        val imageBitmap = intent.getParcelableExtra<Bitmap>("imageBitmap")
        imageView.setImageBitmap(imageBitmap)

        // Create the GoogleSignInClient in onCreate or onCreateView
//        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestScopes(Drive.SCOPE_FILE)
//            .build()
//        googleSignInClient = GoogleSignIn.getClient(requireContext(), signInOptions)

// Request authorization from the user


    }
}



















































