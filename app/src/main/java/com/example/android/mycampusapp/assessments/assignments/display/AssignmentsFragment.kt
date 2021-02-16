package com.example.android.mycampusapp.assessments.assignments.display

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.Selection
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.assessments.AssessmentsFragmentDirections
import com.example.android.mycampusapp.assessments.AssessmentsViewModel
import com.example.android.mycampusapp.assessments.AssessmentsViewModelFactory
import com.example.android.mycampusapp.databinding.FragmentAssignmentsBinding
import com.example.android.mycampusapp.util.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AssignmentsFragment : Fragment() {
    @Inject
    lateinit var courseCollection: CollectionReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var courseId: String
    private lateinit var viewModel: AssessmentsViewModel
    private lateinit var snapshotListener: ListenerRegistration
    private lateinit var tracker: SelectionTracker<Long>
    private lateinit var recyclerView: RecyclerView
    private var isAdmin: Boolean = false
    private var highlightState: Boolean = false
    private lateinit var adapter: AssignmentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentAssignmentsBinding>(
            inflater,
            R.layout.fragment_assignments,
            container,
            false
        )
        sharedPreferences =
            requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        courseId = sharedPreferences.getString(COURSE_ID, "")!!
        isAdmin = sharedPreferences.getBoolean(IS_ADMIN, false)
        adapter = AssignmentsAdapter(AssignmentsListener {
            if (isAdmin && !highlightState)
                viewModel.displayDetails(it)
        })
        recyclerView = binding.assignmentsRecyclerView
        recyclerView.adapter = adapter
        viewModel = ViewModelProvider(
            this,
            AssessmentsViewModelFactory(
                courseCollection.document(courseId).collection("assignments")
            )
        ).get(AssessmentsViewModel::class.java)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        setHasOptionsMenu(true)

        binding.assignmentsRefreshLayout.setOnRefreshListener {
            snapshotListener.remove()
            snapshotListener = viewModel.addSnapshotListener()
            binding.assignmentsRefreshLayout.isRefreshing = false
        }

        viewModel.inputNavigator.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(AssessmentsFragmentDirections.actionAssessmentsFragmentToAssignmentInput())
            Timber.i("input navigator observer")
        })
        viewModel.openDetails.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(
                AssessmentsFragmentDirections.actionAssessmentsFragmentToAssignmentInput(it)
            )
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setupTracker()
        snapshotListener = viewModel.addSnapshotListener()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_all_classes -> {
                showDialogBox()
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.delete_all_classes)
        item.isEnabled = highlightState && isAdmin
        item.isVisible = highlightState && isAdmin
    }

    private fun showDialogBox() {
        val alertBuilder = AlertDialog.Builder(this.context, R.style.MyCampusApp_Dialog)
            .setTitle(R.string.dialog_delete)
            .setMessage(R.string.dialog_delete_confirm)
            .setPositiveButton(R.string.dialog_positive) { _, _ -> viewModel.deleteIconPressed() }
            .setNegativeButton(R.string.dialog_negative) { _, _ -> }
        alertBuilder.create().show()
    }

    private fun deleteSelectedItems(selection: Selection<Long>) {
        val list = selection.map {
            adapter.currentList[it.toInt()]
        }.toList()
        viewModel.deleteList(list)
        tracker.selection.removeAll { true }
        highlightState = false
        requireActivity().invalidateOptionsMenu()

    }

    private fun setupTracker() {
        tracker = SelectionTracker.Builder(
            "assignmentSelection",
            recyclerView,
            MyItemKeyProvider(recyclerView),
            AssignmentItemDetailsLookup(recyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything()).build()

        tracker.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    highlightState = true
                    val nItems: Int? = tracker.selection.size()
                    if (nItems != null) {
                        viewModel.deleteAssignments.observe(viewLifecycleOwner,
                            EventObserver {
                                deleteSelectedItems(tracker.selection)
                            })
                    }
                    if (nItems == 0) {
                        highlightState = false
                    }
                    requireActivity().invalidateOptionsMenu()
                }
            }
        )
        adapter.tracker = tracker
    }

    override fun onPause() {
        super.onPause()
        snapshotListener.remove()
    }

}