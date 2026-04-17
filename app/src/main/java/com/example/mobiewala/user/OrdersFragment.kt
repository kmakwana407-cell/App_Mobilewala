package com.example.mobiewala.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.mobiewala.database.AppDatabase
import com.example.mobiewala.database.OrderEntity
import com.example.mobiewala.databinding.FragmentOrdersBinding
import kotlinx.coroutines.launch

class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels()
    private lateinit var orderAdapter: OrderAdapter
    private var allOrders: List<OrderEntity> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderAdapter = OrderAdapter { order ->
            cancelOrder(order)
        }

        binding.ordersRecyclerView.adapter = orderAdapter

        // Using fixed user ID 1L for now
        userViewModel.getOrderHistory(1L).observe(viewLifecycleOwner) { orders ->
            allOrders = orders
            filterOrders("Pending") // Default view
        }

        setupTabButtons()
    }

    private fun setupTabButtons() {
        binding.btnPending.setOnClickListener { filterOrders("Pending") }
        binding.btnCompleted.setOnClickListener { filterOrders("Completed") }
        binding.btnCanceled.setOnClickListener { filterOrders("Canceled") }
    }

    private fun filterOrders(status: String) {
        val filtered = allOrders.filter { it.status.equals(status, ignoreCase = true) }
        orderAdapter.submitList(filtered)
        
        // Visual feedback for selected tab (Optional enhancement)
        binding.btnPending.alpha = if (status == "Pending") 1.0f else 0.5f
        binding.btnCompleted.alpha = if (status == "Completed") 1.0f else 0.5f
        binding.btnCanceled.alpha = if (status == "Canceled") 1.0f else 0.5f
    }

    private fun cancelOrder(order: OrderEntity) {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireContext())
            val updatedOrder = order.copy(status = "Canceled")
            db.orderDao().update(updatedOrder)
            Toast.makeText(requireContext(), "Order Canceled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}