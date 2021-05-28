package com.mycampusapp.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.mycampusapp.R
import com.mycampusapp.databinding.SendFeedbackFragmentBinding
import com.mycampusapp.util.WORK_EMAIL

class SendFeedbackFragment : Fragment() {

    companion object {
        fun newInstance() = SendFeedbackFragment()
    }

    private lateinit var binding: SendFeedbackFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SendFeedbackFragmentBinding.inflate(inflater, container, false)

        binding.sendFeedbackBtn.setOnClickListener {
            val feedbackText = binding.feedbackEditText.text.toString()
            if (feedbackText.isBlank()) {
                Snackbar.make(requireView(), R.string.fill_blanks, Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val uri = Uri.parse("mailto:$WORK_EMAIL")
                .buildUpon()
                .appendQueryParameter("subject", "My Campus App Feedback")
                .appendQueryParameter("body", feedbackText)
                .appendQueryParameter("to", WORK_EMAIL)
                .build()

            val emailIntent = Intent(Intent.ACTION_SENDTO, uri)

            startActivity(Intent.createChooser(emailIntent, "Select app"))
        }

        return binding.root
    }
}