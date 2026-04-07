package com.omartitouhi.calculatrice

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvDisplay: TextView
    private lateinit var llHistory: LinearLayout

    private var operand1: Double? = null
    private var pendingOperator: String? = null
    private var resetDisplay = false
    private var currentInput = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDisplay = findViewById(R.id.tvDisplay)
        llHistory = findViewById(R.id.llHistory)

        tvDisplay.text = "0"

        // === Listeners des boutons ===
        val digitButtons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        digitButtons.forEach { id ->
            findViewById<Button>(id).setOnClickListener { onDigitClick(it) }
        }

        findViewById<Button>(R.id.btnDot).setOnClickListener { onDotClick() }
        findViewById<Button>(R.id.btnPlus).setOnClickListener { onOperatorClick("+") }
        findViewById<Button>(R.id.btnMinus).setOnClickListener { onOperatorClick("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { onOperatorClick("*") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { onOperatorClick("/") }
        findViewById<Button>(R.id.btnEquals).setOnClickListener { onEqualsClick() }
        findViewById<Button>(R.id.btnAC).setOnClickListener { onClearClick() }
        findViewById<Button>(R.id.btnDEL).setOnClickListener { onDeleteClick() }
        findViewById<Button>(R.id.btnSign).setOnClickListener { onSignClick() }
        findViewById<Button>(R.id.btnPercent).setOnClickListener { onPercentClick() }
    }

    private fun onDigitClick(view: android.view.View) {
        val digit = (view as Button).text.toString()

        if (resetDisplay) {
            tvDisplay.text = digit
            resetDisplay = false
        } else {
            if (tvDisplay.text.toString() == "0" && digit != ".") {
                tvDisplay.text = digit
            } else {
                tvDisplay.append(digit)
            }
        }
    }

    private fun onDotClick() {
        if (resetDisplay) {
            tvDisplay.text = "0."
            resetDisplay = false
        } else if (!tvDisplay.text.contains(".")) {
            tvDisplay.append(".")
        }
    }

    private fun onOperatorClick(op: String) {
        val currentText = tvDisplay.text.toString()
        if (currentText.isEmpty()) return

        if (pendingOperator != null) {
            // Calcul intermédiaire pour chaînage
            val result = calculate(operand1!!, pendingOperator!!, currentText.toDouble())
            tvDisplay.text = formatResult(result)
            operand1 = result
        } else {
            operand1 = currentText.toDouble()
        }

        pendingOperator = op
        resetDisplay = true
    }

    private fun onEqualsClick() {
        if (pendingOperator == null || operand1 == null) return

        val currentText = tvDisplay.text.toString()
        if (currentText.isEmpty()) return

        val secondOperand = currentText.toDoubleOrNull() ?: 0.0
        val result = calculate(operand1!!, pendingOperator!!, secondOperand)

        // Ajout à l'historique
        val historyText = "${formatResult(operand1!!)} $pendingOperator $currentText = ${formatResult(result)}"
        addToHistory(historyText)

        tvDisplay.text = formatResult(result)
        operand1 = result
        pendingOperator = null
        resetDisplay = true
    }

    private fun calculate(first: Double, op: String, second: Double): Double {
        return when (op) {
            "+" -> first + second
            "-" -> first - second
            "*" -> first * second
            "/" -> {
                if (second == 0.0) {
                    Toast.makeText(this, "Division par zéro impossible !", Toast.LENGTH_SHORT).show()
                    0.0
                } else first / second
            }
            else -> first
        }
    }

    private fun formatResult(value: Double): String {
        return if (value % 1 == 0.0) value.toInt().toString() else value.toString()
    }

    private fun addToHistory(text: String) {
        val tv = TextView(this)
        tv.text = text
        tv.textSize = 18f
        tv.setPadding(8, 12, 8, 12)
        tv.setBackgroundColor(0xFFEEEEEE.toInt())
        llHistory.addView(tv)

        // Scroll automatique vers le bas
        (llHistory.parent as ScrollView).fullScroll(android.view.View.FOCUS_DOWN)
    }

    private fun onClearClick() {
        tvDisplay.text = "0"
        operand1 = null
        pendingOperator = null
        resetDisplay = false
    }

    private fun onDeleteClick() {
        var text = tvDisplay.text.toString()
        if (text.length <= 1 || (text == "0")) {
            tvDisplay.text = "0"
        } else {
            text = text.dropLast(1)
            tvDisplay.text = if (text.isEmpty()) "0" else text
        }
    }

    private fun onSignClick() {
        var text = tvDisplay.text.toString()
        if (text == "0") return
        if (text.startsWith("-")) {
            tvDisplay.text = text.drop(1)
        } else {
            tvDisplay.text = "-$text"
        }
    }

    private fun onPercentClick() {
        val value = tvDisplay.text.toString().toDoubleOrNull() ?: 0.0
        tvDisplay.text = formatResult(value / 100)
        resetDisplay = true
    }
}