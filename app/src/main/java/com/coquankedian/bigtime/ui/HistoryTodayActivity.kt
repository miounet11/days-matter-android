package com.coquankedian.bigtime.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.coquankedian.bigtime.databinding.ActivityHistoryTodayBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class HistoryTodayActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityHistoryTodayBinding
    private lateinit var adapter: HistoryEventAdapter
    private val historyEvents = mutableListOf<HistoryEvent>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryTodayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        loadHistoryEvents()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "历史上的今天"
        }
        
        val dateFormat = SimpleDateFormat("MM月dd日", Locale.CHINA)
        binding.tvTodayDate.text = dateFormat.format(Date())
    }
    
    private fun setupRecyclerView() {
        adapter = HistoryEventAdapter(historyEvents)
        binding.rvHistoryEvents.apply {
            layoutManager = LinearLayoutManager(this@HistoryTodayActivity)
            adapter = this@HistoryTodayActivity.adapter
        }
    }
    
    private fun loadHistoryEvents() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvHistoryEvents.visibility = View.GONE
        binding.tvError.visibility = View.GONE
        
        lifecycleScope.launch {
            try {
                val events = fetchHistoryEvents()
                historyEvents.clear()
                historyEvents.addAll(events)
                
                withContext(Dispatchers.Main) {
                    adapter.notifyDataSetChanged()
                    binding.progressBar.visibility = View.GONE
                    
                    if (historyEvents.isEmpty()) {
                        binding.tvError.text = "暂无历史事件"
                        binding.tvError.visibility = View.VISIBLE
                    } else {
                        binding.rvHistoryEvents.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.tvError.text = "加载失败，请检查网络连接"
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        }
    }
    
    private suspend fun fetchHistoryEvents(): List<HistoryEvent> {
        return withContext(Dispatchers.IO) {
            // 使用备用的历史事件数据或本地数据
            // 由于没有具体的API，这里返回示例数据
            val calendar = Calendar.getInstance()
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            
            // 返回一些著名的历史事件作为示例
            val sampleEvents = when {
                month == 10 && day == 1 -> listOf(
                    HistoryEvent(1949, "中华人民共和国成立", "1949年10月1日，中华人民共和国中央人民政府成立典礼在北京天安门广场隆重举行。"),
                    HistoryEvent(1958, "美国航空航天局NASA成立", "美国国家航空航天局正式成立，开启了太空探索的新纪元。"),
                    HistoryEvent(1971, "迪士尼乐园开园", "华特迪士尼世界在佛罗里达州奥兰多正式开园。")
                )
                month == 7 && day == 4 -> listOf(
                    HistoryEvent(1776, "美国独立宣言签署", "美国大陆会议通过《独立宣言》，宣布脱离英国独立。"),
                    HistoryEvent(1862, "《爱丽丝梦游仙境》首次出版", "路易斯·卡罗尔的经典童话首次出版。")
                )
                month == 5 && day == 1 -> listOf(
                    HistoryEvent(1886, "国际劳动节起源", "芝加哥工人大罢工，争取八小时工作制。"),
                    HistoryEvent(1931, "帝国大厦竣工", "纽约帝国大厦正式竣工，成为当时世界最高建筑。")
                )
                else -> listOf(
                    HistoryEvent(0, "历史事件", "今天是${month}月${day}日，历史上的这一天发生了许多重要事件。"),
                    HistoryEvent(0, "名人诞辰", "许多著名人物在这一天诞生。"),
                    HistoryEvent(0, "科技进步", "人类科技发展史上的重要时刻。")
                )
            }
            
            sampleEvents
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    data class HistoryEvent(
        val year: Int,
        val title: String,
        val description: String
    )
    
    inner class HistoryEventAdapter(
        private val events: List<HistoryEvent>
    ) : androidx.recyclerview.widget.RecyclerView.Adapter<HistoryEventAdapter.ViewHolder>() {
        
        inner class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
            val binding = com.coquankedian.bigtime.databinding.ItemHistoryEventBinding.bind(view)
        }
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = layoutInflater.inflate(
                com.coquankedian.bigtime.R.layout.item_history_event,
                parent,
                false
            )
            return ViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val event = events[position]
            holder.binding.apply {
                tvYear.text = if (event.year > 0) "${event.year}年" else ""
                tvTitle.text = event.title
                tvDescription.text = event.description
            }
        }
        
        override fun getItemCount() = events.size
    }
}