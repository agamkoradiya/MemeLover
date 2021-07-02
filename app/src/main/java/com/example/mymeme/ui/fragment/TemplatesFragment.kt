package com.example.mymeme.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mymeme.R
import com.example.mymeme.adapter.ItemOffsetDecoration
import com.example.mymeme.adapter.TemplateImgFlipAdapter
import com.example.mymeme.adapter.TemplateMemeGenAdapter
import com.example.mymeme.databinding.FragmentTemplatesBinding
import com.example.mymeme.other.*
import com.example.mymeme.ui.viewmodel.TemplatesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

private const val TAG = "TemplatesFragment"

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TemplatesFragment : Fragment() {

    private var _binding: FragmentTemplatesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TemplatesViewModel by viewModels()

    private var isInternetAvailable: Boolean = false

    @Inject
    lateinit var memeGenAdapter: TemplateMemeGenAdapter

    @Inject
    lateinit var imgFlipAdapter: TemplateImgFlipAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTemplatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkingInternetAvailability()

        memeGenTemplate()

        binding.memeGenTemplateRetryBtn.setOnClickListener {
            if (isInternetAvailable) {
                binding.memeGenTemplateRetryBtn.makeItGone()
                viewModel.loadMemeGenTemplates()
            } else
                binding.root.snack("Check your Internet Connection!!!")
        }

        memeGenAdapter.setOnTemplateMemeGenClickListener {
            Log.d(TAG, "setOnTemplateMemeGenClickListener: $it")
            if (isInternetAvailable) {
                val action =
                    TemplatesFragmentDirections.actionTemplatesFragmentToMemeGenMakerFragment(it)
                findNavController().navigate(action)
            } else {
                binding.root.snack("Check your Internet Connection!!!")
            }
        }

        imgFlipTemplate()

        binding.imgFlipTemplateRetryBtn.setOnClickListener {
            if (isInternetAvailable) {
                binding.imgFlipTemplateRetryBtn.makeItGone()
                viewModel.loadImgFlipTemplates()
            } else
                binding.root.snack("Check your Internet Connection!!!")
        }

        imgFlipAdapter.setOnTemplateImgFlipClickListener {
            Log.d(TAG, "setOnTemplateImgFlipClickListener: $it")
            if (isInternetAvailable) {
                val action =
                    TemplatesFragmentDirections.actionTemplatesFragmentToImgFlipMakerFragment(it)
                findNavController().navigate(action)
            } else {
                binding.root.snack("Check your Internet Connection!!!")
            }

        }
    }

    private fun imgFlipTemplate() {

        viewModel.loadImgFlipTemplates()

        binding.imgFlipTemplateRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        val itemDecoration = ItemOffsetDecoration(requireContext(), R.dimen.item_offset)
        binding.imgFlipTemplateRecyclerView.addItemDecoration(itemDecoration)

        binding.imgFlipTemplateRecyclerView.adapter = imgFlipAdapter

        viewModel.getImgFlipTemplates.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    is Resource.Error -> {
                        binding.imgFlipTemplateProgressBar.makeItGone()
                        binding.imgFlipTemplateRetryBtn.makeItVisible()
                        binding.root.snack(it.message.toString())
                        Log.d(TAG, "Resource error -> ${it.message}")
                    }
                    is Resource.Loading -> {
                        binding.imgFlipTemplateProgressBar.makeItVisible()
                        binding.imgFlipTemplateRetryBtn.makeItGone()
                    }
                    is Resource.Success -> {
                        binding.imgFlipTemplateProgressBar.makeItGone()
                        binding.imgFlipTemplateRetryBtn.makeItGone()
                        imgFlipAdapter.submitList(it.data?.data?.memes?.filter { meme -> meme.box_count < 6 })
                    }
                }
            }
        }
    }

    private fun memeGenTemplate() {
        viewModel.loadMemeGenTemplates()

        binding.memeGenTemplateRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        val itemDecoration = ItemOffsetDecoration(requireContext(), R.dimen.item_offset)
        binding.memeGenTemplateRecyclerView.addItemDecoration(itemDecoration)

        binding.memeGenTemplateRecyclerView.adapter = memeGenAdapter
        viewModel.getMemeGenTemplates.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    is Resource.Error -> {
                        binding.memeGenTemplateProgressBar.makeItGone()
                        binding.memeGenTemplateRetryBtn.makeItVisible()
                        binding.root.snack(it.message.toString())
                        Log.d(TAG, "Resource error -> ${it.message}")
                    }
                    is Resource.Loading -> {
                        binding.memeGenTemplateProgressBar.makeItVisible()
                        binding.memeGenTemplateRetryBtn.makeItGone()
                    }
                    is Resource.Success -> {
                        binding.memeGenTemplateProgressBar.makeItGone()
                        binding.memeGenTemplateRetryBtn.makeItGone()
                        memeGenAdapter.submitList(it.data?.filter { memeGenTemplateItem -> memeGenTemplateItem.lines < 6 })
                    }
                }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}