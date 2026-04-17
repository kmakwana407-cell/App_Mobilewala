package com.example.mobiewala.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobiewala.R
import com.example.mobiewala.database.AppDatabase
import com.example.mobiewala.database.ProductRepository
import com.example.mobiewala.databinding.FragmentCartBinding
import com.google.gson.Gson
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter
    private var selectedPaymentMethod: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        cartAdapter = CartAdapter(
            onRemoveClick = { userViewModel.removeFromCart(it) },
            onUpdateQuantity = { cartItem, newQuantity ->
                lifecycleScope.launch {
                    val db = AppDatabase.getDatabase(requireContext())
                    db.cartDao().delete(cartItem)
                    db.cartDao().insert(cartItem.copy(id = 0, quantity = newQuantity))
                }
            },
            onUpdateColor = { cartItem, newColor ->
                lifecycleScope.launch {
                    val db = AppDatabase.getDatabase(requireContext())
                    db.cartDao().delete(cartItem)
                    db.cartDao().insert(cartItem.copy(id = 0, selectedColor = newColor))
                }
            }
        )

        binding.cartRecyclerView.adapter = cartAdapter

        binding.continueShoppingButton.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        setupPaymentSelection()

        userViewModel.getCartItems(1L).observe(viewLifecycleOwner) { cartItems ->
            val productDao = AppDatabase.getDatabase(requireContext()).productDao()
            val productRepository = ProductRepository(productDao)
            
            lifecycleScope.launch {
                val productsWithCart = cartItems.mapNotNull { cartItem ->
                    productRepository.getProductById(cartItem.productId)?.let { product ->
                        cartItem to product
                    }
                }
                cartAdapter.submitList(productsWithCart)

                val totalCount = cartItems.sumOf { it.quantity }
                val totalPrice = productsWithCart.sumOf { it.second.price * it.first.quantity }

                binding.totalMobileText.text = "Total Mobile : $totalCount"
                binding.totalAmountText.text = "Total ₹ : ${String.format("%.2f", totalPrice)}"

                binding.confirmOrderButton.setOnClickListener {
                    confirmOrder(productsWithCart, totalPrice)
                }
            }
        }
    }

    private fun setupPaymentSelection() {
        binding.paymentUpi.setOnClickListener { selectPayment("UPI") }
        binding.paymentCard.setOnClickListener { selectPayment("CARD") }
        binding.paymentCod.setOnClickListener { selectPayment("COD") }
    }

    private fun selectPayment(method: String) {
        selectedPaymentMethod = method
        binding.checkUpi.visibility = if (method == "UPI") View.VISIBLE else View.GONE
        binding.checkCard.visibility = if (method == "CARD") View.VISIBLE else View.GONE
        binding.checkCod.visibility = if (method == "COD") View.VISIBLE else View.GONE
    }

    private fun confirmOrder(productsWithCart: List<Pair<com.example.mobiewala.database.CartEntity, com.example.mobiewala.database.ProductEntity>>, totalPrice: Double) {
        val name = binding.nameInput.text.toString()
        val mobile = binding.mobileInput.text.toString()
        val address = binding.addressInput.text.toString()

        if (name.isBlank() || mobile.isBlank() || address.isBlank()) {
            Toast.makeText(requireContext(), "Please fill all delivery details", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedPaymentMethod == null) {
            Toast.makeText(requireContext(), "Please select a payment method", Toast.LENGTH_SHORT).show()
            return
        }

        val orderProducts = productsWithCart.map { (cart, product) ->
            val firstImage = product.imagePaths.split(",").filter { it.isNotBlank() }.firstOrNull() ?: ""
            mapOf(
                "name" to product.name, 
                "quantity" to cart.quantity, 
                "price" to product.price,
                "color" to cart.selectedColor,
                "image" to firstImage
            )
        }
        val productJson = Gson().toJson(orderProducts)

        userViewModel.placeOrder(1L, name, mobile, address, productJson, totalPrice)
        
        Toast.makeText(requireContext(), "Order placed using $selectedPaymentMethod!", Toast.LENGTH_LONG).show()
        findNavController().navigate(R.id.ordersFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}