package com.coquankedian.bigtime.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.coquankedian.bigtime.R
import com.coquankedian.bigtime.databinding.ActivitySettingsBinding
import com.coquankedian.bigtime.ui.dialog.PasscodeDialog

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var encryptedPrefs: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "设置"

        setupEncryptedPreferences()
        setupUI()
        loadSettings()
    }

    private fun setupEncryptedPreferences() {
        val masterKey = MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        encryptedPrefs = EncryptedSharedPreferences.create(
            this,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun setupUI() {
        // Passcode protection switch
        binding.switchPasscode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enablePasscodeProtection()
            } else {
                disablePasscodeProtection()
            }
        }

        // Change passcode button
        binding.btnChangePasscode.setOnClickListener {
            changePasscode()
        }

        // About section
        binding.tvVersion.text = "版本 1.0.0"
        binding.tvAbout.setOnClickListener {
            // Show about dialog
        }
    }

    private fun loadSettings() {
        val passcodeEnabled = encryptedPrefs.getBoolean("passcode_enabled", false)
        binding.switchPasscode.isChecked = passcodeEnabled
        binding.btnChangePasscode.isEnabled = passcodeEnabled
    }

    private fun enablePasscodeProtection() {
        val dialog = PasscodeDialog.newInstance(PasscodeDialog.Mode.SETUP) {
            // Passcode setup successful
            binding.btnChangePasscode.isEnabled = true
            Toast.makeText(this, "密码保护已启用", Toast.LENGTH_SHORT).show()
        }
        dialog.show(supportFragmentManager, "SetupPasscodeDialog")
    }

    private fun disablePasscodeProtection() {
        val dialog = PasscodeDialog.newInstance(PasscodeDialog.Mode.VERIFY) {
            // Passcode verified, disable protection
            encryptedPrefs.edit()
                .putBoolean("passcode_enabled", false)
                .remove("user_passcode")
                .apply()

            binding.btnChangePasscode.isEnabled = false
            Toast.makeText(this, "密码保护已禁用", Toast.LENGTH_SHORT).show()
        }
        dialog.show(supportFragmentManager, "DisablePasscodeDialog")
    }

    private fun changePasscode() {
        val dialog = PasscodeDialog.newInstance(PasscodeDialog.Mode.CHANGE)
        dialog.show(supportFragmentManager, "ChangePasscodeDialog")
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
