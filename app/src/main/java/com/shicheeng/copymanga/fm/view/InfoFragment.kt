package com.shicheeng.copymanga.fm.view

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shicheeng.copymanga.MyApp
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.adapter.MangaChapterKeyProvider
import com.shicheeng.copymanga.adapter.MangaChapterListItemLookUp
import com.shicheeng.copymanga.adapter.MangaInfoChapterAdapter
import com.shicheeng.copymanga.data.LastMangaDownload
import com.shicheeng.copymanga.data.MangaInfoChapterDataBean
import com.shicheeng.copymanga.databinding.MangaInfoBinding
import com.shicheeng.copymanga.server.DownloadService
import com.shicheeng.copymanga.util.FileUtil
import com.shicheeng.copymanga.util.KeyWordSwap
import com.shicheeng.copymanga.util.addChips
import com.shicheeng.copymanga.util.addLongChangeObserver
import com.shicheeng.copymanga.viewmodel.MangaInfoViewModel
import com.shicheeng.copymanga.viewmodel.MangaInfoViewModelFactory
import kotlinx.coroutines.launch

class InfoFragment : Fragment() {

    private var _binding: MangaInfoBinding? = null
    private val binding get() = _binding!!
    private val mangaInfoViewModel: MangaInfoViewModel by viewModels {
        MangaInfoViewModelFactory(
            arguments?.getString(KeyWordSwap.PATH_WORD_TYPE)!!,
            (requireActivity().application as MyApp).repo,
            fileUtil
        )
    }
    private var isShowAll: Boolean = false
    private var isDataBinding: Boolean = false
    private lateinit var pathWord: String
    private lateinit var urlCover: String
    private var titleManga: String? = null
    private var downloadList: List<MangaInfoChapterDataBean>? = null
    private var infoList: List<MangaInfoChapterDataBean>? = null
    private lateinit var fileUtil: FileUtil
    private var track: SelectionTracker<Long>? = null
    private val adapter = MangaInfoChapterAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        pathWord = arguments?.getString(KeyWordSwap.PATH_WORD_TYPE)!!
        fileUtil = FileUtil(requireContext(), lifecycleScope)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = MangaInfoBinding.inflate(inflater, container, false)
        binding.bindMangaInfoToRecyclerView()
        bindState()
        return binding.root
    }

    override fun onResume() {
        mangaInfoViewModel.onHistoryWanna()
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mangaInfoIncludeView.mangaDetailDesc.setOnClickListener {
            isShowAll = !isShowAll
            binding.mangaInfoIncludeView.mangaDetailDesc.maxLines =
                if (isShowAll) Int.MAX_VALUE else 3
        }
        binding.mangaInfoIncludeView.recyclerMangaInfo.isNestedScrollingEnabled = true
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.rank_bottom_item, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {

                    R.id.menu_save_manga -> {
                        if (isDataBinding) {
                            if (downloadList.isNullOrEmpty()) {
                                createDialog {
                                    val list = buildList {
                                        infoList?.forEach { add(it.toDownloadChapter()) }
                                    }
                                    pushDownload(
                                        LastMangaDownload(titleManga ?: return@createDialog, list)
                                    )
                                }
                            } else {
                                val list = buildList {
                                    downloadList?.forEach { add(it.toDownloadChapter()) }
                                }
                                pushDownload(LastMangaDownload(titleManga ?: return false, list))
                                track?.clearSelection()
                            }

                        } else {
                            return false
                        }
                        return true
                    }

                }
                return false
            }

        }, viewLifecycleOwner, Lifecycle.State.CREATED)

        mangaInfoViewModel.chaptersModel.observe(viewLifecycleOwner) {
            adapter.submitList(emptyList())
            adapter.submitList(it)
            infoList = it
        }

        track = SelectionTracker.Builder(
            KeyWordSwap.SELECTION_CHAPTER_ID_CONFIRM,
            binding.mangaInfoIncludeView.recyclerMangaInfo,
            MangaChapterKeyProvider(binding.mangaInfoIncludeView.recyclerMangaInfo),
            MangaChapterListItemLookUp(binding.mangaInfoIncludeView.recyclerMangaInfo),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything()).build()
        adapter.tracker = track
        track?.addLongChangeObserver {
            downloadList = buildList {
                track?.selection?.forEach { id ->
                    add(infoList?.get(id.toInt()) ?: return@addLongChangeObserver)
                }
            }
        }

    }

    private fun createDialog(positiveClick: () -> Unit) {
        val alertDialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(R.string.tip_in_download)
            setMessage(R.string.would_download_all)
        }

        alertDialog.setNegativeButton(android.R.string.cancel) { di: DialogInterface, _: Int ->
            di.dismiss()
        }

        alertDialog.setPositiveButton(R.string.download_all) { di: DialogInterface, _: Int ->
            positiveClick.invoke()
            di.dismiss()
        }
        alertDialog.show()
    }


    private fun bindState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mangaInfoViewModel.uiState.collect {
                    when (it) {
                        is MangaInfoViewModel.UiState.Success -> {
                            bindUiSuccessful(it)
                        }
                        is MangaInfoViewModel.UiState.Error -> bindUiError(it)
                        MangaInfoViewModel.UiState.Loading -> bindUiLoading()
                    }
                }
            }
        }
    }


    private fun pushDownload(downloads: LastMangaDownload) {
        Intent(requireContext(), DownloadService::class.java).apply {
            putExtra(KeyWordSwap.CHAPTER_TYPE, downloads)
            requireContext().startService(this)
        }
    }

    private fun bindUiSuccessful(
        success: MangaInfoViewModel.UiState.Success,
    ) {
        success.apply {
            bindDataWithMangaHeader()
            isDataBinding = true
        }
        binding.errorLayout.errorTextLayout.isVisible = false
        binding.linearProgressBar.isVisible = false
        binding.bigScrollView.isVisible = true
    }

    private fun MangaInfoViewModel.UiState.Success.bindDataWithMangaHeader() {
        binding.mangaInfoIncludeView.mangaTitleText.text = data.info.title.also {
            titleManga = it
        }
        binding.mangaInfoIncludeView.mangaAuthorText.text = data.info.alias
        Glide.with(binding.root).load(data.info.mangaCoverUrl.also { urlCover = it })
            .into(binding.mangaInfoIncludeView.smallCover)
        //Desc
        binding.mangaInfoIncludeView.mangaDetailDesc.text = data.info.mangaDetail
        binding.mangaInfoIncludeView.recyclerMangaInfoChipTheme.addChips(data.info.themeList)
        //The manga's region and manga's viewer number.
        binding.mangaInfoIncludeView.mangaDetailBar.mangaDetailInfoPopular.text =
            data.info.mangaPopularNumber
        binding.mangaInfoIncludeView.mangaDetailBar.mangaDetailInfoRegion.text =
            data.info.mangaRegion
        binding.mangaInfoIncludeView.mangaDetailBar.mangaDetailInfoChapterSize.text =
            getString(R.string.chapter, data.mangaChapterContent?.get("total")?.asInt.toString())

        //Two state of manga. The loading state and done state with their drawable.
        val drawableForState =
            if (data.info.mangaStatusId == 0) AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_baseline_loop
            )
            else AppCompatResources.getDrawable(requireContext(), R.drawable.ic_done_all)
        binding.mangaInfoIncludeView.mangaDetailBar.mangaDetailInfoState.setCompoundDrawablesWithIntrinsicBounds(
            null,
            drawableForState,
            null,
            null
        )
        //The sheet bar of Manga's State
        binding.mangaInfoIncludeView.mangaDetailBar.mangaDetailInfoState.text =
            data.info.mangaStatus
        //The manga's last update
        binding.mangaInfoIncludeView.mangaDetailLastUpdate.text =
            getString(R.string.last_update, data.info.mangaLastUpdate)
        //The manga's author
        binding.mangaInfoIncludeView.mangaInfoAuthor.text = data.info.authorList
        binding.mangaInfoIncludeView.recyclerMangaInfo.isVisible = true
        binding.expandFabInfo.isVisible = true
        binding.expandFabInfo.text = if (data.mangaHistory == null) getString(R.string.start)
        else getString(R.string.continue_read)
        binding.expandFabInfo.setOnClickListener {
            val action =
                InfoFragmentDirections.actionInfoFragmentToMangaReaderActivity2(
                    /* mangaChapterItems = */ infoList?.toTypedArray()
                        ?: return@setOnClickListener,
                    /* pathWord = */ pathWord,
                    /* mangaTitle = */ titleManga ?: getString(R.string.manga_unknown_title),
                    /* coverUrl = */ urlCover,
                    /* mangaChapterItem = */ infoList?.get(data.mangaHistory?.positionChapter ?: 0)
                        ?: return@setOnClickListener
                )
            findNavController().navigate(action)
        }
    }

    private fun bindUiLoading() {
        binding.linearProgressBar.isVisible = true
        binding.bigScrollView.isVisible = false
        binding.errorLayout.errorTextLayout.isVisible = false
    }

    private fun bindUiError(errorState: MangaInfoViewModel.UiState.Error) {
        binding.bigScrollView.isVisible = false
        binding.linearProgressBar.isVisible = false
        binding.errorLayout.errorTextLayout.isVisible = true
        binding.errorLayout.errorTextTipDesc.text =
            errorState.error.message ?: getString(R.string.error)
        binding.errorLayout.btnErrorRetry.setOnClickListener {
            if (pathWord.isNotBlank()) {
                mangaInfoViewModel.onDataLoad()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    private fun MangaInfoBinding.bindMangaInfoToRecyclerView() {

        adapter.setOnItemClickListener {
            val action =
                InfoFragmentDirections.actionInfoFragmentToMangaReaderActivity2(
                    infoList?.toTypedArray()
                        ?: return@setOnItemClickListener,
                    pathWord,
                    titleManga ?: getString(R.string.manga_unknown_title),
                    urlCover,
                    infoList?.get(it.absoluteAdapterPosition) ?: return@setOnItemClickListener
                )
            findNavController().navigate(action)
        }

        mangaInfoIncludeView.recyclerMangaInfo.adapter = adapter


    }


}
