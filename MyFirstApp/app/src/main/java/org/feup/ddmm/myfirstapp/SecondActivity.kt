package org.feup.ddmm.myfirstapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var message = intent.getStringExtra(FirstActivity.EXTRA_MESSAGE)
        var textView = TextView(this)
        textView.textSize = 30f
        textView.text = message

        setContentView(textView)
    }
}
