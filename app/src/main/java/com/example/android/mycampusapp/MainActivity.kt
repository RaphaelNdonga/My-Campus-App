package com.example.android.mycampusapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.android.mycampusapp.data.AdminEmail
import com.example.android.mycampusapp.databinding.ActivityMainBinding
import com.example.android.mycampusapp.timetable.service.TimetableService
import com.example.android.mycampusapp.util.COURSE_ID
import com.example.android.mycampusapp.util.IS_ADMIN
import com.example.android.mycampusapp.util.USER_EMAIL
import com.example.android.mycampusapp.util.sharedPrefFile
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var snapshotListener: ListenerRegistration
    private lateinit var viewModel: AdminAccountsViewModel

    @Inject
    lateinit var courseCollection: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        val serviceIntent = Intent(this, TimetableService::class.java)
        stopService(serviceIntent)
        //TODO: We need to check if the service is running first before we stop it.

        super.onCreate(savedInstanceState)
        @Suppress("UNUSED_VARIABLE")
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        val toolbar = binding.appBar
        setSupportActionBar(toolbar)
        drawerLayout = binding.drawerLayout
        navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController,drawerLayout)
        binding.navView.setupWithNavController(navController)

        sharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val courseId = sharedPreferences.getString(COURSE_ID, "")!!
        val userEmail = sharedPreferences.getString(USER_EMAIL, "")!!
        val isAdmin = sharedPreferences.getBoolean(IS_ADMIN, false)

        val adminEmail = AdminEmail(userEmail)
        viewModel = ViewModelProvider(
            this,
            AdminAccountsViewModelFactory(
                courseCollection.document(courseId).collection("admins")
            )
        ).get(AdminAccountsViewModel::class.java)

        viewModel.adminList.observe(this, {
            if (isAdmin)
                viewModel.checkAndAddEmail(it,adminEmail)
        })

    }

    override fun onStart() {
        super.onStart()
        snapshotListener = viewModel.addSnapshotListener()
    }

    override fun onStop() {
        super.onStop()
        snapshotListener.remove()
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    override fun onBackPressed() {
        if(navController.currentDestination?.id == R.id.timetableFragment){
            return super.onBackPressed()
        }
        navController.navigateUp()
    }
}