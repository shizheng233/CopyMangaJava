package com.shicheeng.copymanga.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.MangaHistoryDataModel
import com.shicheeng.copymanga.databinding.LayoutMangaListItemBinding
import com.shicheeng.copymanga.util.KeyWordSwap

class MangaHistoryListAdapter :
    ListAdapter<MangaHistoryDataModel, MangaHistoryListAdapter.HistoryViewHolder>(
        DiffBackCall) {


    class HistoryViewHolder(private val parent: ViewGroup) :
        RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.layout_manga_list_item, parent, false)) {

        private val binding = LayoutMangaListItemBinding.bind(itemView)

        fun bind(mangaItem: MangaHistoryDataModel) {
            binding.mangaListAuthorText.text =
                parent.context.getString(R.string.watching,
                    mangaItem.nameChapter,
                    mangaItem.positionPage + 1)
            binding.mangaListTextTitle.text = mangaItem.name
            Glide.with(parent.context).load(mangaItem.url).into(binding.mangaListImageView)
            binding.mangaListCardClick.setOnClickListener {
                val bundle = bundleOf(KeyWordSwap.PATH_WORD_TYPE to mangaItem.pathWord)
                it.findNavController().navigate(R.id.infoFragment, bundle)
            }
        }
    }

    object DiffBackCall : DiffUtil.ItemCallback<MangaHistoryDataModel>() {

        override fun areItemsTheSame(
            oldItem: MangaHistoryDataModel,
            newItem: MangaHistoryDataModel,
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: MangaHistoryDataModel,
            newItem: MangaHistoryDataModel,
        ): Boolean {
            return oldItem.name == newItem.name
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder =
        HistoryViewHolder(parent)

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}