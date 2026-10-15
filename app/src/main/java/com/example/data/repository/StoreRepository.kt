package com.example.data.repository

import com.example.R
import com.example.data.model.CartItem
import com.example.data.model.Customer
import com.example.data.model.Product
import com.example.data.model.SaleOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StoreRepository {

    private val initialProducts = listOf(
        Product(
            id = "p1",
            name = "طقم دلة القهوة العربية والضيافة الملكية",
            category = "أدوات وتراثيات",
            price = 380.0,
            costPrice = 270.0,
            wholesalePrice = 330.0,
            stock = 18,
            iconName = "coffee",
            colorHex = "#1F1A10",
            note = "دلة فاخرة مع فناجيل منقوشة وصحن تقديم",
            barcode = "62810055001",
            model = "DAL-ROYAL",
            unit = "طقم",
            imageResId = R.drawable.prod_dallah_set_1792029190965
        ),
        Product(
            id = "p2",
            name = "طقم قدور جرانيت تركي فاخر 9 قطع",
            category = "أدوات وتراثيات",
            price = 295.0,
            costPrice = 210.0,
            wholesalePrice = 260.0,
            stock = 14,
            iconName = "restaurant",
            colorHex = "#181818",
            note = "مقابض مذهبة غير لاصقة • صناعة تركية ممتازة",
            barcode = "62810055002",
            model = "GR-GOLD-9",
            unit = "طقم",
            imageResId = R.drawable.prod_cookware_1792029207646
        ),
        Product(
            id = "p3",
            name = "بوكس هدية ملكي فاخر (ساعة وعود وقلم)",
            category = "هدايا راقية",
            price = 340.0,
            costPrice = 240.0,
            wholesalePrice = 295.0,
            stock = 12,
            iconName = "card_giftcard",
            colorHex = "#1C1914",
            note = "ساعة ذهبية + عطر عود ملكي + قلم فاخر ومحفظة",
            barcode = "62810055003",
            model = "BOX-ROYAL",
            unit = "طقم",
            imageResId = R.drawable.prod_royal_gift_1792029224376
        ),
        Product(
            id = "p4",
            name = "دلة السيف النحاسية نقش يدوي أصيل",
            category = "أدوات وتراثيات",
            price = 220.0,
            costPrice = 160.0,
            wholesalePrice = 190.0,
            stock = 25,
            iconName = "local_cafe",
            colorHex = "#211B0E",
            note = "نحاس خالص أصيل مع ختم الجودة الذهبي",
            barcode = "62810055004",
            model = "DAL-SAIF",
            unit = "حبة",
            imageResId = R.drawable.faisal_dallah_logo_1792029175878
        ),
        Product(
            id = "p5",
            name = "قلاية هوائية فيليبس رقمية 4.1 لتر",
            category = "أجهزة حديثة",
            price = 320.0,
            costPrice = 240.0,
            wholesalePrice = 290.0,
            stock = 10,
            iconName = "kitchen",
            colorHex = "#181818",
            note = "تقنية Rapid Air الصحية • ضمان سنتين",
            barcode = "62810055005",
            model = "HD9200",
            unit = "حبة"
        ),
        Product(
            id = "p6",
            name = "سماعة رأس بلوتوث عازلة للضوضاء P9",
            category = "إلكترونيات",
            price = 95.0,
            costPrice = 60.0,
            wholesalePrice = 78.0,
            stock = 22,
            iconName = "headphones",
            colorHex = "#181818",
            note = "صوت نقي محيطي عالي الدقة وتصميم مريح",
            barcode = "62810055006",
            model = "P9-PRO",
            unit = "حبة"
        ),
        Product(
            id = "p7",
            name = "شاحن أنكر فائق السرعة 65W بثلاثة منافذ",
            category = "إلكترونيات",
            price = 125.0,
            costPrice = 85.0,
            wholesalePrice = 105.0,
            stock = 18,
            iconName = "bolt",
            colorHex = "#181818",
            note = "تقنية GaN المدمجة لشحن الهواتف والحواسيب",
            barcode = "62810055007",
            model = "A2667",
            unit = "حبة"
        ),
        Product(
            id = "p8",
            name = "طقم شرشف سرير قطن مصري فاخر نفرين",
            category = "مفروشات",
            price = 145.0,
            costPrice = 95.0,
            wholesalePrice = 125.0,
            stock = 16,
            iconName = "bed",
            colorHex = "#181818",
            note = "نسيج 500 غرزة فائق النعومة متين وأنيق",
            barcode = "62810055008",
            model = "EG-500TC",
            unit = "طقم"
        ),
        Product(
            id = "p9",
            name = "مج حراري ذكي بشاشة رقمية 500 مل",
            category = "هدايا راقية",
            price = 45.0,
            costPrice = 25.0,
            wholesalePrice = 35.0,
            stock = 30,
            iconName = "coffee",
            colorHex = "#181818",
            note = "عزل حراري 24 ساعة ومستشعر لدرجة الحرارة",
            barcode = "62810055009",
            model = "TM-SMART",
            unit = "حبة"
        ),
        Product(
            id = "p10",
            name = "حافظات طعام زجاجية محكمة 5 قطع",
            category = "أدوات وتراثيات",
            price = 65.0,
            costPrice = 40.0,
            wholesalePrice = 52.0,
            stock = 26,
            iconName = "inventory_2",
            colorHex = "#181818",
            note = "زجاج بوروسيليكات يتحمل الفرن والمايكرويف",
            barcode = "62810055010",
            model = "GL-SEAL-5",
            unit = "طقم"
        )
    )

    private val initialCustomers = listOf(
        Customer(id = "cash", name = "زبون نقدي عام", detail = "بدون حساب", debt = 0.0, phone = "-"),
        Customer(id = "fashion", name = "معرض الأناقة للديكور", detail = "دين: 2,450 ريال", debt = 2450.0, phone = "0551234567"),
        Customer(id = "khaled", name = "خالد بن عبد العزيز", detail = "حساب شخصي • دين: 420 ريال", debt = 420.0, phone = "0509876543"),
        Customer(id = "ibdaa", name = "مؤسسة إبداع للتجهيزات", detail = "دين: 5,800 ريال", debt = 5800.0, phone = "0543219876")
    )

    private val initialCategories = listOf(
        "أدوات وتراثيات",
        "أجهزة حديثة",
        "إلكترونيات",
        "مفروشات",
        "هدايا راقية"
    )

    private val _products = MutableStateFlow<List<Product>>(initialProducts)
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _customers = MutableStateFlow<List<Customer>>(initialCustomers)
    val customers: StateFlow<List<Customer>> = _customers.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(initialCategories)
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _orders = MutableStateFlow<List<SaleOrder>>(createInitialOrders())
    val orders: StateFlow<List<SaleOrder>> = _orders.asStateFlow()

    private fun createInitialOrders(): List<SaleOrder> {
        val now = System.currentTimeMillis()
        val p1 = initialProducts[0]
        val p2 = initialProducts[1]
        val p3 = initialProducts[2]

        return listOf(
            SaleOrder(
                id = "INV-1001",
                timestamp = now - 20 * 60 * 1000,
                items = listOf(CartItem(p1, 1), CartItem(p3, 1)),
                customer = initialCustomers[0],
                subtotal = 720.0,
                vat = 108.0,
                discount = 0.0,
                total = 828.0,
                paymentMethod = "نقدًا"
            ),
            SaleOrder(
                id = "INV-1002",
                timestamp = now - 60 * 60 * 1000,
                items = listOf(CartItem(p2, 1)),
                customer = initialCustomers[1],
                subtotal = 295.0,
                vat = 44.25,
                discount = 0.0,
                total = 339.25,
                paymentMethod = "آجل"
            )
        )
    }

    fun addProduct(product: Product) {
        _products.update { current ->
            listOf(product) + current
        }
    }

    fun deleteProduct(productId: String) {
        _products.update { current ->
            current.filterNot { it.id == productId }
        }
    }

    fun addCategory(category: String) {
        val trimmed = category.trim()
        if (trimmed.isNotEmpty() && !_categories.value.contains(trimmed)) {
            _categories.update { it + trimmed }
        }
    }

    fun addCustomer(customer: Customer) {
        _customers.update { current ->
            current + customer
        }
    }

    fun settleCustomerDebt(customerId: String, amount: Double) {
        _customers.update { list ->
            list.map { customer ->
                if (customer.id == customerId) {
                    val remaining = maxOf(0.0, customer.debt - amount)
                    val detail = if (remaining > 0) "دين: ${"%,.2f".format(remaining)} ريال" else "تم السداد بالكامل"
                    customer.copy(debt = remaining, detail = detail)
                } else {
                    customer
                }
            }
        }
    }

    fun recordSale(order: SaleOrder) {
        _orders.update { listOf(order) + it }

        _products.update { productList ->
            productList.map { product ->
                val orderedItem = order.items.find { it.product.id == product.id }
                if (orderedItem != null) {
                    product.copy(stock = maxOf(0, product.stock - orderedItem.quantity))
                } else {
                    product
                }
            }
        }

        if (order.paymentMethod == "آجل" && order.customer.id != "cash") {
            _customers.update { list ->
                list.map { customer ->
                    if (customer.id == order.customer.id) {
                        val newDebt = customer.debt + order.total
                        customer.copy(
                            debt = newDebt,
                            detail = "دين: ${"%,.2f".format(newDebt)} ريال"
                        )
                    } else {
                        customer
                    }
                }
            }
        }
    }
}
