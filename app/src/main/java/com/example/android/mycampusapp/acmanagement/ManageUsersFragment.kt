package com.example.android.mycampusapp.acmanagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.android.mycampusapp.databinding.FragmentManageUsersBinding
import com.google.android.material.tabs.TabLayoutMediator

class ManageUsersFragment : Fragment() {
    private lateinit var binding: FragmentManageUsersBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageUsersBinding.inflate(layoutInflater, container, false)
        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        viewPager.adapter = ManageUsersAdapter(this)

        val users = listOf("regular", "admin")

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = users[position]
        }.attach()

        return binding.root
    }
}