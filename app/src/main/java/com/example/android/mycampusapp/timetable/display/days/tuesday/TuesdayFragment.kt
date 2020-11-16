package com.example.android.mycampusapp.timetable.display.days.tuesday

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
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
import com.example.android.mycampusapp.databinding.FragmentTuesdayBinding
import com.example.android.mycampusapp.timetable.data.TimetableClass
import com.example.android.mycampusapp.timetable.display.*
import com.example.android.mycampusapp.util.COURSE_ID
import com.example.android.mycampusapp.util.EventObserver
import com.example.android.mycampusapp.util.IS_ADMIN
import com.example.android.mycampusapp.util.sharedPrefFile
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TuesdayFragment : Fragment() {
    private lateinit var snapshotListener: ListenerRegistration

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var courseCollection: CollectionReference

    private lateinit var viewModel: TuesdayViewModel
    private lateinit var tracker: SelectionTracker<Long>
    private lateinit var adapter: TimetableAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var sharedPreferences: SharedPreferences
    private var highlightState: Boolean = false
    private var isAdmin: Boolean = false
    private lateinit var courseId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sharedPreferences =
            requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)

        isAdmin = sharedPreferences.getBoolean(IS_ADMIN, false)
        courseId = sharedPreferences.getString(COURSE_ID, "")!!


        val binding = DataBindingUtil.inflate<FragmentTuesdayBinding>(
            inflater,
            R.layout.fragment_tuesday,
            container,
            false
        )
        Timber.i("tuesday fragment created")

        val fab = binding.tuesdayFab
        if (isAdmin) {
            fab.visibility = View.VISIBLE
        }
        setHasOptionsMenu(true)
        val app = requireActivity().application
        viewModel = ViewModelProvider(
            this,
            TuesdayViewModelFactory(courseCollection.document(courseId), app)
        ).get(TuesdayViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        recyclerView = binding.tuesdayRecyclerView
        adapter =
            TimetableAdapter(
                TimetableListener {
                    if (isAdmin && !highlightState) {
                        viewModel.displayTuesdayClassDetails(it)
                    }
                })
        recyclerView.adapter = adapter


        binding.tuesdayRefreshLayout.setOnRefreshListener {
            snapshotListener.remove()
            viewModel.addSnapshotListener()
            binding.tuesdayRefreshLayout.isRefreshing = false
        }

        viewModel.addNewClass.observe(viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(
                    TimetableFragmentDirections.actionTimetableFragmentToTuesdayInputFragment()
                )
            })

        viewModel.hasPendingWrites.observe(viewLifecycleOwner, EventObserver { hasPendingWrites ->
            if (hasPendingWrites) {
                Snackbar.make(
                    this.requireView(),
                    R.string.admin_internet_request,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })

        viewModel.openTuesdayClass.observe(viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(
                    TimetableFragmentDirections.actionTimetableFragmentToTuesdayInputFragment(it)
                )
            })
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setupTracker()
        snapshotListener = viewModel.addSnapshotListener()
    }

    override fun onPause() {
        super.onPause()
        snapshotListener.remove()
    }

    private fun deleteSelectedItems(selection: Selection<Long>) {
        val list: List<TimetableClass?> = selection.map {
            adapter.currentList[it.toInt()]
        }.toList()
        viewModel.deleteList(list)
        tracker.selection.removeAll { true }
        highlightState = false
        requireActivity().invalidateOptionsMenu()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_all_classes -> {
                showAlertDialogBox()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.timetable_toolbar_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.delete_all_classes)
        item.isEnabled = highlightState && isAdmin
        item.isVisible = highlightState && isAdmin
    }

    private fun setupTracker() {
        tracker = SelectionTracker.Builder(
            "tuesdaySelection",
            recyclerView,
            MyItemKeyProvider(
                recyclerView
            ),
            TimetableItemDetailsLookup(
                recyclerView
            ),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything()).build()

        tracker.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    highlightState = true
                    val nItems: Int? = tracker.selection.size()
                    if (nItems != null)
                        viewModel.deleteTuesdayClasses.observe(viewLifecycleOwner,
                            EventObserver {
                                deleteSelectedItems(tracker.selection)
                            })
                    if (nItems == 0) {
                        highlightState = false
                    }
                    requireActivity().invalidateOptionsMenu()
                }

            })

        adapter.tracker = tracker
    }

    private fun showAlertDialogBox() {
        val builder = AlertDialog.Builder(requireActivity(), R.style.MyCampusApp_Dialog)
        builder.setTitle(getString(R.string.dialog_delete))
        builder.setMessage(getString(R.string.dialog_delete_confirm))

        builder.setPositiveButton(getString(R.string.dialog_positive)) { _, _ -> viewModel.deleteIconPressed() }
        builder.setNegativeButton(getString(R.string.dialog_negative)) { _, _ -> }

        builder.create().show()
    }
}