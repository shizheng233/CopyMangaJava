package com.shicheeng.copymanga.adapter

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.elevation.SurfaceColors
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.MangaInfoChapterDataBean
import com.shicheeng.copymanga.databinding.ListChapterBinding

class MangaInfoChapterAdapter :
    ListAdapter<MangaInfoChapterDataBean, MangaInfoChapterAdapter.MyViewHolder>(Diff) {

    private lateinit var onItemClickListener: (MyViewHolder) -> Unit

    fun setOnItemClickListener(onItemClickListener: (MyViewHolder) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }

    var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MyViewHolder = MyViewHolder(parent)

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int,
    ) {
        holder.bind(
            getItem(position),
            tracker?.isSelected(position.toLong()) ?: false,
        ) {
            onItemClickListener.invoke(holder)
        }
    }


    override fun getItemId(position: Int): Long = position.toLong()


    class MyViewHolder(private val parent: ViewGroup) :
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_chapter, parent, false)
        ) {

        private val binding = ListChapterBinding.bind(itemView)
        val itemDetails: ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {

                override fun getPosition(): Int = this@MyViewHolder.absoluteAdapterPosition

                override fun getSelectionKey(): Long = itemId
            }

        fun bind(
            chapterDataBean: MangaInfoChapterDataBean,
            isActivated: Boolean = false,
            click: () -> Unit,
        ) {
            if (isActivated) {
                itemView.setBackgroundColor(SurfaceColors.SURFACE_5.getColor(parent.context))
            } else {
                itemView.background = null
            }
            binding.chapterIsSave.isVisible = chapterDataBean.isSaved
            binding.chapterItemTime.text = chapterDataBean.chapterTime
            binding.chapterItemTitle.text = chapterDataBean.chapterTitle
            binding.readerProgressItemChapter.apply {
                if (chapterDataBean.readerProgress == null) {
                    isVisible = false
                } else {
                    isVisible = true
                    text = context.getString(
                        R.string.reader_page,
                        chapterDataBean.readerProgress + 1
                    )
                }
            }
            binding.root.setOnClickListener { click.invoke() }
        }

    }


    object Diff : DiffUtil.ItemCallback<MangaInfoChapterDataBean>() {
        override fun areItemsTheSame(
            oldItem: MangaInfoChapterDataBean,
            newItem: MangaInfoChapterDataBean,
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: MangaInfoChapterDataBean,
            newItem: MangaInfoChapterDataBean,
        ): Boolean {
            return oldItem.chapterTitle == newItem.chapterTitle
        }

    }

}

class MangaChapterListItemLookUp(private val recyclerView: RecyclerView) :
    ItemDetailsLookup<Long>() {

    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(e.x, e.y)
        if (view != null) {
            return (recyclerView.getChildViewHolder(view) as MangaInfoChapterAdapter.MyViewHolder).itemDetails
        }
        return null
    }

}

class MangaChapterKeyProvider(private val recyclerView: RecyclerView) : ItemKeyProvider<Long>(
    SCOPE_MAPPED
) {

    override fun getKey(position: Int): Long? {
        return recyclerView.adapter?.getItemId(position)
    }

    override fun getPosition(key: Long): Int {
        val viewHolder = recyclerView.findViewHolderForItemId(key)
        return viewHolder?.layoutPosition ?: RecyclerView.NO_POSITION
    }
}