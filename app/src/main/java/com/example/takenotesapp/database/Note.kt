package com.example.takenotesapp.database

import androidx.room.*


@Entity(tableName = "note_table")
data class Note (

    @PrimaryKey(autoGenerate = true)
    var noteId: Long = 0L,

    @ColumnInfo(name = "title")
    var title: String? = "",

    @ColumnInfo(name = "header")
    var header: String? = "",

    @ColumnInfo(name = "descrip")
    var descrip: String? = "",

    @ColumnInfo(name = "date_created")
    var dateCreated: Long = 0L,

    @ColumnInfo(name = "last_edited")
    var lastEdited: Long = 0L,

    @ColumnInfo(name = "photo")
    var photo: String? = ""

)



