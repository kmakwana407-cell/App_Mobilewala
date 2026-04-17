package com.example.mobiewala.user

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mobiewala.R
import com.example.mobiewala.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Header Profile Icon Link
        binding.profileIcon.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }

        productAdapter = ProductAdapter { product ->
            val action = HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(product.id)
            findNavController().navigate(action)
        }

        binding.productsRecyclerView.adapter = productAdapter

        userViewModel.allProducts.observe(viewLifecycleOwner) {
            products -> productAdapter.submitList(products)
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filter(text: String) {
        val filteredList = userViewModel.allProducts.value?.filter {
            it.name.contains(text, ignoreCase = true)
        }
        productAdapter.submitList(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}