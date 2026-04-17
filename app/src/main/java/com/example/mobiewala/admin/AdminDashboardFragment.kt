package com.example.mobiewala.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mobiewala.R
import com.example.mobiewala.databinding.FragmentAdminDashboardBinding

class AdminDashboardFragment : Fragment() {

    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!

    private val adminViewModel: AdminViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adminViewModel.allUsers.observe(viewLifecycleOwner) { users ->
            binding.usersCountTextView.text = (users.size - 1).toString() // Exclude admin
        }

        adminViewModel.allOrders.observe(viewLifecycleOwner) { orders ->
            binding.ordersCountTextView.text = orders.size.toString()
        }

        adminViewModel.allProducts.observe(viewLifecycleOwner) { products ->
            binding.productsCountTextView.text = products.size.toString()
        }

        binding.manageProductsButton.setOnClickListener {
            findNavController().navigate(R.id.action_adminDashboardFragment_to_manageProductsFragment)
        }

        binding.manageUsersButton.setOnClickListener {
            findNavController().navigate(R.id.action_adminDashboardFragment_to_manageUsersFragment)
        }

        binding.manageOrdersButton.setOnClickListener {
            findNavController().navigate(R.id.action_adminDashboardFragment_to_manageOrdersFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}