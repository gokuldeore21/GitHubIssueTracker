package com.example.githubissuetracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class GitHubIssueAdapter(private var issues: List<GitHubIssue>) :
    RecyclerView.Adapter<GitHubIssueAdapter.ViewHolder>(), Filterable {

    private var filteredIssues: List<GitHubIssue> = issues

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val createdAtTextView: TextView = itemView.findViewById(R.id.createdAtTextView)
        val closedAtTextView: TextView = itemView.findViewById(R.id.closedAtTextView)
        val userNameTextView: TextView = itemView.findViewById(R.id.userNameTextView)
        val userImageView: ImageView = itemView.findViewById(R.id.userImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.issue_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val issue = filteredIssues[position]

        holder.titleTextView.text = issue.title
        holder.createdAtTextView.text = "Created: ${issue.created_at}"
        holder.closedAtTextView.text = "Closed: ${issue.closed_at ?: "Not closed"}"
        holder.userNameTextView.text = "User: ${issue.user.login}"

        // Load user image using Glide
        Glide.with(holder.itemView.context)
            .load(issue.user.avatar_url)
            .into(holder.userImageView)
    }

    override fun getItemCount(): Int {
        return filteredIssues.size
    }

    fun setData(newIssues: List<GitHubIssue>) {
        issues = newIssues
        filteredIssues = newIssues
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<GitHubIssue>()

                if (constraint.isNullOrBlank()) {
                    filteredList.addAll(issues)
                } else {
                    val filterPattern = constraint.toString().trim { it <= ' ' }.lowercase()

                    for (issue in issues) {
                        if (issue.title.lowercase().contains(filterPattern) ||
                            issue.user.login.lowercase().contains(filterPattern)
                        ) {
                            filteredList.add(issue)
                        }
                    }
                }

                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                filteredIssues = results?.values as List<GitHubIssue>
                notifyDataSetChanged()
            }
        }
    }
}
