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
import com.example.android.mycampusapp.databinding.FragmentClassRepBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ClassRepFragment : Fragment() {

    companion object {
        fun newInstance() = ClassRepFragment()
    }

    private lateinit var viewModel: ClassRepViewModel

    @Inject
    lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentClassRepBinding>(
            inflater,
            R.layout.fragment_class_rep,
            container,
            false
        )
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this).get(ClassRepViewModel::class.java)
        binding.viewModel = viewModel


        val submitBtn = binding.classRepSignedUpBtn
        submitBtn.setOnClickListener {
            val email = viewModel.email.value
            val password = viewModel.password.value
            createUser(email,password)
        }


        return binding.root
    }

    private fun createUser(email: String?, password: String?) {
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            Timber.i("values are null")
            return
        }
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Timber.i("User created successfully with Email")
                findNavController().navigate(ClassRepFragmentDirections.actionSignUpClassRepFragmentToLoginFragment())
                return@addOnCompleteListener
            }
            Timber.i("Failed to create user with email ")
            Toast.makeText(this.context, "Failed to create user with email", Toast.LENGTH_SHORT)
                .show()
        }
    }
}