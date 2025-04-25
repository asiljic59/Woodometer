package com.example.woodometer.repository

interface Repository<T, K> {
    suspend fun getById(id : K) : T?
    suspend fun add(vararg item : T)
    suspend fun delete(item: T)
    suspend fun upsert(vararg item: T)
}