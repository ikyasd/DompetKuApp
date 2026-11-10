package com.example.dompetkuapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dompetkuapp.database.DatabaseHelper
import com.example.dompetkuapp.database.TransactionModel
import com.example.dompetkuapp.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var transactionAdapter: TransactionAdapter
    private var allTransactions = listOf<TransactionModel>()
    private var selectedCategoryFilter = "Semua"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        setupRecyclerView()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(
            emptyList(),
            onEditClick = { transaction ->
                val intent = Intent(this, AddEditTransactionActivity::class.java).apply {
                    putExtra("TRANSACTION_EXTRA", transaction)
                }
                startActivity(intent)
            },
            onDeleteClick = { transaction ->
                MaterialAlertDialogBuilder(this)
                    .setTitle("Hapus Transaksi")
                    .setMessage("Apakah Anda yakin ingin menghapus transaksi \"${transaction.title}\"?")
                    .setPositiveButton("Hapus") { _, _ ->
                        dbHelper.deleteTransaction(transaction.id)
                        loadData()
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }
        )

        binding.recyclerViewTransactions.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = transactionAdapter
        }
    }

    private fun setupListeners() {
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddEditTransactionActivity::class.java)
            startActivity(intent)
        }

        binding.chipGroupCategory.setOnCheckedChangeListener { group, checkedId ->
            selectedCategoryFilter = when (checkedId) {
                R.id.chipFood -> "Makanan"
                R.id.chipTransport -> "Transportasi"
                R.id.chipSalary -> "Gaji"
                R.id.chipShopping -> "Belanja"
                else -> "Semua"
            }
            filterTransactions()
        }
    }

    private fun loadData() {
        allTransactions = dbHelper.getAllTransactions()
        filterTransactions()

        val totalIncome = dbHelper.getTotalIncome()
        val totalExpense = dbHelper.getTotalExpense()
        val netBalance = totalIncome - totalExpense

        binding.tvTotalIncome.text = "+ ${TransactionAdapter.formatRupiah(totalIncome)}"
        binding.tvTotalExpense.text = "- ${TransactionAdapter.formatRupiah(totalExpense)}"
        binding.tvNetBalance.text = TransactionAdapter.formatRupiah(netBalance)
    }

    private fun filterTransactions() {
        val filtered = if (selectedCategoryFilter == "Semua") {
            allTransactions
        } else {
            allTransactions.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
        }

        transactionAdapter.updateData(filtered)

        if (filtered.isEmpty()) {
            binding.recyclerViewTransactions.visibility = View.GONE
            binding.tvEmptyState.visibility = View.VISIBLE
        } else {
            binding.recyclerViewTransactions.visibility = View.VISIBLE
            binding.tvEmptyState.visibility = View.GONE
        }
    }
}
