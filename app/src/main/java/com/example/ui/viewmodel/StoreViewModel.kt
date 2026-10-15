package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CartItem
import com.example.data.model.Customer
import com.example.data.model.Product
import com.example.data.model.SaleOrder
import com.example.data.repository.StoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class StoreViewModel(
    private val repository: StoreRepository = StoreRepository()
) : ViewModel() {

    // Filter states
    val categories: StateFlow<List<String>> = repository.categories
    private val _selectedCategory = MutableStateFlow("الكل")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Filtered products
    val products: StateFlow<List<Product>> = combine(
        repository.products,
        _selectedCategory,
        _searchQuery
    ) { allProducts, cat, query ->
        allProducts.filter { product ->
            val matchesCategory = cat == "الكل" || product.category == cat
            val matchesQuery = query.isBlank() ||
                    product.name.contains(query, ignoreCase = true) ||
                    product.barcode.contains(query, ignoreCase = true) ||
                    product.category.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProducts: StateFlow<List<Product>> = repository.products

    // Cart state
    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    val customers: StateFlow<List<Customer>> = repository.customers
    private val _selectedCustomer = MutableStateFlow<Customer?>(null)
    val selectedCustomer: StateFlow<Customer?> = _selectedCustomer.asStateFlow()

    private val _selectedPayment = MutableStateFlow("نقدًا")
    val selectedPayment: StateFlow<String> = _selectedPayment.asStateFlow()

    private val _discount = MutableStateFlow(0.0)
    val discount: StateFlow<Double> = _discount.asStateFlow()

    // Feedback notice
    private val _notice = MutableStateFlow<String?>(null)
    val notice: StateFlow<String?> = _notice.asStateFlow()

    // Receipt Modal state
    private val _completedOrder = MutableStateFlow<SaleOrder?>(null)
    val completedOrder: StateFlow<SaleOrder?> = _completedOrder.asStateFlow()

    // Inspect product detail state
    private val _inspectingProduct = MutableStateFlow<Product?>(null)
    val inspectingProduct: StateFlow<Product?> = _inspectingProduct.asStateFlow()

    // Customer detail dialog / debt settlement
    private val _selectedCustomerForDetail = MutableStateFlow<Customer?>(null)
    val selectedCustomerForDetail: StateFlow<Customer?> = _selectedCustomerForDetail.asStateFlow()

    val orders: StateFlow<List<SaleOrder>> = repository.orders

    init {
        viewModelScope.launch {
            repository.customers.collect { list ->
                if (_selectedCustomer.value == null && list.isNotEmpty()) {
                    _selectedCustomer.value = list.first()
                }
            }
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCustomer(customer: Customer) {
        _selectedCustomer.value = customer
    }

    fun setPayment(payment: String) {
        _selectedPayment.value = payment
    }

    fun setDiscount(value: Double) {
        _discount.value = maxOf(0.0, value)
    }

    fun inspectProduct(product: Product?) {
        _inspectingProduct.value = product
    }

    fun inspectCustomer(customer: Customer?) {
        _selectedCustomerForDetail.value = customer
    }

    fun dismissCompletedOrder() {
        _completedOrder.value = null
    }

    fun addToCart(product: Product) {
        _cart.update { current ->
            val existing = current.find { it.product.id == product.id }
            if (existing != null) {
                current.map {
                    if (it.product.id == product.id) it.copy(quantity = it.quantity + 1) else it
                }
            } else {
                current + CartItem(product = product, quantity = 1)
            }
        }
        showNotice("تمت إضافة \"${product.name}\" للسلة")
    }

    fun updateQuantity(productId: String, delta: Int) {
        _cart.update { current ->
            current.mapNotNull { item ->
                if (item.product.id == productId) {
                    val newQty = item.quantity + delta
                    if (newQty > 0) item.copy(quantity = newQty) else null
                } else {
                    item
                }
            }
        }
    }

    fun clearCart() {
        _cart.value = emptyList()
    }

    fun finishSale(): Boolean {
        val currentCart = _cart.value
        if (currentCart.isEmpty()) {
            showNotice("أضف صنفًا واحدًا على الأقل قبل إتمام البيع")
            return false
        }

        val subtotal = currentCart.sumOf { it.totalPrice }
        val vat = subtotal * 0.15
        val disc = _discount.value
        val total = maxOf(0.0, subtotal + vat - disc)
        val customer = _selectedCustomer.value ?: repository.customers.value.first()
        val payment = _selectedPayment.value

        val order = SaleOrder(
            id = "INV-" + (1000 + (repository.orders.value.size + 1)),
            items = currentCart,
            customer = customer,
            subtotal = subtotal,
            vat = vat,
            discount = disc,
            total = total,
            paymentMethod = payment
        )

        repository.recordSale(order)
        _cart.value = emptyList()
        _discount.value = 0.0
        _completedOrder.value = order
        showNotice("تم تسجيل البيع بنجاح (فاتورة ${order.id})")
        return true
    }

    fun addNewProduct(
        name: String,
        category: String,
        price: Double,
        costPrice: Double,
        wholesalePrice: Double,
        stock: Int,
        model: String,
        unit: String,
        barcode: String,
        note: String
    ) {
        val newProd = Product(
            id = "p_" + UUID.randomUUID().toString().take(6),
            name = name.trim(),
            category = category.trim().ifEmpty { "أدوات منزلية" },
            price = price,
            costPrice = costPrice,
            wholesalePrice = wholesalePrice,
            stock = stock,
            model = model.trim(),
            unit = unit.trim().ifEmpty { "حبة" },
            barcode = barcode.trim(),
            note = note.trim(),
            colorHex = "#E7F5F2"
        )
        repository.addProduct(newProd)
        showNotice("تمت إضافة الصنف \"${newProd.name}\" بنجاح")
    }

    fun deleteProduct(productId: String) {
        repository.deleteProduct(productId)
        showNotice("تم حذف الصنف بنجاح")
    }

    fun addNewCategory(name: String) {
        if (name.isNotBlank()) {
            repository.addCategory(name.trim())
            showNotice("تمت إضافة قسم \"$name\"")
        }
    }

    fun addNewCustomer(name: String, phone: String, initialDebt: Double) {
        if (name.isNotBlank()) {
            val customer = Customer(
                id = "cust_" + UUID.randomUUID().toString().take(6),
                name = name.trim(),
                detail = if (initialDebt > 0) "دين: ${"%,.2f".format(initialDebt)} ريال" else "حساب جديد",
                debt = initialDebt,
                phone = phone.trim().ifEmpty { "-" }
            )
            repository.addCustomer(customer)
            showNotice("تمت إضافة العميل \"${customer.name}\"")
        }
    }

    fun settleDebt(customerId: String, amount: Double) {
        if (amount > 0) {
            repository.settleCustomerDebt(customerId, amount)
            showNotice("تم تسجيل سداد مبلغ ${"%,.2f".format(amount)} ريال")
            _selectedCustomerForDetail.value = null
        }
    }

    private fun showNotice(message: String) {
        _notice.value = message
    }

    fun dismissNotice() {
        _notice.value = null
    }
}
