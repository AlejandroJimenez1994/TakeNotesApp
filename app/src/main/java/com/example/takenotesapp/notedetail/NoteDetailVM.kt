package com.example.takenotesapp.notedetail

import android.app.Application
import android.net.Uri
import android.text.format.DateUtils
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.*
import com.example.takenotesapp.database.Note
import com.example.takenotesapp.database.NoteDatabaseDao
import kotlinx.coroutines.*
import java.nio.file.Files.delete
import java.text.SimpleDateFormat
import java.util.*

class NoteDetailVM(dataSource: NoteDatabaseDao, noteKey: Long, application: Application) : AndroidViewModel(application) {
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val database = dataSource
    private val key = noteKey

    private val _note = MutableLiveData<Note?>()

    private val _navigate = MutableLiveData<Boolean>()

    val navigate : LiveData<Boolean>
        get() = _navigate

    private val _enable = MutableLiveData<Boolean>()

    val enable : LiveData<Boolean>
        get() = _enable

    val lastEdited = Transformations.map(_note) { note ->
        "Last edited on: " + DateUtils.getRelativeTimeSpanString(note?.lastEdited!!)
    }


    val created = Transformations.map(_note) { note ->
        val date = Date(note?.dateCreated!!)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
        val str = "Created on: " + format.format(date)
        str
    }

    init {
        getNoteDB()
        _enable.value = false
    }

    fun getNote() : MutableLiveData<Note?>
    {
        return _note
    }


    private fun getNoteDB(){
        uiScope.launch {
            _note.value = getNoteFromDatabase()
        }
    }

    private suspend fun getNoteFromDatabase(): Note?{
        return withContext(Dispatchers.IO){
            val note = database.get(key)
            note
        }
    }

    fun onDelete(){
        uiScope.launch {
            delete()
            _navigate.value = true
        }
    }

    fun onUpdate(){
        uiScope.launch {
            update(_note.value!!)
        }
    }

    private suspend fun delete(){
        withContext(Dispatchers.IO){
            database.delete(_note.value!!)
        }
    }

    private suspend fun update(note: Note){
        withContext(Dispatchers.IO){
            note.lastEdited = System.currentTimeMillis()
            database.update(note)
        }
    }

    fun onEnabled(){
        if(!_enable.value!!) {
            _enable.value = !_enable.value!!
        }
        else{
            onUpdate()
            _enable.value = !_enable.value!!
        }
    }

    fun onDoneNavigating(){
        _navigate.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }




}