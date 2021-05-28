package com.example.android.mycampusapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.example.android.mycampusapp.assessments.AssessmentType
import com.example.android.mycampusapp.databinding.ActivityMainBinding
import com.example.android.mycampusapp.timetable.display.TimetableFragmentDirections
import com.example.android.mycampusapp.util.DayOfWeek
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var viewModel: MainActivityViewModel

    @Inject
    lateinit var courseCollection: CollectionReference

    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        val toolbar = binding.appBar
        setSupportActionBar(toolbar)
        drawerLayout = binding.drawerLayout
        navController = findNavController(R.id.nav_host_fragment)

        val appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        viewModel = ViewModelProvider(
            this,
            MainActivityViewModelFactory(
                this.application,
                firebaseMessaging,
                auth,
                courseCollection
            )
        ).get(MainActivityViewModel::class.java)

        viewModel.setupRecurringWork()
        viewModel.subscribeToTopic()
        viewModel.confirmAdminStatus()

        val dayOfWeek = intent.getSerializableExtra("dayOfWeek") as DayOfWeek?
        dayOfWeek?.let {
            when (it) {
                DayOfWeek.MONDAY -> {
                    navController.navigate(
                        TimetableFragmentDirections.actionTimetableFragmentToMondayFragment(
                            false
                        )
                    )
                }
                DayOfWeek.TUESDAY -> {
                    navController.navigate(
                        TimetableFragmentDirections.actionTimetableFragmentToTuesdayFragment(false)
                    )
                }
                DayOfWeek.WEDNESDAY -> {
                    navController.navigate(
                        TimetableFragmentDirections.actionTimetableFragmentToWednesdayFragment(
                            false
                        )
                    )
                }
                DayOfWeek.THURSDAY -> {
                    navController.navigate(
                        TimetableFragmentDirections.actionTimetableFragmentToThursdayFragment(
                            false
                        )
                    )
                }
                DayOfWeek.FRIDAY -> {
                    navController.navigate(
                        TimetableFragmentDirections.actionTimetableFragmentToFridayFragment(
                            false
                        )
                    )
                }
                DayOfWeek.SATURDAY -> {
                    navController.navigate(
                        TimetableFragmentDirections.actionTimetableFragmentToSaturdayFragment(
                            false
                        )
                    )
                }
                DayOfWeek.SUNDAY -> {
                    navController.navigate(
                        TimetableFragmentDirections.actionTimetableFragmentToSundayFragment(
                            false
                        )
                    )
                }
            }
        }
        val assessmentType = intent.getSerializableExtra("assessmentType") as AssessmentType?
        assessmentType?.let {
            when (it) {
                AssessmentType.ASSIGNMENT -> {
                    navController.navigate(
                        TimetableFragmentDirections.actionTimetableFragmentToAssignmentsFragment(
                            false
                        )
                    )
                }
                AssessmentType.TEST -> {
                    navController.navigate(
                        TimetableFragmentDirections.actionTimetableFragmentToTestsFragment(
                            false
                        )
                    )
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navigateUp(navController, drawerLayout)
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.timetableFragment) {
            return super.onBackPressed()
        }
        navController.navigateUp()
    }
}