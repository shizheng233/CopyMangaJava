package com.shicheeng.copymanga.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.adapter.PopMenuAdapter
import com.shicheeng.copymanga.data.MangaSortBean
import com.shicheeng.copymanga.databinding.SortTabBinding
import com.shicheeng.copymanga.json.MangaSortJson.order
import com.shicheeng.copymanga.viewmodel.ExploreMangaViewModel
import kotlinx.coroutines.launch

class SortDialogFragment : BottomSheetDialogFragment() {


    private var _binding: SortTabBinding? = null
    private val binding get() = _binding!!

    private lateinit var onButtonClickListener: (v: View?, order: String, theme: String) -> Unit

    private val exploreViewModel by viewModels<ExploreMangaViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = SortTabBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exploreViewModel.loadData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindUi()
    }

    private fun SortTabBinding.bindUi() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                exploreViewModel.uiState.collect {
                    when (it) {
                        is ExploreMangaViewModel.UiState.Success -> bindFilterUi(it.list)
                        is ExploreMangaViewModel.UiState.Error -> bindUiFailure()
                        ExploreMangaViewModel.UiState.Loading -> bindUILoading()
                    }
                }
            }
        }
    }

    private fun SortTabBinding.bindFilterUi(themeList: List<MangaSortBean>) {

        var orderWord: String? = null
        var themeWord: String? = null

        val listPopupWindowSort = ListPopupWindow(requireContext(), null,
            androidx.appcompat.R.attr.listPopupWindowStyle)
        val listPopupWindowOrder = ListPopupWindow(requireContext(), null,
            androidx.appcompat.R.attr.listPopupWindowStyle)

        listPopupWindowSort.anchorView = menuSort
        listPopupWindowOrder.anchorView = menuOrder

        val adapter2 = PopMenuAdapter(order)
        val adapterOfTheme = PopMenuAdapter(themeList)


        listPopupWindowOrder.setAdapter(adapter2)
        listPopupWindowSort.setAdapter(adapterOfTheme)

        auto1.setOnClickListener { listPopupWindowSort.show() }
        auto2.setOnClickListener { listPopupWindowOrder.show() }
        menuOrder.setEndIconOnClickListener { listPopupWindowOrder.show() }
        menuSort.setEndIconOnClickListener { listPopupWindowSort.show() }

        listPopupWindowOrder.setOnItemClickListener { _, _, i, _ ->
            orderWord = order[i].pathWord
            auto2.setText(order[i].pathName)
            listPopupWindowOrder.dismiss()
        }
        listPopupWindowSort.setOnItemClickListener { _, _, position, _ ->
            themeWord = themeList[position].pathWord
            auto1.setText(themeList[position].pathName)
            listPopupWindowSort.dismiss()
        }

        btnFilter.setOnClickListener {
            onButtonClickListener.invoke(it, orderWord ?: "-popular", themeWord ?: "all")
        }

        btnFilter.text = getString(R.string.filter)
        btnFilter.isVisible = true
        filterLoadFailureText.isVisible = false
        circleProBar.isVisible = false
        filterMainContent.isVisible = true

    }

    private fun SortTabBinding.bindUiFailure() {
        btnFilter.isVisible = true
        btnFilter.text = getString(R.string.retry)
        btnFilter.setOnClickListener { exploreViewModel.loadData() }
        filterLoadFailureText.isVisible = true
        circleProBar.isVisible = false
        filterMainContent.isVisible = false
    }

    private fun SortTabBinding.bindUILoading() {
        btnFilter.isVisible = false
        filterLoadFailureText.isVisible = false
        circleProBar.isVisible = true
        filterMainContent.isVisible = false
    }

    fun setOnButtonClickListener(onButtonClickListener: (View?, order: String, theme: String) -> Unit) {
        this.onButtonClickListener = onButtonClickListener
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}