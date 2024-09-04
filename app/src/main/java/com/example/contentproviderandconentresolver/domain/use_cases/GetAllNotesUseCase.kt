package com.example.contentproviderandconentresolver.domain.use_cases

import com.example.contentproviderandconentresolver.domain.repository.NotesRepository
import javax.inject.Inject

class GetAllNotesUseCase @Inject constructor(private val notesRepository: NotesRepository) {

    operator fun invoke() = notesRepository.getAllNotes()

}