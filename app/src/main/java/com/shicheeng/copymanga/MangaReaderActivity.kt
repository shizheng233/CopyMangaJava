package com.shicheeng.copymanga

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.Insets
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2.LAYOUT_DIRECTION_LTR
import androidx.viewpager2.widget.ViewPager2.LAYOUT_DIRECTION_RTL
import com.google.android.material.shape.MaterialShapeDrawable
import com.shicheeng.copymanga.app.AppAttachCompatActivity
import com.shicheeng.copymanga.data.MangaReaderPage
import com.shicheeng.copymanga.data.ReaderContent
import com.shicheeng.copymanga.data.ReaderState
import com.shicheeng.copymanga.databinding.ActivityMangaReaderBinding
import com.shicheeng.copymanga.dialog.ConfigPagerSheet
import com.shicheeng.copymanga.fm.delegate.IdlingDelegate
import com.shicheeng.copymanga.fm.reader.MangaLoader
import com.shicheeng.copymanga.fm.reader.ReaderManager
import com.shicheeng.copymanga.fm.reader.ReaderMode
import com.shicheeng.copymanga.fm.reader.noraml.PageSliderFormatter
import com.shicheeng.copymanga.ui.screen.setting.SettingPref
import com.shicheeng.copymanga.util.GestureHelper
import com.shicheeng.copymanga.util.PageSelectPosition
import com.shicheeng.copymanga.util.ReaderSliderAttach
import com.shicheeng.copymanga.util.copy
import com.shicheeng.copymanga.util.getThemeColor
import com.shicheeng.copymanga.util.hasGlobalPoint
import com.shicheeng.copymanga.util.observe
import com.shicheeng.copymanga.util.transformPair
import com.shicheeng.copymanga.view.control.ReaderControl
import com.shicheeng.copymanga.viewmodel.ReaderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MangaReaderActivity : AppAttachCompatActivity(),
    ConfigPagerSheet.CallBack,
    PageSelectPosition,
    GestureHelper.GestureListener,
    ReaderControl.ControlDelegateListener,
    IdlingDelegate.IdleCallback {


    private lateinit var binding: ActivityMangaReaderBinding

    private val viewModel by viewModels<ReaderViewModel>()
    private val windowInsetsController by lazy {
        WindowInsetsControllerCompat(window, binding.root)
    }


    @Inject
    lateinit var settingPref: SettingPref


    private lateinit var readerManager: ReaderManager
    private lateinit var gestureHelper: GestureHelper
    private lateinit var control: ReaderControl
    private var isLast: Boolean = false
    private var gestureInsets: Insets = Insets.NONE
    private val idlingDelegate = IdlingDelegate(this)

    override val readerMode: ReaderMode?
        get() = readerManager.currentReaderMode


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMangaReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setCutoutShort(settingPref.cutoutDisplay)
        windowsInsets(binding.root) { view, systemGesture ->
            gestureInsets = systemGesture
            binding.mangaReaderToolbar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = top
            }
            binding.mangaReaderBottomToolbar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = bottom
            }
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = left
                rightMargin = right
            }
        }

        readerManager = ReaderManager(supportFragmentManager, R.id.manga_reader_container)
        gestureHelper = GestureHelper(this, this)
        control = ReaderControl(this, settingPref = settingPref)

        viewModel.readerModel.observe(this, Lifecycle.State.STARTED) {
            initializeReaderMode(it)
        }
        viewModel.information.transformPair().observe(this, this::onUIChange)
        viewModel.errorHandler.observe(this, this::onError)
        viewModel.loadingCounter.observe(this, this::onLoading)
        viewModel.mangaContent.observe(this, this::withPageContent)

        initializeBottomMenu()
        binding.mangaReaderSlider.setLabelFormatter(PageSliderFormatter())
        ReaderSliderAttach(this, viewModel).attach(binding.mangaReaderSlider)
        binding.mangaReaderNext.setOnClickListener { loadChapter(true) }
        binding.mangaReaderPrevious.setOnClickListener { loadChapter(false) }
        idlingDelegate.bindToLifecycle(this)
    }


    private fun initializeReaderMode(readerMode: ReaderMode?) {
        if (readerMode == null) return
        binding.readerMangaModeTip.text = when (readerMode) {
            ReaderMode.NORMAL -> getString(R.string.japanese_r_to_l)
            ReaderMode.STANDARD -> getString(R.string.manga_mode_l_t_r)
            ReaderMode.WEBTOON -> getString(R.string.korea_chinese_top_to_bottom)
        }
        if (readerManager.currentReaderMode != readerMode) {
            readerManager.replace(readerMode)
        }
    }

    private fun withPageContent(readerContent: ReaderContent) {
        if (readerContent.list.isNotEmpty()) {
            hideSystemBar(true)
        }
    }

    // FIXME: 有时候没有提示
    private fun initializeBottomMenu() {
        setSupportActionBar(binding.mangaReaderToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.mangaReaderToolbar.setNavigationOnClickListener { finish() }
        //The bottom menu refer from Tachiyomi
        val materialShape = (binding.mangaReaderToolbar.background as MaterialShapeDrawable)
            .apply {
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
                fragmentManager = supportFragmentManager,
                reader = readerManager.currentReaderMode ?: return@setOnClickListener
            )
        }

        val toolbarColor = ColorUtils.setAlphaComponent(
            materialShape.resolvedTintColor,
            materialShape.alpha
        )
        window.statusBarColor = toolbarColor
        window.navigationBarColor = toolbarColor
    }

    private fun onUIChange(pair: Pair<ReaderState?, ReaderState?>) {
        val (old: ReaderState?, state: ReaderState?) = pair
        title = state?.mangaName ?: old?.mangaName ?: getString(android.R.string.unknownName)
        if (state == null) {
            supportActionBar?.subtitle = null
            binding.mangaReaderSeeker.isVisible = false
            return
        }
        binding.readerMangaSubtitle.text = state.subTime ?: getString(R.string.local)
        supportActionBar?.subtitle =
            state.chapterName ?: getString(android.R.string.unknownName)

        isLast = state.currentPage == state.totalPage - 1

        if (old?.chapterName != null && state.chapterName != old.chapterName) {
            if (!state.chapterName.isNullOrEmpty()) {
                binding.mangaReaderCircularProgressIndicator.tip(
                    state.chapterName,
                    TimeUnit.SECONDS.toMillis(1)
                )
            }
        }

        binding.mangaReaderPageIndicator.text =
            getString(R.string.chapter_page_indicator, (state.currentPage + 1), state.totalPage)
        binding.mangaReaderChapterTotalNumber.text = state.totalPage.toString()
        binding.mangaReaderChapterNowNumber.text = (state.currentPage.plus(1)).toString()

        if (!state.isSliderAvailable()) {
            binding.mangaReaderSeeker.isInvisible = true
        } else {
            binding.mangaReaderSeeker.isInvisible = false
            binding.mangaReaderSlider.valueTo = (state.totalPage.toFloat() - 1)
            binding.mangaReaderSlider.value = state.currentPage.toFloat()
        }
        viewModel.saveLocalChapterState(state.currentPage)
    }

    override fun onPositionCallBack(page: MangaReaderPage) {
        lifecycleScope.launch(Dispatchers.Default) {
            val pages = viewModel.mangaContent.value.list
            val index = pages.indexOfFirst { it.urlHashCode == page.urlHashCode }
            if (index != -1) {
                withContext(Dispatchers.Main) {
                    readerManager.currentReader?.moveToPosition(position = index, index <= 2)
                }
            }
        }
    }

    override fun onTouch(area: Int) {
        control.onGridTouch(area, binding.mangaReaderContainer)
    }

    override fun onProcessTouch(rawX: Int, rawY: Int): Boolean {
        return if (
            rawX <= gestureInsets.left ||
            rawY <= gestureInsets.top ||
            rawX >= binding.root.width - gestureInsets.right ||
            rawY >= binding.root.height - gestureInsets.bottom ||
            binding.mangaReaderToolbar.hasGlobalPoint(rawX, rawY) ||
            binding.mangaReaderBottomToolbar.hasGlobalPoint(rawX, rawY)
        ) {
            false
        } else {
            val touchable = window.peekDecorView()?.touchables
            touchable?.none { it.hasGlobalPoint(rawX, rawY) } ?: true
        }
    }

    override fun scrollPage(delta: Int) {
        readerManager.currentReader?.moveDelta(delta)
    }

    override fun hide() {
        hideSystemBar(binding.mangaReaderToolbar.isVisible)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        gestureHelper.dispatchTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    private fun loadChapter(isNext: Boolean) {
        val uuid = viewModel.getCurrentReaderState().uuid
        val predicate: ((MangaReaderPage) -> Boolean) = { it.uuid == uuid }
        val list = viewModel.mangaContent.value.list
        val uuidIndex = if (isNext) list.indexOfFirst(predicate) else list.indexOfLast(predicate)
        if (uuidIndex == -1) return
        val newChapterUUId = list.getOrNull(if (isNext) uuidIndex + 1 else uuidIndex - 1)?.uuid
        viewModel.switchChapter(newChapterUUId)
    }

    override fun onIdle() {
        viewModel.saveCurrentState(readerManager.currentReader?.currentState())
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        idlingDelegate.onUserInteraction()
    }

    override fun onModeChange(mode: ReaderMode) {
        rebuildReaderNavigation(mode)
        viewModel.switchMode(mode)
        viewModel.saveCurrentState(readerManager.currentReader?.currentState())
    }

    private fun onError(e: Throwable?) {
        e?.printStackTrace()
        with(binding.layoutErrorInclude) {
            errorTextTip.setTextColor(getThemeColor(com.google.android.material.R.attr.colorSurface))
            errorTextTipDesc.apply {
                setTextColor(getThemeColor(com.google.android.material.R.attr.colorSurface))
                text = e?.message
            }
            btnErrorRetry.setOnClickListener {
                viewModel.retry()
                this.root.isVisible = false
            }
        }
    }

    private fun onLoading(boolean: Boolean) {
        val hasPages = viewModel.mangaContent.value.list.isNotEmpty()
        binding.loadIndicator.isVisible = boolean && !hasPages
        if (boolean && hasPages) {
            binding.mangaReaderCircularProgressIndicator.show(R.string.in_loading_next_chapter)
        } else {
            binding.mangaReaderCircularProgressIndicator.hide()
        }
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
        val transition = TransitionSet()
            .setOrdering(TransitionSet.ORDERING_TOGETHER)
            .addTransition(Slide(Gravity.TOP).addTarget(binding.mangaReaderToolbar))
            .addTransition(Slide(Gravity.BOTTOM).addTarget(binding.mangaReaderBottomSheet))
        TransitionManager.beginDelayedTransition(binding.root, transition)
        binding.mangaReaderBottomSheet.isGone = isHide
        binding.mangaReaderToolbar.isGone = isHide
        binding.mangaReaderPageIndicator.isVisible = isHide
        if (isHide) {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }

    }

    @TargetApi(Build.VERSION_CODES.P)
    private fun setCutoutShort(enabled: Boolean) {
        window.attributes.layoutInDisplayCutoutMode = when (enabled) {
            true -> WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            false -> WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
        }

        // Trigger relayout
        hideSystemBar(!binding.mangaReaderToolbar.isVisible)
    }

    companion object {

        /**
         * 跳转到[MangaReaderActivity]
         * @param pathWord Path word
         * @param uuid 章节uuid
         */
        fun newInstance(
            context: Context,
            pathWord: String,
            uuid: String,
        ): Intent {
            val intent = Intent(context, MangaReaderActivity::class.java)
            intent.putExtra(MangaLoader.MANGA_PATH_WORD, pathWord)
            intent.putExtra(MangaLoader.MANGA_UUID, uuid)
            return intent
        }

    }

}