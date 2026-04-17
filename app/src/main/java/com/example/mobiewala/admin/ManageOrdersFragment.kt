package com.example.mobiewala.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mobiewala.databinding.FragmentManageOrdersBinding

class ManageOrdersFragment : Fragment() {

    private var _binding: FragmentManageOrdersBinding? = null
    private val binding get() = _binding!!

    private val adminViewModel: AdminViewModel by viewModels()
    private lateinit var manageOrdersAdapter: ManageOrdersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manageOrdersAdapter = ManageOrdersAdapter {
            val newStatus = when (it.status) {
                "Pending" -> "Shipped"
                "Shipped" -> "Delivered"
                else -> "Delivered"
            }
            adminViewModel.updateOrderStatus(it, newStatus)
        }

        binding.ordersRecyclerView.adapter = manageOrdersAdapter

        adminViewModel.allOrders.observe(viewLifecycleOwner) { orders ->
            manageOrdersAdapter.submitList(orders)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}