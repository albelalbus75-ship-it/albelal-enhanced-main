package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.SaleOrder
import com.example.ui.theme.*

@Composable
fun ReceiptDialog(
    order: SaleOrder,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DarkCardBackground,
            border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryGoldBorder),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("receipt_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with close button & success badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkElevated)
                            .testTag("close_receipt_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق", tint = LuxuryGold)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = LuxuryGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "تم البيع بنجاح",
                            color = LuxuryGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Receipt Container
                Surface(
                    color = Color(0xFF111111),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF2E2E2E), RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "متجر الفيصل للتجارة العامة",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = LuxuryGold
                        )
                        Text(
                            text = "فاتورة ضريبية مبسطة",
                            fontSize = 12.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "الرقم الضريبي: 310293847500003",
                            fontSize = 10.sp,
                            color = TextMuted.copy(alpha = 0.7f),
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFF262626))
                        Spacer(modifier = Modifier.height(8.dp))

                        ReceiptRow(label = "رقم الفاتورة", value = order.id)
                        ReceiptRow(label = "التاريخ والوقت", value = order.formattedDate)
                        ReceiptRow(label = "العميل", value = order.customer.name)
                        ReceiptRow(label = "طريقة الدفع", value = order.paymentMethod)

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color(0xFF262626))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Items list
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 160.dp)
                        ) {
                            items(order.items) { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.product.name,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextWhite
                                        )
                                        Text(
                                            text = "${item.quantity} × ${"%,.2f".format(item.product.price)} ريال",
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }
                                    Text(
                                        text = "${"%,.2f".format(item.totalPrice)} ريال",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LuxuryGoldLight
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color(0xFF262626))
                        Spacer(modifier = Modifier.height(8.dp))

                        ReceiptRow(label = "المجموع قبل الضريبة", value = "${"%,.2f".format(order.subtotal)} ريال")
                        ReceiptRow(label = "ضريبة القيمة المضافة (15%)", value = "${"%,.2f".format(order.vat)} ريال")
                        if (order.discount > 0) {
                            ReceiptRow(label = "الخصم", value = "- ${"%,.2f".format(order.discount)} ريال")
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(LuxuryGoldSurface, RoundedCornerShape(8.dp))
                                .border(0.5.dp, LuxuryGold, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "المبلغ الإجمالي",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = LuxuryGold
                            )
                            Text(
                                text = "${"%,.2f".format(order.total)} ريال",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = LuxuryGoldLight
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "شكراً لزيارتكم متجر الفيصل للتجارة العامة",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF444444)),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = LuxuryGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "مشاركة", fontSize = 13.sp, color = TextWhite)
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LuxuryGold,
                            contentColor = TextDark
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("done_receipt_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = null, tint = TextDark, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "طباعة وإغلاق", fontSize = 13.sp, color = TextDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = TextMuted)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
    }
}
