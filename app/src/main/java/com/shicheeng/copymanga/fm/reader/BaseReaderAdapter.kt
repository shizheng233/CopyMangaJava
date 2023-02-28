package com.shicheeng.copymanga.fm.reader

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shicheeng.copymanga.data.MangaReaderPage
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Suppress("LeakingThis")
abstract class BaseReaderAdapter<VH : BaseReaderViewHolder<*>> :
    RecyclerView.Adapter<VH>() {


    private val diff = AsyncListDiffer(this, DIffCallBack())

    init {
        stateRestorationPolicy = StateRestorationPolicy.PREVENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        onCreateViewHolder(parent)

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(url = diff.currentList[position].url)
    }

    open fun getItem(position: Int): MangaReaderPage = diff.currentList[position]

    open fun getItemOrNull(position: Int): MangaReaderPage? = diff.currentList.getOrNull(position)

    override fun getItemCount(): Int = diff.currentList.size

    suspend fun subItems(list: List<MangaReaderPage>) = suspendCoroutine { continuation ->
        diff.submitList(list) {
            continuation.resume(Unit)
        }
    }

    override fun onViewRecycled(holder: VH) {
        holder.onRecycler()
        super.onViewRecycled(holder)
    }

    protected abstract fun onCreateViewHolder(parent: ViewGroup): VH

    private class DIffCallBack : DiffUtil.ItemCallback<MangaReaderPage>() {
        override fun areItemsTheSame(
            oldItem: MangaReaderPage,
            newItem: MangaReaderPage,
        ): Boolean = oldItem === newItem

        override fun areContentsTheSame(
            oldItem: MangaReaderPage,
            newItem: MangaReaderPage,
        ): Boolean = oldItem == newItem
    }

}
