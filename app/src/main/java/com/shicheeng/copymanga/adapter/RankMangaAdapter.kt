package com.shicheeng.copymanga.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.adapter.RankMangaAdapter.VH
import com.shicheeng.copymanga.data.MangaRankMiniModel

class RankMangaAdapter :
    ListAdapter<MangaRankMiniModel, VH>(DiffRank) {

    private lateinit var onRankItemClick: (v: View, position: Int) -> Unit

    fun setOnItemClickListener(onRankItemClick: (v: View, position: Int) -> Unit) {
        this.onRankItemClick = onRankItemClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_rank_manga_detail, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, @SuppressLint("RecyclerView") position: Int) {
        holder.itemView.setOnClickListener(null)
        holder.textName.text = getItem(position).name
        holder.textAuthor.text = getItem(position).author
        Glide.with(holder.itemView)
            .load(getItem(position).urlCover)
            .into(holder.imageCover)
        holder.textHotNumber.text = holder.itemView
            .context.getString(R.string.how_popalur, getItem(position).popular)
        //The region now use in rise hot.Because the Api no give this json data.
        holder.textRegion.text = getItem(position).riseHot
        holder.itemView.setOnClickListener {
            onRankItemClick.invoke(it, position)
        }
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName: TextView = itemView.findViewById(R.id.rank_manga_name)
        val textAuthor: TextView = itemView.findViewById(R.id.rank_manga_author)
        val imageCover: ImageView = itemView.findViewById(R.id.cover_rank)
        val textRegion: TextView = itemView.findViewById(R.id.rank_manga_region)
        val textHotNumber: TextView = itemView.findViewById(R.id.rank_manga_hot_number)
    }

    private object DiffRank : DiffUtil.ItemCallback<MangaRankMiniModel>() {

        override fun areItemsTheSame(
            oldItem: MangaRankMiniModel,
            newItem: MangaRankMiniModel,
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: MangaRankMiniModel,
            newItem: MangaRankMiniModel,
        ): Boolean {
            return oldItem.pathWord == newItem.pathWord
        }

    }
}