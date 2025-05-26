    package com.example.asalariapp.fragments

    import android.content.Context
    import android.graphics.Bitmap
    import android.graphics.Color
    import android.graphics.drawable.ColorDrawable
    import android.os.Bundle
    import android.os.Environment
    import android.util.Log
    import androidx.fragment.app.Fragment
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Toast
    import androidx.appcompat.app.AlertDialog
    import androidx.core.graphics.drawable.toBitmap
    import androidx.navigation.fragment.findNavController
    import com.example.asalariapp.R
    import com.example.asalariapp.adapter.MahsulotAdapter
    import com.example.asalariapp.databinding.CustomDialogBinding
    import com.example.asalariapp.databinding.FragmentHomeBinding
    import com.example.asalariapp.models.Model
    import com.example.asalariapp.utils.MySharedPreferences
    import com.google.firebase.database.DataSnapshot
    import com.google.firebase.database.DatabaseError
    import com.google.firebase.database.FirebaseDatabase
    import com.google.firebase.database.ValueEventListener
    import com.squareup.picasso.Picasso
    import java.io.File
    import java.io.FileOutputStream

    private const val TAG = "HomeFragment"
    class HomeFragment : Fragment() {
        lateinit var mahsulotAdapter: MahsulotAdapter
        lateinit var list : ArrayList<Model>
        lateinit var firebaseDatabase: FirebaseDatabase
        lateinit var reference: com.google.firebase.database.DatabaseReference
        private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            Log.d(TAG, "onCreateView: ")
            MySharedPreferences.init(requireContext())

            firebaseDatabase = FirebaseDatabase.getInstance()
            reference = firebaseDatabase.getReference("users")

            return binding.root
        }

        override fun onResume() {
            super.onResume()
            Log.d(TAG, "onResume: ")
            firebaseDatabase = FirebaseDatabase.getInstance()
            reference = firebaseDatabase.getReference("mahsulotlar")
            reference.addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    list = ArrayList()
                    val children = snapshot.children
                    for (child in children){
                        val model = child.getValue(Model::class.java)
                        Log.d(TAG, "HomeFragment: $model")
                        if (model!!.soni!=0) {
                            list.add(model)
                        }
                    }
                    mahsulotAdapter = MahsulotAdapter(object :MahsulotAdapter.RvAction{
                        override fun itemClick(model: Model) {
                            val dialog = AlertDialog.Builder(requireContext()).create()
                            val customDialogBinding = CustomDialogBinding.inflate(layoutInflater)
                            customDialogBinding.name.text = model.name
                            customDialogBinding.price.text = model.price.toString()
                            Picasso.get().load(model.image).into(customDialogBinding.qrCode)
                            customDialogBinding.downloadBtn.setOnClickListener {
                                val bitmap = customDialogBinding.qrCode.drawable.toBitmap()
                                saveImageToGallery(bitmap)
                                dialog.dismiss()
                            }
                            dialog.setView(customDialogBinding.root)
                            dialog.show()
                            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                        }
                    },list)
                    binding.rv.adapter = mahsulotAdapter
                }


                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        }
        private fun saveImageToGallery(bitmap: Bitmap) {
            val filename = "image_${System.currentTimeMillis()}.jpg"
            val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val file = File(directory, filename)

            try {
                val outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()
                Toast.makeText(requireContext(), "Rasm galereyaga saqlandi", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Rasmni saqlashda xatolik yuz berdi", Toast.LENGTH_SHORT).show()
            }
        }




    }