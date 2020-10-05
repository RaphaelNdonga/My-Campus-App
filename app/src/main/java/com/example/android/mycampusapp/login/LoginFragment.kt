package com.example.android.mycampusapp.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.databinding.FragmentLoginBinding
import com.example.android.mycampusapp.util.EventObserver
import com.example.android.mycampusapp.util.setupSnackbar
import com.example.android.mycampusapp.util.sharedPrefFile
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
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToTimetableFragment()
            )
        })

        viewModel.loadFinish.observe(viewLifecycleOwner, EventObserver {
            finishLoading()
        })

        viewModel.loadStart.observe(viewLifecycleOwner, EventObserver {
            startLoading()
        })

        nextBtn.setOnClickListener {
            val email = viewModel.email.value
            val password = viewModel.password.value
            viewModel.signInUser(email, password)
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        auth.signOut()
        sharedPreferences.edit().clear().apply()
    }


    private fun startLoading() {
        binding.myCampusAppLogo.visibility = View.GONE
        binding.myCampusAppName.visibility = View.GONE
        binding.loginEmail.visibility = View.GONE
        binding.loginPassword.visibility = View.GONE
        binding.loginCancelButton.visibility = View.GONE
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
        binding.loginCancelButton.visibility = View.VISIBLE
        binding.loginNextButton.visibility = View.VISIBLE
        binding.newUserTxt.visibility = View.VISIBLE
        binding.classRepSignUpBtn.visibility = View.VISIBLE
        binding.regularStudentSignUpBtn.visibility = View.VISIBLE
        binding.loggingInTxt.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSnackBar()
    }
    private fun setupSnackBar(){
        view?.setupSnackbar(viewLifecycleOwner,viewModel.snackBarText,Snackbar.LENGTH_SHORT)
    }
}