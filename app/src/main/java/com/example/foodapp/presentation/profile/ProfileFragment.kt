package com.example.foodapp.presentation.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.foodapp.R
import com.example.foodapp.databinding.FragmentProfileBinding
import com.example.foodapp.presentation.main.MainActivity
import com.example.foodapp.utils.proceedWhen
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val profileViewModel: ProfileViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        showUserData()
        setClickListener()
        observeEditMode()
    }

    private fun doEditProfile() {
        if (checkNameValidation()) {
            val fullName = binding.layoutProfileBody.nameEditText.text.toString().trim()
            proceedEdit(fullName)
        }
    }

    private fun proceedEdit(fullName: String) {
        profileViewModel.updateProfileName(fullName = fullName).observe(viewLifecycleOwner) {
            it.proceedWhen(
                doOnSuccess = {
                    binding.layoutProfileBody.pbLoading.isVisible = false
                    binding.layoutProfileBody.btnChangeProfile.isVisible = true
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.text_change_profile_data_success),
                        Toast.LENGTH_SHORT,
                    ).show()
                    binding.layoutProfileBody.btnChangeProfile.isEnabled = false
                },
                doOnError = {
                    binding.layoutProfileBody.pbLoading.isVisible = false
                    binding.layoutProfileBody.btnChangeProfile.isVisible = true
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.text_change_profile_data_failed),
                        Toast.LENGTH_SHORT,
                    ).show()
                },
                doOnLoading = {
                    binding.layoutProfileBody.pbLoading.isVisible = true
                    binding.layoutProfileBody.btnChangeProfile.isVisible = false
                },
            )
        }
    }

    private fun requestChangePassword() {
        profileViewModel.createChangePwdRequest()
        val dialog =
            AlertDialog.Builder(requireContext())
                .setMessage(
                    "Change password request sended to your email : ${profileViewModel.getCurrentUser()?.email} Please check to your inbox or spam",
                )
                .setPositiveButton(
                    "Okay",
                ) { dialog, id ->
                }.create()
        dialog.show()
    }

    private fun showUserData() {
        profileViewModel.getCurrentUser()?.let {
            binding.layoutProfileBody.nameEditText.setText(it.fullName)
//            binding.layoutProfileBody.usernameEditText.setText(it.username)
            binding.layoutProfileBody.emailEditText.setText(it.email)
        }
    }

    private fun checkNameValidation(): Boolean {
        val fullName = binding.layoutProfileBody.nameEditText.text.toString().trim()
        return if (fullName.isEmpty()) {
            binding.layoutProfileBody.nameInputLayout.isErrorEnabled = true
            binding.layoutProfileBody.nameInputLayout.error =
                getString(R.string.text_error_name_cannot_empty)
            false
        } else {
            binding.layoutProfileBody.nameInputLayout.isErrorEnabled = false
            true
        }
    }

    private fun observeEditMode() {
        profileViewModel.isEditMode.observe(viewLifecycleOwner) {
            binding.layoutProfileBody.nameEditText.isEnabled = it
            binding.layoutProfileBody.btnChangeProfile.isEnabled = it
        }
    }

    private fun setClickListener() {
        binding.layoutProfileHeader.ivIcEditText.setOnClickListener {
            profileViewModel.changeEditMode()
        }
        binding.layoutProfileBody.btnChangeProfile.setOnClickListener {
            doEditProfile()
        }
        binding.layoutProfileBody.btnLogout.setOnClickListener {
            doLogout()
        }
        binding.layoutProfileBody.btnResetPassword.setOnClickListener {
            requestChangePassword()
        }
    }

    private fun navigateToMenu() {
        startActivity(
            Intent(requireContext(), MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
        )
    }

    private fun doLogout() {
        val dialog =
            AlertDialog.Builder(requireContext()).setMessage("Do you want to logout ?")
                .setPositiveButton(
                    "Yes",
                ) { dialog, id ->
                    profileViewModel.doLogout()
                    navigateToMenu()
                }
                .setNegativeButton(
                    "No",
                ) { dialog, id ->
                    // no-op , do nothing
                }.create()
        dialog.show()
    }
}
