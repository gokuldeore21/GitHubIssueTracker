package com.example.githubissuetracker

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.SearchView
import android.widget.Spinner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var issueAdapter: GitHubIssueAdapter
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)
        spinner = findViewById(R.id.spinnerFilter)

        // Setup RecyclerView and SearchView
        issueAdapter = GitHubIssueAdapter(emptyList())
        recyclerView.adapter = issueAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Setup Spinner
        val spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.filter_options, android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        // Initialize and display the list of issues
        loadIssues("facebook", "react", null)

        // Add a TextChangedListener to the SearchView for filtering issues
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter the list based on the search query
                issueAdapter.filter.filter(newText)
                return true
            }
        })

        // Handle spinner item selection
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedFilter = resources.getStringArray(R.array.filter_options)[position]
                loadIssues("facebook", "react", mapFilterOption(selectedFilter))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun loadIssues(owner: String, repo: String, state: String?) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                // Make a network request using Retrofit to fetch issues based on the selected state
                val issues = RetrofitClient.githubApi.getIssues(owner, repo, state, 1, 20)

                // Print the size of the issues list
                println("Received ${issues.size} issues")

                // Update the RecyclerView adapter with the received data
                issueAdapter.setData(issues)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun mapFilterOption(selectedFilter: String): String? {
        // Map the selected filter to the corresponding state parameter
        return when (selectedFilter) {
            "All issues" -> null
            "Open issues" -> "open"
            "Closed issues" -> "closed"
            else -> null
        }
    }
}
