package com.example.takenotesapp.notedetail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.takenotesapp.database.NoteDatabaseDao

class NoteDetailsVMFactory(
    private val dataSource: NoteDatabaseDao,
    private val application: Application,
    private val noteKey: Long
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteDetailVM::class.java)) {
            return NoteDetailVM(dataSource,noteKey,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}