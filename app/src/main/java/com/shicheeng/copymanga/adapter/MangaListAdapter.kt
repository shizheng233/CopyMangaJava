package com.shicheeng.copymanga.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.ListBeanManga
import com.shicheeng.copymanga.util.KeyWordSwap

class MangaListAdapter :
    PagingDataAdapter<ListBeanManga, MangaListAdapter.NewViewHolder>(DiffBackCall) {

    companion object {
        const val VIEW_TYPE_MAIN = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_manga_list_item, parent, false)

        return NewViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_MAIN
    }


    override fun onBindViewHolder(
        holder: NewViewHolder,
        position: Int,
    ) {
        holder.textTitle.text = getItem(position)?.nameManga
        holder.textAuthor.text = getItem(position)?.authorManga
        Glide.with(holder.itemView).load(getItem(position)?.urlCoverManga).into(holder.image)
        holder.materialCardView.setOnClickListener {
            val bundle = bundleOf(KeyWordSwap.PATH_WORD_TYPE to getItem(position)?.pathWordManga)
            it.findNavController().navigate(R.id.infoFragment, bundle)
        }
    }

    inner class NewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textTitle: TextView = itemView.findViewById(R.id.manga_list_text_title)
        var textAuthor: TextView = itemView.findViewById(R.id.manga_list_author_text)
        var image: ImageView = itemView.findViewById(R.id.manga_list_image_view)
        var materialCardView: MaterialCardView = itemView.findViewById(R.id.manga_list_card_click)
    }

    private object DiffBackCall : DiffUtil.ItemCallback<ListBeanManga>() {

        override fun areItemsTheSame(oldItem: ListBeanManga, newItem: ListBeanManga): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ListBeanManga, newItem: ListBeanManga): Boolean {
            return oldItem.nameManga.equals(newItem.authorManga)
        }

    }


}