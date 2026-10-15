package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Product
import com.example.ui.components.ProductDetailDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.StoreViewModel

@Composable
fun HomeScreen(
    viewModel: StoreViewModel,
    onNavigateToCart: () -> Unit,
    onOpenAddProductDialog: () -> Unit,
    onQuickCheckout: () -> Unit
) {
    val products by viewModel.products.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val inspectingProduct by viewModel.inspectingProduct.collectAsState()

    var showNotificationsDialog by remember { mutableStateOf(false) }

    val cartTotalCount = cart.sumOf { it.quantity }
    val cartTotalAmount = cart.sumOf { it.totalPrice }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("home_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // 1. الهيدر العلوي: شعار المتجر (أيقونة دلة ذهبية) + اسم "متجر الفيصل للتجارة العامة" + على اليسار أيقونة بحث وإشعارات
            Surface(
                color = DarkBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Right side (in RTL): Store logo (Golden Dallah) + Store Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.5.dp, LuxuryGold, RoundedCornerShape(14.dp))
                                .background(Color(0xFF141414)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.faisal_dallah_logo_1792029175878),
                                contentDescription = "شعار دلة الفيصل",
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = "متجر الفيصل",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp,
                                    color = LuxuryGold
                                )
                            )
                            Text(
                                text = "للتجارة العامة",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    color = TextWhite.copy(alpha = 0.85f),
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }

                    // Left side (in RTL): Search & Notifications icons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Quick Cart Count Pill if items exist
                        if (cartTotalCount > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, LuxuryGold, RoundedCornerShape(10.dp))
                                    .background(LuxuryGoldSurface)
                                    .clickable { onNavigateToCart() }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "$cartTotalCount سلة",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LuxuryGold
                                )
                            }
                        }

                        // Notification Icon with Gold Dot Badge
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFF262626), RoundedCornerShape(12.dp))
                                .background(DarkCardBackground)
                                .clickable { showNotificationsDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "الإشعارات",
                                tint = LuxuryGold,
                                modifier = Modifier.size(20.dp)
                            )
                            // Unread notification badge
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-6).dp, y = 6.dp)
                                    .clip(CircleShape)
                                    .background(LuxuryGold)
                            )
                        }
                    }
                }
            }

            // 2. شريط بحث: حقل بحث كبير بحدود ذهبية وحاشية دائرية، فيه placeholder "ابحث عن منتج..." وأيقونة بحث ذهبية
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = "ابحث عن منتج...",
                            color = TextMuted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "بحث",
                            tint = LuxuryGold,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "مسح",
                                    tint = LuxuryGoldLight,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF141414),
                        unfocusedContainerColor = Color(0xFF141414),
                        focusedBorderColor = LuxuryGold,
                        unfocusedBorderColor = LuxuryGold.copy(alpha = 0.55f),
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = LuxuryGold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("luxury_search_field")
                )
            }

            // شريط الفئات السريعة
            val allCats = remember(categories) { listOf("الكل") + categories }
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allCats.forEach { cat ->
                    item(key = cat) {
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectCategory(cat) },
                            label = {
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LuxuryGold,
                                selectedLabelColor = TextDark,
                                containerColor = DarkElevated,
                                labelColor = TextWhite
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = Color(0xFF2E2E2E),
                                selectedBorderColor = LuxuryGold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // 3. شبكة المنتجات (الأهم): Grid من عمودين (2 Columns)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("product_grid_section"),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Products Count Indicator
                item(span = { GridItemSpan(2) }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "المنتجات المتاحة (${products.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = LuxuryGoldLight
                        )
                        if (searchQuery.isNotBlank()) {
                            Text(
                                text = "نتائج البحث عن \"$searchQuery\"",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                if (products.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Outlined.Inventory2,
                                    contentDescription = null,
                                    tint = LuxuryGold.copy(alpha = 0.4f),
                                    modifier = Modifier.size(50.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "لا توجد منتجات مطابقة",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "جرّب كتابة كلمة بحث أخرى",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                } else {
                    items(products, key = { it.id }) { product ->
                        LuxuryProductGridCard(
                            product = product,
                            onCardClick = { viewModel.inspectProduct(product) },
                            onAddToCart = { viewModel.addToCart(product) }
                        )
                    }
                }
            }

            // 4. أزرار الإجراءات الرئيسية: في أسفل الشاشة فوق شريط التنقل
            // - زر ذهبي ممتلئ: "إضافة منتج جديد +"
            // - زر بحدود ذهبية وخلفية شفافة: "إنهاء عملية البيع"
            Surface(
                color = DarkBackground,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFF262626), Color.Transparent)
                        ),
                        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // الزر الذهبي الممتلئ: "إضافة منتج جديد +"
                    Button(
                        onClick = onOpenAddProductDialog,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LuxuryGold,
                            contentColor = TextDark
                        ),
                        shape = RoundedCornerShape(14.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("action_btn_add_product")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = TextDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "إضافة منتج جديد +",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = TextDark
                        )
                    }

                    // الزر بحدود ذهبية وخلفية شفافة: "إنهاء عملية البيع"
                    OutlinedButton(
                        onClick = onQuickCheckout,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = LuxuryGold
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, LuxuryGold),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("action_btn_checkout")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = LuxuryGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (cartTotalAmount > 0)
                                "إنهاء عملية البيع • ${"%,.2f".format(cartTotalAmount * 1.15)} ريال"
                            else
                                "إنهاء عملية البيع",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = LuxuryGold
                        )
                    }
                }
            }
        }

        // Product Details Modal
        inspectingProduct?.let { prod ->
            ProductDetailDialog(
                product = prod,
                onDismiss = { viewModel.inspectProduct(null) },
                onAddToCart = { qty ->
                    repeat(qty) {
                        viewModel.addToCart(prod)
                    }
                }
            )
        }

        // Notifications Dialog
        if (showNotificationsDialog) {
            AlertDialog(
                onDismissRequest = { showNotificationsDialog = false },
                containerColor = DarkCardBackground,
                shape = RoundedCornerShape(18.dp),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = LuxuryGold)
                        Text(text = "إشعارات المتجر", color = LuxuryGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "• مرحباً بك في متجر الفيصل للتجارة العامة.",
                            color = TextWhite,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "• تم تحديث أسعار أطقم القهوة والمباخر والقدور التركية الفاخرة.",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "• نظام البيع السريع ونقاط البيع جاهز لإتمام الفواتير بدقة وسرعة.",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showNotificationsDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = LuxuryGold)
                    ) {
                        Text("حسناً", color = TextDark, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

/**
 * بطاقة المنتج الفاخرة بنظام الشبكة Grid (2 Columns)
 * - الحواف دائرية 16px (16.dp)
 * - الخلفية سوداء أفتح قليلاً (#161616)
 * - حدود ذهبية رفيعة (#D4AF37)
 * - صورة المنتج، اسم المنتج، السعر بالريال، زر صغير ذهبي "إضافة +"
 */
@Composable
fun LuxuryProductGridCard(
    product: Product,
    onCardClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryGoldBorder.copy(alpha = 0.55f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .testTag("luxury_product_card_${product.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // صورة المنتج مع زوايا دائرية
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F0F0F))
                    .border(1.dp, Color(0xFF262626), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (product.imageResId != null) {
                    Image(
                        painter = painterResource(id = product.imageResId),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(LuxuryGoldSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Inventory2,
                            contentDescription = null,
                            tint = LuxuryGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Category mini pill
                Surface(
                    color = Color.Black.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, LuxuryGold.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                ) {
                    Text(
                        text = product.category,
                        color = LuxuryGoldLight,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // اسم المنتج بخط أبيض
            Text(
                text = product.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            // السعر بالريال وزر الإضافة الصغير الذهبي "إضافة +"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // السعر بالريال (ذهبي فاخر)
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "${"%,.0f".format(product.price)} ريال",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = LuxuryGold
                    )
                }

                // زر صغير ذهبي "إضافة +"
                Button(
                    onClick = onAddToCart,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LuxuryGold,
                        contentColor = TextDark
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(30.dp)
                        .testTag("add_btn_${product.id}")
                ) {
                    Text(
                        text = "إضافة +",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark
                    )
                }
            }
        }
    }
}
