package com.mycampusapp.timetable.display.days.wednesday

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.selection.Selection
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.functions.FirebaseFunctions
import com.mycampusapp.R
import com.mycampusapp.data.TimetableClass
import com.mycampusapp.databinding.FragmentTimetableDisplayBinding
import com.mycampusapp.timetable.display.*
import com.mycampusapp.util.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class WednesdayFragment : Fragment() {
    private lateinit var snapshotListener: ListenerRegistration

    @Inject
    lateinit var courseCollection: CollectionReference

    @Inject
    lateinit var functions: FirebaseFunctions

    private lateinit var viewModel: TimetableViewModel
    private lateinit var tracker: SelectionTracker<Long>
    private lateinit var adapter: TimetableAdapter
    private lateinit var recyclerView: RecyclerView
    private var highlightState: Boolean = false
    private var isAdmin: Boolean = false
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var courseId: String
    private val wednesday = DayOfWeek.WEDNESDAY

    private val wednesdayArgs by navArgs<WednesdayFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences =
            requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        courseId = sharedPreferences.getString(COURSE_ID, "")!!
        isAdmin = sharedPreferences.getBoolean(IS_ADMIN, false)
        val binding = DataBindingUtil.inflate<FragmentTimetableDisplayBinding>(
            inflater,
            R.layout.fragment_timetable_display,
            container,
            false
        )
        Timber.i("wednesday fragment created")

        /**
         * The try catch below has to be implemented because android does not support nullable
         * booleans in NavDirections.
         * Thanks to the viewpager, there are situations whereby the fragment has been instantiated
         * without the navArgs
         */
        val fragmentIsClickable = try {
            wednesdayArgs.isClickable
        } catch (ex: IllegalStateException) {
            true
        }

        val fab = binding.timetableFab
        if (isAdmin && fragmentIsClickable) {
            fab.visibility = View.VISIBLE
        }
        setHasOptionsMenu(true)
        val app = requireActivity().application
        val dayCollection = courseCollection.document(courseId).collection(wednesday.name)
        viewModel = ViewModelProvider(
            this,
            TimetableViewModelFactory(
                dayCollection,
                functions,
                wednesday,
                app
            )
        ).get(TimetableViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        recyclerView = binding.timetableRecyclerView
        adapter =
            TimetableAdapter(isAdmin, TimetableListener {
                if (isAdmin && !highlightState && fragmentIsClickable)
                    viewModel.displayFridayClassDetails(it)
            },
                OverflowListener { timetableClass: TimetableClass, button: View ->
                    val popupMenu = PopupMenu(requireContext(), button)
                    val popupInflater = popupMenu.menuInflater
                    popupInflater.inflate(R.menu.timetable_class_menu, popupMenu.menu)
                    val menuItem = popupMenu.menu.findItem(R.id.skip_switch)
                    if (timetableClass.isActive) {
                        menuItem.title = "Skip next"
                    } else {
                        menuItem.title = "Undo skip next"
                    }
                    popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                        return@OnMenuItemClickListener when (it.itemId) {
                            R.id.skip_switch -> {
                                if (timetableClass.isActive) {
                                    Timber.i("is active. Deactivating...")
                                    dayCollection.document(timetableClass.id)
                                        .update("active", false)
                                    viewModel.cancelData(
                                        timetableClass,
                                        wednesday,
                                        courseId
                                    )
                                } else {
                                    Timber.i("is inactive. Activating...")
                                    dayCollection.document(timetableClass.id)
                                        .update("active", true)
                                    viewModel.updateData(timetableClass, wednesday, courseId)
                                }
                                true
                            }
                            else -> true
                        }
                    })
                    popupMenu.show()
                })
        viewModel.timetableClasses.observe(viewLifecycleOwner, {
            Timber.i("$it")
            adapter.submitList(it)
        })
        recyclerView.adapter = adapter

        binding.timetableRefreshLayout.setOnRefreshListener {
            snapshotListener.remove()
            viewModel.addSnapshotListener()
            binding.timetableRefreshLayout.isRefreshing = false
        }

        viewModel.addNewClass.observe(viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(
                    TimetableFragmentDirections.actionTimetableFragmentToWednesdayInputFragment()
                )
            })

        viewModel.openFridayClass.observe(viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(
                    TimetableFragmentDirections.actionTimetableFragmentToWednesdayInputFragment(it)
                )
            })
        viewModel.deleteFridayClasses.observe(viewLifecycleOwner,
            EventObserver {
                deleteSelectedItems(tracker.selection)
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
        inflater.inflate(R.menu.main_toolbar_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.delete_all_classes)
        item.isEnabled = highlightState && isAdmin
        item.isVisible = highlightState && isAdmin
    }

    private fun setupTracker() {
        tracker = SelectionTracker.Builder(
            "wednesdaySelection",
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
                    val nItems: Int? = tracker.selection.size()
                    highlightState = nItems != null && nItems > 0
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