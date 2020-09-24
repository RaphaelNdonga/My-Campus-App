package com.example.android.mycampusapp.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.databinding.FragmentLoginBinding
import com.example.android.mycampusapp.util.EventObserver
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    companion object {
        fun newInstance(): LoginFragment = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel

    @Inject
    lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentLoginBinding>(
            inflater,
            R.layout.fragment_login,
            container,
            false
        )
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val nextBtn = binding.loginNextButton

        viewModel.adminNavigator.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToSignUpFragment(StudentStatus.ADMIN)
            )
        })

        viewModel.regularNavigator.observe(viewLifecycleOwner,EventObserver{
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToSignUpFragment(StudentStatus.REGULAR)
            )
        })

        nextBtn.setOnClickListener {
            val email = viewModel.email.value
            val password = viewModel.password.value
            signInUser(email, password)
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        auth.signOut()
    }

    private fun signInUser(email: String?, password: String?) {
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            Timber.i("values are null")
            return
        }
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Timber.i("Signed in successfully with email and password")
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToTimetableFragment())
                return@addOnCompleteListener
            }
            Timber.i("Sign in with email and password failed")
            Timber.i("$email and $password are the email and password put in")
            Toast.makeText(this.context, "Failed to sign in", Toast.LENGTH_SHORT).show()

        }
    }
}