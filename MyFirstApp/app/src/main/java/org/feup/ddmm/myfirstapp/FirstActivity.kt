package org.feup.ddmm.myfirstapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class FirstActivity : AppCompatActivity() {

    companion object {
        val EXTRA_MESSAGE: String = "ddmm.MESSAGE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)

        findViewById<Button>(R.id.button_send).setOnClickListener {
            var intent = Intent(this, SecondActivity::class.java)
            val editText = findViewById<EditText>(R.id.edit_message).text.toString()
            intent.putExtra(EXTRA_MESSAGE, editText)
            startActivity(intent)
        }
    }
}
