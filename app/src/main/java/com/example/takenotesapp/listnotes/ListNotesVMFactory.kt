package com.example.takenotesapp.listnotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.takenotesapp.database.NoteDatabaseDao

class ListNotesViewModelFactory(
    private val dataSource: NoteDatabaseDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListNotesVM::class.java)) {
            return ListNotesVM(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}