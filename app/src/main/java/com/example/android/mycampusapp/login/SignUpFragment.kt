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

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var functions: FirebaseFunctions

    private val status: SignUpFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentSignUpBinding>(
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


        val submitBtn = binding.classRepSignedUpBtn
        submitBtn.setOnClickListener {
            val email: String? = viewModel.email.value
            val password = viewModel.password.value
            val courseId = viewModel.courseName.value
            val data = hashMapOf<String?, String?>(
                "email" to email,
                "courseId" to courseId
            )
            viewModel.createUser(data, password)
        }

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


}