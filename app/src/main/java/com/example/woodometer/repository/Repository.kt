package com.example.woodometer.repository

import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface Repository<T : Any, ID : Any> {
    suspend fun add(item : T) : Boolean
    suspend fun get(key : ID) : T?
    suspend fun getAll() : List<T>
    fun observe() : Flow<List<T>>
    suspend fun delete(key : ID) : Boolean
}