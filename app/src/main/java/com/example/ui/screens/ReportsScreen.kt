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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SaleOrder
import com.example.ui.components.ReceiptDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.StoreViewModel

@Composable
fun ReportsScreen(
    viewModel: StoreViewModel
) {
    val orders by viewModel.orders.collectAsState()
    var selectedPeriod by remember { mutableStateOf("اليوم") }
    var selectedOrderForReceipt by remember { mutableStateOf<SaleOrder?>(null) }

    val baseSales = 4860.0
    val baseInvoices = 28
    val baseItems = 64

    val newOrdersTotal = orders.sumOf { it.total }
    val newOrdersCount = orders.size
    val newItemsCount = orders.sumOf { it.totalItemsCount }

    val totalSales = baseSales + newOrdersTotal
    val totalInvoices = baseInvoices + newOrdersCount
    val totalItems = baseItems + newItemsCount
    val averageInvoice = if (totalInvoices > 0) totalSales / totalInvoices else 0.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("reports_screen"),
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
                    Surface(
                        color = DarkElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262626)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(modifier = Modifier.padding(4.dp)) {
                            listOf("اليوم", "الأسبوع").forEach { period ->
                                val isSelected = selectedPeriod == period
                                Surface(
                                    color = if (isSelected) LuxuryGold else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.clickable { selectedPeriod = period }
                                ) {
                                    Text(
                                        text = period,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) TextDark else TextMuted,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "تقارير المبيعات",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = LuxuryGold
                        )
                        Text(
                            text = "نظرة سريعة على أداء المتجر والأرباح",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        // Stats 2x2 Grid
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ReportStatCard(
                        title = "مبيعات اليوم",
                        value = "${"%,.2f".format(totalSales)} ريال",
                        icon = Icons.Outlined.TrendingUp,
                        color = LuxuryGold,
                        modifier = Modifier.weight(1f)
                    )

                    ReportStatCard(
                        title = "عدد الفواتير",
                        value = "$totalInvoices",
                        icon = Icons.Default.Receipt,
                        color = LuxuryGoldLight,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ReportStatCard(
                        title = "متوسط الفاتورة",
                        value = "${"%,.2f".format(averageInvoice)} ريال",
                        icon = Icons.Default.Calculate,
                        color = LuxuryGoldLight,
                        modifier = Modifier.weight(1f)
                    )

                    ReportStatCard(
                        title = "الأصناف المباعة",
                        value = "$totalItems حبة",
                        icon = Icons.Outlined.Inventory2,
                        color = LuxuryGold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Movement Breakdown Card
        item {
            Surface(
                color = DarkCardBackground,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryGoldBorder.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ملخص الحركة حسب الأقسام",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = LuxuryGold,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    CategoryProgressBar(
                        label = "أدوات وتراثيات",
                        amount = "2,140 ريال",
                        percent = 0.82f,
                        color = LuxuryGold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CategoryProgressBar(
                        label = "هدايا راقية",
                        amount = "1,420 ريال",
                        percent = 0.64f,
                        color = LuxuryGoldLight
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CategoryProgressBar(
                        label = "أجهزة حديثة",
                        amount = "860 ريال",
                        percent = 0.40f,
                        color = LuxuryGoldDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CategoryProgressBar(
                        label = "إلكترونيات ومفروشات",
                        amount = "440 ريال",
                        percent = 0.24f,
                        color = Color(0xFFC29B38)
                    )
                }
            }
        }

        // Recent Invoices Section
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "سجل الفواتير الأخيرة",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = LuxuryGoldLight,
                textAlign = TextAlign.Right,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 4.dp)
            )
        }

        items(orders) { order ->
            Surface(
                color = DarkCardBackground,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262626)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable { selectedOrderForReceipt = order }
                    .testTag("report_order_${order.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "${"%,.2f".format(order.total)} ريال",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = LuxuryGold
                        )
                        Surface(
                            color = LuxuryGoldSurface,
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, LuxuryGold),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = order.paymentMethod,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = LuxuryGold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = order.id,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ${order.customer.name}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextMuted
                            )
                        }
                        Text(
                            text = "${order.formattedDate} • ${order.totalItemsCount} أصناف",
                            fontSize = 10.sp,
                            color = TextMuted.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }

    selectedOrderForReceipt?.let { order ->
        ReceiptDialog(
            order = order,
            onDismiss = { selectedOrderForReceipt = null }
        )
    }
}

@Composable
private fun ReportStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DarkCardBackground,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E2E2E)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.End
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(LuxuryGoldSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = TextWhite,
                textAlign = TextAlign.Right
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                color = TextMuted,
                textAlign = TextAlign.Right
            )
        }
    }
}

@Composable
private fun CategoryProgressBar(
    label: String,
    amount: String,
    percent: Float,
    color: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = amount, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LuxuryGoldLight)
            Text(text = label, fontSize = 12.sp, color = TextWhite)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { percent },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = Color(0xFF222222)
        )
    }
}
