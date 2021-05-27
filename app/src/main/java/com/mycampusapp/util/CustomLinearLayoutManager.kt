package com.mycampusapp.util

import androidx.recyclerview.widget.LinearLayoutManager
import com.mycampusapp.MainActivity

class CustomLinearLayoutManager: LinearLayoutManager(MainActivity()) {
    override fun supportsPredictiveItemAnimations(): Boolean {
        return false
    }
}