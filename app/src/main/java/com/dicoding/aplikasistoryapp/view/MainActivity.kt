package com.dicoding.aplikasistoryapp.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.aplikasistoryapp.R
import com.dicoding.aplikasistoryapp.databinding.ActivityMainBinding
import com.dicoding.aplikasistoryapp.view.adapter.LoadingAdapter
import com.dicoding.aplikasistoryapp.view.adapter.StoryAdapter
import com.dicoding.aplikasistoryapp.view.model.MainViewModel
import com.dicoding.aplikasistoryapp.view.model.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        viewModel.getSession().observe(this) { user ->
            if (!user.isLoggedIn) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                setStoriesData()
            }
        }
        viewModel.showLoading.observe(this) {
            showLoading(it)
        }
        binding.fabAdd.setOnClickListener {
            moveToStoryActivity()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStory.addItemDecoration(itemDecoration)
    }

    private fun setStoriesData() {
        val adapter = StoryAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingAdapter {
                adapter.retry()
            }
        )
        viewModel.listStory.observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    private fun moveToStoryActivity() {
        startActivity(Intent(this, StoryActivity::class.java))
    }

    private fun moveToMapActivity(){
        startActivity(Intent(this, MapActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.logout))
                    setMessage(getString(R.string.valid_logout))
                    setPositiveButton(getString(R.string.yes)) { _, _ ->
                        viewModel.logout()
                    }
                    setNegativeButton(getString(R.string.no)) { _, _ ->
                        return@setNegativeButton
                    }
                    create()
                    show()
                }
                true
            }
            R.id.action_maps -> {
                moveToMapActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}