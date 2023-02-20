package com.shicheeng.copymanga.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.PersonalDataModel
import com.shicheeng.copymanga.databinding.PersonalDataInBinding
import com.shicheeng.copymanga.view.list.SpaceItem

class PersonalAdapter(private val sharePool: RecycledViewPool) :
    RecyclerView.Adapter<PersonalViewHolder>() {

    private val differList = AsyncListDiffer(this, CallBackDiff)
    private var onClick: ((id: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonalViewHolder {
        return PersonalViewHolder(parent, sharePool)
    }

    var items: List<PersonalDataModel>
        get() {
            return differList.currentList
        }
        set(value) {
            differList.submitList(value)
        }

    override fun getItemCount(): Int = differList.currentList.size

    override fun onBindViewHolder(holder: PersonalViewHolder, position: Int) {
        holder.bind(differList.currentList[position]) {
            if (onClick != null) {
                onClick?.invoke(it)
            }
        }
    }

    fun setOnHeaderViewOnClickListener(onClick: (id: Int) -> Unit) {
        this.onClick = onClick
    }

    override fun onViewRecycled(holder: PersonalViewHolder) {
        holder.onRecycle()
        super.onViewRecycled(holder)
    }

    private object CallBackDiff : DiffUtil.ItemCallback<PersonalDataModel>() {
        override fun areItemsTheSame(
            oldItem: PersonalDataModel,
            newItem: PersonalDataModel,
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: PersonalDataModel,
            newItem: PersonalDataModel,
        ): Boolean {
            return oldItem.title == newItem.title
        }
    }

}

class PersonalViewHolder(private val parent: ViewGroup, sharePool: RecycledViewPool) : ViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.personal_data_in, parent, false
    )
) {
    private val binding = PersonalDataInBinding.bind(itemView)
    private val adapter = PersonalGroupAdapter()

    init {
        binding.personalDataRecyclerView.setRecycledViewPool(sharePool)
        binding.personalDataRecyclerView.addItemDecoration(
            SpaceItem(
                parent.context.resources.getDimensionPixelOffset(
                    R.dimen.item_space
                )
            )
        )
    }

    fun bind(data: PersonalDataModel, onClick: (id: Int) -> Unit) {
        binding.personalDataHeaderView.handLineText = parent.context.getString(data.title)
        binding.personalDataHeaderView.setOnHeadClickListener { onClick.invoke(data.title) }
        binding.personalDataRecyclerView.adapter = adapter
        adapter.submitList(data.list)
    }

    fun onRecycle() {
        adapter.submitList(emptyList())
    }


}