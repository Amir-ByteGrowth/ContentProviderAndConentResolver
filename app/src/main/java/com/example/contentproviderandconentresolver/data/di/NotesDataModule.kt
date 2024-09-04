package com.example.contentproviderandconentresolver.data.di

import android.content.Context
import com.example.contentproviderandconentresolver.data.local.NotesDao
import com.example.contentproviderandconentresolver.data.local.NotesDatabase
import com.example.contentproviderandconentresolver.data.repository.NotesRepoImpl
import com.example.contentproviderandconentresolver.domain.repository.NotesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NotesDataModule {


    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NotesDatabase {
        return NotesDatabase.getInstance(context)
    }

    @Provides
    fun provideNotesDao(notesDatabase: NotesDatabase): NotesDao {
        return notesDatabase.getNotesDao()
    }

    @Provides
    fun provideRepository(notesDao: NotesDao): NotesRepository {
        return NotesRepoImpl(notesDao)
    }

}