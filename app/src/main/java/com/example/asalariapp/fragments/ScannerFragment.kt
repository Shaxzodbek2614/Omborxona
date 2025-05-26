package com.example.asalariapp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.asalariapp.adapter.ScannerAdapter
import com.example.asalariapp.databinding.FragmentScannerBinding
import com.example.asalariapp.models.Model
import com.example.asalariapp.utils.MyData
import com.example.asalariapp.utils.MySharedPreferences
import com.google.firebase.database.*

private const val TAG = "ScannerFragment"
class ScannerFragment : Fragment() {
    private val binding by lazy { FragmentScannerBinding.inflate(layoutInflater) }

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    private lateinit var list: ArrayList<Model>
    private lateinit var scannerAdapter: ScannerAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: ")
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("mahsulotlar")

        list = ArrayList()

        binding.apply {
            MySharedPreferences.init(requireContext())

            if (MySharedPreferences.sharedList1.isNotEmpty()) {
                rv.visibility = View.VISIBLE
                card.visibility = View.VISIBLE
                empty.visibility = View.GONE
            } else {
                rv.visibility = View.GONE
                card.visibility = View.GONE
                empty.visibility = View.VISIBLE
            }

            scannerAdapter = ScannerAdapter(requireContext(), list)
            rv.adapter = scannerAdapter

            loadProducts()

            MyData.isScanner.observe(viewLifecycleOwner) {
                if (it) {
                    loadProducts()
                }
            }

            btnCancel.setOnClickListener {
                val sharedList1 = MySharedPreferences.sharedList1
                sharedList1.clear()
                MySharedPreferences.sharedList1 = sharedList1
                MyData.umumiyNarx.postValue(0)
                MyData.isScanner.postValue(false)
                findNavController().popBackStack()
            }

            btnNew.setOnClickListener {
                var sum = 0
                for (model in list) {
                    sum+=model.price!!*model.soni!!
                }
                MySharedPreferences.chiqim=sum
                updateProductCount()
                val sharedList1 = MySharedPreferences.sharedList1
                sharedList1.clear()
                MySharedPreferences.sharedList1 = sharedList1
                MyData.umumiyNarx.postValue(0)
                MyData.isScanner.postValue(false)
                findNavController().popBackStack()
            }
        }
        return binding.root
    }

    private fun loadProducts() {
        binding.apply {
            reference.addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n", "NotifyDataSetChanged", "FragmentLiveDataObserve")
                override fun onDataChange(snapshot: DataSnapshot) {

                    val children = snapshot.children
                    list.clear()

                    for (child in children) {
                        val product = child.getValue(Model::class.java)
                        Log.d(TAG, "onDataChange: $product")
                        Log.d(TAG, "shared: ${MySharedPreferences.sharedList1}")
                        for (model in MySharedPreferences.sharedList1) {
                            if (product != null && product.id == model.id) {
                                if (list.contains(product)) {
                                    val index = list.indexOf(product)
                                    list[index].soni = list[index].soni?.plus(1)
                                } else {
                                    product.soni = 1
                                    list.add(product)
                                }

                            }
                        }
                    }
                    MyData.umumiyNarx.observe(this@ScannerFragment) {
                        totalPrice.text = "Jami narx: $it so'm"
                    }
                    if (list.isNotEmpty()) {
                        rv.visibility = View.VISIBLE
                        card.visibility = View.VISIBLE
                        empty.visibility = View.GONE
                    } else {
                        rv.visibility = View.GONE
                        card.visibility = View.GONE
                        empty.visibility = View.VISIBLE
                    }
                    scannerAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun updateProductCount() {
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("mahsulotlar")
        for (product in list) {
            val productId = product.id ?: continue
            Log.d(TAG, "updateProductCount: productId = $productId")
            product.soni?.let { countToSubtract ->
                reference.child(productId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) {
                            Log.d(TAG, "No data found for productId: $productId")
                            return
                        }
                        val currentProduct = snapshot.getValue(Model::class.java)
                        Log.d(TAG, "onDataChange: currentProduct = $currentProduct")
                        if (currentProduct != null) {
                            val currentCount = currentProduct.soni ?: 0
                            val newCount = (currentCount - countToSubtract).coerceAtLeast(0)
                            val updatedProduct = Model(
                                id = currentProduct.id,
                                name = currentProduct.name,
                                price = currentProduct.price,
                                soni = newCount,
                                image = currentProduct.image
                            )
                            reference.child(productId).setValue(updatedProduct)
                        } else {
                            Log.d(TAG, "currentProduct is null for productId: $productId")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Xatolik: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }




}