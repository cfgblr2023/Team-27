package com.example.team27

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import android.provider.OpenableColumns
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.ui.AppBarConfiguration
import com.example.team27.databinding.ActivityHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class HomeActivity : AppCompatActivity(){
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private lateinit var mMap: GoogleMap
    private lateinit var upload : Button
    private lateinit var recentre : Button
    private lateinit var map: MapView
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var currentPhotoPath: String
    private var latitude: Double=0.0
    private var longitude: Double=0.0

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationDao: LocationDao
    private lateinit var storageRef: StorageReference
    private val mStorage: StorageReference? = null

    private var mImageUri: Uri? = null


    private val cameraPermission = Manifest.permission.CAMERA
    private val cameraActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Image captured successfully
                val imageUri: Uri? = result.data?.data
                // Process the captured image URI
                uploadImageToFirebase(imageUri)
            }
        }


    private lateinit var start: Button
    private lateinit var hamburgerImageButton: ImageButton
    private lateinit var button_click: Animation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storageRef = FirebaseStorage.getInstance().reference
        setContentView(R.layout.content_home)
        upload = findViewById<Button>(R.id.uploadButton)

        val policy = StrictMode.ThreadPolicy.Builder()
            .permitAll().build()
        StrictMode.setThreadPolicy(policy)

        recentre = findViewById(R.id.recenter)
        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string

        //inflate and create the map
        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)

        val mapController = map.controller
        mapController.setZoom(9.5)
        val startPoint = GeoPoint(48.8583, 2.2944)
        mapController.setCenter(startPoint)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (checkLocationPermissions()) {
            getCurrentLocation()
        } else {
            requestLocationPermissions()
        }

        val mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), map)
        mLocationOverlay.enableMyLocation()
        map.overlays.add(mLocationOverlay)

//        val markerIcon = resources.getDrawable(R.drawable.ic_launcher_background)
//        markerIcon.setTint(Color.BLUE) // Replace with your desired color

        val marker1 = Marker(map)
        marker1.position = GeoPoint(latitude, longitude) // Marker coordinates
//        marker1.icon = markerIcon
        marker1.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(marker1)

        val marker = Marker(map)

        marker.position = GeoPoint(51.5074, -0.1278) // Marker coordinates
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(marker)
        map.invalidate()
        button_click = AnimationUtils.loadAnimation(this, R.anim.button_click)

        hamburgerImageButton = findViewById(R.id.hamburgerImageButton)

        hamburgerImageButton.setOnClickListener(View.OnClickListener {
            hamburgerImageButton.setAnimation(button_click)
            Handler().postDelayed({
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
                finish()
            }, 200)
        })


