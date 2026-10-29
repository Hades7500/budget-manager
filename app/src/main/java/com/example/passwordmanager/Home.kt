package com.example.passwordmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.passwordmanager.data.db.AppDatabaseHelper
import com.example.passwordmanager.ui.AddExpenseBottomSheet

class Home : Fragment() {

    private lateinit var dbHelper: AppDatabaseHelper
    private var currentYear = 2026
    private var currentMonth = 10 // October

    private val monthNames = arrayOf(
        "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
        "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
    )

    private lateinit var tvTotalHeaderBalance: TextView
    private lateinit var tvMonthText: TextView
    private lateinit var tvDonutExpenses: TextView

    // Category Views & Amounts
    private val categoryViewsMap = mutableMapOf<String, Pair<LinearLayout, TextView>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        dbHelper = AppDatabaseHelper(requireContext())

        // Top Header
        val llHeaderTitle = view.findViewById<LinearLayout>(R.id.llHeaderTitle)
        tvTotalHeaderBalance = llHeaderTitle.getChildAt(1) as TextView

        // Month Selector
        tvMonthText = view.findViewById<LinearLayout>(R.id.llDatePill).getChildAt(1) as TextView
        val ivPrevMonth = view.findViewById<ImageView>(R.id.ivPrevMonth)
        val ivNextMonth = view.findViewById<ImageView>(R.id.ivNextMonth)

        // Donut Chart
        val flDonutContainer = view.findViewById<FrameLayout>(R.id.flDonutContainer)
        val llDonutContent = flDonutContainer.getChildAt(0) as LinearLayout
        tvDonutExpenses = llDonutContent.getChildAt(1) as TextView

        ivPrevMonth.setOnClickListener {
            changeMonth(-1)
        }

        ivNextMonth.setOnClickListener {
            changeMonth(1)
        }

        // Map categories
        mapCategoryView("Leisure", view.findViewById(R.id.catLeisure))
        mapCategoryView("Food", view.findViewById(R.id.catFood))
        mapCategoryView("College", view.findViewById(R.id.catCollege))
        mapCategoryView("Transport", view.findViewById(R.id.catTransport))
        mapCategoryView("Gifts", view.findViewById(R.id.catGifts))
        mapCategoryView("Family", view.findViewById(R.id.catFamily))
        mapCategoryView("Shopping", view.findViewById(R.id.catShopping))
        mapCategoryView("Laundry", view.findViewById(R.id.catLaundry))
        mapCategoryView("Misc", view.findViewById(R.id.catMisc))
        mapCategoryView("Health", view.findViewById(R.id.catHealth))

        refreshData()
        return view
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun mapCategoryView(categoryName: String, container: LinearLayout) {
        // Third child is ImageView, Fourth child is TextView for amount
        val tvAmount = container.getChildAt(3) as TextView
        categoryViewsMap[categoryName] = Pair(container, tvAmount)

        container.setOnClickListener {
            openAddExpenseDialog(categoryName)
        }
    }

    private fun openAddExpenseDialog(defaultCategory: String) {
        AddExpenseBottomSheet.show(
            context = requireContext(),
            dbHelper = dbHelper,
            defaultCategory = defaultCategory,
            year = currentYear,
            month = currentMonth,
            onSaved = {
                refreshData()
            }
        )
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

    private fun refreshData() {
        tvMonthText.text = "${monthNames[currentMonth - 1]} $currentYear"

        var monthTotalExpense = 0.0

        for ((catName, pair) in categoryViewsMap) {
            val tvAmount = pair.second
            val catTotal = dbHelper.getTotalExpenseForCategoryAndMonth(catName, currentYear, currentMonth)
            monthTotalExpense += catTotal
            tvAmount.text = "\u20B9 ${catTotal.toInt()}"
        }

        val totalMonthDbExpense = dbHelper.getTotalExpenseForMonth(currentYear, currentMonth)
        tvDonutExpenses.text = "\u20B9 ${totalMonthDbExpense.toInt()}"

        val endingBalance = dbHelper.getEndingBalance(currentYear, currentMonth)
        tvTotalHeaderBalance.text = "-\u20B9 ${Math.abs(endingBalance).toInt()}"
    }
}