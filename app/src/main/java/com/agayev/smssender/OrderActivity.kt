package com.agayev.smssender

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agayev.smssender.ui.theme.SmsSenderTheme

class OrderActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmsSenderTheme {
                val orderNumber: Int
                val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()

                if (sharedPreference.getString("orderNumber", "").isNullOrEmpty()) {
                    orderNumber = (10000..99999).random()
                    editor.putString("orderNumber", orderNumber.toString())
                    editor.apply()
                }
                OrderScreen(sharedPreference.getString("orderNumber", ""))
            }
        }
    }
}

@Composable
fun OrderScreen(orderNumber: String?) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier =
                Modifier
                    .padding(8.dp)
                    .shadow(5.dp, RoundedCornerShape(10.dp))
                    .padding(8.dp)
        ) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ваша заявка успешно принята.",
                    textAlign = TextAlign.Left,
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Номер вашей заявки - $orderNumber",
                    textAlign = TextAlign.Left,
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Статус заявки: В обработке.",
                    textAlign = TextAlign.Left,
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Для корректного зачисления выплаты не удаляйте приложение с вашего устройства до окончания обработки.",
                    textAlign = TextAlign.Left,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}