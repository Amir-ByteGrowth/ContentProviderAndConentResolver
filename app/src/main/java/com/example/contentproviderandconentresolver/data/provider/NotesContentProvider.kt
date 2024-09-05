package com.example.contentproviderandconentresolver.data.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import com.example.contentproviderandconentresolver.data.local.NOTES_DATABASE
import com.example.contentproviderandconentresolver.data.local.NOTES_TABLE
import com.example.contentproviderandconentresolver.data.local.NoteEntity
import com.example.contentproviderandconentresolver.data.local.NotesDao
import com.example.contentproviderandconentresolver.data.local.NotesDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class NotesContentProvider : ContentProvider() {

    private lateinit var notesDatabase: NotesDatabase
    private lateinit var notesDao: NotesDao


    companion object {

        val AUTHORITY = "com.example.contentproviderandconentresolver.provider"

        // which table we want to expose or what what ever we want to expose
        val CONTENT_URI = Uri.parse("content://$AUTHORITY/$NOTES_TABLE")
    }


    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {

        // this uri will return list of nots
        // this code will be returned if uri matches  to identify uri
        addURI(AUTHORITY, NOTES_TABLE, 1)

        // this uri will return the not with specific id provided by content resolver
        addURI(AUTHORITY, NOTES_TABLE.plus("#"), 2)
    }

    override fun onCreate(): Boolean {
        // we are returning false here because it returns some time null here
        val context = context ?: return false

        notesDatabase =
            Room.databaseBuilder(context, NotesDatabase::class.java, NOTES_DATABASE).build()
        notesDao = notesDatabase.getNotesDao()

        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor {
        return when (uriMatcher.match(uri)) {
            1 -> {
                val query = SupportSQLiteQueryBuilder.builder(NOTES_TABLE).columns(projection)
                    .selection(selection, selectionArgs).orderBy(sortOrder).create()
                runBlocking(Dispatchers.IO) {
                    notesDatabase.query(query)
                }
            }

            2 -> {
                val noteId = ContentUris.parseId(uri).toInt()
                val query =
                    SupportSQLiteQueryBuilder.builder(NOTES_TABLE).columns(projection).selection(
                        "${NoteEntity::id.name} =?",
                        arrayOf(noteId.toString())
                    ).orderBy(sortOrder).create()
                runBlocking(Dispatchers.IO) { notesDatabase.query(query) }
            }

            else -> {
                throw IllegalArgumentException("No uri found")
            }
        }
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            1 -> {
                "vnd.android.cursor.dir/$AUTHORITY.$NOTES_TABLE"
            }

            2 -> {
                "vnd.android.cursor.item/$AUTHORITY.$NOTES_TABLE"
            }

            else -> {
                null
            }
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val entity = NoteEntity(
            title = values?.getAsString("title").orEmpty(),
            desc = values?.getAsString("desc").orEmpty()
        )
        val id = runBlocking(Dispatchers.IO) { notesDao.insert(noteEntity = entity) }

        // to notify any changes
        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return ContentUris.withAppendedId(CONTENT_URI, id)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val id = ContentUris.parseId(uri).toInt()
        val entity = runBlocking(Dispatchers.IO) { notesDao.getNoteWithId(id) }
        val idDeletedRow = runBlocking(Dispatchers.IO) { notesDao.delete(entity) }
        // to notify any changes
        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return idDeletedRow
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int {
        val id = ContentUris.parseId(uri).toInt()
        val entity = runBlocking(Dispatchers.IO) {
            notesDao.getNoteWithId(id)
        }
        val updatedEntity = entity.copy(
            id = entity.id,
            title = values?.getAsString("title").orEmpty(),
            desc = values?.getAsString("desc").orEmpty()
        )
        val idUpdatedEntity = runBlocking(Dispatchers.IO) {
            notesDao.update(updatedEntity)
        }
// to notify any changes
        context?.contentResolver?.notifyChange(CONTENT_URI, null)

        return idUpdatedEntity
    }
}