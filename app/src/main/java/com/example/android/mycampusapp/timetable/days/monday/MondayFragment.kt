package com.example.android.mycampusapp.timetable.days.monday

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android.mycampusapp.EventObserver
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource
import com.example.android.mycampusapp.databinding.FragmentMondayBinding
import com.example.android.mycampusapp.di.TimetableDatabase
import com.example.android.mycampusapp.timetable.TimetableFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MondayFragment : Fragment() {
    @TimetableDatabase
    @Inject
    lateinit var repository: TimetableDataSource

    private val viewModel by viewModels<MondayViewModel> {
        MondayViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentMondayBinding>(
            inflater,
            R.layout.fragment_monday,
            container,
            false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.mondayRecyclerView.adapter = MondayAdapter(MondayListener {
            viewModel.displayMondayClassDetails(it)
        })

        viewModel.navigateToSelectedClass.observe(viewLifecycleOwner, EventObserver {
                findNavController().navigate(TimetableFragmentDirections.actionTimetableFragmentToClassInputFragment())
        })

        return binding.root
    }
}