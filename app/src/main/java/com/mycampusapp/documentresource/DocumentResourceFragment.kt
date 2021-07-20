package com.mycampusapp.documentresource

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mycampusapp.databinding.DocumentResourceFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class DocumentResourceFragment : Fragment() {

    private val viewModel: DocumentResourceViewModel by viewModels()
    private lateinit var binding: DocumentResourceFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DocumentResourceFragmentBinding.inflate(inflater, container, false)

        val getFileResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val fileUri = result.data?.data

                fileUri?.let {
                    val fileName = it.lastPathSegment.toString().substringAfter("/")
                    val docRef = viewModel.getDocumentsRef()
                        .child(fileName)
                    docRef.putFile(it).addOnSuccessListener {
                        docRef.downloadUrl.addOnSuccessListener { url ->
                            Timber.i("url is $url")
                        }
                    }
                }
            }
        binding.addDocument.setOnClickListener {
            val mimeTypes = arrayOf(
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                "application/vnd.ms-powerpoint",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                "text/plain",
                "application/pdf",
                "application/zip"
            )
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
            getFileResult.launch(intent)
        }
        return binding.root
    }
}