package org.feup.ddmm.lunchlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class MainActivity : AppCompatActivity() {
    val restaurants = ArrayList<Restaurant>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setIcon(R.drawable.rest_icon)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val btnSave = findViewById<Button>(R.id.button_save)
        btnSave.setOnClickListener {
            var editName = findViewById<EditText>(R.id.edit_name).text.toString()
            var editAddress = findViewById<EditText>(R.id.edit_address).text.toString()

            var radioTypeId = findViewById<RadioGroup>(R.id.radio_type).checkedRadioButtonId
            var radioType = findViewById<RadioButton>(radioTypeId).text.toString()

            var restaurant = Restaurant(name = editName, address = editAddress, type = radioType)
            restaurants.add(restaurant)

            Toast.makeText(this, restaurant.name + " " + restaurant.address + " " + restaurant.type, Toast.LENGTH_SHORT).show()
        }

    }
}
