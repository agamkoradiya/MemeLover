package com.example.mymeme.ui.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.mymeme.databinding.FragmentMemeGenMakerBinding
import com.example.mymeme.other.*
import com.example.mymeme.other.Permissions.hasExternalStoragePermission
import com.example.mymeme.other.Permissions.requestExternalStoragePermission
import com.example.mymeme.ui.viewmodel.MakerViewModel
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

private const val TAG = "MemeGenMakerFragment"

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MemeGenMakerFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentMemeGenMakerBinding? = null
    private val binding get() = _binding!!

    private val args: MemeGenMakerFragmentArgs by navArgs()

    private val viewModel: MakerViewModel by viewModels()

    private var isInternetAvailable: Boolean = false

    private var formattedUrl: String? = null

    @Inject
    lateinit var glide: RequestManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMemeGenMakerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Checking Internet Availability
        checkingInternetAvailability()

        setVisibilityForRequiredEditText()

        binding.checkMarkFabBtn.setOnClickListener {
            it.hideKeyboard()
            if (isInternetAvailable) {
                getFormattedUrlFromViewModel()
                Log.d(TAG, "formattedUrl -> $formattedUrl")

                showPreview()
            } else {
                binding.root.snack("Check your Internet Connection!!!")
            }
        }

        binding.imagePreviewRetryBtn.setOnClickListener {
            showPreview()
        }

        binding.downloadFabButton.setOnClickListener {
            it.hideKeyboard()
            if (isInternetAvailable) {
                if (hasExternalStoragePermission(requireContext())) {
                    downloadIt()
                } else {
                    requestExternalStoragePermission(this)
                }
            } else {
                binding.root.snack("Check your Internet Connection!!!")
            }
        }

        binding.downloadWithSizeFabButton.setOnClickListener {
            it.hideKeyboard()
            if (binding.heightTxt.text.isNullOrEmpty() && binding.widthTxt.text.isNullOrEmpty()) {
                binding.root.snack("Enter Valid Height and Width")
            } else {
                if (isInternetAvailable) {
                    if (hasExternalStoragePermission(requireContext())) {
                        formattedUrl?.let { url ->
                            viewModel.getBitmapFromUrl(
                                viewModel.downloadCustomMemeForMemeGen(
                                    url,
                                    binding.heightTxt.text.toString().toInt(),
                                    binding.widthTxt.text.toString().toInt()
                                ), requireContext(), binding.root
                            )
                        } ?: viewModel.getBitmapFromUrl(
                            viewModel.downloadCustomMemeForMemeGen(
                                args.memeGenTemplateItem.blank,
                                binding.heightTxt.text.toString().toInt(),
                                binding.widthTxt.text.toString().toInt()
                            ), requireContext(), binding.root
                        )
                    } else {
                        requestExternalStoragePermission(this)
                    }
                } else {
                    binding.root.snack("Check your Internet Connection!!!")
                }
            }
        }
    }

    private fun downloadIt() {
        formattedUrl?.let { url ->
            viewModel.getBitmapFromUrl(url, requireContext(), binding.root)
        } ?: viewModel.getBitmapFromUrl(
            args.memeGenTemplateItem.blank,
            requireContext(),
            binding.root
        )
    }

    private fun checkingInternetAvailability() {
        lifecycleScope.launchWhenStarted {
            requireContext().checkNetwork().collect {
                isInternetAvailable = it

                when (it) {
                    true -> {
                        binding.root.snack("Internet Connection is Available")
                    }
                    false -> {
                        binding.root.snack("Check your Internet Connection!!!")
                    }
                }
            }
        }
    }

    private fun setVisibilityForRequiredEditText() {
        glide.load(args.memeGenTemplateItem.blank).into(binding.imagePreviewImg)
        when (args.memeGenTemplateItem.lines) {
            1 -> {
                binding.firstLineTxtLayout.makeItVisible()
            }
            2 -> {
                binding.firstLineTxtLayout.makeItVisible()
                binding.secondLineTxtLayout.makeItVisible()
            }
            3 -> {
                binding.firstLineTxtLayout.makeItVisible()
                binding.secondLineTxtLayout.makeItVisible()
                binding.thirdLineTxtLayout.makeItVisible()
            }
            4 -> {
                binding.firstLineTxtLayout.makeItVisible()
                binding.secondLineTxtLayout.makeItVisible()
                binding.thirdLineTxtLayout.makeItVisible()
                binding.forthLineTxtLayout.makeItVisible()
            }
            5 -> {
                binding.firstLineTxtLayout.makeItVisible()
                binding.secondLineTxtLayout.makeItVisible()
                binding.thirdLineTxtLayout.makeItVisible()
                binding.forthLineTxtLayout.makeItVisible()
                binding.fifthLineTxtLayout.makeItVisible()
            }
        }
    }

    private fun getFormattedUrlFromViewModel() {
        formattedUrl = when (args.memeGenTemplateItem.lines) {
            1 -> {
                viewModel.getFormattedUrlForMemeGen(
                    args.memeGenTemplateItem.id,
                    binding.firstLineTxt.text.toString().trim(),
                )
            }
            2 -> {
                viewModel.getFormattedUrlForMemeGen(
                    args.memeGenTemplateItem.id,
                    binding.firstLineTxt.text.toString().trim(),
                    binding.secondLineTxt.text.toString().trim(),
                )
            }
            3 -> {
                viewModel.getFormattedUrlForMemeGen(
                    args.memeGenTemplateItem.id,
                    binding.firstLineTxt.text.toString().trim(),
                    binding.secondLineTxt.text.toString().trim(),
                    binding.thirdLineTxt.text.toString().trim(),
                )
            }
            4 -> {
                viewModel.getFormattedUrlForMemeGen(
                    args.memeGenTemplateItem.id,
                    binding.firstLineTxt.text.toString().trim(),
                    binding.secondLineTxt.text.toString().trim(),
                    binding.thirdLineTxt.text.toString().trim(),
                    binding.forthLineTxt.text.toString().trim(),
                )
            }
            5 -> {
                viewModel.getFormattedUrlForMemeGen(
                    args.memeGenTemplateItem.id,
                    binding.firstLineTxt.text.toString().trim(),
                    binding.secondLineTxt.text.toString().trim(),
                    binding.thirdLineTxt.text.toString().trim(),
                    binding.forthLineTxt.text.toString().trim(),
                    binding.fifthLineTxt.text.toString().trim(),
                )
            }
            else -> null
        }
    }

    private fun showPreview() {
        formattedUrl?.let {
            binding.progressBar.makeItVisible()
            binding.imagePreviewRetryBtn.makeItGone()
            glide.load(it).listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.progressBar.makeItGone()
                    binding.imagePreviewRetryBtn.makeItVisible()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.progressBar.makeItGone()
                    binding.imagePreviewRetryBtn.makeItGone()
                    return false
                }
            }).into(binding.imagePreviewImg)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(requireActivity()).build().show()
        } else {
            requestExternalStoragePermission(this)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        downloadIt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}