package com.example.mymeme.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.mymeme.R
import com.example.mymeme.adapter.HomeAdapter
import com.example.mymeme.data.entities.ItemInfo
import com.example.mymeme.databinding.FragmentHomeBinding
import com.example.mymeme.other.*
import com.example.mymeme.ui.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

private const val TAG = "HomeFragment"

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeFragment : Fragment(), OnItemInfoClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private var isInternetAvailable: Boolean = false

    private var isTopicDropDownBtnClicked: Boolean = false
    private var isInstagramDropDownBtnClicked: Boolean = false
    private var isDiscordDropDownBtnClicked: Boolean = false
    private var isTelegramDropDownBtnClicked: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val animSlideDown: Animation = AnimationUtils.loadAnimation(
            requireContext(), R.anim.slide_down
        )

        //Checking Internet Availability
        checkingInternetAvailability()

        binding.topicsDropDownBtn.setOnClickListener {
            if (!isTopicDropDownBtnClicked) {
                if (isInternetAvailable) {
                    val loadedData = viewModel.getHotTopics.value?.data
                    Log.d(TAG, "topicsDropDownBtn: ${loadedData.toString()}")
                    if (loadedData == null) {
                        viewModel.loadHotTopics()
                    }
                    val adapter = HomeAdapter(this)
                    binding.hotTopicsRecyclerView.adapter = adapter
                    viewModel.getHotTopics.observe(viewLifecycleOwner) {
                        when (it) {
                            is Resource.Error -> {
                                binding.topicsProgressBar.makeItGone()
                                binding.root.snack(it.message!!)
                            }
                            is Resource.Loading -> {
                                binding.topicsProgressBar.makeItVisible()
                            }
                            is Resource.Success -> {
                                binding.topicsProgressBar.makeItGone()
                                binding.hotTopicsRecyclerView.makeItVisible()

                                binding.hotTopicsRecyclerView.startAnimation(animSlideDown)

                                adapter.submitList(it.data)
                                binding.topicsDropDownBtn.setImageResource(R.drawable.ic_up_arrow)
                                isTopicDropDownBtnClicked = true
                            }
                        }
                    }
                } else {
                    binding.root.snack("Check your Internet Connection!")
                }
            } else {
                isTopicDropDownBtnClicked = !isTopicDropDownBtnClicked

                hideWithAnimate(binding.hotTopicsRecyclerView, binding.topicsDropDownBtn)
            }
        }

        binding.instagramDropDownBtn.setOnClickListener {
            if (!isInstagramDropDownBtnClicked) {
                if (isInternetAvailable) {
                    val loadedData = viewModel.getInstagramAccounts.value?.data
                    Log.d(TAG, "instagramDropDownBtn: ${loadedData.toString()}")
                    if (loadedData == null) {
                        viewModel.loadInstagramAccounts()
                    }
                    val adapter = HomeAdapter(this)
                    binding.instagramRecyclerView.adapter = adapter
                    viewModel.getInstagramAccounts.observe(viewLifecycleOwner) {
                        when (it) {
                            is Resource.Error -> {
                                binding.instagramProgressBar.makeItGone()
                                binding.root.snack(it.message!!)
                            }
                            is Resource.Loading -> {
                                binding.instagramProgressBar.makeItVisible()
                            }
                            is Resource.Success -> {
                                binding.instagramProgressBar.makeItGone()
                                if (it.data.isNullOrEmpty())
                                    return@observe
                                binding.instagramRecyclerView.makeItVisible()

                                binding.instagramRecyclerView.startAnimation(animSlideDown)

                                adapter.submitList(it.data)
                                binding.instagramDropDownBtn.setImageResource(R.drawable.ic_up_arrow)
                                isInstagramDropDownBtnClicked = true
                            }
                        }
                    }
                } else {
                    binding.root.snack("Check your Internet Connection!")
                }
            } else {
                isInstagramDropDownBtnClicked = !isInstagramDropDownBtnClicked

                hideWithAnimate(binding.instagramRecyclerView, binding.instagramDropDownBtn)

//                binding.instagramRecyclerView.visibility = View.GONE
//                binding.instagramDropDownBtn.setImageResource(R.drawable.ic_down_arrow)
            }
        }

        binding.discordDropDownBtn.setOnClickListener {
            if (!isDiscordDropDownBtnClicked) {
                if (isInternetAvailable) {
                    val loadedData = viewModel.getDiscordServers.value?.data
                    Log.d(TAG, "discordDropDownBtn: ${loadedData.toString()}")
                    if (loadedData == null) {
                        viewModel.loadDiscordServers()
                    }
                    val adapter = HomeAdapter(this)
                    binding.discordRecyclerView.adapter = adapter
                    viewModel.getDiscordServers.observe(viewLifecycleOwner) {
                        when (it) {
                            is Resource.Error -> {
                                binding.discordProgressBar.makeItGone()
                                binding.root.snack(it.message!!)
                            }
                            is Resource.Loading -> {
                                binding.discordProgressBar.makeItVisible()
                            }
                            is Resource.Success -> {
                                binding.discordProgressBar.makeItGone()
                                if (it.data.isNullOrEmpty())
                                    return@observe
                                binding.discordRecyclerView.makeItVisible()

                                binding.discordRecyclerView.startAnimation(animSlideDown)

                                adapter.submitList(it.data)
                                binding.discordDropDownBtn.setImageResource(R.drawable.ic_up_arrow)
                                isDiscordDropDownBtnClicked = true
                            }
                        }
                    }
                } else {
                    binding.root.snack("Check your Internet Connection!")
                }
            } else {
                isDiscordDropDownBtnClicked = !isDiscordDropDownBtnClicked

                hideWithAnimate(binding.discordRecyclerView, binding.discordDropDownBtn)

//                binding.discordRecyclerView.visibility = View.GONE
//                binding.discordDropDownBtn.setImageResource(R.drawable.ic_down_arrow)
            }
        }

        binding.telegramDropDownBtn.setOnClickListener {
            if (!isTelegramDropDownBtnClicked) {
                if (isInternetAvailable) {
                    val loadedData = viewModel.getTelegramChannels.value?.data
                    Log.d(TAG, "telegramDropDownBtn: ${loadedData.toString()}")
                    if (loadedData == null) {
                        viewModel.loadTelegramChannels()
                    }
                    val adapter = HomeAdapter(this)
                    binding.telegramRecyclerView.adapter = adapter
                    viewModel.getTelegramChannels.observe(viewLifecycleOwner) {
                        when (it) {
                            is Resource.Error -> {
                                binding.telegramProgressBar.makeItGone()
                                binding.root.snack(it.message!!)
                            }
                            is Resource.Loading -> {
                                binding.telegramProgressBar.makeItVisible()
                            }
                            is Resource.Success -> {
                                binding.telegramProgressBar.makeItGone()
                                if (it.data.isNullOrEmpty())
                                    return@observe
                                binding.telegramRecyclerView.makeItVisible()

                                binding.telegramRecyclerView.startAnimation(animSlideDown)

                                adapter.submitList(it.data)
                                binding.telegramDropDownBtn.setImageResource(R.drawable.ic_up_arrow)
                                isTelegramDropDownBtnClicked = true
                            }
                        }
                    }
                } else {
                    binding.root.snack("Check your Internet Connection!")
                }
            } else {
                isTelegramDropDownBtnClicked = !isTelegramDropDownBtnClicked

                hideWithAnimate(binding.telegramRecyclerView, binding.telegramDropDownBtn)

//                binding.telegramRecyclerView.visibility = View.GONE
//                binding.telegramDropDownBtn.setImageResource(R.drawable.ic_down_arrow)
            }
        }

        binding.createNewMemeTxt.setOnClickListener {
            if (isInternetAvailable) {
                val action = HomeFragmentDirections.actionHomeFragmentToTemplatesFragment()
                findNavController().navigate(action)
            } else {
                binding.root.snack("Check your Internet Connection")
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

    private fun hideWithAnimate(recyclerView: RecyclerView, dropDownBtn: ImageButton) {

        val animSlideUp: Animation = AnimationUtils.loadAnimation(
            requireContext(), R.anim.slide_up
        )
        animSlideUp.fillAfter = true
        animSlideUp.duration = 300

        animSlideUp.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                Log.d(TAG, "onAnimationStart: called")
            }

            override fun onAnimationEnd(animation: Animation?) {
                Log.d(TAG, "onAnimationEnd: called")

                recyclerView.makeItGone()
                dropDownBtn.setImageResource(R.drawable.ic_down_arrow)
            }

            override fun onAnimationRepeat(animation: Animation?) {
                Log.d(TAG, "onAnimationRepeat: called")
            }
        })
        recyclerView.startAnimation(animSlideUp)
    }

    override fun onItemClick(itemInfo: ItemInfo) {
        Log.d(TAG, "onItemClick: $itemInfo")

        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(itemInfo.url)
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}