package com.shicheeng.copymanga.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.shicheeng.copymanga.util.KeyWordSwap
import com.shicheeng.copymanga.data.ListBeanManga
import com.shicheeng.copymanga.R

class MangaRankBigAdapter(private val list: List<ListBeanManga>) :
    RecyclerView.Adapter<MangaRankBigAdapter.NewViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_manga_list_item, parent, false)
        return NewViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewViewHolder, position: Int) {
        holder.textViewTitle.text = list[position].nameManga
        holder.textViewAuthor.text = list[position].authorManga
        Glide.with(holder.imageView.context)
            .load(list[position].urlCoverManga)
            .into(holder.imageView)
        holder.cardView.setOnClickListener { view: View? ->
            val bundle = bundleOf(KeyWordSwap.PATH_WORD_TYPE to list[position].pathWordManga)
            view?.findNavController()?.navigate(R.id.action_mainFragment_to_infoFragment, bundle)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class NewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewTitle: TextView
        var textViewAuthor: TextView
        var imageView: ImageView
        var cardView: MaterialCardView

        init {
            textViewTitle = itemView.findViewById(R.id.manga_list_text_title)
            textViewAuthor = itemView.findViewById(R.id.manga_list_author_text)
            imageView = itemView.findViewById(R.id.manga_list_image_view)
            cardView = itemView.findViewById(R.id.manga_list_card_click)
        }
    }
}