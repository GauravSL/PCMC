package com.example.pcmcdiwyang.screens

import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.pcmcdiwyang.R
import com.example.pcmcdiwyang.Temp
import com.example.pcmcdiwyang.Utils
import com.example.pcmcdiwyang.data.model.ApplicantData
import com.example.pcmcdiwyang.databinding.ActivityPersonDetailsBinding
import com.example.pcmcdiwyang.scanners.ActivityScanFingerprint
import com.example.pcmcdiwyang.scanners.ActivityScanIris
import com.example.pcmcdiwyang.scanners.face.FaceRecognitionActivity
import com.example.pcmcdiwyang.support.PDFConverter


class PersonDetailsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPersonDetailsBinding
    //private lateinit var binding : PdfLayoutBinding
    private val FACE_RECOGNATION = 1
    private val FINGER_RECOGNATION = 2
    private val IRIS_RECOGNATION = 3
    private lateinit var personDetailsViewModel : PersonDetailsViewModel
    private var applicantData :ApplicantData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonDetailsBinding.inflate(layoutInflater)
        //binding = PdfLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        personDetailsViewModel =
            ViewModelProvider(this).get(PersonDetailsViewModel::class.java)
        applicantData = intent.extras?.getSerializable("data") as ApplicantData?
        applicantData?.let { setApplicantData(it) }
        if (Utils.isInternetAvailable(this)){
            personDetailsViewModel.getPersonBiometricDetails(applicantData?.applicantId.toString())
           // personDetailsViewModel.getPersonBiometricDetails("16")
            personDetailsViewModel.getProfilePic(applicantData?.applicantId.toString())

        }
       /* binding.buttonGenerateCert.setOnClickListener {
            val pdfConverter = PDFConverter()
            pdfConverter.createPdf(this, this, applicantData)
        }*/


        personDetailsViewModel.biometricData.observe(this, Observer {
            it.face?.let { it1 -> Log.e("Gaurav", it1) }
            val faceVal = it.face
            if (!faceVal.equals("face", true )) {
                Temp.face = Temp.getRecognationData(it.face)
                Temp.fingerPrint = it.fingerprint
                Temp.iris = it.iris
            }
            personDetailsViewModel.getProfilePic(applicantData?.applicantId.toString())
        })

        personDetailsViewModel.picData.observe(this, Observer {
            val byteArray = Temp.StringToByteArray(it)
            val decodedByte = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            binding.profilePic.setImageBitmap(decodedByte)
        })

        binding.buttonGenerateCert.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.layout_verify_dialog)
            dialog.setTitle("Authenticate ")

            val buttonFaceR: AppCompatButton = dialog.findViewById(R.id.buttonFaceR) as AppCompatButton
            buttonFaceR.setOnClickListener {
                val intent = Intent(this, FaceRecognitionActivity::class.java)
                val bundle = Bundle()
                bundle.putBoolean("isVerification", true);
                intent.putExtras(bundle)
                startActivityForResult(intent, FACE_RECOGNATION)
                dialog.dismiss()
            }

            val buttonFinerPrint: AppCompatButton = dialog.findViewById(R.id.buttonFinerPrint) as AppCompatButton
            buttonFinerPrint.setOnClickListener {
                val intent =Intent(this, ActivityScanFingerprint::class.java)
                val bundle = Bundle()
                bundle.putBoolean("isVerification", true);
                intent.putExtras(bundle)
                startActivityForResult(intent, FINGER_RECOGNATION)
                dialog.dismiss()
            }

            val buttonIris: AppCompatButton = dialog.findViewById(R.id.buttonIris) as AppCompatButton
            buttonIris.setOnClickListener {
                val intent =Intent(this, ActivityScanIris::class.java)
                val bundle = Bundle()
                bundle.putBoolean("isVerification", true);
                intent.putExtras(bundle)
                startActivityForResult(intent, IRIS_RECOGNATION)
                dialog.dismiss()
            }
            dialog.show()
        }

    }

    private fun setApplicantData(applicantData : ApplicantData) {
        binding.txtName.text = "${applicantData.firstName} ${applicantData.middleName} ${applicantData.lastName}"
        binding.txtaadharNumber.text = "${applicantData.addhar}"
        binding.txtUDID.text = "${applicantData.UDID}"
        binding.txtDateOfBirth.text = "${applicantData.DOB}"
        binding.txtDivyangType.text = "${applicantData.handicapType}"
        binding.txtMobileNumber.text = "${applicantData.mobile}"
        binding.txtPCMCZone.text = "${applicantData.city}"
        binding.txtPCMCWard.text = "${applicantData.zipcode}"
        binding.txtAddress.text = "${applicantData.address}"
        binding.txtParentName.text = "${applicantData.pName}"
        binding.txtParentAadharNumber.text = "${applicantData.pAddhar}"
        binding.txtParentMobileNumber.text = "${applicantData.pMobile}"
        binding.txtParentAddress.text = "${applicantData.pAddress}"
    }

    override fun onDestroy() {
        super.onDestroy()
        Temp.face = null
        Temp.fingerPrint = ""
        Temp.iris = ""
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var isSuccess = false
        if (resultCode != 0) {
            isSuccess = data!!.extras!!["result"] as Boolean
        }
        if (requestCode == FACE_RECOGNATION) {
            if (Temp.faceRecognitionSuccess) {
                Temp.faceRecognitionSuccess =false
                printPDF()
            } else {
                Toast.makeText(this, "Authentication Failed", Toast.LENGTH_LONG).show()
            }
        } else if (requestCode == FINGER_RECOGNATION && resultCode != 0) {
            if (isSuccess) {
                printPDF()
            } else {
                Toast.makeText(this, "Authentication Failed", Toast.LENGTH_LONG).show()
            }
        } else if (requestCode == IRIS_RECOGNATION && resultCode != 0) {
            if (isSuccess) {
                printPDF()
            } else {
                Toast.makeText(this, "Authentication Failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun printPDF(){
        val pdfConverter = PDFConverter()
        pdfConverter.createPdf(this, this, applicantData)
    }

}