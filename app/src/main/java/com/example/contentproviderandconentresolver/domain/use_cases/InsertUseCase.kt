package com.example.contentproviderandconentresolver.domain.use_cases

import com.example.contentproviderandconentresolver.domain.model.Note
import com.example.contentproviderandconentresolver.domain.repository.NotesRepository
import javax.inject.Inject

class InsertUseCase @Inject constructor(private val notesRepository: NotesRepository) {

    suspend operator fun invoke(note: Note) = notesRepository.insert(note)

}