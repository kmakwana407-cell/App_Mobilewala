package com.example.mobiewala.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mobiewala.databinding.FragmentManageUsersBinding

class ManageUsersFragment : Fragment() {

    private var _binding: FragmentManageUsersBinding? = null
    private val binding get() = _binding!!

    private val adminViewModel: AdminViewModel by viewModels()
    private lateinit var manageUsersAdapter: ManageUsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manageUsersAdapter = ManageUsersAdapter {
            adminViewModel.deleteUser(it)
        }

        binding.usersRecyclerView.adapter = manageUsersAdapter

        adminViewModel.allUsers.observe(viewLifecycleOwner) { users ->
            manageUsersAdapter.submitList(users)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}