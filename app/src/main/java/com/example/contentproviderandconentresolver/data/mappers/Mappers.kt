package com.example.contentproviderandconentresolver.data.mappers

import com.example.contentproviderandconentresolver.data.local.NoteEntity
import com.example.contentproviderandconentresolver.domain.model.Note

fun Note.toNoteEntity(): NoteEntity {
    return NoteEntity(id, title, desc)
}

fun NoteEntity.toNote(): Note {
    return Note(id, title, desc)
}