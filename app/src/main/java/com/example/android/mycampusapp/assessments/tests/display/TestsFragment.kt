package com.example.android.mycampusapp.assessments.tests.display

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.selection.Selection
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.assessments.*
import com.example.android.mycampusapp.databinding.FragmentTestBinding
import com.example.android.mycampusapp.util.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TestsFragment : Fragment() {

    private lateinit var viewModel: AssessmentsViewModel
    private lateinit var binding: FragmentTestBinding
    private lateinit var snapshotListener: ListenerRegistration
    private lateinit var tracker: SelectionTracker<Long>
    private lateinit var adapter: AssessmentsAdapter
    private var isAdmin: Boolean = false
    private var highlightState: Boolean = false

    @Inject
    lateinit var courseCollection: CollectionReference
    private lateinit var sharedPreferences: SharedPreferences

    private val testArgs by navArgs<TestsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        sharedPreferences =
            requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val courseId = sharedPreferences.getString(COURSE_ID, "")!!
        isAdmin = sharedPreferences.getBoolean(IS_ADMIN, false)
        val testsCollection =
            courseCollection.document(courseId).collection(AssessmentType.TEST.name)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_test, container, false)

        viewModel = ViewModelProvider(
            this,
            AssessmentsViewModelFactory(testsCollection)
        ).get(AssessmentsViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val fragmentIsClickable = try {
            testArgs.isClickable
        } catch (ex: IllegalStateException) {
            true
        }

        adapter = AssessmentsAdapter(AssessmentsListener {
            if (isAdmin && !highlightState && fragmentIsClickable)
                viewModel.displayDetails(it)
        })
        binding.testsRecyclerView.adapter = adapter

        if (isAdmin && fragmentIsClickable) {
            binding.testsFab.visibility = View.VISIBLE
        }

        binding.testsRefresher.setOnRefreshListener {
            snapshotListener.remove()
            snapshotListener = viewModel.addSnapshotListener()
            binding.testsRefresher.isRefreshing = false
        }

        viewModel.inputNavigator.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(AssessmentsFragmentDirections.actionAssessmentsFragmentToTestsInputFragment())
            Timber.i("Navigating to input")
        })
        viewModel.openDetails.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(
                AssessmentsFragmentDirections.actionAssessmentsFragmentToTestsInputFragment(it)
            )
        })
        viewModel.deleteAssignments.observe(viewLifecycleOwner, EventObserver {
            deleteSelectedItems(tracker.selection)
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
                showDeleteDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDeleteDialog() {
        val alertDialog = AlertDialog.Builder(this.context, R.style.MyCampusApp_Dialog)
            .setTitle(R.string.dialog_delete)
            .setMessage(R.string.dialog_delete_confirm)
            .setPositiveButton(R.string.dialog_positive) { _, _ -> viewModel.deleteIconPressed() }
            .setNegativeButton(R.string.dialog_negative) { _, _ -> }
        alertDialog.create().show()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.delete_all_classes)
        item.isVisible = highlightState && isAdmin
        item.isEnabled = highlightState && isAdmin
    }

    private fun setupTracker() {
        tracker = SelectionTracker.Builder(
            "testSelection",
            binding.testsRecyclerView,
            MyItemKeyProvider(binding.testsRecyclerView),
            AssessmentItemDetailsLookup(binding.testsRecyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything()).build()

        tracker.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                highlightState = tracker.selection.size() > 0
                requireActivity().invalidateOptionsMenu()
            }
        })
        adapter.tracker = tracker
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

    override fun onPause() {
        super.onPause()
        snapshotListener.remove()
    }
}