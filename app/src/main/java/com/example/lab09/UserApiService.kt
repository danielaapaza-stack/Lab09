package com.example.lab09

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApiService {
    @GET("users")
    suspend fun getUsers(@Query("limit") limit: Int): UsersResponse

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): UserModel
}