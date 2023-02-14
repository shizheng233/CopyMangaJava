package com.shicheeng.copymanga.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonSyntaxException
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.databinding.LayoutLoadStateBinding

class MangaLoadStateViewHolder(parent: ViewGroup, retry: () -> Unit) :
    RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(
        R.layout.layout_load_state, parent, false)
    ) {
    private val binding = LayoutLoadStateBinding.bind(itemView)

    init {
        binding.loadStateText.setOnClickListener {
            retry()
        }
    }

    fun bind(loadState: LoadState) {
        when (loadState) {
            is LoadState.Loading -> {
                binding.root.isVisible = true
                binding.loadStateCircularProgressIndicator.isVisible = true
                binding.loadStateText.text = itemView.context.getText(R.string.loading)
            }
            is LoadState.NotLoading -> {
                binding.root.isVisible = false
            }
            is LoadState.Error -> {
                binding.root.isVisible = true
                binding.loadStateText.text = itemView.context.getString(R.string.touch_to_retry)
                binding.loadStateCircularProgressIndicator.isVisible = false
            }
        }

    }
}

class MangaLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<MangaLoadStateViewHolder>() {

    companion object {
        const val VIEW_TYPE_FOOTER = 0
    }

    override fun onBindViewHolder(holder: MangaLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)

    }

    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return if (loadState is LoadState.Error) {
            loadState.error is JsonSyntaxException
        } else {
            true
        }
    }

    override fun getStateViewType(loadState: LoadState): Int {
        return VIEW_TYPE_FOOTER
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState,
    ): MangaLoadStateViewHolder = MangaLoadStateViewHolder(parent, retry)

}