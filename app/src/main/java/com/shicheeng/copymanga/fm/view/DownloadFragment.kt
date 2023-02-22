package com.shicheeng.copymanga.fm.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.shicheeng.copymanga.MyApp
import com.shicheeng.copymanga.adapter.DownloadListAdapter
import com.shicheeng.copymanga.app.BaseFragment
import com.shicheeng.copymanga.databinding.FragmentDownloadBinding
import com.shicheeng.copymanga.util.FileUtil
import com.shicheeng.copymanga.util.gridLayout
import com.shicheeng.copymanga.viewmodel.DownloadViewModel
import com.shicheeng.copymanga.viewmodel.DownloadViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DownloadFragment : BaseFragment<FragmentDownloadBinding>() {

    private val viewModel: DownloadViewModel by viewModels {
        DownloadViewModelFactory(
            FileUtil(requireContext(), viewLifecycleOwner.lifecycleScope),
            (requireActivity().application as MyApp).repo
        )
    }

    override fun onViewBindingIn(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentDownloadBinding = FragmentDownloadBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = DownloadListAdapter()

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.list.collectLatest {
                    adapter.submitList(it)
                }
            }
        }
        with(binding.downloadList) {
            setAdapter(adapter)
            layoutManager = gridLayout(2)
        }
    }

    override fun onFragmentInsets(systemBarInsets: Insets?, view: View) {
        if (systemBarInsets != null) {
            binding.downloadList.updatePadding(bottom = systemBarInsets.bottom)
        }
    }


}