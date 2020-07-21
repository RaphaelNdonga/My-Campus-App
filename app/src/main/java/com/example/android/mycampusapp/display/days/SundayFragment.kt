package com.example.android.mycampusapp.display.days

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.databinding.FragmentSundayBinding

class SundayFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentSundayBinding>(
            inflater,
            R.layout.fragment_sunday,
            container,
            false
        )
        return binding.root
    }
}