package com.example.android.mycampusapp.timetable.display.days.tuesday

import android.content.Context
import android.content.SharedPreferences
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
import com.example.android.mycampusapp.databinding.FragmentTuesdayBinding
import com.example.android.mycampusapp.timetable.data.TuesdayClass
import com.example.android.mycampusapp.timetable.display.MyItemKeyProvider
import com.example.android.mycampusapp.timetable.display.TimetableFragmentDirections
import com.example.android.mycampusapp.util.COURSE_ID
import com.example.android.mycampusapp.util.EventObserver
import com.example.android.mycampusapp.util.IS_ADMIN
import com.example.android.mycampusapp.util.sharedPrefFile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TuesdayFragment : Fragment() {
    private lateinit var snapshotListener: ListenerRegistration

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var courseCollection:CollectionReference

    private val viewModel by viewModels<TuesdayViewModel> {
        TuesdayViewModelFactory(
            courseCollection.document(courseId)
        )
    }
    private lateinit var tracker: SelectionTracker<Long>
    private lateinit var adapter: TuesdayAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var sharedPreferences: SharedPreferences
    private var highlightState: Boolean = false
    private var isAdmin: Boolean = false
    private lateinit var courseId:String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sharedPreferences =
            requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)

        isAdmin = sharedPreferences.getBoolean(IS_ADMIN,false)
        courseId = sharedPreferences.getString(COURSE_ID,"")!!


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
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        recyclerView = binding.tuesdayRecyclerView
        adapter =
            TuesdayAdapter(
                TuesdayListener {
                    if (isAdmin) {
                        viewModel.displayTuesdayClassDetails(it)
                    }
                })
        recyclerView.adapter = adapter


        viewModel.addNewClass.observe(viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(
                    TimetableFragmentDirections.actionTimetableFragmentToTuesdayInputFragment()
                )
            })

        viewModel.openTuesdayClass.observe(viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(
                    TimetableFragmentDirections.actionTimetableFragmentToTuesdayInputFragment(it)
                )
            })
        setupTracker()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val tuesdayFirestore = courseCollection.document(courseId).collection("tuesday")
        snapshotListener =
            tuesdayFirestore.addSnapshotListener { querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
                val mutableList: MutableList<TuesdayClass> = mutableListOf()
                querySnapshot?.documents?.forEach { document ->
                    val id = document.getString("id")
                    val subject = document.getString("subject")
                    val time = document.getString("time")
                    if (id != null && subject != null && time != null) {
                        val tuesdayClass = TuesdayClass(id, subject, time)
                        mutableList.add(tuesdayClass)
                    }
                }
                viewModel.updateData(mutableList)
                viewModel.checkTuesdayDataStatus()
            }
    }

    override fun onPause() {
        super.onPause()
        snapshotListener.remove()
    }

    private fun deleteSelectedItems(selection: Selection<Long>) {
        val list: List<TuesdayClass?> = selection.map {
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
            TuesdayItemDetailsLookup(
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