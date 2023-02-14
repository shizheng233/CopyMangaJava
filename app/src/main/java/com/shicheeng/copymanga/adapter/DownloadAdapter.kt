package com.shicheeng.copymanga.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.databinding.ItemMangaDownloadInfoBinding
import com.shicheeng.copymanga.server.DownloadStateChapter
import com.shicheeng.copymanga.util.DownloadJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DownloadAdapter(private val coroutineScope: CoroutineScope) :
    ListAdapter<DownloadJob<DownloadStateChapter>, DownloadViewHolder>(DiffBackCall) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadViewHolder {
        return DownloadViewHolder(parent)
    }

    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        holder.bind(getItem(position), coroutineScope)
    }

    override fun onViewRecycled(holder: DownloadViewHolder) {
        holder.onRecycler()
        super.onViewRecycled(holder)
    }

    object DiffBackCall : DiffUtil.ItemCallback<DownloadJob<DownloadStateChapter>>() {
        override fun areItemsTheSame(
            oldItem: DownloadJob<DownloadStateChapter>,
            newItem: DownloadJob<DownloadStateChapter>,
        ): Boolean {
            return oldItem.progressValue === newItem.progressValue
        }

        override fun areContentsTheSame(
            oldItem: DownloadJob<DownloadStateChapter>,
            newItem: DownloadJob<DownloadStateChapter>,
        ): Boolean {
            return oldItem.progressValue.chapterID == newItem.progressValue.chapterID
        }

    }

}

class DownloadViewHolder(
    parent: ViewGroup,
) : ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.item_manga_download_info, parent, false)
) {
    val binding = ItemMangaDownloadInfoBinding.bind(itemView)
    private var job: Job? = null
    val context: Context = parent.context

    fun bind(item: DownloadJob<DownloadStateChapter>, coroutineScope: CoroutineScope) {
        job?.cancel()
        job = item.progressAsFlow().onEach {
            binding.downloadInfoTitle.text = it.chapter.mangaName
            binding.downloadInfoChapter.isVisible = false
            when (it) {
                is DownloadStateChapter.DOWNLOADING -> {
                    binding.downloadInfoProgressIndication.apply {
                        isIndeterminate = false
                        progress = it.progress
                        max = it.max
                    }
                    val text = "${(it.percent * 100).toInt()}%"
                    binding.downloadInfoChapterProgressText.text = text
                }
                is DownloadStateChapter.CANCEL -> {
                    binding.downloadInfoChapter.text = context.getString(R.string.cancel)
                    binding.downloadInfoProgressIndication.isVisible = false
                    binding.downloadInfoChapterProgressText.isVisible = false
                }
                is DownloadStateChapter.ChapterChange -> {
                    binding.downloadInfoChapter.isVisible = true
                    binding.downloadInfoChapter.text = it.chapterInDownload.chapterTitle
                }
                is DownloadStateChapter.DONE -> {
                    binding.downloadInfoChapter.text = context.getString(R.string.all_done)
                    binding.downloadInfoProgressIndication.isVisible = false
                    binding.downloadInfoChapterProgressText.isVisible = false
                }
                is DownloadStateChapter.ERROR -> {
                    binding.downloadInfoChapter.text = context.getString(R.string.error_in_download)
                    binding.downloadInfoProgressIndication.isVisible = false
                    binding.downloadInfoChapterProgressText.isVisible = false
                }
                is DownloadStateChapter.PREPARE -> {
                    binding.downloadInfoProgressIndication.isIndeterminate = true
                    binding.downloadInfoChapterProgressText.text =
                        context.getString(R.string.manga_download_get_ready)
                }
                is DownloadStateChapter.PostBeforeDone -> {
                    binding.downloadInfoProgressIndication.isIndeterminate = true
                    binding.downloadInfoChapterProgressText.text =
                        context.getString(R.string.post_before_done)
                }
                is DownloadStateChapter.WAITING -> {
                    binding.downloadInfoProgressIndication.isIndeterminate = true
                    binding.downloadInfoChapterProgressText.text =
                        context.getString(R.string.waiting)
                }
            }
        }.launchIn(coroutineScope)
    }

    fun onRecycler() {
        job?.cancel()
        job = null
    }

}