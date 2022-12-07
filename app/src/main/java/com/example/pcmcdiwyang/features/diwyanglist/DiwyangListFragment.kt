package com.example.pcmcdiwyang.features.diwyanglist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pcmcdiwyang.MainActivity
import com.example.pcmcdiwyang.PersonDetailsActivity
import com.example.pcmcdiwyang.data.ApplicantData
import com.example.pcmcdiwyang.databinding.FragmentDiwyangListBinding

class DiwyangListFragment : Fragment(), DivyangListAdapter.DivyangListListner{

    private var _binding: FragmentDiwyangListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val diwyangListViewModel = ViewModelProvider(this).get(DiwyangListViewModel::class.java)
        _binding = FragmentDiwyangListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val listener : DivyangListAdapter.DivyangListListner = this

        diwyangListViewModel.getDivyangList()

        /*with(binding.rvDivyangList) {
            val list : List<ApplicantData> = ArrayList()

            layoutManager = LinearLayoutManager(context)
            adapter = DivyangListAdapter(list, listener)
        }*/

        diwyangListViewModel.divyangList.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()){
                with(binding.rvDivyangList) {
                    //val list : List<ApplicantData> = ArrayList()
                    layoutManager = LinearLayoutManager(context)
                    adapter = DivyangListAdapter(it, listener)
                }
            }
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemSelected(applicantData: ApplicantData) {
        val intent =Intent(activity, PersonDetailsActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("data", applicantData)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}