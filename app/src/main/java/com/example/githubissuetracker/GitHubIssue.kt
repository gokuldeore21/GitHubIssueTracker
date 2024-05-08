package com.example.githubissuetracker

data class GitHubIssue(
    val title: String,
    val created_at: String,
    val closed_at: String?,
    val user: GitHubUser
)
data class GitHubUser(
    val login: String,
    val avatar_url: String
)