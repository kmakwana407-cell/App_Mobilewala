package com.example.mobiewala.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mobiewala.databinding.FragmentManageProductsBinding

class ManageProductsFragment : Fragment() {

    private var _binding: FragmentManageProductsBinding? = null
    private val binding get() = _binding!!

    private val adminViewModel: AdminViewModel by viewModels()
    private lateinit var manageProductsAdapter: ManageProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manageProductsAdapter = ManageProductsAdapter(
            onUpdateClick = { product ->
                val action = ManageProductsFragmentDirections.actionManageProductsFragmentToAddEditProductFragment(product.id)
                findNavController().navigate(action)
            },
            onDeleteClick = { product ->
                adminViewModel.deleteProduct(product)
            }
        )

        binding.productsRecyclerView.adapter = manageProductsAdapter

        adminViewModel.allProducts.observe(viewLifecycleOwner) { products ->
            manageProductsAdapter.submitList(products)
        }

        binding.fabAddProduct.setOnClickListener {
            val action = ManageProductsFragmentDirections.actionManageProductsFragmentToAddEditProductFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}