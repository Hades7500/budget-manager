package com.example.budgetmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetmanager.R
import com.example.budgetmanager.data.db.AppDatabaseHelper
import com.example.budgetmanager.data.model.TransactionModel
import com.example.budgetmanager.ui.AddExpenseBottomSheet
import com.example.budgetmanager.ui.TransactionAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale
import kotlin.math.abs

class Transactions : Fragment() {

    private lateinit var dbHelper: AppDatabaseHelper
    private var currentYear = 2026
    private var currentMonth = 10 // October

    private val monthNames = arrayOf(
        "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
        "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER",
    )

    private lateinit var tvTxTotalBalance: TextView
    private lateinit var tvTxMonthText: TextView
    private lateinit var tvStartingBalance: TextView
    private lateinit var tvEndingBalance: TextView
    private lateinit var tvDayNum: TextView
    private lateinit var tvDaySub: TextView
    private lateinit var tvMonthSub: TextView
    private lateinit var tvDayTotalAmount: TextView
    private lateinit var rvTransactions: RecyclerView
    private lateinit var adapter: TransactionAdapter

    private var filterQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transactions, container, false)
        dbHelper = AppDatabaseHelper(requireContext())

        tvTxTotalBalance = view.findViewById(R.id.tvTxTotalBalance)
        tvTxMonthText = view.findViewById(R.id.tvTxMonthText)
        tvStartingBalance = view.findViewById(R.id.tvStartingBalance)
        tvEndingBalance = view.findViewById(R.id.tvEndingBalance)
        tvDayNum = view.findViewById(R.id.tvDayNum)
        tvDaySub = view.findViewById(R.id.tvDaySub)
        tvMonthSub = view.findViewById(R.id.tvMonthSub)
        tvDayTotalAmount = view.findViewById(R.id.tvDayTotalAmount)
        rvTransactions = view.findViewById(R.id.rvTransactions)

        val ivTxPrevMonth = view.findViewById<ImageView>(R.id.ivTxPrevMonth)
        val ivTxNextMonth = view.findViewById<ImageView>(R.id.ivTxNextMonth)
        val ivTxSearch = view.findViewById<ImageView>(R.id.ivTxSearch)
        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fabAdd)

        adapter = TransactionAdapter(emptyList()) { item ->
            confirmDeleteTransaction(item)
        }

        rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        rvTransactions.adapter = adapter

        ivTxPrevMonth.setOnClickListener { changeMonth(-1) }
        ivTxNextMonth.setOnClickListener { changeMonth(1) }

        fabAdd.setOnClickListener {
            AddExpenseBottomSheet.show(
                context = requireContext(),
                dbHelper = dbHelper,
                defaultCategory = "Food",
                year = currentYear,
                month = currentMonth,
            ) { refreshData() }
        }

        ivTxSearch.setOnClickListener {
            showSearchDialog()
        }

        refreshData()
        return view
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun changeMonth(delta: Int) {
        currentMonth += delta
        if (currentMonth > 12) {
            currentMonth = 1
            currentYear++
        } else if (currentMonth < 1) {
            currentMonth = 12
            currentYear--
        }
        refreshData()
    }

    private fun showSearchDialog() {
        val input = EditText(requireContext()).apply {
            hint = "Search by title or category..."
            setText(filterQuery)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Search Transactions")
            .setView(input)
            .setPositiveButton("Search") { _, _ ->
                filterQuery = input.text.toString().trim()
                refreshData()
            }
            .setNegativeButton("Clear") { _, _ ->
                filterQuery = ""
                refreshData()
            }
            .show()
    }

    private fun confirmDeleteTransaction(item: TransactionModel) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete '${item.title}' (\u20B9${item.amount.toInt()})?")
            .setPositiveButton("Delete") { _, _ ->
                dbHelper.deleteTransaction(item.id)
                Toast.makeText(requireContext(), "Transaction deleted", Toast.LENGTH_SHORT).show()
                refreshData()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun refreshData() {
        tvTxMonthText.text = "${monthNames[currentMonth - 1]} $currentYear"
        tvMonthSub.text = "${monthNames[currentMonth - 1]} $currentYear"

        val allTxs = dbHelper.getTransactionsForMonth(currentYear, currentMonth)

        val filteredList = if (filterQuery.isEmpty()) {
            allTxs
        } else {
            allTxs.filter {
                it.title.contains(filterQuery, ignoreCase = true) ||
                it.category.contains(filterQuery, ignoreCase = true)
            }
        }

        adapter.updateList(filteredList)

        val monthExpense = dbHelper.getTotalExpenseForMonth(currentYear, currentMonth)
        tvDayTotalAmount.text = "\u20B9 ${monthExpense.toInt()}"

        val startBal = dbHelper.getStartingBalance(currentYear, currentMonth)
        val endBal = dbHelper.getEndingBalance(currentYear, currentMonth)

        tvStartingBalance.text = "-\u20B9 ${String.format(Locale.US, "%.2f", abs(startBal))}"
        tvEndingBalance.text = "-\u20B9 ${String.format(Locale.US, "%.2f", abs(endBal))}"
        tvTxTotalBalance.text = "-\u20B9 ${abs(endBal).toInt()}"
    }
}