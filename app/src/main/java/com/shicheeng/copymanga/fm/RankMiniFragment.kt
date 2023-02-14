package com.shicheeng.copymanga.fm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.adapter.RankMangaAdapter
import com.shicheeng.copymanga.data.MangaRankMiniModel
import com.shicheeng.copymanga.databinding.LayoutMangaRankBackBinding
import com.shicheeng.copymanga.util.KeyWordSwap
import com.shicheeng.copymanga.util.authorNameReformation
import com.shicheeng.copymanga.util.formNumberToRead

class RankMiniFragment : Fragment() {

    companion object {
        fun newInstance(jsonArrayString: String): RankMiniFragment {
            val args = Bundle()
            args.putString("type", jsonArrayString)
            val fragment = RankMiniFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: LayoutMangaRankBackBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = LayoutMangaRankBackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val type = arguments?.getString("type")!!
        val arrayJson = JsonParser.parseString(type).asJsonArray
        val list = parserJsonData(arrayJson)
        val adapter = RankMangaAdapter()
        adapter.submitList(list)
        binding.recyclerMangaRank.adapter = adapter
        binding.recyclerMangaRank.isNestedScrollingEnabled = true
        adapter.setOnItemClickListener { v, position ->
            val bundle = bundleOf(KeyWordSwap.PATH_WORD_TYPE to list[position].pathWord)
            v.findNavController().navigate(R.id.infoFragment, bundle)
        }
    }

    private fun parserJsonData(array: JsonArray): List<MangaRankMiniModel> {

        return buildList {
            array.forEach { jsonElement ->
                val comic = jsonElement.asJsonObject["comic"].asJsonObject
                val popular = comic["popular"].asLong.formNumberToRead()
                val name = comic["name"].asString
                val pathWord = comic["path_word"].asString
                val author = comic["author"].asJsonArray.authorNameReformation()
                val cover = comic["cover"].asString
                val riseNum = jsonElement.asJsonObject["rise_num"].asLong.formNumberToRead()
                val data = MangaRankMiniModel(name, author, cover, popular, riseNum, pathWord)
                add(data)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}