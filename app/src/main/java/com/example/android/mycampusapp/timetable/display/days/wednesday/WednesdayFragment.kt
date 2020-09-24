package com.example.android.mycampusapp.timetable.display.days.wednesday

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.Selection
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.databinding.FragmentWednesdayBinding
import com.example.android.mycampusapp.timetable.data.WednesdayClass
import com.example.android.mycampusapp.timetable.display.MyItemKeyProvider
import com.example.android.mycampusapp.timetable.display.TimetableFragmentDirections
import com.example.android.mycampusapp.util.EventObserver
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class WednesdayFragment : Fragment() {
    private lateinit var snapshotListener: ListenerRegistration

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var auth: FirebaseAuth

    private val viewModel by viewModels<WednesdayViewModel> {
        WednesdayViewModelFactory(
            firestore
        )
    }
    private lateinit var tracker: SelectionTracker<Long>
    private lateinit var adapter: WednesdayAdapter
    private lateinit var recyclerView: RecyclerView
    private var highlightState: Boolean = false
    private var isAdmin: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentWednesdayBinding>(
            inflater,
            R.layout.fragment_wednesday,
            container,
            false
        )
        Timber.i("wednesday fragment created")

        val fab = binding.wednesdayFab
        setHasOptionsMenu(true)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        recyclerView = binding.wednesdayRecyclerView
        adapter =
            WednesdayAdapter(
                WednesdayListener {
                    if (isAdmin)
                        viewModel.displayWednesdayClassDetails(it)
                })
        recyclerView.adapter = adapter


        viewModel.addNewClass.observe(viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(
                    TimetableFragmentDirections.actionTimetableFragmentToWednesdayInputFragment()
                )
            })

        viewModel.openWednesdayClass.observe(viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(
                    TimetableFragmentDirections.actionTimetableFragmentToWednesdayInputFragment(it)
                )
            })
        val currentUser = auth.currentUser!!
        currentUser.getIdToken(false).addOnSuccessListener { result: GetTokenResult? ->
            val isModerator = result?.claims?.get("admin") as Boolean?
            if (isModerator != null) {
                isAdmin = isModerator
                fab.visibility = View.VISIBLE
            }
        }
        setupTracker()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val wednesdayFirestore = firestore.collection("wednesday")
        snapshotListener =
            wednesdayFirestore.addSnapshotListener { querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
                val mutableList: MutableList<WednesdayClass> = mutableListOf()
                querySnapshot?.documents?.forEach { document ->
                    val id = document.getString("id")
                    val subject = document.getString("subject")
                    val time = document.getString("time")
                    if (id != null && subject != null && time != null) {
                        val wednesdayClass = WednesdayClass(id, subject, time)
                        mutableList.add(wednesdayClass)
                    }
                }
                viewModel.updateData(mutableList)
                viewModel.checkWednesdayDataStatus()
            }
    }

    override fun onPause() {
        super.onPause()
        snapshotListener.remove()
    }

    private fun deleteSelectedItems(selection: Selection<Long>) {
        val list: List<WednesdayClass?> = selection.map {
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
        inflater.inflate(R.menu.toolbar_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.delete_all_classes)
        item.isEnabled = highlightState
        item.isVisible = highlightState
    }

    private fun setupTracker() {
        tracker = SelectionTracker.Builder(
            "wednesdaySelection",
            recyclerView,
            MyItemKeyProvider(
                recyclerView
            ),
            WednesdayItemDetailsLookup(
                recyclerView
            ),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything()).build()

        if (isAdmin) {
            tracker.addObserver(
                object : SelectionTracker.SelectionObserver<Long>() {
                    override fun onSelectionChanged() {
                        super.onSelectionChanged()
                        highlightState = true
                        val nItems: Int? = tracker.selection.size()
                        if (nItems != null)
                            viewModel.deleteWednesdayClasses.observe(viewLifecycleOwner,
                                EventObserver {
                                    deleteSelectedItems(tracker.selection)
                                })
                        if (nItems == 0) {
                            highlightState = false
                        }
                        requireActivity().invalidateOptionsMenu()
                    }

                })
        }
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