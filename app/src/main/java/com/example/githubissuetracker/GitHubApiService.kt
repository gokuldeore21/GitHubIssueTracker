package com.example.githubissuetracker


import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApiService {
    @GET("repos/{owner}/{repo}/issues")
    suspend fun getIssues(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("state") state: String?,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<GitHubIssue>
}
