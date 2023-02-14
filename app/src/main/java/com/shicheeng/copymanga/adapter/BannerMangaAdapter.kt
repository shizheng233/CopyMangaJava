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
import com.shicheeng.copymanga.data.DataBannerBean

class BannerMangaAdapter(private val bannerBeans: ArrayList<DataBannerBean>) :
    RecyclerView.Adapter<BannerMangaAdapter.BannerViewHolder>() {

    private lateinit var onItem: (v: View?, position: Int) -> Unit


    fun setOnItemClickListener(onItem: (v: View?, position: Int) -> Unit) {
        this.onItem = onItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.manga_banner_mian, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: BannerViewHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {
        holder.briefText.text = bannerBeans[position].bannerBrief
        Glide.with(holder.itemView)
            .load(bannerBeans[position].bannerImageUrl)
            .into(holder.bannerImage)
        holder.itemView.setOnClickListener { view: View? -> onItem.invoke(view, position) }
    }

    override fun getItemCount(): Int {
        return bannerBeans.size
    }

    inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val briefText: TextView
        val bannerImage: ImageView

        init {
            bannerImage = itemView.findViewById(R.id.banner_pic)
            briefText = itemView.findViewById(R.id.brief_text)
        }
    }

    interface OnItemClickListener {
        fun onItem(position: Int)
    }
}