//        setSupportActionBar(binding.toolbar)
//
//        val navController = findNavController(R.id.nav_host_fragment_content_home)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
//
//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
        upload.setOnClickListener {

        val galleryIntent = Intent(Intent.ACTION_PICK)
        // here item is type of image
        galleryIntent.type = "image/*"
        // ActivityResultLauncher callback
        imagePickerActivityResult.launch(galleryIntent)



//            if (ContextCompat.checkSelfPermission(
//                    this,
//                    cameraPermission
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                openCamera()
//            } else {
//                // Request camera permission
//                requestPermissions(arrayOf(cameraPermission), CAMERA_PERMISSION_REQUEST_CODE)
//            }


        }

        recentre.setOnClickListener {
           getCurrentLocation()

            Log.e("LATITUDE",latitude.toString())
            val startPoint = GeoPoint(latitude, longitude)
            mapController.setCenter(startPoint)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Check for location permissions and request if not granted


    }

    private fun showAlertDialog() {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Congratulations!")
        alertDialogBuilder.setMessage("Yayyy! You have earned 100 points!")
        alertDialogBuilder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }



    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =

        registerForActivityResult( ActivityResultContracts.StartActivityForResult()) { result ->
            if (result != null) {
                // getting URI of selected Image
                val imageUri: Uri? = result.data?.data

                // val fileName = imageUri?.pathSegments?.last()

                // extract the file name with extension
                val sd = getFileName(applicationContext, imageUri!!)

                // Upload Task with upload to directory 'file'
                // and name of the file remains same
                val uploadTask = storageRef.child("file/$sd").putFile(imageUri)

                // On success, download the file URL and display it
                uploadTask.addOnSuccessListener {

                    showAlertDialog()
                    RewardsManager.rewardsValue += 100
                    // using glide library to display the image
//                    storageRef.child("upload/$sd").downloadUrl.addOnSuccessListener {
//                        Glide.with(this@MainActivity)
//                            .load(it)
//                            .into(imageview)
//
//                        Log.e("Firebase", "download passed")
//                    }.addOnFailureListener {
//                        Log.e("Firebase", "Failed in downloading")
//                    }
                }.addOnFailureListener {
                    Log.e("Firebase", "Image Upload fail")
                }
            }
        }

    private fun getFileName(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (displayNameIndex != -1) {
                        return it.getString(displayNameIndex)
                    }
                }
            }
        }
        return uri.path?.substringAfterLast('/')
    }


    public override fun onResume() {
        super.onResume()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume() //needed for compass, my location overlays, v6.0.0 and up
    }

    private fun checkLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_LOCATION_PERMISSIONS
        )
    }

    private fun getCurrentLocation() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Use the location coordinates
                    latitude = location?.latitude!!
                    longitude = location?.longitude!!

                    Log.e("LLLL 1",latitude.toString())
                    Log.e("LLLL 2",longitude.toString())

                    // Handle latitude and longitude values here
                }
                .addOnFailureListener { exception: Exception ->
                    // Handle failure to retrieve location
                }
        } catch (e: SecurityException) {
            // Handle security exception
        }
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSIONS = 1
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
        private const val CAMERA_REQUEST_CODE = 1
    }

    public override fun onPause() {
        super.onPause()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause()  //needed for compass, my location overlays, v6.0.0 and up
    }
    private fun dispatchTakePictureIntent() {

        val locationDatabase = Room.databaseBuilder(
            applicationContext,
            LocationDatabase::class.java, "location-database"
        ).allowMainThreadQueries().build()
        locationDao = locationDatabase.locationDao()

        val location = LocationEntity(latitude = latitude, longitude = longitude)
        var allLocations = listOf<LocationEntity>()
        lifecycleScope.launch {
            saveLocation(location)

            // Fetch all locations from the database
            allLocations = getAllLocations()
        }


        val marker = Marker(map)

        allLocations.forEach { location ->
            val marker1 = Marker(map)
            marker1.position = GeoPoint(location.latitude,location.longitude) // Marker coordinates
//        marker1.icon = markerIcon
            marker1.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            map.overlays.add(marker1)
            map.invalidate()
        }

//        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
//        map.overlays.add(marker)


        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {


                mImageUri = data!!.getData();
            }

            val intent = Intent(this, UploadActivity::class.java)

            startActivity(intent)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun pictures() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    ex.printStackTrace()
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        Log.e("STORAGE",storageDir.toString())
        val imageFile = File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir
        )
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = imageFile.absolutePath
        return imageFile
    }

    private fun saveLocation(location: LocationEntity) {
        Thread {
            locationDao.insertLocation(location)
        }.start()
    }

    private fun getAllLocations(): List<LocationEntity> {
        var locations = listOf<LocationEntity>()
        lifecycleScope.launch {
            locations = locationDao.getAllLocations()
        }
        return locations
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.isMyLocationEnabled = true
                }
            }
        }

        if (requestCode == REQUEST_LOCATION_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                // Handle permission denied case
            }
        }

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            // Camera permission denied
            // Handle the denial as per your requirement
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraActivityResult.launch(cameraIntent)
    }

    private fun uploadImageToFirebase(imageUri: Uri?) {
        if (imageUri != null) {
            // Upload the image to Firebase Storage
            val storageRef = FirebaseStorage.getInstance().reference
            val fileName = getFileName(applicationContext, imageUri)
            val uploadTask = storageRef.child("file/$fileName").putFile(imageUri)

            uploadTask.addOnSuccessListener {
                // Image upload successful
                // Handle the success event as per your requirement
            }.addOnFailureListener {
                // Image upload failed
                // Handle the failure event as per your requirement
            }
        }
    }





//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_home)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }
}

// Location entity class
@Entity(tableName = "location_table")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val latitude: Double,
    val longitude: Double
)

// Location DAO
@Dao
interface LocationDao {
    @Insert
    fun insertLocation(location: LocationEntity)

    @Query("SELECT * FROM location_table")
    fun getAllLocations(): List<LocationEntity>
}

// Room database class
@Database(entities = [LocationEntity::class], version = 1)
abstract class LocationDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
}
