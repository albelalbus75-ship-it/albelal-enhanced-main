package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.StoreViewModel

enum class AppTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    HOME("الرئيسية", Icons.Filled.Home, Icons.Outlined.Home, "tab_home"),
    CART("السلة", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart, "tab_cart"),
    REPORTS("التقارير", Icons.Filled.Assessment, Icons.Outlined.Assessment, "tab_reports"),
    ACCOUNTS("الحسابات", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet, "tab_accounts"),
    SETTINGS("الإعدادات", Icons.Filled.Settings, Icons.Outlined.Settings, "tab_settings")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val viewModel: StoreViewModel = viewModel()
                    var currentTab by remember { mutableStateOf(AppTab.HOME) }
                    val cart by viewModel.cart.collectAsState()
                    val cartCount = cart.sumOf { it.quantity }

                    var showAddProductModal by remember { mutableStateOf(false) }

                    // BackHandler returns to HOME tab if user is on another screen
                    BackHandler(enabled = currentTab != AppTab.HOME) {
                        currentTab = AppTab.HOME
                    }

                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("main_scaffold"),
                        containerColor = DarkBackground,
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        bottomBar = {
                            NavigationBar(
                                containerColor = Color(0xFF0F0F0F),
                                tonalElevation = 10.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = 1.dp,
                                        color = Color(0xFF222222),
                                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                                    )
                                    .testTag("bottom_nav_bar")
                            ) {
                                AppTab.values().forEach { tab ->
                                    val isSelected = currentTab == tab
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { currentTab = tab },
                                        icon = {
                                            if (tab == AppTab.CART && cartCount > 0) {
                                                BadgedBox(
                                                    badge = {
                                                        Badge(containerColor = LuxuryGold, contentColor = TextDark) {
                                                            Text(
                                                                text = "$cartCount",
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 10.sp
                                                            )
                                                        }
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                                        contentDescription = tab.title,
                                                        tint = if (isSelected) LuxuryGold else TextMuted,
                                                        modifier = Modifier.size(22.dp)
                                                    )
                                                }
                                            } else {
                                                Icon(
                                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                                    contentDescription = tab.title,
                                                    tint = if (isSelected) LuxuryGold else TextMuted,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                        },
                                        label = {
                                            Text(
                                                text = tab.title,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                                color = if (isSelected) LuxuryGold else TextMuted
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = LuxuryGold,
                                            selectedTextColor = LuxuryGold,
                                            indicatorColor = LuxuryGoldSurface,
                                            unselectedIconColor = TextMuted,
                                            unselectedTextColor = TextMuted
                                        ),
                                        modifier = Modifier.testTag(tab.tag)
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            Crossfade(targetState = currentTab, label = "TabSwitch") { tab ->
                                when (tab) {
                                    AppTab.HOME -> HomeScreen(
                                        viewModel = viewModel,
                                        onNavigateToCart = { currentTab = AppTab.CART },
                                        onOpenAddProductDialog = { showAddProductModal = true },
                                        onQuickCheckout = { currentTab = AppTab.CART }
                                    )
                                    AppTab.CART -> CartScreen(
                                        viewModel = viewModel,
                                        onNavigateToSales = { currentTab = AppTab.HOME }
                                    )
                                    AppTab.REPORTS -> ReportsScreen(
                                        viewModel = viewModel
                                    )
                                    AppTab.ACCOUNTS -> AccountsScreen(
                                        viewModel = viewModel
                                    )
                                    AppTab.SETTINGS -> SettingsScreen(
                                        viewModel = viewModel,
                                        onNavigateToInventoryManagement = { showAddProductModal = true }
                                    )
                                }
                            }
                        }
                    }

                    // Add Product & Inventory Modal
                    if (showAddProductModal) {
                        ManagementScreen(
                            viewModel = viewModel,
                            onNavigateBack = { showAddProductModal = false }
                        )
                    }
                }
            }
        }
    }
}
