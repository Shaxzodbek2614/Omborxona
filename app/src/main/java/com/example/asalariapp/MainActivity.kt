package com.example.asalariapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.asalariapp.databinding.ActivityMainBinding
import com.example.asalariapp.models.Model
import com.example.asalariapp.utils.MyData
import com.example.asalariapp.utils.MySharedPreferences
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var navHostFragment: NavHostFragment
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("Products")
        val id = reference.push().key!!

        MyData.permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { check ->
            MyData.writePermissionGranted =
                check[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: MyData.writePermissionGranted
        }
        updateOrRequestPermission()

        navHostFragment = supportFragmentManager.findFragmentById(R.id.my_navigation_host) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        binding.scanner.setOnClickListener {
            checkPermissionCamera(this)
        }


    }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                showCamera()
            }
        }

    private val scanLauncher =
        registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
            run {
                if (result.contents == null) {
                    Toast.makeText(this, "Skanerlash bekor qilindi", Toast.LENGTH_SHORT).show()
                    MyData.isScanner.postValue(false)
                } else {
                    val gson = Gson()
                    val jsonString = result.contents
                    val mahsulot: Model = gson.fromJson(jsonString, Model::class.java)
                    MySharedPreferences.init(this)

                    val sharedList1 = MySharedPreferences.sharedList1
                    sharedList1.add(mahsulot)
                    Log.d(TAG, "skanner: ${result.contents}")
                    Toast.makeText(this, "${result.contents}", Toast.LENGTH_SHORT).show()
                    MySharedPreferences.sharedList1= sharedList1
                    Log.d(TAG, "onCreate: ${MySharedPreferences.sharedList1}")
                    MyData.isScanner.postValue(true)
                }
            }
        }
    
    private fun showCamera() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("QR kodni skanerlash")
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(false)

        scanLauncher.launch(options)
    }

    private fun checkPermissionCamera(context: Context) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showCamera()
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            Toast.makeText(context, "CAMERA permission required", Toast.LENGTH_SHORT).show()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    private fun updateOrRequestPermission() {
        val hasWritePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        MyData.writePermissionGranted = hasWritePermission || minSdk29

        val permissionToRequest = mutableListOf<String>()
        if (!MyData.writePermissionGranted) {
            permissionToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionToRequest.isNotEmpty()) {
            MyData.permissionLauncher.launch(permissionToRequest.toTypedArray())
        }
    }
}