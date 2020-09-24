package com.example.android.mycampusapp.timetable.display.days.friday

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
import com.example.android.mycampusapp.databinding.FragmentFridayBinding
import com.example.android.mycampusapp.timetable.data.FridayClass
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
class FridayFragment : Fragment() {


    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var auth: FirebaseAuth

    private lateinit var snapshotListener: ListenerRegistration

    private lateinit var tracker: SelectionTracker<Long>
    private lateinit var adapter: FridayAdapter
    private lateinit var recyclerView: RecyclerView
    private var highlightState: Boolean = false
    private var isAdmin: Boolean = false
    private lateinit var viewModel: FridayViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentFridayBinding>(
            inflater,
            R.layout.fragment_friday,
            container,
            false
        )

        viewModel = ViewModelProvider(this, FridayViewModelFactory(firestore)).get(
            FridayViewModel::class.java
        )

        val fab = binding.fridayFab
        setHasOptionsMenu(true)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        recyclerView = binding.fridayRecyclerView
        adapter =
            FridayAdapter(
                FridayListener {
                    if (isAdmin) {
                        viewModel.displayFridayClassDetails(it)
                    }
                })
        recyclerView.adapter = adapter


        viewModel.addNewClass.observe(viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(
                    TimetableFragmentDirections.actionTimetableFragmentToFridayInputFragment()
                )
            })

        viewModel.openFridayClass.observe(viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(
                    TimetableFragmentDirections.actionTimetableFragmentToFridayInputFragment(it)
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

    private fun deleteSelectedItems(selection: Selection<Long>) {
        val list: List<FridayClass?> = selection.map {
            adapter.currentList[it.toInt()]
        }.toList()
        viewModel.deleteList(list)
        tracker.selection.removeAll { true }
        highlightState = false
        requireActivity().invalidateOptionsMenu()
    }

    override fun onStart() {
        super.onStart()
        val fridayFirestore = firestore.collection("friday")
        snapshotListener =
            fridayFirestore.addSnapshotListener { querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
                val mutableList: MutableList<FridayClass> = mutableListOf()
                querySnapshot?.documents?.forEach { document ->
                    Timber.i("We are in the loop")
                    val id = document.getString("id")
                    val subject = document.getString("subject")
                    val time = document.getString("time")
                    if (id != null && subject != null && time != null) {
                        val fridayClass = FridayClass(id, subject, time)
                        mutableList.add(fridayClass)
                    }
                }
                viewModel.updateData(mutableList)
                viewModel.checkFridayDataStatus()
            }
    }

    override fun onPause() {
        super.onPause()
        snapshotListener.remove()
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
            "fridaySelection",
            recyclerView,
            MyItemKeyProvider(
                recyclerView
            ),
            FridayItemDetailsLookup(
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
                            viewModel.deleteFridayClasses.observe(viewLifecycleOwner,
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