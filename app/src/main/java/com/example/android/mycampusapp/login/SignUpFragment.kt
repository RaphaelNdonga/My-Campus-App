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
import com.example.android.mycampusapp.util.setupSnackbar
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
        viewModel =
            ViewModelProvider(this, SignUpViewModelFactory(functions, auth, studentStatus)).get(
                SignUpViewModel::class.java
            )
        binding.viewModel = viewModel

        var data: HashMap<String?, String?> = hashMapOf()
        var password: String? = null


        val submitBtn = binding.classRepSignedUpBtn
        submitBtn.setOnClickListener {
            val email: String? = viewModel.email.value
            password = viewModel.password.value
            val courseId = viewModel.courseName.value
            data = hashMapOf(
                "email" to email,
                "courseId" to courseId
            )
            viewModel.checkIfAdminExists(data)
            startLoading()
        }

        viewModel.adminExists.observe(viewLifecycleOwner, { adminExists ->
            if (adminExists) {
                when (status.studentStatus) {
                    StudentStatus.ADMIN -> {
                        Snackbar.make(
                            requireView(),
                            "This course already has an admin",
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
                            "This course does not exist",
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


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSnackBar()
    }

    private fun setupSnackBar() {
        view?.setupSnackbar(this, viewModel.snackBarText, Snackbar.LENGTH_SHORT)
    }

    private fun startLoading() {
        binding.classRepCourseName.visibility = View.GONE
        binding.classRepEmail.visibility = View.GONE
        binding.classRepPassword.visibility = View.GONE
        binding.classRepSignedUpBtn.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.signUpTxt.visibility = View.VISIBLE
    }

    private fun stopLoading() {
        binding.classRepCourseName.visibility = View.VISIBLE
        binding.classRepEmail.visibility = View.VISIBLE
        binding.classRepPassword.visibility = View.VISIBLE
        binding.classRepSignedUpBtn.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.signUpTxt.visibility = View.GONE
    }


}