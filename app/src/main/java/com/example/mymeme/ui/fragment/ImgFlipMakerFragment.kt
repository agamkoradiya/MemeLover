package com.example.mymeme.ui.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.mymeme.databinding.FragmentImgFlipMakerBinding
import com.example.mymeme.other.*
import com.example.mymeme.ui.viewmodel.MakerViewModel
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

private const val TAG = "ImgFlipMakerFragment"

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ImgFlipMakerFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentImgFlipMakerBinding? = null
    private val binding get() = _binding!!

    private val args: ImgFlipMakerFragmentArgs by navArgs()

    private val viewModel: MakerViewModel by viewModels()

    private var isInternetAvailable: Boolean = false

    private var resultUrl: String? = null

    @Inject
    lateinit var glide: RequestManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImgFlipMakerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //Checking Internet Availability
        checkingInternetAvailability()

        setVisibilityForRequiredEditText()

        showPreview()

        binding.checkMarkFabBtn.setOnClickListener {
            it.hideKeyboard()
            if (isInternetAvailable) {
                sendPostReqToImgFlip()
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
                if (Permissions.hasExternalStoragePermission(requireContext())) {
                    downloadIt()
                } else {
                    Permissions.requestExternalStoragePermission(this)
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
                    if (Permissions.hasExternalStoragePermission(requireContext())) {
                        resultUrl?.let { url ->
                            viewModel.getBitmapFromUrl(
                                viewModel.downloadCustomMemeForImgFlip(
                                    url,
                                    binding.heightTxt.text.toString().toInt(),
                                    binding.widthTxt.text.toString().toInt()
                                ), requireContext(), binding.root
                            )
                        } ?: viewModel.getBitmapFromUrl(
                            viewModel.downloadCustomMemeForImgFlip(
                                args.imgFlipTemplateItem.url,
                                binding.heightTxt.text.toString().toInt(),
                                binding.widthTxt.text.toString().toInt()
                            ), requireContext(), binding.root
                        )
                    } else {
                        Permissions.requestExternalStoragePermission(this)
                    }
                } else {
                    binding.root.snack("Check your Internet Connection!!!")
                }
            }
        }

    }

    private fun downloadIt() {
        resultUrl?.let { url ->
            viewModel.getBitmapFromUrl(url, requireContext(), binding.root)
        } ?: viewModel.getBitmapFromUrl(
            args.imgFlipTemplateItem.url,
            requireContext(),
            binding.root
        )
    }

    private fun showPreview() {
        viewModel.getPostImgFlipMemeResult.observe(viewLifecycleOwner) {
            Log.d(TAG, "showPreview: called")
            when (it) {
                is Resource.Error -> {
                    binding.progressBar.makeItGone()
                    binding.imagePreviewRetryBtn.makeItVisible()
                }
                is Resource.Loading -> {
                    binding.progressBar.makeItVisible()
                    binding.imagePreviewRetryBtn.makeItGone()
                }
                is Resource.Success -> {
                    binding.progressBar.makeItGone()
                    binding.imagePreviewRetryBtn.makeItGone()

                    resultUrl = it.data?.data?.url
                    glide.load(it.data?.data?.url).listener(object : RequestListener<Drawable> {
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
        }
    }

    private fun sendPostReqToImgFlip() {
        when (args.imgFlipTemplateItem.box_count) {
            1 -> {
                viewModel.imgFlipMemeReq(
                    args.imgFlipTemplateItem.id,
                    binding.firstLineTxt.text.toString().trim(),
                )
            }
            2 -> {
                viewModel.imgFlipMemeReq(
                    args.imgFlipTemplateItem.id,
                    binding.firstLineTxt.text.toString().trim(),
                    binding.secondLineTxt.text.toString().trim(),
                )
            }
            3 -> {
                viewModel.imgFlipMemeReq(
                    args.imgFlipTemplateItem.id,
                    binding.firstLineTxt.text.toString().trim(),
                    binding.secondLineTxt.text.toString().trim(),
                    binding.thirdLineTxt.text.toString().trim(),
                )
            }
            4 -> {
                viewModel.imgFlipMemeReq(
                    args.imgFlipTemplateItem.id,
                    binding.firstLineTxt.text.toString().trim(),
                    binding.secondLineTxt.text.toString().trim(),
                    binding.thirdLineTxt.text.toString().trim(),
                    binding.forthLineTxt.text.toString().trim(),
                )
            }
            5 -> {
                viewModel.imgFlipMemeReq(
                    args.imgFlipTemplateItem.id,
                    binding.firstLineTxt.text.toString().trim(),
                    binding.secondLineTxt.text.toString().trim(),
                    binding.thirdLineTxt.text.toString().trim(),
                    binding.forthLineTxt.text.toString().trim(),
                    binding.fifthLineTxt.text.toString().trim(),
                )
            }
        }
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
        glide.load(args.imgFlipTemplateItem.url).into(binding.imagePreviewImg)
        when (args.imgFlipTemplateItem.box_count) {
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
            Permissions.requestExternalStoragePermission(this)
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