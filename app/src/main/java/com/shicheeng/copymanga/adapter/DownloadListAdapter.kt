package com.shicheeng.copymanga.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.PersonalInnerDataModel
import com.shicheeng.copymanga.databinding.LayoutMangaListItemBinding
import com.shicheeng.copymanga.fm.view.DownloadFragmentDirections
import com.shicheeng.copymanga.fm.view.PersonalFragmentDirections

class DownloadListAdapter :
    ListAdapter<PersonalInnerDataModel, DownloadListAdapter.DownloadViewHolder>(DiffOfDownloadLIst) {

    class DownloadViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.layout_manga_list_item, parent, false)
    ) {
        private val binding = LayoutMangaListItemBinding.bind(itemView)

        fun bind(data: PersonalInnerDataModel) {
            binding.mangaListAuthorText.isVisible = false
            binding.mangaListImageView.setImageURI(data.url)
            binding.mangaListTextTitle.text = data.name
            binding.mangaListCardClick.setOnClickListener {
                val action = DownloadFragmentDirections
                    .actionDownloadFragmentToDownloadMangaInfoDialogFragment(data)
                it.findNavController().navigate(action)
            }
        }
    }


    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): DownloadViewHolder = DownloadViewHolder(parent)


    private object DiffOfDownloadLIst : DiffUtil.ItemCallback<PersonalInnerDataModel>() {
        override fun areItemsTheSame(
            oldItem: PersonalInnerDataModel,
            newItem: PersonalInnerDataModel,
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: PersonalInnerDataModel,
            newItem: PersonalInnerDataModel,
        ): Boolean {
            return oldItem.name == newItem.name
        }
    }
}