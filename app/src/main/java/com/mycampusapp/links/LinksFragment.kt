package com.mycampusapp.links

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.Selection
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.mycampusapp.R
import com.mycampusapp.data.DataStatus
import com.mycampusapp.databinding.LinksFragmentBinding
import com.mycampusapp.util.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LinksFragment : Fragment() {

    private lateinit var viewModel: LinksViewModel
    private lateinit var binding: LinksFragmentBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var snapshotListener: ListenerRegistration
    private lateinit var adapter: EssentialLinksAdapter
    private lateinit var tracker: SelectionTracker<Long>
    private var highlightState: Boolean = false
    private var isAdmin: Boolean = false

    @Inject
    lateinit var courseCollection: CollectionReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences =
            requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val courseId = sharedPreferences.getString(COURSE_ID, "")!!
        viewModel = ViewModelProvider(
            this, LinksViewModelFactory(
                courseCollection.document(courseId).collection(
                    LINKS
                )
            )
        ).get(LinksViewModel::class.java)
        binding = LinksFragmentBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        binding.linksRefresher.setOnRefreshListener {
            snapshotListener.remove()
            viewModel.addSnapshotListener()
            binding.linksRefresher.isRefreshing = false
        }

        isAdmin = sharedPreferences.getBoolean(IS_ADMIN, false)

        if (isAdmin) {
            binding.floatingActionButton.visibility = View.VISIBLE
            binding.floatingActionButton.setOnClickListener {
                findNavController().navigate(R.id.LinksInputFragment)
            }
        }
        adapter = EssentialLinksAdapter(EssentialLinksListener {
            if (isAdmin) {
                findNavController().navigate(
                    LinksFragmentDirections.actionLinksFragmentToLinksInputFragment(
                        it
                    )
                )
            }
        })
        viewModel.links.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
        viewModel.status.observe(viewLifecycleOwner, { dataStatus ->
            dataStatus?.let {
                when (it) {
                    DataStatus.EMPTY -> {
                        binding.chainImage.setImageResource(R.drawable.ic_chain)
                        binding.chainImage.visibility = View.VISIBLE
                        binding.noLinksTxt.visibility = View.VISIBLE

                    }
                    DataStatus.NOT_EMPTY -> {
                        binding.chainImage.visibility = View.GONE
                        binding.noLinksTxt.visibility = View.GONE
                    }
                    DataStatus.LOADING -> {
                        binding.chainImage.setImageResource(R.drawable.loading_animation)
                        binding.chainImage.visibility = View.VISIBLE
                        binding.noLinksTxt.visibility = View.GONE
                    }
                }
            }
        })
        binding.linksRecyclerView.adapter = adapter
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_all_classes -> {
                showAlertDialog()
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

    override fun onStart() {
        super.onStart()
        snapshotListener = viewModel.addSnapshotListener()
        tracker = SelectionTracker.Builder(
            "linksSelection",
            binding.linksRecyclerView,
            MyItemKeyProvider(
                binding.linksRecyclerView
            ),
            LinkItemDetailsLookup(binding.linksRecyclerView),
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

    override fun onStop() {
        super.onStop()
        snapshotListener.remove()
    }

    private fun showAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext(), R.style.MyCampusApp_Dialog)
        alertDialogBuilder.setPositiveButton(R.string.dialog_positive) { _, _ ->
            deleteSelectedItems(tracker.selection)
        }
        alertDialogBuilder.setNegativeButton(R.string.dialog_negative) { _, _ -> }
        alertDialogBuilder.setMessage(R.string.dialog_delete_confirm)
        alertDialogBuilder.setTitle(R.string.dialog_delete)
        alertDialogBuilder.create().show()
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
}