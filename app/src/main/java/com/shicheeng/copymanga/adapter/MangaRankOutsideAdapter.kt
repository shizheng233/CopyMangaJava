package com.shicheeng.copymanga.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.shicheeng.copymanga.fm.RankMiniFragment
import com.shicheeng.copymanga.fm.view.RankFragmentChild

class MangaRankPagerAdapter(
    private val listFragment: List<String>,
    fragment: FragmentManager,
    viewLifecycle: Lifecycle,
) :
    FragmentStateAdapter(fragment, viewLifecycle) {
    override fun getItemCount(): Int = listFragment.size

    override fun createFragment(position: Int): Fragment =
        RankMiniFragment.newInstance(listFragment[position])


}

class MangaRankOutsideAdapter(
    private val typeList: List<String>,
    fragment: FragmentManager,
    viewLifecycle: Lifecycle,
) : FragmentStateAdapter(fragment, viewLifecycle) {
    override fun getItemCount(): Int = typeList.size

    override fun createFragment(position: Int): Fragment {
        return RankFragmentChild.newInstance(typeList[position])
    }
}