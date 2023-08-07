package com.shicheeng.copymanga.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import androidx.appcompat.widget.ListPopupWindow
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.data.MangaSortBean
import com.shicheeng.copymanga.databinding.MaganSelectBarBinding

class ExpandSelectionBar(context: Context, attributeSet: AttributeSet) :
    LinearLayout(context, attributeSet, 0, 0) {

    private val itemView =
        View.inflate(context, R.layout.magan_select_bar, this)
    private val binding = MaganSelectBarBinding.bind(itemView)
    private val listPopupWindowSort = ListPopupWindow(
        context, null,
        androidx.appcompat.R.attr.listPopupWindowStyle
    )
    private var _onClick: ((MangaSortBean?) -> Unit)? = null

    var menuList: List<MangaSortBean>? = null
        set(value) {
            if (value != null) {
                field = value
            }
        }

    fun setOnItemClickListener(onClickListener: (MangaSortBean?) -> Unit) {
        this._onClick = onClickListener
    }

    var tipText: CharSequence
        get() = binding.selectBarText.text
        set(value) {
            binding.selectBarText.text = value
        }

    var autoCompleteText: CharSequence? = binding.auto2.text.toString()
        set(value) {
            if (value != null) {
                field = value
                binding.auto2.setText(value)
            }
        }

    init {
        listPopupWindowSort.anchorView = binding.selectBarExpandMenu
        binding.auto2.setOnClickListener {
            listPopupWindowSort.show()
        }
        listPopupWindowSort.setOnItemClickListener { _: AdapterView<*>, _: View, i: Int, _: Long ->
            val name = menuList?.get(i)?.pathName
            _onClick?.invoke(menuList?.get(i))
            binding.auto2.setText(name)
            listPopupWindowSort.dismiss()
        }
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.ExpandSelectionBar,
            0, 0
        ).apply {
            try {
                binding.selectBarText.text = getString(R.styleable.ExpandSelectionBar_tipText)
                binding.selectBarExpandMenu.hint =
                    getString(R.styleable.ExpandSelectionBar_hintText)
            } finally {
                recycle()
            }
        }
    }
}