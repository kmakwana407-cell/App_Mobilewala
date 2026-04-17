package com.example.mobiewala.user

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mobiewala.R
import com.example.mobiewala.database.AppDatabase
import com.example.mobiewala.database.UserEntity
import com.example.mobiewala.databinding.FragmentProfileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private var currentUser: UserEntity? = null
    private var selectedImageUri: Uri? = null

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.profileImage.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initial state: Everything blank as requested
        clearFields()

        binding.btnEditProfile.setOnClickListener {
            loadAndEnableEditing()
        }

        binding.btnSaveProfile.setOnClickListener {
            saveProfileChanges()
        }

        binding.btnChangePhoto.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        binding.btnLogout.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun clearFields() {
        binding.etName.setText("")
        binding.etEmail.setText("")
        binding.etPassword.setText("")
        binding.etBirthDate.setText("")
        binding.etMobile.setText("")
        binding.profileImage.setImageResource(R.drawable.ic_profile)
        
        setEditingEnabled(false)
    }

    private fun setEditingEnabled(enabled: Boolean) {
        binding.etName.isEnabled = enabled
        binding.etEmail.isEnabled = enabled
        binding.etPassword.isEnabled = enabled
        binding.etBirthDate.isEnabled = enabled
        binding.etMobile.isEnabled = enabled
        
        binding.btnChangePhoto.visibility = if (enabled) View.VISIBLE else View.GONE
        binding.btnSaveProfile.visibility = if (enabled) View.VISIBLE else View.GONE
        binding.btnEditProfile.visibility = if (enabled) View.GONE else View.VISIBLE
    }

    private fun loadAndEnableEditing() {
        val userDao = AppDatabase.getDatabase(requireContext()).userDao()
        
        lifecycleScope.launch {
            // Using ID 2L assuming 1L is Admin and 2L is the first registered user
            val user = withContext(Dispatchers.IO) { userDao.getUserById(2L) }
            
            if (user != null) {
                currentUser = user
                binding.etName.setText(user.name)
                binding.etEmail.setText(user.email)
                binding.etPassword.setText(user.password)
                binding.etBirthDate.setText(user.birthDate ?: "")
                binding.etMobile.setText(user.mobileNumber ?: "")
                
                user.imagePath?.let {
                    Glide.with(this@ProfileFragment).load(File(it)).into(binding.profileImage)
                }
                
                setEditingEnabled(true)
            } else {
                Toast.makeText(requireContext(), "User not found. Please sign up first.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveProfileChanges() {
        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val birthDate = binding.etBirthDate.text.toString()
        val mobile = binding.etMobile.text.toString()

        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            Toast.makeText(requireContext(), "Name, Email and Password are required", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            var imagePath = currentUser?.imagePath
            selectedImageUri?.let { uri ->
                val savedPath = saveImageToInternalStorage(uri, "user_profile_${System.currentTimeMillis()}.jpg")
                if (savedPath != null) imagePath = savedPath
            }

            val updatedUser = currentUser?.copy(
                name = name,
                email = email,
                password = password,
                birthDate = birthDate,
                mobileNumber = mobile,
                imagePath = imagePath
            )

            if (updatedUser != null) {
                withContext(Dispatchers.IO) {
                    AppDatabase.getDatabase(requireContext()).userDao().update(updatedUser)
                }
                currentUser = updatedUser
                setEditingEnabled(false)
                Toast.makeText(requireContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImageToInternalStorage(uri: Uri, fileName: String): String? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)!!
            val file = File(requireContext().filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}