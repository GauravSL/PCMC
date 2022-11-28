package com.example.pcmcdiwyang.features.diwyanglist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pcmcdiwyang.MainActivity
import com.example.pcmcdiwyang.PersonDetailsActivity
import com.example.pcmcdiwyang.databinding.FragmentDiwyangListBinding

class DiwyangListFragment : Fragment() {

    private var _binding: FragmentDiwyangListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val diwyangListViewModel =
            ViewModelProvider(this).get(DiwyangListViewModel::class.java)

        _binding = FragmentDiwyangListBinding.inflate(inflater, container, false)
        val root: View = binding.root


        binding.card1.setOnClickListener {
            startActivity(Intent(activity, PersonDetailsActivity::class.java))
        }

        with(binding.list) {
            layoutManager = LinearLayoutManager(context)
          //  adapter = MyItemRecyclerViewAdapter(PlaceholderContent.ITEMS)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}