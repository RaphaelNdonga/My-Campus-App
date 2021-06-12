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
import androidx.navigation.fragment.navArgs
import com.google.firebase.firestore.CollectionReference
import com.mycampusapp.R
import com.mycampusapp.data.Links
import com.mycampusapp.databinding.LinksInputFragmentBinding
import com.mycampusapp.util.COURSE_ID
import com.mycampusapp.util.LINKS
import com.mycampusapp.util.isValidUrl
import com.mycampusapp.util.sharedPrefFile
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LinksInputFragment : Fragment() {

    private lateinit var viewModel: LinksInputViewModel
    private lateinit var binding: LinksInputFragmentBinding
    private lateinit var sharedPreferences: SharedPreferences

    private val linksArgs by navArgs<LinksInputFragmentArgs>()

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
            this,
            LinksInputViewModelFactory(
                courseCollection.document(courseId).collection(LINKS)
            )
        ).get(
            LinksInputViewModel::class.java
        )
        binding = LinksInputFragmentBinding.inflate(inflater, container, false)

        binding.linkEditText.setText(linksArgs.link?.link)
        binding.subjectEditText.setText(linksArgs.link?.subject)

        binding.saveButton.setOnClickListener {
            val subject = binding.subjectEditText.text.toString()
            val link = binding.linkEditText.text.toString()

            if (subject.isBlank()) {
                binding.subjectInput.error = requireActivity().getString(R.string.fill_blanks)
                return@setOnClickListener
            }
            binding.subjectInput.error = null

            if (link.isBlank()) {
                binding.linkInput.error = requireActivity().getString(R.string.fill_blanks)
                return@setOnClickListener
            }
            binding.linkInput.error = null

            if (!link.isValidUrl()) {
                binding.linkInput.error = requireActivity().getString(R.string.valid_url_request)
                return@setOnClickListener
            }
            binding.linkInput.error = null

            val essentialLink = Links(subject = subject, link = link)
            Timber.i("essential link is $essentialLink")
            viewModel.addData(essentialLink)
            findNavController().navigateUp()
        }
        return binding.root
    }
}