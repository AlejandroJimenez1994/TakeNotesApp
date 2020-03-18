package com.example.takenotesapp.addnote

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.example.takenotesapp.database.Note
import com.example.takenotesapp.database.NoteDatabaseDao
import kotlinx.coroutines.*

class AddNoteVM(dataSource: NoteDatabaseDao, application: Application) : AndroidViewModel(application) {
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val database = dataSource

    private val _navigateToNoteList = MutableLiveData<Boolean>()

    val navigateToNoteList : LiveData<Boolean>
        get() = _navigateToNoteList

    private val title = MutableLiveData<String>()

    private val header = MutableLiveData<String>()

    private val description = MutableLiveData<String>()

    private val photo = MutableLiveData<Uri>()

    fun getTitle() : MutableLiveData<String>
    {
        return title
    }

    fun getHeader() : MutableLiveData<String>
    {
        return header
    }

    fun getDescription() : MutableLiveData<String>
    {
        return description
    }

    fun setPhoto(uri: Uri?){
        photo.value = uri
    }

    fun getPhoto() : MutableLiveData<Uri>{
        return photo
    }



    fun onAdd() {
        uiScope.launch {
            var note = Note()
            if (validateAddButton()) {
            note.title = title.value
            note.header = header.value
            note.descrip = description.value
            note.dateCreated = System.currentTimeMillis()
            note.lastEdited = note.dateCreated
            note.photo = photo.value.toString()
            insert(note)
            _navigateToNoteList.value = true
            }
        }
    }
    private suspend fun insert(note: Note){
        withContext(Dispatchers.IO) {
            database.insert(note)
        }
    }


    private fun validateAddButton() : Boolean {
        return !title.value.isNullOrEmpty() && !header.value.isNullOrEmpty() && !description.value.isNullOrEmpty()
    }

    fun doneNavigating() {
        _navigateToNoteList.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
