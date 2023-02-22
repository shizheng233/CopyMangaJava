package com.shicheeng.copymanga.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shicheeng.copymanga.MyApp
import com.shicheeng.copymanga.adapter.MangaInfoChapterAdapter
import com.shicheeng.copymanga.databinding.FragmentDialogDownloadInfoBinding
import com.shicheeng.copymanga.util.FileUtil
import com.shicheeng.copymanga.viewmodel.DownloadViewModel
import com.shicheeng.copymanga.viewmodel.DownloadViewModelFactory

class DownloadMangaInfoDialogFragment : BottomSheetDialogFragment(),
    View.OnAttachStateChangeListener {

    private var _binding: FragmentDialogDownloadInfoBinding? = null
    private val binding: FragmentDialogDownloadInfoBinding get() = _binding!!
    private val mangaInfoDialogNav: DownloadMangaInfoDialogFragmentArgs by navArgs()
    private val viewModel: DownloadViewModel by viewModels {
        DownloadViewModelFactory(
            FileUtil(requireContext(), viewLifecycleOwner.lifecycleScope),
            (requireActivity().application as MyApp).repo
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDialogDownloadInfoBinding.inflate(inflater, container, false)
        binding.root.addOnAttachStateChangeListener(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val data = mangaInfoDialogNav.baseMangaData
        viewModel.findDownloadedChapter(data)
        binding.downloadDialogInfoName.text = data.name
        binding.downloadDialogIndoImage.setImageURI(data.url)
        val adapter = MangaInfoChapterAdapter()
        with(binding.downloadDialogInfoRecyclerView) {
            setAdapter(adapter)
            isNestedScrollingEnabled = true
        }
        viewModel.listOfDownload.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        adapter.setOnItemClickListener {
            val action = DownloadMangaInfoDialogFragmentDirections
                .actionDownloadMangaInfoDialogFragmentToMangaReaderActivity2(
                    adapter.currentList.toTypedArray(),
                    data.pathWord ?: getString(android.R.string.unknownName),
                    data.name,
                    data.url.toString(),
                    adapter.currentList[it.absoluteAdapterPosition]
                )
            findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateHistoryInInfo(mangaInfoDialogNav.baseMangaData)
    }

    override fun onViewAttachedToWindow(v: View) {
        val insetsCompat = ViewCompat.getRootWindowInsets(v)
        val systemBarInsets = insetsCompat?.getInsets(WindowInsetsCompat.Type.systemBars())
        if (systemBarInsets != null) {
            binding.downloadDialogInfoRecyclerView.updatePadding(bottom = systemBarInsets.bottom)
        }
    }

    override fun onViewDetachedFromWindow(v: View) {

    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}