package com.example.takenotesapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.takenotesapp.database.Note

@Dao
interface NoteDatabaseDao {

    @Insert
    fun insert(note: Note)

    @Update
    fun update(note: Note)

    @Delete
    fun delete(note: Note)

    @Delete
    fun deleteSeveral(list: List<Note>)

    @Query("DELETE FROM note_table")
    fun clear()

    @Query("SELECT * FROM note_table WHERE noteId = :key")
    fun get(key: Long) : Note?

    @Query("SELECT * FROM note_table ORDER BY date_created ASC")
    fun getAll() : LiveData<List<Note>>



}