package com.example.dompetkuapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dompetkuapp.database.TransactionModel
import com.example.dompetkuapp.databinding.ItemTransactionBinding
import java.text.NumberFormat
import java.util.Locale

class TransactionAdapter(
    private var transactions: List<TransactionModel>,
    private val onEditClick: (TransactionModel) -> Unit,
    private val onDeleteClick: (TransactionModel) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    fun updateData(newTransactions: List<TransactionModel>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int = transactions.size

    inner class TransactionViewHolder(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: TransactionModel) {
            binding.tvTitle.text = transaction.title
            binding.tvCategoryAndDate.text = "${transaction.category} • ${transaction.date}"

            val isIncome = transaction.type == "PEMASUKAN"
            if (isIncome) {
                binding.tvAmount.text = "+ ${formatRupiah(transaction.amount)}"
                binding.tvAmount.setTextColor(itemView.context.getColor(R.color.income_green))
                binding.tvCategoryBadge.text = transaction.category.take(1).uppercase()
                binding.tvCategoryBadge.setTextColor(itemView.context.getColor(R.color.income_green))
                binding.tvCategoryBadge.setBackgroundResource(R.drawable.bg_pill_income)
            } else {
                binding.tvAmount.text = "- ${formatRupiah(transaction.amount)}"
                binding.tvAmount.setTextColor(itemView.context.getColor(R.color.expense_red))
                binding.tvCategoryBadge.text = transaction.category.take(1).uppercase()
                binding.tvCategoryBadge.setTextColor(itemView.context.getColor(R.color.expense_red))
                binding.tvCategoryBadge.setBackgroundResource(R.drawable.bg_pill_expense)
            }

            binding.btnEdit.setOnClickListener {
                onEditClick(transaction)
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(transaction)
            }
        }
    }

    companion object {
        fun formatRupiah(amount: Double): String {
            val localeID = Locale("in", "ID")
            val numberFormat = NumberFormat.getCurrencyInstance(localeID)
            numberFormat.maximumFractionDigits = 0
            return numberFormat.format(amount).replace("Rp", "Rp ")
        }
    }
}
