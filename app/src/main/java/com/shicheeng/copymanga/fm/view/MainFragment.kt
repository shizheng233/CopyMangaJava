package com.shicheeng.copymanga.fm.view

import android.os.Bundle
import android.view.*
import android.view.ViewGroup.MarginLayoutParams
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.JsonArray
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.adapter.BannerMangaAdapter
import com.shicheeng.copymanga.adapter.MangaRankPagerAdapter
import com.shicheeng.copymanga.adapter.RecyclerViewMangaAdapter
import com.shicheeng.copymanga.data.BannerList
import com.shicheeng.copymanga.data.DataBannerBean
import com.shicheeng.copymanga.data.ListBeanManga
import com.shicheeng.copymanga.databinding.ActivityMainBinding
import com.shicheeng.copymanga.util.KeyWordSwap
import com.shicheeng.copymanga.util.authorNameReformation
import com.shicheeng.copymanga.viewmodel.HomeViewModel
import com.shicheeng.copymanga.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainFragment : Fragment(), View.OnAttachStateChangeListener {


    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = ActivityMainBinding.inflate(inflater, container, false)
        binding.root.addOnAttachStateChangeListener(this)
        bindState()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel.loadData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)

                val search = menu.findItem(R.id.id_manga_search)
                val searchView = search.actionView as SearchView
                val mangaExploreView = menu.findItem(R.id.id_manga_login)
                searchView.queryHint = getString(R.string.search_text)

                search.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                    override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                        mangaExploreView.isVisible = false
                        return true
                    }

                    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                        mangaExploreView.isVisible = true
                        return true
                    }

                })

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (query != null) {
                            val action =
                                MainFragmentDirections.actionMainFragmentToSearchResultFragment(
                                    query
                                )
                            findNavController().navigate(action)
                        }
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return false
                    }

                })

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.about_main -> {
                        val action = MainFragmentDirections.actionMainFragmentToSettingFragment()
                        findNavController().navigate(action)
                        true
                    }
                    R.id.id_manga_login -> {
                        val action = MainFragmentDirections.actionMainFragmentToExploreFragment()
                        findNavController().navigate(action)
                        true
                    }
                    R.id.id_manga_history -> {
                        val action = MainFragmentDirections.actionMainFragmentToPersonalFragment()
                        findNavController().navigate(action)
                        true
                    }
                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.included1.headline1.setOnHeadClickListener {
            it.findNavController().navigate(R.id.action_mainFragment_to_recommendMangaFragment)
        }

        binding.included1.headline2.setOnHeadClickListener {
            it.findNavController().navigate(R.id.action_mainFragment_to_rankFragment)
        }

        binding.included1.headline3.setOnHeadClickListener {
            it.findNavController().navigate(R.id.action_mainFragment_to_hotFragment)
        }

        binding.included1.headline4.setOnHeadClickListener {
            it.findNavController().navigate(R.id.action_mainFragment_to_newestFragment)
        }

        binding.included1.headline5.setOnHeadClickListener {
            it.findNavController().navigate(R.id.action_mainFragment_to_finishedFragment)
        }
    }

    override fun onViewAttachedToWindow(v: View) {
        val insetsCompat = ViewCompat.getRootWindowInsets(v)
        val systemBarInsets = insetsCompat?.getInsets(WindowInsetsCompat.Type.systemBars())
        binding.included1.recyclerViewManga6.updateLayoutParams<MarginLayoutParams> {
            bottomMargin = systemBarInsets?.bottom ?: 0
        }
    }

    override fun onViewDetachedFromWindow(v: View) {

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun bindState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.uiState.collect {
                    when (it) {
                        is HomeViewModel.UiState.Success -> bindUiSuccessful(it)
                        is HomeViewModel.UiState.Error -> bindUiError(it.error)
                        else -> bindUiLoading()
                    }
                }
            }
        }
    }

    private fun bindUiSuccessful(homeData: HomeViewModel.UiState.Success) {
        homeData.apply {
            JsonToRecIntegrator.setJsonToBanner(
                binding.included1.recyclerViewManga1,
                data.listBanner
            )
            JsonToRecIntegrator.setJsonToRecommend(
                binding.included1.recyclerViewManga2,
                data.listRecommend
            )

            //Rank
            //漫画排行榜
            val listTitle = listOf(
                getString(R.string.day_rank),
                getString(R.string.week_rank),
                getString(R.string.month_rank)
            )
            val list = buildList {
                data.listRank.forEach { (_, u) ->
                    add(u.toString())
                }
            }
            val adapter =
                MangaRankPagerAdapter(list, childFragmentManager, viewLifecycleOwner.lifecycle)
            binding.included1.rankViewPager1.apply {
                setAdapter(adapter)
                offscreenPageLimit = 2
            }
            TabLayoutMediator(
                binding.included1.rankTab1,
                binding.included1.rankViewPager1
            ) { tab: TabLayout.Tab, position: Int ->
                tab.text = listTitle[position]
            }.attach()

            JsonToRecIntegrator.setJsonToRecommend(
                binding.included1.recyclerViewManga4,
                data.listHot
            )
            JsonToRecIntegrator.setJsonToRecommend(
                binding.included1.recyclerViewManga5,
                data.listNewest
            )
            JsonToRecIntegrator.setJsonToOther(
                binding.included1.recyclerViewManga6,
                data.listFinished
            )
        }

        binding.homeLoadingProgressBar.isVisible = false
        binding.nestedScrollView.isVisible = true
        binding.errorLayout.errorTextLayout.isVisible = false
    }

    private fun bindUiLoading() {
        binding.homeLoadingProgressBar.isVisible = true
        binding.nestedScrollView.isVisible = false
        binding.errorLayout.errorTextLayout.isVisible = false
    }

    private fun bindUiError(e: Exception) {
        binding.errorLayout.errorTextLayout.isVisible = true
        binding.homeLoadingProgressBar.isVisible = false
        binding.errorLayout.errorTextTipDesc.text = e.message ?: getString(R.string.error)
        binding.errorLayout.btnErrorRetry.setOnClickListener {
            homeViewModel.loadData()
        }
    }

    private object JsonToRecIntegrator {

        fun setJsonToBanner(recyclerview1: RecyclerView?, list: List<BannerList>) {
            val myList1: ArrayList<DataBannerBean> = ArrayList()
            for (i in list.indices) {

                val bannerBean = DataBannerBean()
                val jsonObject1 = list[i].jsonObject //Banner组下面的各个jsonObject
                bannerBean.bannerBrief = jsonObject1["brief"].asString
                bannerBean.bannerImageUrl = jsonObject1["cover"].asString
                bannerBean.uuidManga = jsonObject1["comic"]
                    .asJsonObject["path_word"].asString
                myList1.add(bannerBean)
            }
            val adapter = BannerMangaAdapter(myList1)
            recyclerview1?.adapter = adapter
            adapter.setOnItemClickListener { v, position ->
                val bundle = bundleOf(KeyWordSwap.PATH_WORD_TYPE to myList1[position].uuidManga)
                v?.findNavController()?.navigate(R.id.infoFragment, bundle)
            }

        }


        /**
         * 将JSONArray与RecyclerView连接起来
         * @array JsonObject
         *
         */
        fun setJsonToRecommend(recyclerView_2: RecyclerView?, array: JsonArray) {
            val myList1: MutableList<ListBeanManga> = ArrayList()
            for (i in 0 until array.size()) {
                val beanManga = ListBeanManga()
                //推荐组下面的各个jsonObject，因为有第二个jsonOBj，所以需要再次获取一次
                val jsonObject1 = array[i].asJsonObject.getAsJsonObject("comic")
                beanManga.nameManga = jsonObject1["name"].asString
                beanManga.urlCoverManga = jsonObject1["cover"].asString
                beanManga.pathWordManga = jsonObject1["path_word"].asString
                //获取作者列表，判定是否大于一位作家
                val mangaAuthor = jsonObject1["author"].asJsonArray.authorNameReformation()
                beanManga.authorManga = mangaAuthor
                myList1.add(beanManga)
            }
            val adapter1 = RecyclerViewMangaAdapter(myList1)
            adapter1.setOnItemClickListener { v, position ->
                val bundle = bundleOf(KeyWordSwap.PATH_WORD_TYPE to myList1[position].pathWordManga)
                v?.findNavController()?.navigate(R.id.infoFragment, bundle)
            }
            recyclerView_2!!.adapter = adapter1
        }

        /**
         * 将JSONArray与RecyclerView连接起来(类型二)
         * @array JsonObject
         */
        fun setJsonToOther(rec_2: RecyclerView?, array: JsonArray) {
            val myList1: MutableList<ListBeanManga> = ArrayList()
            for (i in 0 until array.size()) {
                val beanManga = ListBeanManga()
                val jsonObject1 = array[i].asJsonObject
                beanManga.nameManga = jsonObject1["name"].asString
                beanManga.urlCoverManga = jsonObject1["cover"].asString
                beanManga.pathWordManga = jsonObject1["path_word"].asString

                val mangaAuthorList = jsonObject1["author"].asJsonArray.authorNameReformation()
                beanManga.authorManga = mangaAuthorList
                myList1.add(beanManga)
            }
            val adapter2 = RecyclerViewMangaAdapter(myList1)
            adapter2.setOnItemClickListener { v, position ->
                val bundle = bundleOf(KeyWordSwap.PATH_WORD_TYPE to myList1[position].pathWordManga)
                v?.findNavController()?.navigate(R.id.infoFragment, bundle)
            }
            rec_2!!.adapter = adapter2
        }
    }

}