package com.example.asalariapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.asalariapp.R
import com.example.asalariapp.databinding.FragmentHisobotBinding
import com.example.asalariapp.utils.MySharedPreferences

class HisobotFragment : Fragment() {

    private val binding by lazy { FragmentHisobotBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        MySharedPreferences.init(requireContext())
        binding.kirim.text = MySharedPreferences.kirim.toString()
        binding.chiqim.text = MySharedPreferences.chiqim.toString()
        return binding.root
    }


}