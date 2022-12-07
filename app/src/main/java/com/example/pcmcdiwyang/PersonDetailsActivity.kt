package com.example.pcmcdiwyang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.pcmcdiwyang.data.ApplicantData
import com.example.pcmcdiwyang.databinding.ActivityMainBinding
import com.example.pcmcdiwyang.databinding.ActivityPersonDetailsBinding
import com.example.pcmcdiwyang.databinding.PdfLayoutBinding
import com.example.pcmcdiwyang.support.PDFConverter

class PersonDetailsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPersonDetailsBinding
    //private lateinit var binding : PdfLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonDetailsBinding.inflate(layoutInflater)
        //binding = PdfLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val applicantData = intent.extras?.getSerializable("data") as ApplicantData?
        Log.e("Gaurav", "${applicantData?.address}")
        binding.buttonGenerateCert.setOnClickListener {
            val pdfConverter = PDFConverter()
            pdfConverter.createPdf(this, this, applicantData)
        }
    }
}