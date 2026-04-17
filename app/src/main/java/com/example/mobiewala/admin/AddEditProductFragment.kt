package com.example.mobiewala.admin

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.mobiewala.database.ProductEntity
import com.example.mobiewala.databinding.FragmentAddEditProductBinding
import java.io.File
import java.io.FileOutputStream

class AddEditProductFragment : Fragment() {

    private var _binding: FragmentAddEditProductBinding? = null
    private val binding get() = _binding!!

    private val adminViewModel: AdminViewModel by viewModels()
    private val args: AddEditProductFragmentArgs by navArgs()

    private val imageUris = mutableMapOf<Int, Uri?>()
    private var existingImagePaths = ""

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val targetImageView = when (imageRequestCode) {
                1 -> binding.imageView1
                2 -> binding.imageView2
                3 -> binding.imageView3
                else -> null
            }
            targetImageView?.let {
                it.setImageURI(uri)
                imageUris[imageRequestCode] = uri
            }
        }
    }

    private var imageRequestCode = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupImagePickers()

        val productId = args.productId
        if (productId != -1L) {
            adminViewModel.allProducts.observe(viewLifecycleOwner) { products ->
                val product = products.find { it.id == productId }
                product?.let { populateFields(it) }
            }
        }

        binding.saveProductButton.setOnClickListener {
            saveProduct(productId)
        }
    }

    private fun setupImagePickers() {
        binding.selectImage1Button.setOnClickListener { pickImage(1) }
        binding.selectImage2Button.setOnClickListener { pickImage(2) }
        binding.selectImage3Button.setOnClickListener { pickImage(3) }
    }

    private fun pickImage(requestCode: Int) {
        imageRequestCode = requestCode
        selectImageLauncher.launch("image/*")
    }

    private fun populateFields(product: ProductEntity) {
        binding.nameEditText.setText(product.name)
        binding.brandEditText.setText(product.brand)
        binding.priceEditText.setText(product.price.toString())
        binding.ramEditText.setText(product.ram)
        binding.storageEditText.setText(product.storage)
        binding.batteryEditText.setText(product.battery)
        binding.osEditText.setText(product.os)
        binding.colorsEditText.setText(product.colors)
        binding.otherDetailsEditText.setText(product.otherDetails)
        
        existingImagePaths = product.imagePaths
        val paths = product.imagePaths.split(",").filter { it.isNotBlank() }
        loadImage(paths.getOrNull(0), binding.imageView1)
        loadImage(paths.getOrNull(1), binding.imageView2)
        loadImage(paths.getOrNull(2), binding.imageView3)
    }

    private fun loadImage(path: String?, imageView: ImageView) {
        if (path != null) {
            Glide.with(this).load(File(path)).into(imageView)
        }
    }

    private fun saveProduct(productId: Long) {
        val newImagePaths = mutableListOf<String>()
        
        // Save new selected images
        for (i in 1..3) {
            val uri = imageUris[i]
            if (uri != null) {
                val path = saveImageToInternalStorage(uri, "product_${System.currentTimeMillis()}_$i.jpg")
                path?.let { newImagePaths.add(it) }
            } else {
                // Keep existing image if no new one is selected
                val existingPaths = existingImagePaths.split(",").filter { it.isNotBlank() }
                existingPaths.getOrNull(i-1)?.let { newImagePaths.add(it) }
            }
        }

        val finalImagePaths = newImagePaths.joinToString(",")

        if (finalImagePaths.isBlank() && productId == -1L) {
            Toast.makeText(requireContext(), "Please select at least one image", Toast.LENGTH_SHORT).show()
            return
        }
        
        val name = binding.nameEditText.text.toString()
        val brand = binding.brandEditText.text.toString()
        val price = binding.priceEditText.text.toString().toDoubleOrNull() ?: 0.0
        val ram = binding.ramEditText.text.toString()
        val storage = binding.storageEditText.text.toString()
        val battery = binding.batteryEditText.text.toString()
        val os = binding.osEditText.text.toString()
        val colors = binding.colorsEditText.text.toString()
        val otherDetails = binding.otherDetailsEditText.text.toString()

        if (name.isBlank() || brand.isBlank() || ram.isBlank() || storage.isBlank()) {
            Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val product = ProductEntity(
            id = if (productId == -1L) 0 else productId,
            name = name,
            brand = brand,
            price = price,
            storage = storage,
            ram = ram,
            battery = battery,
            os = os,
            colors = colors,
            imagePaths = finalImagePaths,
            otherDetails = otherDetails
        )

        if (productId == -1L) {
            adminViewModel.addProduct(
                product.name, product.brand, product.price, product.storage, 
                product.ram, product.battery, product.os, product.colors, 
                product.imagePaths, product.otherDetails
            )
        } else {
            adminViewModel.updateProduct(product)
        }
        
        Toast.makeText(requireContext(), "Product Saved Successfully", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
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