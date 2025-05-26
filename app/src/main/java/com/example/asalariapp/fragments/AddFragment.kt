package com.example.asalariapp.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.asalariapp.databinding.FragmentAddBinding
import com.example.asalariapp.models.Mahsulot
import com.example.asalariapp.models.Model
import com.example.asalariapp.utils.MySharedPreferences
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream


@Suppress("NAME_SHADOWING")
class AddFragment : Fragment() {
    private val binding by lazy { FragmentAddBinding.inflate(layoutInflater) }
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var referenceStorage: StorageReference
    lateinit var bitmap: Bitmap
    lateinit var reference: DatabaseReference
    private val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_WRITE_EXTERNAL_STORAGE)
        }
        MySharedPreferences.init(requireContext())
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("mahsulotlar")
        firebaseStorage = FirebaseStorage.getInstance()
        referenceStorage = firebaseStorage.getReference(
            "QRCodeImages"
        )
        val id = reference.push().key!!

        binding.apply {
            qrCodeBtn.setOnClickListener {
                if (name.text.isNotEmpty() && price.text.isNotEmpty() && soni.text.isNotEmpty()) {
                    saveBtn.isEnabled = true
                    val mahsulot = Mahsulot(id,name.text.toString(), price.text.toString().toInt(),soni.text.toString().toInt())
                    val gson = Gson()
                    val gsonString = gson.toJson(mahsulot)
                    generateQrCode(gsonString)
                }
            }
            saveBtn.setOnClickListener {
                saveBtn.isEnabled = false
                val m = System.currentTimeMillis()
                val uri = getImageUri(requireContext(), bitmap)
                val uploadTask = referenceStorage.child(m.toString()).putFile(uri)
                uploadTask.addOnSuccessListener { it ->
                    if (it.task.isSuccessful){
                        val downloadUrl = it.metadata?.reference?.downloadUrl
                        downloadUrl?.addOnSuccessListener {
                            var kirim = MySharedPreferences.kirim
                            kirim+=price.text.toString().toInt()*soni.text.toString().toInt()
                            MySharedPreferences.kirim =kirim
                            val model = Model(id,name.text.toString(), price.text.toString().toInt(),soni.text.toString().toInt(),it.toString())
                            reference.child(id).setValue(model)
                            Toast.makeText(requireContext(), "Saqlandi", Toast.LENGTH_SHORT).show()
                            name.text.clear()
                            price.text.clear()
                            soni.text.clear()
                            qrCode.setImageBitmap(null)
                            saveBtn.isEnabled = false


                        }
                    }
                }
            }
        }


        return binding.root
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Storage permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Storage permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun generateQrCode(text: String): Bitmap {
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
            }
        }
        binding.qrCode.setImageBitmap(bmp)
        bitmap = bmp
        return bmp
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }
}