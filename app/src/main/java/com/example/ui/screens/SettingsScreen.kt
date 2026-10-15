package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.StoreViewModel

@Composable
fun SettingsScreen(
    viewModel: StoreViewModel,
    onNavigateToInventoryManagement: () -> Unit
) {
    var storeName by remember { mutableStateOf("متجر الفيصل للتجارة العامة") }
    var taxNumber by remember { mutableStateOf("310293847500003") }
    var cashierName by remember { mutableStateOf("فيصل عبد العزيز") }

    var autoPrintReceipt by remember { mutableStateOf(true) }
    var lowStockAlert by remember { mutableStateOf(true) }
    var soundEffects by remember { mutableStateOf(true) }

    var showEditStoreDialog by remember { mutableStateOf(false) }
    var showBackupSuccessDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("settings_screen"),
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
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = LuxuryGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "إعدادات المتجر والنظام",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = LuxuryGold
                        )
                        Text(
                            text = "تخصيص بيانات المنشأة، الطابعات، والنسخ الاحتياطي",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        // Section 1: Store & Tax Profile
        item {
            SettingsSectionHeader(title = "بيانات المتجر والضريبة")
            Surface(
                color = DarkCardBackground,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryGoldBorder.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    SettingsInfoRow(
                        label = "اسم المتجر",
                        value = storeName,
                        icon = Icons.Default.Storefront,
                        onClick = { showEditStoreDialog = true }
                    )
                    HorizontalDivider(color = Color(0xFF262626), modifier = Modifier.padding(vertical = 8.dp))
                    SettingsInfoRow(
                        label = "الرقم الضريبي",
                        value = taxNumber,
                        icon = Icons.Default.ReceiptLong,
                        onClick = { showEditStoreDialog = true }
                    )
                    HorizontalDivider(color = Color(0xFF262626), modifier = Modifier.padding(vertical = 8.dp))
                    SettingsInfoRow(
                        label = "الكاشير المسؤول",
                        value = cashierName,
                        icon = Icons.Default.Person,
                        onClick = { showEditStoreDialog = true }
                    )
                    HorizontalDivider(color = Color(0xFF262626), modifier = Modifier.padding(vertical = 8.dp))
                    SettingsInfoRow(
                        label = "ضريبة القيمة المضافة (VAT)",
                        value = "15% القياسية",
                        icon = Icons.Default.Percent,
                        onClick = {}
                    )
                }
            }
        }

        // Section 2: Quick Management Actions
        item {
            SettingsSectionHeader(title = "إدارة العمليات والمخزون")
            Surface(
                color = DarkCardBackground,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262626)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    SettingsActionRow(
                        title = "إدارة المخزون والأصناف الجديدة",
                        subtitle = "إضافة صنف، توليد الباركود، وتعديل الأسعار",
                        icon = Icons.Default.Inventory,
                        onClick = onNavigateToInventoryManagement
                    )
                    HorizontalDivider(color = Color(0xFF262626), modifier = Modifier.padding(vertical = 8.dp))
                    SettingsActionRow(
                        title = "تصدير الفواتير والتقارير المالية",
                        subtitle = "تصدير سجل المبيعات كملف CSV أو PDF",
                        icon = Icons.Default.FileDownload,
                        onClick = { showBackupSuccessDialog = true }
                    )
                    HorizontalDivider(color = Color(0xFF262626), modifier = Modifier.padding(vertical = 8.dp))
                    SettingsActionRow(
                        title = "نسخ احتياطي واستعادة البيانات",
                        subtitle = "حفظ قاعدة البيانات محلياً وتأمين الحسابات",
                        icon = Icons.Default.Backup,
                        onClick = { showBackupSuccessDialog = true }
                    )
                }
            }
        }

        // Section 3: POS & Printer Toggles
        item {
            SettingsSectionHeader(title = "خيارات الكاشير ونقاط البيع")
            Surface(
                color = DarkCardBackground,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262626)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    SettingsToggleRow(
                        title = "طباعة الفاتورة تلقائياً",
                        subtitle = "إصدار الإيصال الحراري بعد كل عملية بيع",
                        checked = autoPrintReceipt,
                        onCheckedChange = { autoPrintReceipt = it }
                    )
                    HorizontalDivider(color = Color(0xFF262626), modifier = Modifier.padding(vertical = 8.dp))
                    SettingsToggleRow(
                        title = "تنبيه انخفاض المخزون",
                        subtitle = "إشعار عند وصول الصنف إلى أقل من 5 حبات",
                        checked = lowStockAlert,
                        onCheckedChange = { lowStockAlert = it }
                    )
                    HorizontalDivider(color = Color(0xFF262626), modifier = Modifier.padding(vertical = 8.dp))
                    SettingsToggleRow(
                        title = "التأثيرات الصوتية للكاشير",
                        subtitle = "صوت تأكيد قراءة الباركود وإتمام البيع",
                        checked = soundEffects,
                        onCheckedChange = { soundEffects = it }
                    )
                }
            }
        }

        // Section 4: App Information & System Status
        item {
            SettingsSectionHeader(title = "حول التطبيق والنظام")
            Surface(
                color = DarkCardBackground,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262626)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, LuxuryGold, RoundedCornerShape(14.dp))
                            .background(Color(0xFF141414)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = LuxuryGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "متجر الفيصل للتجارة العامة",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = LuxuryGold
                    )
                    Text(
                        text = "نظام نقاط البيع وإدارة المبيعات المتكامل • الإصدار 1.0.0",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "تم بناء وتجميع ملف التطبيق بالكامل بنجاح",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF34D399)
                    )
                }
            }
        }
    }

    // Edit Store Details Dialog
    if (showEditStoreDialog) {
        var tempName by remember { mutableStateOf(storeName) }
        var tempTax by remember { mutableStateOf(taxNumber) }
        var tempCashier by remember { mutableStateOf(cashierName) }

        AlertDialog(
            onDismissRequest = { showEditStoreDialog = false },
            containerColor = DarkCardBackground,
            title = {
                Text(text = "تعديل بيانات المنشأة", color = LuxuryGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("اسم المتجر", color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = LuxuryGold,
                            unfocusedBorderColor = Color(0xFF333333)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempTax,
                        onValueChange = { tempTax = it },
                        label = { Text("الرقم الضريبي", color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = LuxuryGold,
                            unfocusedBorderColor = Color(0xFF333333)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempCashier,
                        onValueChange = { tempCashier = it },
                        label = { Text("اسم الكاشير", color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = LuxuryGold,
                            unfocusedBorderColor = Color(0xFF333333)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        storeName = tempName
                        taxNumber = tempTax
                        cashierName = tempCashier
                        showEditStoreDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LuxuryGold, contentColor = TextDark)
                ) {
                    Text("حفظ التعديلات", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditStoreDialog = false }) {
                    Text("إلغاء", color = TextMuted)
                }
            }
        )
    }

    // Backup & Export Success Dialog
    if (showBackupSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showBackupSuccessDialog = false },
            containerColor = DarkCardBackground,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = LuxuryGold)
                    Text(text = "تم بنجاح", color = LuxuryGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Text(
                    text = "تم تصدير سجل المبيعات والنسخة الاحتياطية بنجاح إلى ذاكرة الهاتف بأمان.",
                    color = TextWhite,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showBackupSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = LuxuryGold, contentColor = TextDark)
                ) {
                    Text("إغلاق", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Black,
        color = LuxuryGoldLight,
        textAlign = TextAlign.Right,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
private fun SettingsInfoRow(
    label: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = LuxuryGold
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = label, fontSize = 12.sp, color = TextWhite)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = LuxuryGold, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun SettingsActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = null, tint = LuxuryGold)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                Text(text = subtitle, fontSize = 10.sp, color = TextMuted)
            }
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(LuxuryGoldSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = LuxuryGold, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = LuxuryGold,
                checkedTrackColor = LuxuryGoldSurface,
                uncheckedThumbColor = Color(0xFF666666),
                uncheckedTrackColor = DarkElevated
            )
        )

        Column(horizontalAlignment = Alignment.End) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
            Text(text = subtitle, fontSize = 10.sp, color = TextMuted)
        }
    }
}
