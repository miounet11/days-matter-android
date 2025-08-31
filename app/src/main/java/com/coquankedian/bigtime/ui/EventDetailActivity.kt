package com.coquankedian.bigtime.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.coquankedian.bigtime.R
import com.coquankedian.bigtime.data.database.AppDatabase
import com.coquankedian.bigtime.data.model.Event
import com.coquankedian.bigtime.data.repository.AppRepository
import com.coquankedian.bigtime.databinding.ActivityEventDetailBinding
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class EventDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityEventDetailBinding
    private var currentEvent: Event? = null
    private lateinit var repository: AppRepository
    
    companion object {
        const val EXTRA_EVENT_ID = "event_id"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupRepository()
        loadEvent()
        setupButtons()
    }
    
    private fun setupRepository() {
        val database = AppDatabase.getDatabase(this, lifecycleScope)
        repository = AppRepository(database.eventDao(), database.categoryDao())
    }
    
    private fun loadEvent() {
        val eventId = intent.getLongExtra(EXTRA_EVENT_ID, -1)
        if (eventId != -1L) {
            lifecycleScope.launch {
                currentEvent = repository.getEventById(eventId)
                currentEvent?.let { displayEvent(it) }
            }
        }
    }
    
    private fun displayEvent(event: Event) {
        // Calculate days
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
        
        val diffInMillis = event.date.time - today.time
        val daysCount = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
        
        // Update UI
        binding.tvEventTitle.text = when {
            daysCount > 0 -> "${event.title} 还有"
            daysCount < 0 -> "${event.title} 已经"
            else -> event.title
        }
        
        binding.tvDaysCount.text = abs(daysCount).toString()
        
        binding.tvDaysLabel.text = when {
            daysCount == 0 -> "今天"
            else -> "天"
        }
        
        // Format date
        val dateFormat = SimpleDateFormat("目标日: yyyy-MM-dd EEEE", Locale.CHINA)
        binding.tvTargetDate.text = dateFormat.format(event.date)
        
        // Set background based on category
        val backgroundRes = when (event.categoryId) {
            1L -> R.drawable.bg_wood_texture // Anniversary
            2L -> R.drawable.bg_blue_gradient // Work
            3L -> R.drawable.bg_orange_gradient // Life
            else -> R.drawable.bg_default_gradient
        }
        binding.backgroundImage.setImageResource(backgroundRes)
    }
    
    private fun setupButtons() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        binding.btnEdit.setOnClickListener {
            // Open edit dialog
            currentEvent?.let { event ->
                val intent = Intent(this, com.coquankedian.bigtime.MainActivity::class.java).apply {
                    putExtra("open_edit", true)
                    putExtra("event_id", event.id)
                }
                startActivity(intent)
                finish()
            }
        }
        
        binding.btnShare.setOnClickListener {
            shareEventAsImage()
        }
        
        binding.btnSaveImage.setOnClickListener {
            saveEventAsImage()
        }
        
        binding.btnChangeBackground.setOnClickListener {
            // TODO: Show background picker dialog
        }
        
        binding.btnPin.setOnClickListener {
            togglePin()
        }
    }
    
    private fun togglePin() {
        currentEvent?.let { event ->
            lifecycleScope.launch {
                repository.updatePinnedStatus(event.id, !event.isPinned)
                // Update UI
                binding.btnPin.setImageResource(
                    if (!event.isPinned) R.drawable.ic_baseline_push_pin_24_filled
                    else R.drawable.ic_baseline_push_pin_24
                )
                currentEvent = event.copy(isPinned = !event.isPinned)
            }
        }
    }
    
    private fun shareEventAsImage() {
        val bitmap = captureView(binding.contentContainer)
        val file = saveBitmapToCache(bitmap)
        
        val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "分享倒数日"))
    }
    
    private fun saveEventAsImage() {
        val bitmap = captureView(binding.contentContainer)
        saveBitmapToGallery(bitmap)
    }
    
    private fun captureView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
    
    private fun saveBitmapToCache(bitmap: Bitmap): File {
        val cachePath = File(cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "event_${System.currentTimeMillis()}.png")
        
        FileOutputStream(file).use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }
        
        return file
    }
    
    private fun saveBitmapToGallery(bitmap: Bitmap) {
        // TODO: Save to gallery with proper permissions
    }
}