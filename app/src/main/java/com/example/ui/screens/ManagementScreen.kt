package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.StoreViewModel

@Composable
fun ManagementScreen(
    viewModel: StoreViewModel,
    onNavigateBack: () -> Unit
) {
    val categories by viewModel.categories.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val notice by viewModel.notice.collectAsState()

    var activeSubTab by remember { mutableStateOf("إضافة صنف") }

    // Form fields
    var itemCode by remember { mutableStateOf("") }
    var itemName by remember { mutableStateOf("") }
    var itemModel by remember { mutableStateOf("") }
    var itemUnit by remember { mutableStateOf("حبة") }
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull() ?: "أدوات وتراثيات") }
    var barcode by remember { mutableStateOf("") }
    var costPriceStr by remember { mutableStateOf("") }
    var retailPriceStr by remember { mutableStateOf("") }
    var wholesalePriceStr by remember { mutableStateOf("") }
    var stockStr by remember { mutableStateOf("10") }
    var notes by remember { mutableStateOf("") }

    var formError by remember { mutableStateOf<String?>(null) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("management_screen"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Screen Header
        item {
            Surface(
                color = DarkBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = DarkCardBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryGoldBorder),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(modifier = Modifier.padding(3.dp)) {
                                listOf("المخزون", "إضافة صنف").forEach { tab ->
                                    val isSelected = activeSubTab == tab
                                    Surface(
                                        color = if (isSelected) LuxuryGold else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.clickable { activeSubTab = tab }
                                    ) {
                                        Text(
                                            text = tab,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) TextDark else TextWhite,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "إدارة المنتجات والمخزون",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = LuxuryGold
                            )
                            Text(
                                text = "إضافة صنف جديد وضبط الأسعار",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }

        // Feedback / Notice
        notice?.let { msg ->
            item {
                Surface(
                    color = LuxuryGoldSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryGold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = LuxuryGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = msg, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LuxuryGoldLight)
                    }
                }
            }
        }

        if (activeSubTab == "إضافة صنف") {
            // Form Container
            item {
                Surface(
                    color = DarkCardBackground,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryGoldBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "البيانات الأساسية",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = LuxuryGold,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Item name
                        OutlinedTextField(
                            value = itemName,
                            onValueChange = {
                                itemName = it
                                formError = null
                            },
                            label = { Text("اسم الصنف *", color = LuxuryGoldLight) },
                            placeholder = { Text("مثال: طقم قدور جرانيت فاخر") },
                            singleLine = true,
                            isError = formError != null && itemName.isBlank(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = LuxuryGold,
                                unfocusedBorderColor = Color(0xFF333333)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("form_item_name")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Code & Model Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = itemModel,
                                onValueChange = { itemModel = it },
                                label = { Text("موديل الصنف", color = TextMuted) },
                                placeholder = { Text("GR-9PCS") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedBorderColor = LuxuryGold,
                                    unfocusedBorderColor = Color(0xFF333333)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("form_item_model")
                            )

                            OutlinedTextField(
                                value = itemCode,
                                onValueChange = { itemCode = it },
                                label = { Text("رقم الصنف / SKU", color = TextMuted) },
                                placeholder = { Text("1001") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedBorderColor = LuxuryGold,
                                    unfocusedBorderColor = Color(0xFF333333)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("form_item_code")
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Unit
                        OutlinedTextField(
                            value = itemUnit,
                            onValueChange = { itemUnit = it },
                            label = { Text("الوحدة", color = TextMuted) },
                            placeholder = { Text("مثال: حبة، كرتون، طقم، كجم") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = LuxuryGold,
                                unfocusedBorderColor = Color(0xFF333333)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("form_item_unit")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Category Selection
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { showAddCategoryDialog = true }) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = LuxuryGold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "قسم جديد", color = LuxuryGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(text = "القسم", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(categories) { cat ->
                                val isSelected = selectedCategory == cat
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = LuxuryGold,
                                        selectedLabelColor = TextDark,
                                        containerColor = DarkElevated,
                                        labelColor = TextWhite
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Barcode input with scan button
                        Text(text = "باركود الصنف", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite, textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    barcode = "628" + (10000000..99999999).random().toString()
                                },
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(LuxuryGold)
                                    .testTag("scan_or_gen_barcode_btn")
                            ) {
                                Icon(imageVector = Icons.Outlined.QrCodeScanner, contentDescription = "توليد/مسح", tint = TextDark)
                            }

                            OutlinedTextField(
                                value = barcode,
                                onValueChange = { barcode = it },
                                placeholder = { Text("أدخل الباركود يدوياً أو اضغط للتوليد") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedBorderColor = LuxuryGold,
                                    unfocusedBorderColor = Color(0xFF333333)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("form_barcode_input")
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFF262626))
                        Spacer(modifier = Modifier.height(14.dp))

                        // Pricing & Stock Section
                        Text(
                            text = "الأسعار والمخزون",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = LuxuryGold,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = costPriceStr,
                                onValueChange = { costPriceStr = it },
                                label = { Text("سعر التكلفة", color = TextMuted) },
                                placeholder = { Text("0.00") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedBorderColor = LuxuryGold,
                                    unfocusedBorderColor = Color(0xFF333333)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("form_cost_price")
                            )

                            OutlinedTextField(
                                value = retailPriceStr,
                                onValueChange = {
                                    retailPriceStr = it
                                    formError = null
                                },
                                label = { Text("سعر التجزئة *", color = LuxuryGoldLight) },
                                placeholder = { Text("0.00") },
                                singleLine = true,
                                isError = formError != null && retailPriceStr.isBlank(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedBorderColor = LuxuryGold,
                                    unfocusedBorderColor = Color(0xFF333333)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("form_retail_price")
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = wholesalePriceStr,
                                onValueChange = { wholesalePriceStr = it },
                                label = { Text("سعر الجملة", color = TextMuted) },
                                placeholder = { Text("0.00") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedBorderColor = LuxuryGold,
                                    unfocusedBorderColor = Color(0xFF333333)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("form_wholesale_price")
                            )

                            OutlinedTextField(
                                value = stockStr,
                                onValueChange = { stockStr = it },
                                label = { Text("الكمية المتوفرة", color = TextMuted) },
                                placeholder = { Text("10") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedBorderColor = LuxuryGold,
                                    unfocusedBorderColor = Color(0xFF333333)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("form_stock")
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Notes
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("ملاحظات إضافية", color = TextMuted) },
                            placeholder = { Text("ملاحظات المنتج أو العروض...") },
                            minLines = 3,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = LuxuryGold,
                                unfocusedBorderColor = Color(0xFF333333)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("form_notes")
                        )

                        formError?.let { err ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = err, color = ErrorRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onNavigateBack,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF444444)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                            ) {
                                Text(text = "إغلاق")
                            }

                            Button(
                                onClick = {
                                    val price = retailPriceStr.toDoubleOrNull()
                                    if (itemName.isBlank() || price == null || price <= 0) {
                                        formError = "الرجاء إدخال اسم الصنف وسعر التجزئة على الأقل"
                                    } else {
                                        val cost = costPriceStr.toDoubleOrNull() ?: (price * 0.75)
                                        val wholesale = wholesalePriceStr.toDoubleOrNull() ?: (price * 0.9)
                                        val stock = stockStr.toIntOrNull() ?: 10

                                        viewModel.addNewProduct(
                                            name = itemName,
                                            category = selectedCategory,
                                            price = price,
                                            costPrice = cost,
                                            wholesalePrice = wholesale,
                                            stock = stock,
                                            model = itemModel,
                                            unit = itemUnit,
                                            barcode = barcode,
                                            note = notes
                                        )

                                        // Reset fields
                                        itemName = ""
                                        itemCode = ""
                                        itemModel = ""
                                        costPriceStr = ""
                                        retailPriceStr = ""
                                        wholesalePriceStr = ""
                                        barcode = ""
                                        notes = ""
                                        formError = null
                                        onNavigateBack()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LuxuryGold,
                                    contentColor = TextDark
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(50.dp)
                                    .testTag("save_product_btn")
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = TextDark)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "حفظ الصنف", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            }
                        }
                    }
                }
            }
        } else {
            // Inventory Table List
            item {
                Text(
                    text = "الأصناف الحالية (${allProducts.size} صنف)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = LuxuryGold,
                    textAlign = TextAlign.Right,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 8.dp)
                )
            }

            items(allProducts) { prod ->
                Surface(
                    color = DarkCardBackground,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262626)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.deleteProduct(prod.id) },
                            modifier = Modifier.testTag("delete_product_${prod.id}")
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف الصنف", tint = ErrorRed)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = prod.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Text(
                                text = "السعر: ${"%,.2f".format(prod.price)} ريال • المخزون: ${prod.stock} ${prod.unit} • ${prod.category}",
                                fontSize = 11.sp,
                                color = LuxuryGold
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            containerColor = DarkCardBackground,
            title = { Text(text = "إضافة قسم جديد للمتجر", fontWeight = FontWeight.Black, fontSize = 16.sp, color = LuxuryGold) },
            text = {
                OutlinedTextField(
                    value = newCategoryInput,
                    onValueChange = { newCategoryInput = it },
                    placeholder = { Text("مثال: عطورات ومباخر ملكية") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = LuxuryGold,
                        unfocusedBorderColor = Color(0xFF333333)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryInput.isNotBlank()) {
                            viewModel.addNewCategory(newCategoryInput)
                            selectedCategory = newCategoryInput.trim()
                            newCategoryInput = ""
                            showAddCategoryDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LuxuryGold, contentColor = TextDark)
                ) {
                    Text("إضافة", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text("إلغاء", color = TextMuted)
                }
            }
        )
    }
}
