package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Customer
import com.example.ui.components.AddCustomerDialog
import com.example.ui.components.PaymentSettlementDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.StoreViewModel

@Composable
fun AccountsScreen(
    viewModel: StoreViewModel
) {
    val customers by viewModel.customers.collectAsState()
    val totalDebt = customers.sumOf { it.debt }

    var showAddCustomerDialog by remember { mutableStateOf(false) }
    var selectedCustomerForSettle by remember { mutableStateOf<Customer?>(null) }

    val registeredCustomers = remember(customers) {
        customers.filter { it.id != "cash" }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("accounts_screen"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Screen Header
        item {
            Surface(
                color = DarkBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, LuxuryGold, RoundedCornerShape(12.dp))
                            .background(LuxuryGoldSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${registeredCustomers.size}",
                            color = LuxuryGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "الحسابات والديون",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = LuxuryGold
                        )
                        Text(
                            text = "متابعة حسابات العملاء ومستحقات الآجل",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        // Total Debt Banner Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryGoldBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(LuxuryGoldSurface)
                                .border(1.dp, LuxuryGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = LuxuryGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "إجمالي الديون القائمة",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "${"%,.2f".format(totalDebt)} ريال",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = LuxuryGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { showAddCustomerDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LuxuryGold,
                            contentColor = TextDark
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("add_customer_screen_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = TextDark, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "إضافة عميل أو حساب جديد", fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextDark)
                    }
                }
            }
        }

        // Section Title
        item {
            Text(
                text = "قائمة حسابات العملاء",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = LuxuryGoldLight,
                textAlign = TextAlign.Right,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 4.dp)
            )
        }

        // Customer Cards
        items(registeredCustomers) { customer ->
            Surface(
                color = DarkCardBackground,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262626)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp)
                    .clickable { selectedCustomerForSettle = customer }
                    .testTag("customer_row_${customer.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Surface(
                            color = if (customer.debt > 0) ErrorContainer else LuxuryGoldSurface,
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, if (customer.debt > 0) ErrorRed else LuxuryGold),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (customer.debt > 0) "${"%,.2f".format(customer.debt)} ريال" else "خالص",
                                color = if (customer.debt > 0) ErrorRed else LuxuryGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        if (customer.debt > 0) {
                            TextButton(
                                onClick = { selectedCustomerForSettle = customer },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text(
                                    text = "سداد دفعة",
                                    color = LuxuryGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = customer.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            if (customer.phone.isNotBlank() && customer.phone != "-") {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Text(
                                        text = customer.phone,
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = LuxuryGold,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, LuxuryGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .background(DarkElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = customer.name.take(1),
                                color = LuxuryGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Tip Card
        item {
            Surface(
                color = LuxuryGoldSurface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "إدارة أوضح للحسابات",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = LuxuryGold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "التطبيق يفصل الحسابات عن شاشة البيع حتى لا تتكدس الإجراءات في مسار واحد.",
                            fontSize = 11.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Right,
                            lineHeight = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = LuxuryGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    if (showAddCustomerDialog) {
        AddCustomerDialog(
            onDismiss = { showAddCustomerDialog = false },
            onConfirm = { name, phone, debt ->
                viewModel.addNewCustomer(name, phone, debt)
            }
        )
    }

    selectedCustomerForSettle?.let { cust ->
        PaymentSettlementDialog(
            customer = cust,
            onDismiss = { selectedCustomerForSettle = null },
            onSettle = { amount ->
                viewModel.settleDebt(cust.id, amount)
                selectedCustomerForSettle = null
            }
        )
    }
}
