package com.shicheeng.copymanga.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.adapter.RecyclerViewMangaAdapter.MangaViewHolder
import com.shicheeng.copymanga.data.ListBeanManga

class RecyclerViewMangaAdapter(private val mangaList: List<ListBeanManga>) :
    RecyclerView.Adapter<MangaViewHolder>() {

    private lateinit var onItemClick: (view: View?, position: Int) -> Unit

    fun setOnItemClickListener(onItemClick: (view: View?, position: Int) -> Unit) {
        this.onItemClick = onItemClick
    }

    //创建ViewHolder
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MangaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_manga_detail, parent, false)
        return MangaViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MangaViewHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {
        holder.mangaTitle.text = mangaList[position].nameManga
        holder.mangaAuthor.text = mangaList[position].authorManga
        Glide.with(holder.itemView)
            .load(mangaList[position].urlCoverManga)
            .centerCrop().into(holder.mangaCover)
        holder.itemView.setOnClickListener { view: View? ->
            onItemClick.invoke(view, position)
        }
    }

    override fun getItemCount(): Int {
        return mangaList.size
    }

    inner class MangaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mangaTitle: TextView
        val mangaAuthor: TextView
        val mangaCover: ImageView

        init {
            mangaAuthor = itemView.findViewById(R.id.author_manga_item)
            mangaTitle = itemView.findViewById(R.id.title_manga_item)
            mangaCover = itemView.findViewById(R.id.cover_image)
        }
    }
}