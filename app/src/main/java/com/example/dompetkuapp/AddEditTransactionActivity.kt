package com.example.dompetkuapp

import android.R
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dompetkuapp.database.DatabaseHelper
import com.example.dompetkuapp.database.TransactionModel
import com.example.dompetkuapp.databinding.ActivityAddEditTransactionBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddEditTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditTransactionBinding
    private lateinit var dbHelper: DatabaseHelper
    private var transactionId: Int = -1
    private var isEditMode = false

    private val categories = arrayOf("Makanan", "Transportasi", "Belanja", "Hiburan", "Tagihan", "Gaji", "Lainnya")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        setupToolbar()
        setupDropdown()
        setupDatePicker()
        setupQuickAddChips()

        // Check if editing existing transaction
        val existingTransaction = intent.getSerializableExtra("TRANSACTION_EXTRA") as? TransactionModel
        if (existingTransaction != null) {
            isEditMode = true
            transactionId = existingTransaction.id
            binding.toolbar.title = "Edit Transaksi"
            binding.btnSave.text = "Perbarui Transaksi"

            binding.etTitle.setText(existingTransaction.title)
            binding.etAmount.setText(existingTransaction.amount.toInt().toString())
            binding.etDate.setText(existingTransaction.date)

            if (existingTransaction.type == "PEMASUKAN") {
                binding.rbIncome.isChecked = true
            } else {
                binding.rbExpense.isChecked = true
            }

            if (existingTransaction.category.isNotEmpty()) {
                binding.actCategory.setText(existingTransaction.category, false)
            }
        } else {
            // Create mode: set today's date
            val currentDate = SimpleDateFormat("dd MMM yyyy", Locale("in", "ID")).format(Calendar.getInstance().time)
            binding.etDate.setText(currentDate)
        }

        binding.btnSave.setOnClickListener {
            saveTransaction()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupDropdown() {
        val adapter = ArrayAdapter(this, R.layout.simple_dropdown_item_1line, categories)
        binding.actCategory.setAdapter(adapter)
    }

    private fun setupDatePicker() {
        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("in", "ID"))
                    binding.etDate.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupQuickAddChips() {
        binding.chip10k.setOnClickListener { incrementAmount(10000.0) }
        binding.chip50k.setOnClickListener { incrementAmount(50000.0) }
        binding.chip100k.setOnClickListener { incrementAmount(100000.0) }
        binding.chip500k.setOnClickListener { incrementAmount(500000.0) }
    }

    private fun incrementAmount(value: Double) {
        val currentText = binding.etAmount.text.toString()
        val currentAmount = currentText.toDoubleOrNull() ?: 0.0
        val newAmount = currentAmount + value
        binding.etAmount.setText(newAmount.toInt().toString())
    }

    private fun saveTransaction() {
        val title = binding.etTitle.text.toString().trim()
        val amountStr = binding.etAmount.text.toString().trim()
        val date = binding.etDate.text.toString().trim()
        val category = binding.actCategory.text.toString().trim()
        if (category.isEmpty()) {
            binding.tilCategory.error = "Kategori tidak boleh kosong"
            return
        } else {
            binding.tilCategory.error = null
        }
        val type = if (binding.rbIncome.isChecked) "PEMASUKAN" else "PENGELUARAN"

        if (title.isEmpty()) {
            binding.tilTitle.error = "Judul tidak boleh kosong"
            return
        } else {
            binding.tilTitle.error = null
        }

        if (amountStr.isEmpty()) {
            binding.tilAmount.error = "Nominal tidak boleh kosong"
            return
        }

        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            binding.tilAmount.error = "Nominal harus lebih besar dari 0"
            return
        } else {
            binding.tilAmount.error = null
        }

        val transaction = TransactionModel(
            id = if (isEditMode) transactionId else 0,
            title = title,
            amount = amount,
            type = type,
            category = category,
            date = date
        )

        if (isEditMode) {
            dbHelper.updateTransaction(transaction)
            Toast.makeText(this, "Transaksi diperbarui", Toast.LENGTH_SHORT).show()
        } else {
            dbHelper.insertTransaction(transaction)
            Toast.makeText(this, "Transaksi berhasil disimpan", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}
