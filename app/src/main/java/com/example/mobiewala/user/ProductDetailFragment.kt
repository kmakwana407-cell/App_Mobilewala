package com.example.mobiewala.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mobiewala.R
import com.example.mobiewala.database.ProductRepository
import com.example.mobiewala.database.AppDatabase
import com.example.mobiewala.databinding.FragmentProductDetailBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import kotlinx.coroutines.launch

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels()
    private val args: ProductDetailFragmentArgs by navArgs()
    
    private var selectedPaymentMethod: String? = null
    private var selectedColor: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }

        // Header Profile Icon Link
        binding.profileIcon.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }

        setupPaymentSelection()

        val productId = args.productId
        val productDao = AppDatabase.getDatabase(requireContext()).productDao()
        val productRepository = ProductRepository(productDao)

        lifecycleScope.launch {
            val product = productRepository.getProductById(productId)
            product?.let { currentProduct ->
                // Basic Info
                binding.productNameTextView.text = currentProduct.name
                binding.productPriceTextView.text = "₹${String.format("%.2f", currentProduct.price)}"
                
                // Specifications
                binding.brandDetail.text = "Brand: ${currentProduct.brand}"
                binding.ramDetail.text = "RAM: ${currentProduct.ram}"
                binding.storageDetail.text = "Storage: ${currentProduct.storage}"
                binding.batteryDetail.text = "Battery: ${currentProduct.battery}"
                binding.osDetail.text = "OS: ${currentProduct.os}"
                binding.otherDetailsDetail.text = "Other: ${currentProduct.otherDetails ?: "N/A"}"

                // Setup Color Spinner
                val colors = currentProduct.colors.split(",").map { it.trim() }.filter { it.isNotBlank() }
                if (colors.isNotEmpty()) {
                    val colorAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, colors)
                    colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.colorSpinner.adapter = colorAdapter
                    binding.colorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                            selectedColor = colors[pos]
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                }

                // Setup Image Slider
                val images = currentProduct.imagePaths.split(",").filter { it.isNotBlank() }
                if (images.isNotEmpty()) {
                    val adapter = ProductImageAdapter(images)
                    binding.imageSlider.adapter = adapter
                    TabLayoutMediator(binding.sliderIndicator, binding.imageSlider) { _, _ -> }.attach()
                }

                binding.addToCartButton.setOnClickListener {
                    // Using fixed user ID 1L for now
                    userViewModel.addToCart(1L, productId, 1)
                    Toast.makeText(requireContext(), "Item added to cart successfully!", Toast.LENGTH_SHORT).show()
                }

                binding.confirmOrderButton.setOnClickListener {
                    confirmOrder(currentProduct)
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

    private fun confirmOrder(product: com.example.mobiewala.database.ProductEntity) {
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

        // Include image path in the JSON
        val firstImage = product.imagePaths.split(",").filter { it.isNotBlank() }.firstOrNull() ?: ""
        val orderProduct = listOf(mapOf(
            "name" to product.name,
            "quantity" to 1,
            "price" to product.price,
            "color" to selectedColor,
            "image" to firstImage
        ))
        val productJson = Gson().toJson(orderProduct)

        userViewModel.placeOrder(1L, name, mobile, address, productJson, product.price)
        
        Toast.makeText(requireContext(), "Order placed using $selectedPaymentMethod!", Toast.LENGTH_LONG).show()
        findNavController().navigate(R.id.ordersFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}