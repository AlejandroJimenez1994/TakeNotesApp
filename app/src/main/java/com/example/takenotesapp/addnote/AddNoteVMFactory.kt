package com.example.takenotesapp.addnote

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.takenotesapp.database.NoteDatabaseDao

class AddNoteViewModelFactory(
    private val dataSource: NoteDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddNoteVM::class.java)) {
            return AddNoteVM(dataSource,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}