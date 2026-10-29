package com.example.budgetmanager.ui

import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.budgetmanager.R
import com.example.budgetmanager.data.db.AppDatabaseHelper
import com.example.budgetmanager.data.model.TransactionModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import android.R as AndroidR

object AddExpenseBottomSheet {

    val categories = listOf(
        "Food", "Leisure", "College", "Transport", "Gifts",
        "Family", "Shopping", "Laundry", "Misc", "Health"
    )

    val paymentMethods = listOf("Card", "Cash", "Bank", "UPI")

    fun show(
        context: Context,
        dbHelper: AppDatabaseHelper,
        defaultCategory: String = "Food",
        year: Int = 2026,
        month: Int = 10,
        onSaved: () -> Unit
    ) {
        val dialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_expense, null)
        dialog.setContentView(view)

        val etAmount = view.findViewById<TextInputEditText>(R.id.etAmount)
        val etTitle = view.findViewById<TextInputEditText>(R.id.etTitle)
        val spinnerCategory = view.findViewById<Spinner>(R.id.spinnerCategory)
        val spinnerPaymentMethod = view.findViewById<Spinner>(R.id.spinnerPaymentMethod)
        val btnCancel = view.findViewById<MaterialButton>(R.id.btnCancel)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)

        val catAdapter = ArrayAdapter(context, AndroidR.layout.simple_spinner_dropdown_item, categories)
        spinnerCategory.adapter = catAdapter
        val initialCatIdx = categories.indexOf(defaultCategory).takeIf { it >= 0 } ?: 0
        spinnerCategory.setSelection(initialCatIdx)

        val payAdapter = ArrayAdapter(context, AndroidR.layout.simple_spinner_dropdown_item, paymentMethods)
        spinnerPaymentMethod.adapter = payAdapter

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            val amountStr = etAmount.text.toString().trim()
            val titleStr = etTitle.text.toString().trim()
            val category = spinnerCategory.selectedItem?.toString() ?: "Food"
            val paymentMethod = spinnerPaymentMethod.selectedItem?.toString() ?: "Card"

            if (amountStr.isEmpty()) {
                Toast.makeText(context, "Please enter an amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull() ?: 0.0
            val title = if (titleStr.isEmpty()) category else titleStr

            val transaction = TransactionModel(
                title = title,
                category = category,
                amount = amount,
                type = "EXPENSE",
                paymentMethod = paymentMethod,
                year = year,
                month = month,
                day = 3
            )

            dbHelper.addTransaction(transaction)
            Toast.makeText(context, "Expense saved!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            onSaved()
        }

        dialog.show()
    }
}