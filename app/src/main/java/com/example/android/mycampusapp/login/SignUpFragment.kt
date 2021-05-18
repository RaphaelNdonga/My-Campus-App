package com.example.android.mycampusapp.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.databinding.FragmentSignUpBinding
import com.example.android.mycampusapp.util.EventObserver
import com.example.android.mycampusapp.util.isValidEmail
import com.example.android.mycampusapp.util.isValidMessagingTopic
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
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
            val courseId = viewModel.courseName.value
            val confirmPassword = viewModel.confirmPassword.value

            if (email.isNullOrEmpty() || courseId.isNullOrEmpty() || password.isEmpty()) {
                Snackbar.make(requireView(), R.string.fill_blanks, Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                Snackbar.make(
                    requireView(),
                    R.string.confirm_password_request,
                    Snackbar.LENGTH_LONG
                )
                    .show()
                return@setOnClickListener
            }
            if (courseId.isValidMessagingTopic()) {
                Snackbar.make(requireView(), R.string.course_id_requirements, Snackbar.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }
            if (!email.isValidEmail()) {
                Snackbar.make(requireView(), R.string.invalid_email, Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (password.length < 8) {
                Snackbar.make(requireView(), R.string.invalid_password, Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
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
        binding.classRepCourseName.visibility = View.GONE
        binding.classRepEmail.visibility = View.GONE
        binding.password.visibility = View.GONE
        binding.classRepSignedUpBtn.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.signUpTxt.visibility = View.VISIBLE
    }

    private fun stopLoading() {
        binding.classRepCourseName.visibility = View.VISIBLE
        binding.classRepEmail.visibility = View.VISIBLE
        binding.password.visibility = View.VISIBLE
        binding.classRepSignedUpBtn.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.signUpTxt.visibility = View.GONE
    }

}