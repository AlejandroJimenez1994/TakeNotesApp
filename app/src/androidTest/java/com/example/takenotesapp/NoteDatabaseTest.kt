package com.example.takenotesapp

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.takenotesapp.database.Note
import com.example.takenotesapp.database.NoteDatabase
import com.example.takenotesapp.database.NoteDatabaseDao
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class NoteDatabaseTest {
    private lateinit var sleepDao: NoteDatabaseDao
    private lateinit var db: NoteDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, NoteDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        sleepDao = db.noteDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetNight() {
        val night = Note()
        night.title = "title"
        sleepDao.insert(night)
        val tonight = sleepDao.get(1)
        Assert.assertEquals(tonight?.title, "title")
    }
}