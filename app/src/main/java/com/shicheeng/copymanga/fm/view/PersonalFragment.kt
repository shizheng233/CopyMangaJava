package com.shicheeng.copymanga.fm.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.shicheeng.copymanga.MyApp
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.adapter.PersonalAdapter
import com.shicheeng.copymanga.app.BaseFragment
import com.shicheeng.copymanga.databinding.FragmentPersonalBinding
import com.shicheeng.copymanga.util.FileUtil
import com.shicheeng.copymanga.viewmodel.PersonalViewModel
import com.shicheeng.copymanga.viewmodel.PersonalViewModelFactory

class PersonalFragment : BaseFragment<FragmentPersonalBinding>() {

    private val model: PersonalViewModel by viewModels {
        PersonalViewModelFactory(
            FileUtil(requireContext(), viewLifecycleOwner.lifecycleScope),
            (requireActivity().application as MyApp).repo
        )
    }

    override fun onViewBindingIn(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentPersonalBinding = FragmentPersonalBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pool = RecyclerView.RecycledViewPool()
        val adapter = PersonalAdapter(pool)
        model.combineOfList.observe(viewLifecycleOwner) {
            adapter.items = it
        }
        binding.personalRecyclerView.adapter = adapter
        adapter.setOnHeaderViewOnClickListener {
            when (it) {
                R.string.history -> {
                    val action =
                        PersonalFragmentDirections.actionPersonalFragmentToHistoryFragment()
                    findNavController().navigate(action)
                }
            }
        }
    }

    override fun onFragmentInsets(systemBarInsets: Insets?, view: View) {
        if (systemBarInsets == null) {
            return
        }
        binding.personalRecyclerView.updatePadding(bottom = systemBarInsets.bottom)
    }


}