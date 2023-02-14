package com.shicheeng.copymanga.fm.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.adapter.MangaRankOutsideAdapter
import com.shicheeng.copymanga.databinding.RankMangaLayoutBinding
import com.shicheeng.copymanga.util.KeyWordSwap.*

class RankFragment : Fragment() {

    private var _binding: RankMangaLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = RankMangaLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindViewPager()
    }

    private fun RankMangaLayoutBinding.bindViewPager() {
        val list = listOf(getString(R.string.day_rank),
            getString(R.string.week_rank),
            getString(R.string.month_rank),
            getString(R.string.all_rank))

        val rankFragments = listOf(DAY_RANK,
            WEEK_RANK,
            MONTH_RANK,
            TOTAL_RANK)
        val adapter = MangaRankOutsideAdapter(rankFragments,
                childFragmentManager,
                viewLifecycleOwner.lifecycle)
        viewPagerRank.adapter = adapter
        TabLayoutMediator(tabsRank, viewPagerRank) { tab: TabLayout.Tab, i: Int ->
            tab.text = list[i]
        }.attach()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}