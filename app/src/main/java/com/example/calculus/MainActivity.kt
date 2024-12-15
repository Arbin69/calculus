package com.example.calculus

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    lateinit var workingTv : TextView
    lateinit var resultTv : TextView

    private var canAddOperation = false
    private var canAddDecimal = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        workingTv = findViewById(R.id.workingsTv)
        resultTv = findViewById(R.id.resultsTv)
    }

    fun numberAction(view : View) {
        if (view is Button) {
            if (view.text == ".") {
                if (canAddDecimal)
                    workingTv.append(view.text)
                canAddDecimal = false
            } else {
                workingTv.append(view.text)
            }
            canAddOperation = true
        }
    }

    fun operatorAction(view : View) {
        if (view is Button && canAddOperation) {
            workingTv.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }

    fun allClearAction(view : View) {
        workingTv.text = ""
        resultTv.text = ""
    }

    fun backSpaceAction(view : View) {
        val length = workingTv.length()
        if (length > 0) {
            workingTv.text = workingTv.text.subSequence(0, length - 1)
        }
    }

    fun equalsAction(view : View) {
        resultTv.text = calculateResults()
    }

    private fun calculateResults(): String {
        val digitsOperators = digitsOperators()
        if (digitsOperators.isEmpty()) return ""

        val timesDivisionResult = handleMultiplicationAndDivision(digitsOperators)
        if (timesDivisionResult.isEmpty()) return ""

        val result = handleAdditionAndSubtraction(timesDivisionResult)
        return result.toString()
    }

    private fun digitsOperators() : MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigit = ""

        for (character in workingTv.text) {
            if (character.isDigit() || character == '.') {
                currentDigit += character
            } else {
                if (currentDigit.isNotEmpty()) {
                    list.add(currentDigit.toFloat())
                }
                currentDigit = ""
                list.add(character)
            }
        }

        if (currentDigit.isNotEmpty()) {
            list.add(currentDigit.toFloat())
        }

        return list
    }

    private fun handleMultiplicationAndDivision(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while (list.contains('x') || list.contains('/')) {
            list = calcTimesDiv(list)
        }
        return list
    }

    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex) {
                val operator = passedList[i]
                val prev = passedList[i - 1] as Float
                val next = passedList[i + 1] as Float

                when (operator) {
                    'x' -> {
                        newList.add(prev * next)
                        restartIndex = i + 1
                    }
                    '/' -> {
                        newList.add(prev / next)
                        restartIndex = i + 1
                    }
                    else -> {
                        newList.add(prev)
                        newList.add(operator)
                    }
                }
            } else if (i > restartIndex) {
                newList.add(passedList[i])
            }
        }

        return newList
    }

    private fun handleAdditionAndSubtraction(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex) {
                val operator = passedList[i]
                val next = passedList[i + 1] as Float

                when (operator) {
                    '+' -> result += next
                    '-' -> result -= next
                }
            }
        }

        return result
    }
}
