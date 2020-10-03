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
import androidx.navigation.fragment.navArgs
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.databinding.FragmentSignUpBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
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
    lateinit var functions:FirebaseFunctions

    private val status by navArgs<SignUpFragmentArgs>()


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
        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        binding.viewModel = viewModel


        val submitBtn = binding.classRepSignedUpBtn
        submitBtn.setOnClickListener {
            val email: String? = viewModel.email.value
            val password = viewModel.password.value
            val courseId = viewModel.username.value
            val data = hashMapOf<String?,String?>(
                "email" to email,
                "courseId" to courseId
            )
            createUser(data,password)
        }


        return binding.root
    }

    private fun createUser(data:HashMap<String?,String?>, password: String?) {
        val email = data["email"]

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            Timber.i("values are null")
            return
        }
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Timber.i("User created successfully with Email")

                findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToLoginFragment())
                checkStudentStatus(status.studentStatus,data)
                return@addOnCompleteListener
            }
            Timber.i("Failed to create user with email ")
            Toast.makeText(this.context, "Failed to create user with email", Toast.LENGTH_SHORT)
                .show()
        }
    }
    private fun setAdminCourseId(data: HashMap<String?, String?>): Task<String> {
        Timber.i("Making admin...")

        return functions.getHttpsCallable("addAdminCourseId").call(data).continueWith { task: Task<HttpsCallableResult> ->
            // This continuation runs on either success or failure, but if the task
            // has failed then result will throw an Exception which will be
            // propagated down.
            Timber.i("Made admin!")
            val result = task.result?.data as String
            result
        }
    }
    private fun checkStudentStatus(status: StudentStatus,data: HashMap<String?, String?>){
        when(status){
            StudentStatus.ADMIN -> setAdminCourseId(data)
            StudentStatus.REGULAR -> setCourseId(data)
            StudentStatus.UNDEFINED -> return
        }
    }
    private fun setCourseId(data:HashMap<String?,String?>):Task<String>{
        return functions.getHttpsCallable("addCourseId").call(data).continueWith { task:Task<HttpsCallableResult>->

            Timber.i("Setting course id")
            val result = task.result?.data as String
            result
        }
    }
}