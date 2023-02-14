package com.shicheeng.copymanga.fm.view

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.shicheeng.copymanga.adapter.DownloadAdapter
import com.shicheeng.copymanga.databinding.FragmentDownloadInfoBinding
import com.shicheeng.copymanga.server.DownloadService
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class DownloadInfoFragment : Fragment() {

    private var _binding: FragmentDownloadInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDownloadInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = DownloadAdapter(lifecycleScope)
        binding.mangaDownloadInfoRecyclerView.setHasFixedSize(true)
        binding.mangaDownloadInfoRecyclerView.adapter = adapter
        val connection = DownloadConnection(adapter)
        requireActivity().bindService(
            Intent(requireContext(), DownloadService::class.java),
            connection,
            0
        )
        lifecycle.addObserver(connection)
    }


    private inner class DownloadConnection(private val adapter: DownloadAdapter) :
        ServiceConnection, DefaultLifecycleObserver {

        private var collectJob: Job? = null

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            collectJob?.cancel()
            val binder = service as? DownloadService.DownloadBinder
            collectJob = if (binder == null) {
                null
            } else {
                lifecycleScope.launch {
                    binder.downloads.collect {
                        adapter.submitList(it)
                        binding.mangaDownloadInfoEmptyTip.isVisible = it.isEmpty()
                    }
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            collectJob?.cancel()
            collectJob = null
            adapter.submitList(null)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            collectJob?.cancel()
            collectJob = null
            owner.lifecycle.removeObserver(this)
            requireActivity().unbindService(this)
        }

    }

}