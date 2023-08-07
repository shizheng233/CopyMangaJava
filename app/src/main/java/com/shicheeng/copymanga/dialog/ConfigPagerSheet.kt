package com.shicheeng.copymanga.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButtonToggleGroup
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.databinding.SheetMangaModelSwitcherBinding
import com.shicheeng.copymanga.fm.reader.ReaderMode

class ConfigPagerSheet : BottomSheetDialogFragment(),
    MaterialButtonToggleGroup.OnButtonCheckedListener {

    private var _binding: SheetMangaModelSwitcherBinding? = null
    private val binding get() = _binding!!
    private lateinit var mode: ReaderMode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mode = arguments?.getInt(MODE_BUNDLE)?.let {
            ReaderMode.idOf(it)
        } ?: ReaderMode.NORMAL
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = SheetMangaModelSwitcherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.readerSwitcherToHorizontal.isChecked = mode == ReaderMode.WEBTOON
        binding.readerSwitcherToVert.isChecked = mode == ReaderMode.NORMAL
        binding.readerSwitcherToLToR.isChecked = mode == ReaderMode.STANDARD


        binding.readerSwitchersGroup.addOnButtonCheckedListener(this)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onButtonChecked(
        group: MaterialButtonToggleGroup?,
        checkedId: Int,
        isChecked: Boolean,
    ) {
        if (!isChecked) {
            return
        }
        val newMode = when (checkedId) {
            R.id.reader_switcher_to_vert -> ReaderMode.NORMAL
            R.id.reader_switcher_to_horizontal -> ReaderMode.WEBTOON
            R.id.reader_switcher_to_l_to_r -> ReaderMode.STANDARD
            else -> return
        }
        if (newMode == mode) {
            return
        }
        findCallBackSetMode()?.onModeChange(newMode) ?: return
        mode = newMode
    }

    private fun findCallBackSetMode(): CallBack? {
        return (parentFragment as? CallBack) ?: (activity as? CallBack)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


    interface CallBack {
        fun onModeChange(mode: ReaderMode)
    }

    companion object {
        private const val TAG = "TAG_CONFIG_PAGER"
        private const val MODE_BUNDLE = "bundle_reader_mode"

        fun show(fragmentManager: FragmentManager, reader: ReaderMode) {
            val args = Bundle()
            args.putInt(MODE_BUNDLE, reader.id)
            val fragment = ConfigPagerSheet()
            fragment.arguments = args
            return fragment.show(fragmentManager, TAG)
        }
    }

}