package com.ilshatcompany.giicalculatorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilshatcompany.giicalculatorapp.ui.theme.GIICalculatorAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GIICalculatorAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalculatorScreen()
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen() {
    var result by remember { mutableStateOf("0") }
    var input by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = result,
            fontSize = 36.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        val buttons = listOf(
            listOf("7", "8", "9", "/"),
            listOf("4", "5", "6", "*"),
            listOf("1", "2", "3", "-"),
            listOf("C", "0", "=", "+")
        )

        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { label ->
                    CalculatorButton(label) {
                        when (label) {
                            "=" -> {
                                result = calculateResult(input)
                                input = ""
                            }
                            "C" -> {
                                input = ""
                                result = "0"
                            }
                            else -> {
                                input += label
                                result = input
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun CalculatorButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(80.dp)
            .background(Color.LightGray)
    ) {
        Text(text = label, fontSize = 24.sp)
    }
}

fun calculateResult(input: String): String {
    return try {
        val result = eval(input)
        if (result.isNaN()) "Ошибка" else result.toString()
    } catch (e: Exception) {
        "Ошибка"
    }
}


fun eval(expression: String): Double {
    try {
        // Удаляем пробелы и проверяем на пустое выражение
        val cleanExpression = expression.replace(" ", "")
        if (cleanExpression.isEmpty()) return 0.0

        // Создаем списки для чисел и операторов
        val numbers = mutableListOf<Double>()
        val operators = mutableListOf<Char>()

        var index = 0
        while (index < cleanExpression.length) {
            var currentChar = cleanExpression[index]

            // Проверяем, является ли текущий символ оператором
            if (currentChar in "+-*/") {
                operators.add(currentChar)
                index++
            } else {
                // Ищем следующее число
                val start = index
                while (index < cleanExpression.length && (cleanExpression[index].isDigit() || cleanExpression[index] == '.')) {
                    index++
                }
                val number = cleanExpression.substring(start, index).toDoubleOrNull() ?: return Double.NaN
                numbers.add(number)
            }
        }

        // Выполняем операции * и / сначала
        var i = 0
        while (i < operators.size) {
            if (operators[i] == '*' || operators[i] == '/') {
                val left = numbers[i]
                val right = numbers.removeAt(i + 1)
                val op = operators.removeAt(i)

                val result = if (op == '*') left * right else left / right
                numbers[i] = result
            } else {
                i++
            }
        }

        // Выполняем операции + и -
        i = 0
        while (i < operators.size) {
            val left = numbers[i]
            val right = numbers.removeAt(i + 1)
            val op = operators.removeAt(i)

            val result = if (op == '+') left + right else left - right
            numbers[i] = result
        }

        // Возвращаем итоговый результат
        return numbers.firstOrNull() ?: Double.NaN
    } catch (e: Exception) {
        return Double.NaN
    }
}

