package com.shicheeng.copymanga

import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.view.*
import androidx.activity.viewModels
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.Insets
import androidx.core.view.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import androidx.viewpager2.widget.ViewPager2.LAYOUT_DIRECTION_LTR
import androidx.viewpager2.widget.ViewPager2.LAYOUT_DIRECTION_RTL
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.snackbar.Snackbar
import com.shicheeng.copymanga.app.AppAttachCompatActivity
import com.shicheeng.copymanga.data.MangaHistoryDataModel
import com.shicheeng.copymanga.data.MangaInfoChapterDataBean
import com.shicheeng.copymanga.data.MangaReaderPage
import com.shicheeng.copymanga.data.ReaderState
import com.shicheeng.copymanga.databinding.ActivityMangaReaderBinding
import com.shicheeng.copymanga.dialog.ConfigPagerSheet
import com.shicheeng.copymanga.fm.domain.PagerCache
import com.shicheeng.copymanga.fm.domain.PagerLoader
import com.shicheeng.copymanga.fm.reader.ReaderManager
import com.shicheeng.copymanga.fm.reader.ReaderMode
import com.shicheeng.copymanga.fm.reader.noraml.PageSliderFormatter
import com.shicheeng.copymanga.util.*
import com.shicheeng.copymanga.viewmodel.ReaderViewModel
import com.shicheeng.copymanga.viewmodel.ReaderViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MangaReaderActivity : AppAttachCompatActivity(), ConfigPagerSheet.CallBack,
    PageSelectPosition {

    private val mangaReaderNavArgs: MangaReaderActivityArgs by navArgs()
    private lateinit var binding: ActivityMangaReaderBinding
    private val viewModel by viewModels<ReaderViewModel> {
        ReaderViewModelFactory(
            (application as MyApp).repo,
            (application as MyApp).appPreference,
            PagerLoader(PagerCache(this)),
            FileUtil(this, lifecycleScope),
            mangaReaderNavArgs.mangaChapterItems,
            mangaReaderNavArgs.mangaChapterItem
        )
    }
    private val windowInsetsController by lazy {
        WindowCompat.getInsetsController(window, window.decorView)
    }
    private lateinit var pathWord: String
    private lateinit var uuid: String
    private lateinit var sharedPref: SharedPreferences
    private lateinit var readerManager: ReaderManager
    private lateinit var readerHistoryDataModel: MangaHistoryDataModel
    private var isLast: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMangaReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        windowsInsets(binding.root) { view: View, insets: Insets ->
            binding.mangaReaderToolbar
                .updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    topMargin = insets.top
                }
            binding.mangaReaderBottomToolbar.updatePadding(bottom = insets.bottom)
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                rightMargin = insets.right
            }
        }

        pathWord = mangaReaderNavArgs.pathWord
        uuid = mangaReaderNavArgs.mangaChapterItem.uuidText
        binding.mangaReaderToolbar.title = mangaReaderNavArgs.mangaTitle
        sharedPref = AppSetting.getInstance(this)
        readerManager = ReaderManager(supportFragmentManager, R.id.manga_reader_container)


        viewModel.readerModel.observe(this, this::initializeReaderMode)
        viewModel.information.observeWithPrevious(this, this::onUIChange)
        viewModel.hide.observe(this, this::hideSystemBar)
        viewModel.historyData.observe(this, this::onHistoryModelAttach)
        viewModel.errorHandler.observe(this, this::onError)
        viewModel.loadingCounter.observe(this, this::onLoading)


        initializeBottomMenu()
        binding.mangaReaderSlider.setLabelFormatter(PageSliderFormatter())
        ReaderSliderAttach(this, viewModel).attach(binding.mangaReaderSlider)
        binding.mangaReaderNext.setOnClickListener { loadChapter(true) }
        binding.mangaReaderPrevious.setOnClickListener { loadChapter(false) }
        initializeReaderModeText(readerManager.currentReaderMode ?: ReaderMode.NORMAL)
    }

    private fun initializeReaderModeText(readerMode: ReaderMode) {
        binding.readerMangaModeTip.text = when (readerMode) {
            ReaderMode.NORMAL -> getString(R.string.japanese_r_to_l)
            ReaderMode.STANDARD -> getString(R.string.manga_mode_l_t_r)
            ReaderMode.WEBTOON -> getString(R.string.korea_chinese_top_to_bottom)
        }
    }

    private fun initializeReaderMode(readerMode: ReaderMode) {
        rebuildReaderNavigation(readerMode)
        if (readerManager.currentReaderMode != readerMode) {
            readerManager.replace(readerMode)
        }
    }


    private fun initializeBottomMenu() {
        setSupportActionBar(binding.mangaReaderToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.mangaReaderToolbar.setNavigationOnClickListener { finish() }
        //The bottom menu.refer to Tachiyomi
        val materialShape = (binding.mangaReaderToolbar.background as MaterialShapeDrawable).apply {
            elevation =
                resources.getDimension(com.google.android.material.R.dimen.m3_sys_elevation_level2)
            alpha = 242
        }
        binding.mangaReaderBottomToolbar.background = materialShape.copy(this@MangaReaderActivity)
        binding.mangaReaderSeeker.background = materialShape.copy(this@MangaReaderActivity)?.apply {
            setCornerSize(999f)
        }

        listOf(
            binding.mangaReaderPrevious,
            binding.mangaReaderNext,
            binding.mangaReaderSetting
        ).forEach {
            it.background = binding.mangaReaderSeeker.background.copy(this)
            it.foreground = RippleDrawable(
                ColorStateList.valueOf(getThemeColor(android.R.attr.colorControlHighlight)),
                null,
                it.background,
            )
        }

        binding.mangaReaderSetting.setOnClickListener {
            ConfigPagerSheet.show(
                supportFragmentManager,
                readerManager.currentReaderMode ?: return@setOnClickListener
            )
        }

        val toolbarColor = ColorUtils.setAlphaComponent(
            materialShape.resolvedTintColor,
            materialShape.alpha
        )
        window.statusBarColor = toolbarColor
    }

    private fun onUIChange(state: ReaderState?, old: ReaderState?) {

        if (state == null) {
            return
        }
        binding.readerMangaSubtitle.text = state.subTime ?: getString(R.string.local)
        binding.mangaReaderToolbar.subtitle =
            state.chapterName ?: getString(android.R.string.unknownName)

        readerHistoryDataModel = MangaHistoryDataModel(
            name = mangaReaderNavArgs.mangaTitle,
            time = System.currentTimeMillis(),
            url = mangaReaderNavArgs.coverUrl,
            pathWord = pathWord,
            nameChapter = state.chapterName ?: getString(android.R.string.unknownName),
            positionChapter = state.chapterPosition,
            positionPage = state.currentPage,
            readerModeId = readerManager.currentReaderMode?.id ?: ReaderMode.NORMAL.id
        )
        isLast = state.currentPage == state.totalPage - 1
        viewModel.insertOrUpdateHistory(readerHistoryDataModel)

        if (old?.chapterName != null && state.chapterName != old.chapterName) {
            if (!state.chapterName.isNullOrEmpty()) {
                Snackbar.make(
                    binding.mangaReaderContainer,
                    state.chapterName,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        binding.mangaReaderPageIndicator.text =
            getString(R.string.chapter_page_indicator, (state.currentPage + 1), state.totalPage)
        binding.mangaReaderChapterTotalNumber.text = state.totalPage.toString()
        binding.mangaReaderChapterNowNumber.text = (state.currentPage.plus(1)).toString()
        if (state.totalPage == 1) {
            binding.mangaReaderSeeker.isInvisible = true
        } else {
            binding.mangaReaderSeeker.isInvisible = false
            binding.mangaReaderSlider.valueTo = (state.totalPage.toFloat() - 1)
            binding.mangaReaderSlider.value = state.currentPage.toFloat()
        }
    }

    private fun onHistoryModelAttach(historyDataModel: MangaHistoryDataModel?) {
        ReaderMode.idOf(historyDataModel?.readerModeId ?: ReaderMode.NORMAL.id)?.let {
            viewModel.switchMode(it)
            initializeReaderModeText(it)
        }
    }

    override fun onPositionCallBack(page: MangaReaderPage) {
        lifecycleScope.launch(Dispatchers.Default) {
            val pages = viewModel.mangaContent.value?.list
                ?: return@launch
            val index = pages.indexOfFirst { it.urlHashCode == page.urlHashCode }
            if (index != -1) {
                withContext(Dispatchers.Main) {
                    readerManager.currentReader?.moveToPosition(position = index, true)
                }
            }
        }
    }

    private fun loadChapter(isNext: Boolean) {
        val uuid = viewModel.getCurrentReaderState().uuid
        val predicate: ((MangaInfoChapterDataBean) -> Boolean) = { it.uuidText == uuid }
        val list = mangaReaderNavArgs.mangaChapterItems
        val uuidIndex = if (isNext) list.indexOfFirst(predicate) else list.indexOfLast(predicate)
        if (uuidIndex == -1) return
        val newChapterUUId = list.getOrNull(if (isNext) uuidIndex + 1 else uuidIndex - 1)?.uuidText
        viewModel.switchChapter(newChapterUUId)
    }

    override fun onModeChange(mode: ReaderMode) {
        rebuildReaderNavigation(mode)
        viewModel.switchMode(mode)
        initializeReaderModeText(mode)
        viewModel.saveCurrentState(readerManager.currentReader?.currentState())
        val wannaSave = readerHistoryDataModel.copy(readerModeId = mode.id)
        viewModel.insertOrUpdateHistory(wannaSave)
    }

    private fun onError(e: Throwable) {
        e.printStackTrace()
        binding.mangaReaderCircularProgressIndicator.isVisible = true
        binding.mangaReaderCircularProgressIndicator.text = getString(R.string.touch_to_retry)
        binding.mangaReaderCircularProgressIndicator.setOnClickListener {
            viewModel.retry()
        }
    }

    private fun onLoading(boolean: Boolean) {
        binding.mangaReaderCircularProgressIndicator.isVisible = boolean
    }

    private fun rebuildReaderNavigation(mode: ReaderMode) {
        if (mode == ReaderMode.STANDARD || mode == ReaderMode.WEBTOON) {
            binding.mangaReaderNav.layoutDirection = LAYOUT_DIRECTION_LTR
        } else {
            binding.mangaReaderNav.layoutDirection = LAYOUT_DIRECTION_RTL
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                readerManager.currentReader?.moveDelta(-1)
                true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                readerManager.currentReader?.moveDelta(1)
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }


    private fun hideSystemBar(
        isHide: Boolean,
    ) {
        // window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        val transition = TransitionSet()
            .setOrdering(TransitionSet.ORDERING_TOGETHER)
            .addTransition(Slide(Gravity.TOP).addTarget(binding.mangaReaderToolbar))
            .addTransition(Slide(Gravity.BOTTOM).addTarget(binding.mangaReaderBottomSheet))
        TransitionManager.beginDelayedTransition(binding.root, transition)
        binding.mangaReaderBottomSheet.isGone = isHide
        binding.mangaReaderToolbar.isGone = isHide
        if (isHide) {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }

    }

}