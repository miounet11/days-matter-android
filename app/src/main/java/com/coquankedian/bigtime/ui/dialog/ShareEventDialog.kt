package com.coquankedian.bigtime.ui.dialog

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.coquankedian.bigtime.R
import com.coquankedian.bigtime.data.model.Event
import com.coquankedian.bigtime.databinding.DialogShareEventBinding
import com.coquankedian.bigtime.utils.ImageGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ShareEventDialog : DialogFragment() {

    private var _binding: DialogShareEventBinding? = null
    private val binding get() = _binding!!

    private var event: Event? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogShareEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get event from arguments
        event = arguments?.getSerializable("event") as? com.coquankedian.bigtime.data.model.Event
        event?.let { setupShareContent(it) }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setupShareContent(event: Event) {
        binding.tvEventTitle.text = event.title

        // Generate share text
        val shareText = generateShareText(event)
        binding.tvSharePreview.text = shareText

        // Setup share buttons
        binding.btnShareText.setOnClickListener {
            shareAsText(shareText)
        }

        binding.btnShareImage.setOnClickListener {
            shareAsImage()
        }

        binding.btnCopyText.setOnClickListener {
            copyToClipboard(shareText)
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun generateShareText(event: Event): String {
        val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())

        return buildString {
            append("📅 ${event.title}\n")
            append("📆 ${dateFormat.format(event.date)}\n")
            append("⏰ ${event.countdownText}\n")

            if (event.description.isNotEmpty()) {
                append("📝 ${event.description}\n")
            }

            append("\n")
            append("来自 Day Counter - 完美的事件倒计时应用")
        }
    }

    private fun shareAsText(text: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }

        val chooserIntent = Intent.createChooser(shareIntent, "分享事件")
        startActivity(chooserIntent)
        dismiss()
    }

    private fun shareAsImage() {
        event?.let { currentEvent ->
            // Show loading toast
            Toast.makeText(requireContext(), "正在生成图片...", Toast.LENGTH_SHORT).show()
            
            lifecycleScope.launch(Dispatchers.IO) {
                val imageGenerator = ImageGenerator(requireContext())
                val imageFile = imageGenerator.generateEventImage(currentEvent)
                
                withContext(Dispatchers.Main) {
                    if (imageFile != null) {
                        try {
                            val uri = FileProvider.getUriForFile(
                                requireContext(),
                                "${requireContext().packageName}.fileprovider",
                                imageFile
                            )
                            
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                type = "image/png"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                putExtra(Intent.EXTRA_TEXT, "来自 Days Matter - 重要日子倒计时")
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            
                            val chooserIntent = Intent.createChooser(shareIntent, "分享图片")
                            startActivity(chooserIntent)
                            dismiss()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(requireContext(), "分享失败：${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "生成图片失败，请重试", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = requireContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Day Counter Event", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "已复制到剪贴板", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(event: Event) = ShareEventDialog().apply {
            arguments = Bundle().apply {
                putSerializable("event", event as java.io.Serializable) // Use Serializable instead of Parcelable for now
            }
        }
    }
}
