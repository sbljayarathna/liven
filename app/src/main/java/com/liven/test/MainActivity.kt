package com.liven.test

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.liven.test.databinding.ActivityMainBinding
import java.text.DecimalFormat


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var selectedFoodItems = HashMap<FoodItem, Int>()
    var selectedItem: FoodItem? = null

    companion object {
        const val BIG_BREKKIE = "Big Brekkie"
        const val BRUSCHETTA = "Bruschetta"
        const val POACHED_EGGS = "Poached Eggs"
        const val COFFEE = "Coffee"
        const val TEA = "Tea"
        const val SODA = "Soda"
        const val GARDEN_SALAD = "Garden Salad"

        var items = arrayListOf(
            FoodItem(BIG_BREKKIE, 16),
            FoodItem(BRUSCHETTA, 8),
            FoodItem(POACHED_EGGS, 12),
            FoodItem(COFFEE, 5),
            FoodItem(TEA, 3),
            FoodItem(SODA, 4),
            FoodItem(GARDEN_SALAD, 10)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdd.setOnClickListener {
            onAddButtonClicked()
        }

        binding.btnClear.setOnClickListener {
            onClearButtonClicked()
        }

        binding.tvSelectItem.setOnClickListener {
            onSelectItemClicked()
        }

        binding.rgCashCard.setOnCheckedChangeListener { _, _ ->
            updateTotal()
        }

        binding.rgDiscount.setOnCheckedChangeListener { _, _ ->
            updateTotal()
        }

        binding.etDiscount.addTextChangedListener {
            updateTotal()
        }
    }

    private fun onClearButtonClicked() {
        selectedFoodItems.clear()
        selectedItem = null
        binding.tvSelectItem.text = ""
        binding.tvTotal.text = ""
        binding.tvItemList.text = ""
        binding.etCount.setText("")
        binding.etDiscount.setText("")
    }

    private fun onAddButtonClicked() {
        if (selectedItem != null && binding.etCount.text.isNotEmpty()) {
            selectedFoodItems[selectedItem!!] = binding.etCount.text.toString().toInt()

            var itemList = ""
            selectedFoodItems.forEach { (key, value) ->
                itemList += "#${key.name} - UnitPrice:$${key.price}   Count:$value   Subtotal:$${
                    currencyFormat(
                        (key.price * value).toString()
                    )
                }\n"
            }
            binding.tvItemList.text = itemList
            updateTotal()

            binding.tvSelectItem.text = ""
            binding.etCount.setText("")
        } else {
            Toast.makeText(this, "Please select an item and set item count.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun onSelectItemClicked() {
        val builderSingle: AlertDialog.Builder = AlertDialog.Builder(this)
        builderSingle.setTitle("Select Item")

        val arrayAdapter =
            ArrayAdapter<String>(this, android.R.layout.select_dialog_item)
        arrayAdapter.addAll(items.map { it.name })

        builderSingle.setNegativeButton(
            "cancel"
        ) { dialog, _ -> dialog.dismiss() }

        builderSingle.setAdapter(
            arrayAdapter
        ) { _, which ->
            selectedItem = items[which]
            binding.tvSelectItem.text = items[which].name
        }
        builderSingle.show()
    }

    private fun updateTotal() {
        var actualTotal = 0.0
        var newTotal = 0.0
        var surcharges = 0.0
        var discount = 0.0
        selectedFoodItems.forEach { (key, value) ->
            actualTotal += key.price * value
            newTotal += key.price * value
        }

        if (binding.rbCard.isChecked) {
            surcharges = actualTotal * 0.12
            newTotal += surcharges
        }

        if (binding.etDiscount.text.toString().isNotEmpty()) {
            if (binding.rbDiscountPercentage.isChecked) {
                discount = actualTotal * binding.etDiscount.text.toString().toDouble() / 100
                newTotal -= discount
            } else if (binding.rbDiscountAmount.isChecked) {
                discount = binding.etDiscount.text.toString().toDouble()
                newTotal -= discount
            }
        }

        binding.tvTotal.text =
            "Net amount  =  $${currencyFormat(actualTotal.toString())} \nSurcharges  =  +$${
                currencyFormat(surcharges.toString())
            } \nDiscount  =  -$${currencyFormat(discount.toString())} \nTotal  =  $${
                currencyFormat(
                    newTotal.toString()
                )
            }"

    }

    private fun currencyFormat(amount: String): String? {
        val formatter = DecimalFormat("###,###,##0.00")
        return formatter.format(amount.toDouble())
    }
}