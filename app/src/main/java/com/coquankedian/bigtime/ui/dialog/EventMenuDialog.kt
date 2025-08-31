package com.coquankedian.bigtime.ui.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.coquankedian.bigtime.R
import com.coquankedian.bigtime.data.model.Event
import com.coquankedian.bigtime.data.repository.AppRepository
import com.coquankedian.bigtime.databinding.DialogEventMenuBinding
import com.coquankedian.bigtime.ui.EventViewModel
import com.coquankedian.bigtime.ui.EventViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.coquankedian.bigtime.ui.dialog.PasscodeDialog

class EventMenuDialog : DialogFragment() {

    private var _binding: DialogEventMenuBinding? = null
    private val binding get() = _binding!!

    private var event: Event? = null

    // TODO: Use proper dependency injection
    private val eventViewModel: EventViewModel by lazy {
        val database = com.coquankedian.bigtime.data.database.AppDatabase.getDatabase(requireContext(), kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO))
        val repository = com.coquankedian.bigtime.data.repository.AppRepository(database.eventDao(), database.categoryDao())
        com.coquankedian.bigtime.ui.EventViewModel(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogEventMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get event from arguments
        event = arguments?.getSerializable("event") as? com.coquankedian.bigtime.data.model.Event
        event?.let { setupMenu(it) }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setupMenu(event: Event) {
        binding.tvEventTitle.text = event.title

        // Pin/Unpin
        binding.btnPin.apply {
            text = if (event.isPinned) "取消置顶" else "置顶"
            setOnClickListener {
                togglePinStatus(event)
            }
        }

        // Archive/Unarchive
        binding.btnArchive.apply {
            text = if (event.isArchived) "取消归档" else "归档"
            setOnClickListener {
                toggleArchiveStatus(event)
            }
        }

        // Lock/Unlock
        binding.btnLock.apply {
            text = if (event.isLocked) "解锁" else "锁定"
            setOnClickListener {
                toggleLockStatus(event)
            }
        }

        // Edit
        binding.btnEdit.setOnClickListener {
            editEvent(event)
        }

        // Share
        binding.btnShare.setOnClickListener {
            shareEvent(event)
        }

        // Delete
        binding.btnDelete.setOnClickListener {
            showDeleteConfirmation(event)
        }

        // Cancel
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun togglePinStatus(event: Event) {
        eventViewModel.updatePinnedStatus(event.id, !event.isPinned)
        val message = if (event.isPinned) "已取消置顶" else "已置顶"
        showMessage(message)
        dismiss()
    }

    private fun toggleArchiveStatus(event: Event) {
        if (event.isArchived) {
            eventViewModel.unarchiveEvent(event.id)
            showMessage("已从归档中恢复")
        } else {
            eventViewModel.archiveEvent(event.id)
            showMessage("已归档")
        }
        dismiss()
    }

    private fun toggleLockStatus(event: Event) {
        eventViewModel.updateLockedStatus(event.id, !event.isLocked)
        val message = if (event.isLocked) "已解锁" else "已锁定"
        showMessage(message)
        dismiss()
    }

    private fun editEvent(event: Event) {
        if (event.isLocked && PasscodeDialog.isPasscodeEnabled(requireContext())) {
            // Verify passcode before editing locked event
            val passcodeDialog = PasscodeDialog.newInstance(PasscodeDialog.Mode.VERIFY) {
                showEditDialog(event)
            }
            passcodeDialog.show(parentFragmentManager, "VerifyPasscodeForEdit")
        } else {
            showEditDialog(event)
        }
    }

    private fun showEditDialog(event: Event) {
        val dialog = AddEditEventDialog.newInstance(event)
        dialog.show(parentFragmentManager, "EditEventDialog")
        dismiss()
    }

    private fun shareEvent(event: Event) {
        val dialog = ShareEventDialog.newInstance(event)
        dialog.show(parentFragmentManager, "ShareEventDialog")
        dismiss()
    }

    private fun showDeleteConfirmation(event: Event) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("删除事件")
            .setMessage("确定要删除这个事件吗？此操作无法撤销。")
            .setPositiveButton("删除") { _, _ ->
                eventViewModel.deleteEvent(event)
                showMessage("事件已删除")
                dismiss()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showMessage(message: String) {
        view?.let { Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(event: Event) = EventMenuDialog().apply {
            arguments = Bundle().apply {
                putSerializable("event", event as java.io.Serializable)
            }
        }
    }
}
