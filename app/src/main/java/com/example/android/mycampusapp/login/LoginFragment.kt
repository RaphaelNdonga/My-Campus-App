package com.example.android.mycampusapp.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.mycampusapp.MainActivity
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.databinding.FragmentLoginBinding
import com.example.android.mycampusapp.util.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding

    companion object {
        fun newInstance(): LoginFragment = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel
    private lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences =
            requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login,
            container,
            false
        )
        checkCurrentUser()

        viewModel = ViewModelProvider(this, LoginViewModelFactory(auth, sharedPreferences)).get(
            LoginViewModel::class.java
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val nextBtn = binding.loginNextButton

        viewModel.adminNavigator.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToSignUpFragment(StudentStatus.ADMIN)
            )
        })

        viewModel.regularNavigator.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToSignUpFragment(StudentStatus.REGULAR)
            )
        })

        viewModel.mainNavigator.observe(viewLifecycleOwner, EventObserver {
            val mainIntent = Intent(this.context, MainActivity::class.java)
            startActivity(mainIntent)
            requireActivity().finish()
        })

        viewModel.loadFinish.observe(viewLifecycleOwner, EventObserver {
            finishLoading()
        })

        viewModel.loadStart.observe(viewLifecycleOwner, EventObserver {
            startLoading()
        })

        viewModel.snackBarText.observe(viewLifecycleOwner, EventObserver {
            if (it != null) {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
            }
        })

        nextBtn.setOnClickListener {
            val email = viewModel.email.value
            val password = viewModel.password.value
            if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
                Snackbar.make(requireView(), R.string.fill_blanks, Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (!email.isValidEmail()) {
                Snackbar.make(requireView(), R.string.invalid_email, Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.signInUser(email, password)
        }

        return binding.root
    }


    private fun startLoading() {
        binding.myCampusAppLogo.visibility = View.GONE
        binding.myCampusAppName.visibility = View.GONE
        binding.loginEmail.visibility = View.GONE
        binding.loginPassword.visibility = View.GONE
        binding.loginNextButton.visibility = View.GONE
        binding.newUserTxt.visibility = View.GONE
        binding.classRepSignUpBtn.visibility = View.GONE
        binding.regularStudentSignUpBtn.visibility = View.GONE
        binding.loggingInTxt.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun finishLoading() {
        binding.myCampusAppLogo.visibility = View.VISIBLE
        binding.myCampusAppName.visibility = View.VISIBLE
        binding.loginEmail.visibility = View.VISIBLE
        binding.loginPassword.visibility = View.VISIBLE
        binding.loginNextButton.visibility = View.VISIBLE
        binding.newUserTxt.visibility = View.VISIBLE
        binding.classRepSignUpBtn.visibility = View.VISIBLE
        binding.regularStudentSignUpBtn.visibility = View.VISIBLE
        binding.loggingInTxt.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    private fun checkCurrentUser() {
        val currentUser = auth.currentUser
        val courseId = sharedPreferences.getString(COURSE_ID, "")
        if (currentUser != null && !courseId.isNullOrBlank()) {
            startLoading()
            val mainIntent = Intent(this.context, MainActivity::class.java)
            startActivity(mainIntent)
            requireActivity().finish()
        }
    }
}