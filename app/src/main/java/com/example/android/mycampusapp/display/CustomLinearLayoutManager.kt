package com.example.android.mycampusapp.display

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.mycampusapp.MainActivity

class CustomLinearLayoutManager: LinearLayoutManager(MainActivity()) {
    override fun supportsPredictiveItemAnimations(): Boolean {
        return false
    }
}