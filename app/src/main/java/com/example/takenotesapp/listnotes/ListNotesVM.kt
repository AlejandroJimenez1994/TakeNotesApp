package com.example.takenotesapp.listnotes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.takenotesapp.database.Note
import com.example.takenotesapp.database.NoteDatabaseDao
import kotlinx.coroutines.*
import java.util.*

enum class Direction {
    LIST, GRID
}

enum class Filter {
    ALL, CREATED, ALPHABETICAL
}

class ListNotesVM(dataSource: NoteDatabaseDao) : ViewModel() {

    val database = dataSource
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val notes = database.getAll()

    private val _navigateToNoteDetail = MutableLiveData<Long>()

    val navigateToNoteDetail : LiveData<Long>
        get() = _navigateToNoteDetail

    private val _filter = MutableLiveData<Filter>()

    val filter : LiveData<Filter>
        get() = _filter

    private val _changeLayout = MutableLiveData<Direction>()

    val changeLayout : LiveData<Direction>
        get() = _changeLayout

    private val _search = MutableLiveData<String>()

    val search : LiveData<String>
        get() = _search

    private val searchLowCase = Transformations.map(search) { string ->
        string.toLowerCase(Locale.ROOT)
    }

    private val _queryNotes = MutableLiveData<List<Note>>()

    val queryNotes : LiveData<List<Note>>
        get() = _queryNotes

    fun onNoteClicked(id: Long){
        _navigateToNoteDetail.value = id
    }

    fun onNoteDetailNavigated(){
        _navigateToNoteDetail.value = null
    }

    fun onClear(){
        uiScope.launch {
            clear()
        }
    }

    fun onLayoutChange(){
        if(_changeLayout.value == Direction.LIST){
            _changeLayout.value = Direction.GRID
        }
        else
        {
            _changeLayout.value = Direction.LIST
        }
    }

    init {
        onLayoutChange()
        _filter.value = Filter.ALL
    }

    fun onFilter() : Filter?{
        when(_filter.value){
            Filter.ALL -> _filter.value = Filter.CREATED
            Filter.CREATED -> _filter.value = Filter.ALPHABETICAL
            Filter.ALPHABETICAL -> _filter.value = Filter.ALL
        }
        return _filter.value
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }

    fun loadQuery(string: String?){
        _search.value = string
    }

    fun onSearch(){
        _queryNotes.value = notes.value?.filter {
            it.title?.toLowerCase()?.contains(search.value?.toLowerCase()!!)!!
        }
    }



    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}