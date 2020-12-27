package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapter.NewsAdapter
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.androiddevs.mvvmnewsapp.util.Constant.Companion.SEARCH_NEWS_DELAY
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter:NewsAdapter

    val TAG = "SearchNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel

        setupRecylerView()

        newsAdapter.setOnItemClickedListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }

        var job: Job? = null
        etSearch.addTextChangedListener {editableText ->
            job?.cancel()

            job = MainScope().launch {
                delay(SEARCH_NEWS_DELAY)

                editableText?.let {
                    if (editableText.toString().isNotEmpty()){
                        viewModel.getSearchedNews(editableText.toString())
                    }
                }
            }
        }

        viewModel.searchedNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressLoader()
                    response.data?.let {
                            newsResponse -> newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }

                is Resource.Error -> {
                    hideProgressLoader()
                    response.message?.let{
                            message -> Log.d(TAG, "onViewCreated: " + message)
                    }
                }

                is Resource.Loading -> {
                    showProgressLoader()
                }
            }
        })
    }

    private fun hideProgressLoader() {
        paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressLoader() {
        paginationProgressBar.visibility = View.VISIBLE
    }

    private fun setupRecylerView() {
        newsAdapter = NewsAdapter()
        rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}