package com.example.passwordmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.passwordmanager.data.db.AppDatabaseHelper
import com.google.android.material.switchmaterial.SwitchMaterial

class Settings : Fragment() {

    private lateinit var dbHelper: AppDatabaseHelper

    private lateinit var tvSubLanguage: TextView
    private lateinit var tvSubTheme: TextView
    private lateinit var tvSubStartup: TextView
    private lateinit var switchBudget: SwitchMaterial
    private lateinit var switchPasscode: SwitchMaterial
    private lateinit var switchNotifications: SwitchMaterial
    private lateinit var tvSubCurrency: TextView
    private lateinit var tvSubCurrencyFormat: TextView
    private lateinit var tvSubFirstDayWeek: TextView
    private lateinit var tvSubFirstDayMonth: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        dbHelper = AppDatabaseHelper(requireContext())

        val ivSettingsBack = view.findViewById<ImageView>(R.id.ivSettingsBack)
        ivSettingsBack.setOnClickListener {
            (activity as? MainActivity)?.selectHomeTab()
        }

        tvSubLanguage = view.findViewById(R.id.tvSubLanguage)
        tvSubTheme = view.findViewById(R.id.tvSubTheme)
        tvSubStartup = view.findViewById(R.id.tvSubStartup)
        switchBudget = view.findViewById(R.id.switchBudget)
        switchPasscode = view.findViewById(R.id.switchPasscode)
        switchNotifications = view.findViewById(R.id.switchNotifications)
        tvSubCurrency = view.findViewById(R.id.tvSubCurrency)
        tvSubCurrencyFormat = view.findViewById(R.id.tvSubCurrencyFormat)
        tvSubFirstDayWeek = view.findViewById(R.id.tvSubFirstDayWeek)
        tvSubFirstDayMonth = view.findViewById(R.id.tvSubFirstDayMonth)

        // Setup click listeners for options
        view.findViewById<LinearLayout>(R.id.itemLanguage).setOnClickListener {
            showChoiceDialog("Language", arrayOf("Default", "English", "Spanish", "Hindi"), tvSubLanguage, "setting_language")
        }

        view.findViewById<LinearLayout>(R.id.itemTheme).setOnClickListener {
            showChoiceDialog("Theme", arrayOf("Dark", "Light", "System"), tvSubTheme, "setting_theme")
        }

        view.findViewById<LinearLayout>(R.id.itemStartup).setOnClickListener {
            showChoiceDialog("Startup screen", arrayOf("Categories", "Transactions", "Home"), tvSubStartup, "setting_startup")
        }

        view.findViewById<LinearLayout>(R.id.itemCurrency).setOnClickListener {
            showChoiceDialog("Default currency", arrayOf("Indian rupee - \u20B9", "US Dollar - $", "Euro - \u20AC", "Pound - \u00A3"), tvSubCurrency, "setting_currency")
        }

        view.findViewById<LinearLayout>(R.id.itemCurrencyFormat).setOnClickListener {
            showChoiceDialog("Currency format", arrayOf("-\u20B9 1,234,567.90", "-\u20B9 1.234.567,90", "-\u20B9 1234567.90"), tvSubCurrencyFormat, "setting_currency_format")
        }

        view.findViewById<LinearLayout>(R.id.itemFirstDayWeek).setOnClickListener {
            showChoiceDialog("First day of week", arrayOf("Monday", "Sunday", "Saturday"), tvSubFirstDayWeek, "setting_first_day_week")
        }

        view.findViewById<LinearLayout>(R.id.itemFirstDayMonth).setOnClickListener {
            showChoiceDialog("First day of month", arrayOf("1", "15", "30"), tvSubFirstDayMonth, "setting_first_day_month")
        }

        // Setup switch listeners
        switchBudget.setOnCheckedChangeListener { _, isChecked ->
            dbHelper.saveSetting("setting_budget", isChecked.toString())
            Toast.makeText(requireContext(), "Budget ${if (isChecked) "Enabled" else "Disabled"}", Toast.LENGTH_SHORT).show()
        }

        switchPasscode.setOnCheckedChangeListener { _, isChecked ->
            dbHelper.saveSetting("setting_passcode", isChecked.toString())
            Toast.makeText(requireContext(), "Passcode ${if (isChecked) "Enabled" else "Disabled"}", Toast.LENGTH_SHORT).show()
        }

        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            dbHelper.saveSetting("setting_notifications", isChecked.toString())
            Toast.makeText(requireContext(), "Notifications ${if (isChecked) "Enabled" else "Disabled"}", Toast.LENGTH_SHORT).show()
        }

        loadSavedSettings()
        return view
    }

    private fun loadSavedSettings() {
        tvSubLanguage.text = dbHelper.getSetting("setting_language", "Default")
        tvSubTheme.text = dbHelper.getSetting("setting_theme", "Dark")
        tvSubStartup.text = dbHelper.getSetting("setting_startup", "Categories")
        tvSubCurrency.text = dbHelper.getSetting("setting_currency", "Indian rupee - \u20B9")
        tvSubCurrencyFormat.text = dbHelper.getSetting("setting_currency_format", "-\u20B9 1,234,567.90")
        tvSubFirstDayWeek.text = dbHelper.getSetting("setting_first_day_week", "Monday")
        tvSubFirstDayMonth.text = dbHelper.getSetting("setting_first_day_month", "1")

        switchBudget.isChecked = dbHelper.getSetting("setting_budget", "true").toBoolean()
        switchPasscode.isChecked = dbHelper.getSetting("setting_passcode", "false").toBoolean()
        switchNotifications.isChecked = dbHelper.getSetting("setting_notifications", "true").toBoolean()
    }

    private fun showChoiceDialog(title: String, options: Array<String>, targetTextView: TextView, key: String) {
        val current = targetTextView.text.toString()
        val checkedItem = options.indexOf(current).takeIf { it >= 0 } ?: 0

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setSingleChoiceItems(options, checkedItem) { dialog, which ->
                val selected = options[which]
                targetTextView.text = selected
                dbHelper.saveSetting(key, selected)
                Toast.makeText(requireContext(), "$title set to $selected", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}