package com.example.android.mycampusapp.acmanagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.android.mycampusapp.databinding.FragmentManageUsersBinding

class ManageUsersFragment : Fragment() {
    private lateinit var binding: FragmentManageUsersBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageUsersBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}