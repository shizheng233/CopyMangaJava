package com.shicheeng.copymanga.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.MangaHistoryDataModel
import com.shicheeng.copymanga.data.PersonalInnerDataModel
import com.shicheeng.copymanga.databinding.PersionalDataBinding
import com.shicheeng.copymanga.fm.view.PersonalFragmentDirections
import com.shicheeng.copymanga.util.KeyWordSwap
import com.shicheeng.copymanga.util.toTimeReadable

class PersonalGroupAdapter : ListAdapter<Any, PersonalGroupViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonalGroupViewHolder {
        return PersonalGroupViewHolder(parent)
    }

    override fun onBindViewHolder(holder: PersonalGroupViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object Diff : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is MangaHistoryDataModel && newItem is MangaHistoryDataModel -> {
                    newItem.name == oldItem.name
                }
                oldItem is PersonalInnerDataModel && newItem is PersonalInnerDataModel -> {
                    newItem.name == oldItem.name
                }
                else -> false
            }
        }

    }


}

class PersonalGroupViewHolder(parent: ViewGroup) : ViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.persional_data,
        parent,
        false
    )
) {

    private val binding = PersionalDataBinding.bind(itemView)

    fun bind(any: Any) {
        when (any) {
            is MangaHistoryDataModel -> {
                binding.personalInnerTextDate.isVisible = true
                val url = Uri.parse(any.url)
                if (url.scheme == "https") {
                    Glide.with(binding.root).load(any.url).into(binding.personalInnerImage)
                } else {
                    binding.personalInnerImage.setImageURI(url)
                }
                binding.personalInnerTextTitle.text = any.name
                binding.personalInnerTextDate.text = any.time.toTimeReadable()
                binding.root.setOnClickListener {
                    val bundle = bundleOf(KeyWordSwap.PATH_WORD_TYPE to any.pathWord)
                    it.findNavController().navigate(R.id.infoFragment, bundle)
                }
            }
            is PersonalInnerDataModel -> {
                binding.personalInnerTextDate.isVisible = false
                binding.personalInnerImage.setImageURI(any.url)
                binding.personalInnerTextTitle.text = any.name
                binding.root.setOnClickListener {
                    val action = PersonalFragmentDirections
                        .actionPersonalFragmentToDownloadMangaInfoDialogFragment(any)
                    it.findNavController().navigate(action)
                }
            }
        }
    }

}