package com.example.android.mycampusapp.acmanagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.databinding.UsersFragmentBinding

class AdminsFragment : Fragment() {

    private lateinit var viewModel: AdminsViewModel
    private lateinit var binding: UsersFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = UsersFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(AdminsViewModel::class.java)
        return binding.root
    }
}