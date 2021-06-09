package com.mycampusapp.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.mycampusapp.R
import com.mycampusapp.databinding.FragmentSignUpBinding
import com.mycampusapp.util.EventObserver
import com.mycampusapp.util.isValidEmail
import com.mycampusapp.util.isValidMessagingTopic
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    companion object {
        fun newInstance() = SignUpFragment()
    }

    private lateinit var viewModel: SignUpViewModel
    private lateinit var binding: FragmentSignUpBinding

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var functions: FirebaseFunctions

    private val status: SignUpFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_sign_up,
            container,
            false
        )
        binding.lifecycleOwner = this
        val studentStatus = status.studentStatus
        val application = requireActivity().application
        viewModel =
            ViewModelProvider(
                this,
                SignUpViewModelFactory(functions, auth, studentStatus, application)
            ).get(
                SignUpViewModel::class.java
            )
        binding.viewModel = viewModel

        val signUpHeadingText = "Sign up for ${status.studentStatus.name} account"
        binding.signUpHeading.text = signUpHeadingText

        var data: HashMap<String, String> = hashMapOf()
        var password = ""


        val submitBtn = binding.classRepSignedUpBtn
        submitBtn.setOnClickListener {
            val email: String? = viewModel.email.value
            viewModel.password.value?.let {
                password = it
            }
            val courseId = viewModel.courseId.value
            val confirmPassword = viewModel.confirmPassword.value

            if (email.isNullOrEmpty()) {
                binding.email.error = requireActivity().getString(R.string.fill_blanks)
                return@setOnClickListener
            }
            binding.email.error = null

            if (courseId.isNullOrEmpty()) {
                binding.courseId.error = requireActivity().getString(R.string.fill_blanks)
                return@setOnClickListener
            }
            binding.email.error = null


            if (password.isEmpty()) {
                binding.password.error = requireActivity().getString(R.string.fill_blanks)
                return@setOnClickListener
            }
            binding.password.error = null

            if (password != confirmPassword) {
                binding.confirmPassword.error =
                    requireActivity().getString(R.string.confirm_password_request)
                return@setOnClickListener
            }
            binding.confirmPassword.error = null

            if (courseId.isValidMessagingTopic()) {
                binding.courseId.error = requireActivity().getString(R.string.course_id_requirements)
                return@setOnClickListener
            }
            binding.courseId.error = null

            if (!email.isValidEmail()) {
                binding.email.error = requireActivity().getString(R.string.invalid_email)
                return@setOnClickListener
            }
            binding.email.error = null

            if (password.length < 8) {
                binding.password.error = requireActivity().getString(R.string.invalid_password)
                return@setOnClickListener
            }
            binding.password.error = null

            data = hashMapOf(
                "email" to email,
                "courseId" to courseId
            )
            viewModel.checkIfAdminExists(data)
            startLoading()
        }

        viewModel.finishLoading.observe(viewLifecycleOwner, EventObserver {
            stopLoading()
        })

        viewModel.adminExists.observe(viewLifecycleOwner, { adminExists ->
            if (adminExists) {
                when (status.studentStatus) {
                    StudentStatus.ADMIN -> {
                        Snackbar.make(
                            requireView(),
                            R.string.course_exists,
                            Snackbar.LENGTH_LONG
                        ).show()
                        stopLoading()
                    }
                    StudentStatus.REGULAR -> {
                        viewModel.createUser(data, password)
                    }
                    StudentStatus.UNDEFINED -> {
                        throw IllegalArgumentException("The student status should not be undefined")
                    }
                }
            } else {
                when (status.studentStatus) {
                    StudentStatus.ADMIN -> {
                        viewModel.createUser(data, password)
                    }
                    StudentStatus.REGULAR -> {
                        Snackbar.make(
                            requireView(),
                            R.string.course_non_existent,
                            Snackbar.LENGTH_LONG
                        ).show()
                        stopLoading()
                    }
                    StudentStatus.UNDEFINED -> {
                        throw IllegalArgumentException("The student status should not be undefined")
                    }
                }
            }
        })
        viewModel.navigator.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToLoginFragment())
        })
        viewModel.snackBarText.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
        })


        return binding.root
    }

    private fun startLoading() {
        binding.courseId.visibility = View.GONE
        binding.email.visibility = View.GONE
        binding.password.visibility = View.GONE
        binding.classRepSignedUpBtn.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.signUpTxt.visibility = View.VISIBLE
        binding.signUpHeading.visibility = View.GONE
        binding.confirmPassword.visibility = View.GONE
    }

    private fun stopLoading() {
        binding.courseId.visibility = View.VISIBLE
        binding.email.visibility = View.VISIBLE
        binding.password.visibility = View.VISIBLE
        binding.classRepSignedUpBtn.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.signUpTxt.visibility = View.GONE
        binding.signUpHeading.visibility = View.VISIBLE
        binding.confirmPassword.visibility = View.VISIBLE
    }

}