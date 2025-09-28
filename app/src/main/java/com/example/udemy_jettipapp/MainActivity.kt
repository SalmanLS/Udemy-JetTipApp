package com.example.udemy_jettipapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.udemy_jettipapp.components.InputField
import com.example.udemy_jettipapp.ui.theme.UdemyJetTipAppTheme
import com.example.udemy_jettipapp.util.calculateTotalPerPerson
import com.example.udemy_jettipapp.util.calculateTotalTip
import com.example.udemy_jettipapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UdemyJetTipAppTheme {
                MyApp {
                    TipCalculatorApp()
                }
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.padding(top = 36.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        content()
    }
}

@Composable
fun TipCalculatorApp() {
    var billAmountText by remember { mutableStateOf("") }
    var splitCount by remember { mutableStateOf(1) }
    var tipPercentage by remember { mutableStateOf(0f) }
    val billAmount by remember(billAmountText) {
        derivedStateOf { billAmountText.toDoubleOrNull() ?: 0.0 }
    }
    val isValidBill by remember(billAmount) {
        derivedStateOf { billAmount > 0.0 }
    }
    val tipAmount by remember(billAmount, tipPercentage) {
        derivedStateOf {
            if (isValidBill) calculateTotalTip(billAmount, (tipPercentage * 100).toInt())
            else 0.0
        }
    }
    val totalPerPerson by remember(billAmount, tipAmount, splitCount) {
        derivedStateOf {
            if (isValidBill) calculateTotalPerPerson(billAmount, splitCount, (tipPercentage * 100).toInt())
            else 0.0
        }
    }

    Column(modifier = Modifier.padding(horizontal = 12.dp)) {
        TopHeader(totalPerPerson = totalPerPerson)

        Spacer(modifier = Modifier.height(24.dp))

        BillForm(
            billAmountText = billAmountText,
            onBillAmountChange = { billAmountText = it },
            splitCount = splitCount,
            onSplitCountChange = { splitCount = it },
            tipPercentage = tipPercentage,
            onTipPercentageChange = { tipPercentage = it },
            tipAmount = tipAmount,
            isValidBill = isValidBill
        )
    }
}

@Composable
fun TopHeader(
    modifier: Modifier = Modifier,
    totalPerPerson: Double
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
        color = Color(0xFFA3D2C9)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(
                text = "Total Per Person",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = "$$total",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun BillForm(
    billAmountText: String,
    onBillAmountChange: (String) -> Unit,
    splitCount: Int,
    onSplitCountChange: (Int) -> Unit,
    tipPercentage: Float,
    onTipPercentageChange: (Float) -> Unit,
    tipAmount: Double,
    isValidBill: Boolean,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(
        modifier = modifier
            .padding(2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            BillInputField(
                billAmountText = billAmountText,
                onBillAmountChange = onBillAmountChange,
                onDone = {
                    keyboardController?.hide()
                }
            )

            if (isValidBill) {
                Spacer(modifier = Modifier.height(16.dp))

                SplitSection(
                    splitCount = splitCount,
                    onSplitCountChange = onSplitCountChange
                )

                Spacer(modifier = Modifier.height(12.dp))

                TipDisplaySection(tipAmount = tipAmount)

                Spacer(modifier = Modifier.height(12.dp))

                TipSliderSection(
                    tipPercentage = tipPercentage,
                    onTipPercentageChange = onTipPercentageChange
                )
            }
        }
    }
}

@Composable
fun BillInputField(
    billAmountText: String,
    onBillAmountChange: (String) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val billAmountState = remember { mutableStateOf(billAmountText) }
    billAmountState.value = billAmountText

    InputField(
        valueState = billAmountState,
        labelId = "Enter Bill",
        enabled = true,
        isSingleLine = true,
        onAction = KeyboardActions {
            onBillAmountChange(billAmountState.value.trim())
            onDone()
        }
    )
}

@Composable
fun SplitSection(
    splitCount: Int,
    onSplitCountChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Split",
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        Row {
            RoundIconButton(
                imageVector = Icons.Default.Remove,
                onClick = {
                    if (splitCount > 1) {
                        onSplitCountChange(splitCount - 1)
                    }
                }
            )

            Text(
                text = splitCount.toString(),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 8.dp)
            )

            RoundIconButton(
                imageVector = Icons.Default.Add,
                onClick = {
                    if (splitCount < 100) {
                        onSplitCountChange(splitCount + 1)
                    }
                }
            )
        }
    }
}

@Composable
fun TipDisplaySection(
    tipAmount: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Tip",
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        Text(
            text = "$${String.format("%.2f", tipAmount)}",
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun TipSliderSection(
    tipPercentage: Float,
    onTipPercentageChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "${(tipPercentage * 100).toInt()}%")

        Spacer(modifier = Modifier.height(12.dp))

        Slider(
            value = tipPercentage,
            onValueChange = onTipPercentageChange,
            modifier = Modifier.padding(horizontal = 16.dp),
            steps = 9,
            valueRange = 0f..1f
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TipCalculatorPreview() {
    UdemyJetTipAppTheme {
        MyApp {
            TipCalculatorApp()
        }
    }
}