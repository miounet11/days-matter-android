package com.coquankedian.bigtime.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.coquankedian.bigtime.R
import com.coquankedian.bigtime.databinding.DialogPasscodeBinding

class PasscodeDialog : DialogFragment() {

    private var _binding: DialogPasscodeBinding? = null
    private val binding get() = _binding!!

    private var mode: Mode = Mode.SETUP
    private var onPasscodeVerified: (() -> Unit)? = null

    private lateinit var encryptedPrefs: android.content.SharedPreferences
    private val passcodeDigits = mutableListOf<String>()

    enum class Mode {
        SETUP, VERIFY, CHANGE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupEncryptedPreferences()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogPasscodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupNumberPad()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setupEncryptedPreferences() {
        val masterKey = MasterKey.Builder(requireContext())
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        encryptedPrefs = EncryptedSharedPreferences.create(
            requireContext(),
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun setupUI() {
        when (mode) {
            Mode.SETUP -> {
                binding.tvTitle.text = "设置密码"
                binding.tvSubtitle.text = "请输入6位数字密码"
            }
            Mode.VERIFY -> {
                binding.tvTitle.text = "输入密码"
                binding.tvSubtitle.text = "请输入您的密码"
            }
            Mode.CHANGE -> {
                binding.tvTitle.text = "修改密码"
                binding.tvSubtitle.text = "请输入当前密码"
            }
        }
    }

    private fun setupNumberPad() {
        // Number buttons
        val numberButtons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3, binding.btn4,
            binding.btn5, binding.btn6, binding.btn7, binding.btn8, binding.btn9
        )

        numberButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (passcodeDigits.size < 6) {
                    passcodeDigits.add(index.toString())
                    updatePasscodeDisplay()
                }
            }
        }

        // Delete button
        binding.btnDelete.setOnClickListener {
            if (passcodeDigits.isNotEmpty()) {
                passcodeDigits.removeAt(passcodeDigits.lastIndex)
                updatePasscodeDisplay()
            }
        }

        // Cancel button
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun updatePasscodeDisplay() {
        val displayText = "*".repeat(passcodeDigits.size)
        binding.tvPasscodeDisplay.text = displayText

        // Auto-submit when 6 digits entered
        if (passcodeDigits.size == 6) {
            handlePasscodeComplete()
        }
    }

    private fun handlePasscodeComplete() {
        val passcode = passcodeDigits.joinToString("")

        when (mode) {
            Mode.SETUP -> {
                savePasscode(passcode)
                Toast.makeText(requireContext(), "密码设置成功", Toast.LENGTH_SHORT).show()
                dismiss()
            }
            Mode.VERIFY -> {
                if (verifyPasscode(passcode)) {
                    onPasscodeVerified?.invoke()
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "密码错误", Toast.LENGTH_SHORT).show()
                    passcodeDigits.clear()
                    updatePasscodeDisplay()
                }
            }
            Mode.CHANGE -> {
                if (verifyPasscode(passcode)) {
                    // Show new passcode setup dialog
                    val newDialog = PasscodeDialog().apply {
                        this.mode = Mode.SETUP
                        this.onPasscodeVerified = {
                            Toast.makeText(requireContext(), "密码修改成功", Toast.LENGTH_SHORT).show()
                        }
                    }
                    newDialog.show(parentFragmentManager, "NewPasscodeDialog")
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "当前密码错误", Toast.LENGTH_SHORT).show()
                    passcodeDigits.clear()
                    updatePasscodeDisplay()
                }
            }
        }
    }

    private fun savePasscode(passcode: String) {
        encryptedPrefs.edit()
            .putString("user_passcode", passcode)
            .putBoolean("passcode_enabled", true)
            .apply()
    }

    private fun verifyPasscode(passcode: String): Boolean {
        val savedPasscode = encryptedPrefs.getString("user_passcode", null)
        return savedPasscode == passcode
    }

    companion object {
        fun newInstance(mode: Mode = Mode.SETUP, onVerified: (() -> Unit)? = null) = PasscodeDialog().apply {
            this.mode = mode
            this.onPasscodeVerified = onVerified
        }

        fun isPasscodeEnabled(context: android.content.Context): Boolean {
            return try {
                val masterKey = androidx.security.crypto.MasterKey.Builder(context)
                    .setKeyScheme(androidx.security.crypto.MasterKey.KeyScheme.AES256_GCM)
                    .build()

                val encryptedPrefs = androidx.security.crypto.EncryptedSharedPreferences.create(
                    context,
                    "secure_prefs",
                    masterKey,
                    androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )

                encryptedPrefs.getBoolean("passcode_enabled", false)
            } catch (e: Exception) {
                false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
