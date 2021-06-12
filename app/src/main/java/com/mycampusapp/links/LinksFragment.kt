package com.mycampusapp.links

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.mycampusapp.R
import com.mycampusapp.databinding.LinksFragmentBinding
import com.mycampusapp.util.COURSE_ID
import com.mycampusapp.util.IS_ADMIN
import com.mycampusapp.util.LINKS
import com.mycampusapp.util.sharedPrefFile
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LinksFragment : Fragment() {

    private lateinit var viewModel: LinksViewModel
    private lateinit var binding: LinksFragmentBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var snapshotListener:ListenerRegistration

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

        val isAdmin = sharedPreferences.getBoolean(IS_ADMIN, false)

        if (isAdmin) {
            binding.floatingActionButton.visibility = View.VISIBLE
            binding.floatingActionButton.setOnClickListener {
                findNavController().navigate(R.id.LinksInputFragment)
            }
        }
        val adapter = EssentialLinksAdapter(EssentialLinksListener {
            if (isAdmin) {
                findNavController().navigate(
                    LinksFragmentDirections.actionLinksFragmentToLinksInputFragment(
                        it
                    )
                )
            }
        })
        viewModel.links.observe(viewLifecycleOwner,{
            adapter.submitList(it)
        })
        binding.linksRecyclerView.adapter = adapter
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        snapshotListener = viewModel.addSnapshotListener()
    }

    override fun onStop() {
        super.onStop()
        snapshotListener.remove()
    }
}