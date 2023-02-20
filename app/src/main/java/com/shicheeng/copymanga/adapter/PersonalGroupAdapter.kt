package com.shicheeng.copymanga.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.MangaHistoryDataModel
import com.shicheeng.copymanga.data.PersonalInnerDataModel
import com.shicheeng.copymanga.databinding.PersionalDataBinding
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
                Glide.with(binding.root).load(any.url).into(binding.personalInnerImage)
                binding.personalInnerTextTitle.text = any.name
                binding.personalInnerTextDate.text = any.time.toTimeReadable()
            }
            is PersonalInnerDataModel -> {
                binding.personalInnerTextDate.isVisible = false
                binding.personalInnerImage.setImageURI(any.url)
                binding.personalInnerTextTitle.text = any.name
            }
        }
    }

}