package com.example.pcmcdiwyang.features.diwyanglist

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pcmcdiwyang.Utils
import com.example.pcmcdiwyang.data.model.ApplicantData
import com.example.pcmcdiwyang.databinding.FragmentDiwyangListBinding
import com.example.pcmcdiwyang.scanners.face.tflite.SimilarityClassifier
import com.example.pcmcdiwyang.screens.PersonDetailsActivity


class DiwyangListFragment : Fragment(), DivyangListAdapter.DivyangListListner{

    private var _binding: FragmentDiwyangListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    val nonDigits = Regex("[^\\d]")
    lateinit var  diwyangListViewModel : DiwyangListViewModel
    lateinit var adapter1 : DivyangListAdapter
    lateinit var applicantList: List<ApplicantData>
    lateinit var face1: SimilarityClassifier.Recognition
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        diwyangListViewModel = ViewModelProvider(this).get(DiwyangListViewModel::class.java)
        _binding = FragmentDiwyangListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val listener : DivyangListAdapter.DivyangListListner = this

        if (Utils.isInternetAvailable(requireContext())) {
            binding.progressBar.visibility= View.VISIBLE
            diwyangListViewModel.getDivyangList()
        }

        /*with(binding.rvDivyangList) {
            val list : List<ApplicantData> = ArrayList()

            layoutManager = LinearLayoutManager(context)
            adapter = DivyangListAdapter(list, listener)
        }*/

        diwyangListViewModel.divyangList.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()){
                applicantList =it

                adapter1 = DivyangListAdapter(it, listener)
                val layoutManager  = LinearLayoutManager(context)
                binding.rvDivyangList.layoutManager = layoutManager
                binding.rvDivyangList.adapter = adapter1
                binding.progressBar.visibility= View.GONE

            }
        })

        diwyangListViewModel.biometricData.observe(viewLifecycleOwner, Observer {
            it.face?.let { it1 -> Log.e("Gaurav", it1) }
            val faceVal = it.face
            if (!faceVal.equals("face", true )) {
              //  face1 = Gson().fromJson(it.face, SimilarityClassifier.Recognition::class.java)
                //Temp.face = Temp.getRecognationData(it.face)
               // adapter1.setFace(face1)
//                for (app in applicantList){
//                    app.face = face1.crop
//                }
//                Log.e("Gaurav", applicantList[0].face.toString())
                //val bmp2: Bitmap = face1.crop.copy(face1.crop.config, true)
                /*if (Temp.recognitionToSend!=null)
                binding.profilePic1.setImageBitmap(Gson().fromJson(Temp.recognitionToSend, SimilarityClassifier.Recognition::class.java).crop)
*/
               // binding.profilePic1.setImageBitmap(Temp.face.crop)
            }
        })

        binding.etAadharNumber.addTextChangedListener(object : TextWatcher {
            var current = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString() != current) {
                    val userInput = s.toString().replace(nonDigits,"")
                    if (userInput.length <= 12) {
                        current = userInput.chunked(4).joinToString("-")
                        s.filters = arrayOfNulls<InputFilter>(0)
                    }
                    s.replace(0, s.length, current, 0, current.length)
                }
            }
        })

        binding.buttonCheck.setOnClickListener {

        }
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