package com.example.android.mycampusapp.timetable.display

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.databinding.FragmentTimetableBinding
import com.example.android.mycampusapp.util.TimePickerValues
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TimetableFragment : Fragment() {
    @Inject
    lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentTimetableBinding>(
            inflater,
            R.layout.fragment_timetable,
            container,
            false
        )
        val timetableAdapter =
            TimetableViewPagerAdapter(
                this
            )
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout
        viewPager.adapter = timetableAdapter

        val days =
            listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = days[position]
        }.attach()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser == null) {
            findNavController().navigate(TimetableFragmentDirections.actionTimetableFragmentToLoginFragment())
            return
        }
        currentUser.getIdToken(false).addOnSuccessListener { result: GetTokenResult? ->
            val isModerator: Boolean? = result?.claims?.get("admin") as Boolean?
            if (isModerator!=null) {
                Timber.i("This user is an admin")
            }else{
                Timber.i("This user is not an admin")
            }
            val courseId:String? = result?.claims?.get("courseId") as String?
            Timber.i("The course id is $courseId")
        }
        Timber.i("The current user is ${currentUser.email}")
        val isDateFormat = DateFormat.is24HourFormat(this.context)
        TimePickerValues.is24HourFormat.value = isDateFormat
        Timber.i("Is it 24 hour format? $isDateFormat")
    }

}