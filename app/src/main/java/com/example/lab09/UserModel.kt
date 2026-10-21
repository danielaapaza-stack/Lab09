package com.example.lab09

import com.google.gson.annotations.SerializedName

data class UsersResponse(
    @SerializedName("users") val users: List<UserModel>,
    @SerializedName("total") val total: Int
)

data class UserModel(
    @SerializedName("id") val id: Int,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("age") val age: Int,
    @SerializedName("gender") val gender: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("username") val username: String,
    @SerializedName("image") val image: String,
    @SerializedName("university") val university: String,
    @SerializedName("role") val role: String,
    @SerializedName("address") val address: AddressModel,
    @SerializedName("company") val company: CompanyModel
)

data class AddressModel(
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String,
    @SerializedName("country") val country: String
)

data class CompanyModel(
    @SerializedName("name") val name: String,
    @SerializedName("department") val department: String,
    @SerializedName("title") val title: String
)
