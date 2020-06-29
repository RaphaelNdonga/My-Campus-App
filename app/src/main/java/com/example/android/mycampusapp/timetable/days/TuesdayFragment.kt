package com.example.android.mycampusapp.timetable.days

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.databinding.FragmentTuesdayBinding

class TuesdayFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentTuesdayBinding>(
            inflater,
            R.layout.fragment_tuesday,
            container,
            false
        )
        return binding.root
    }
}