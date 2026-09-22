package com.wardcare.app.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wardcare.app.data.local.UserManager
import com.wardcare.app.databinding.FragmentUserProfileBinding

class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProfile()

        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }
    }

    private fun loadProfile() {
        val userManager = UserManager(requireContext())
        binding.tvProfileName.text = userManager.userName
        binding.tvProfileRole.text = "${userManager.userRole} — ${userManager.userWard}"
        binding.tvProfileEmail.text = "Email: ${userManager.userEmail}"
        binding.tvProfileWard.text = "Ward / Department: ${userManager.userWard}"
        binding.tvProfileStaffId.text = "Staff ID: ${userManager.staffId}"
    }

    private fun showEditProfileDialog() {
        val userManager = UserManager(requireContext())
        val editText = EditText(requireContext()).apply {
            setText(userManager.userName)
            hint = "Full Name"
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Full Name")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty()) {
                    userManager.userName = newName
                    loadProfile()
                    Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
