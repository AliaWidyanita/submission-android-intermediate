package com.dicoding.aplikasistoryapp.view

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.aplikasistoryapp.R
import com.dicoding.aplikasistoryapp.data.remote.ListStoryItem
import com.dicoding.aplikasistoryapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detailStory = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(KEY_DETAIL, ListStoryItem::class.java)
        } else {
            intent.getParcelableExtra(KEY_DETAIL)
        }

        if (detailStory != null) {
            binding.tvNameStory.text = detailStory.name.toString()
            binding.tvDescriptionStory.text = detailStory.description.toString()
            Glide.with(this@DetailActivity)
                .load(detailStory.photoUrl.toString())
                .into(binding.ivPhotoStory)
        }
        supportActionBar?.title = resources.getString(R.string.detail_story, detailStory?.name.toString())
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val KEY_DETAIL = "key_detail"
    }
}