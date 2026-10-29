package com.example.budgetmanager.ui

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetmanager.R
import com.example.budgetmanager.data.model.TransactionModel

class TransactionAdapter(
    private var transactions: List<TransactionModel>,
    private val onItemLongClick: (TransactionModel) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcon: ImageView = itemView.findViewById(R.id.ivCategoryIcon)
        val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
        val tvPaymentMethod: TextView = itemView.findViewById(R.id.tvPaymentMethod)
        val tvTitleNote: TextView = itemView.findViewById(R.id.tvTitleNote)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = transactions[position]

        holder.tvCategoryName.text = item.category
        holder.tvPaymentMethod.text = item.paymentMethod
        holder.tvTitleNote.text = item.title
        holder.tvAmount.text = "\u20B9 ${item.amount.toInt()}"

        val (iconRes, bgTint) = getCategoryIconAndColor(item.category)
        holder.ivIcon.setImageResource(iconRes)
        holder.ivIcon.backgroundTintList = ColorStateList.valueOf(bgTint)

        holder.itemView.setOnLongClickListener {
            onItemLongClick(item)
            true
        }
    }

    override fun getItemCount(): Int = transactions.size

    fun updateList(newList: List<TransactionModel>) {
        transactions = newList
        notifyDataSetChanged()
    }

    private fun getCategoryIconAndColor(category: String): Pair<Int, Int> {
        return when (category.lowercase()) {
            "food" -> Pair(R.drawable.ic_food, 0xFF2D55FF.toInt())
            "leisure" -> Pair(R.drawable.ic_leisure, 0xFF3D1B28.toInt())
            "college" -> Pair(R.drawable.ic_college, 0xFF1E2B40.toInt())
            "transport" -> Pair(R.drawable.ic_transport, 0xFF3C3218.toInt())
            "gifts" -> Pair(R.drawable.ic_gift, 0xFF3D1B28.toInt())
            "family" -> Pair(R.drawable.ic_family, 0xFF271F42.toInt())
            "shopping" -> Pair(R.drawable.ic_shopping, 0xFF2D282A.toInt())
            "laundry" -> Pair(R.drawable.ic_laundry, 0xFF1A3025.toInt())
            "misc" -> Pair(R.drawable.ic_misc, 0xFF3D1B28.toInt())
            "health" -> Pair(R.drawable.ic_health, 0xFF1A3025.toInt())
            else -> Pair(R.drawable.ic_food, 0xFF2D55FF.toInt())
        }
    }
}