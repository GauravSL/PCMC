package com.example.pcmcdiwyang.features.registerDiwyang

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.example.PcmcData
import com.example.pcmcdiwyang.Constant
import com.example.pcmcdiwyang.data.ServerDataTransfer
import com.example.pcmcdiwyang.data.model.ApplicantData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.reflect.Type

class RegisterDiwyangViewModel : ViewModel() {

    var dataTransfer = ServerDataTransfer()
    var isAadharCardExists = MutableLiveData<Boolean>()
    var isApplicantDataSubmitted = MutableLiveData<Boolean>()
    var isFaceSubmitted = MutableLiveData<Boolean>()
    var isFingerPrintSubmitted = MutableLiveData<Boolean>()
    var isIrisSubmitted = MutableLiveData<Boolean>()
    var isFinalSubmitted = MutableLiveData<Boolean>()
    var isAadhrFrontSubmitted = MutableLiveData<Boolean>()
    var isAadhrBackSubmitted = MutableLiveData<Boolean>()
    var isUDIDSubmitted = MutableLiveData<Boolean>()
    var isProfilePicSubmitted = MutableLiveData<Boolean>()
    var pcmcData = MutableLiveData<PcmcData?>()
    var errorMessage = MutableLiveData<String?>()
    var applicantId :String = ""
    var isPresentWithPCMC :Boolean = false
    var pcmcDataLocal : PcmcData? = null

    fun checkAadharCardonPCMC(aadharNumber : String){
        GlobalScope.launch {
            checkAadharCardExistPCMC(aadharNumber)
        }
    }

    private fun checkAadharCardExistPCMC(aadharNumber : String){
        val response = dataTransfer.accessAPI(Constant.GET_DATA_FROM_PCMC+aadharNumber, Constant.GET,null)
        if(response.statusCode == 200){
            val type: Type = object : TypeToken<PcmcData?>() {}.type
            pcmcDataLocal = Gson().fromJson<PcmcData>(response.response, type)
            val tempPcmcDataLocal = Gson().fromJson<PcmcData>(response.response, type)
            isPresentWithPCMC = tempPcmcDataLocal.code.equals("200")
            if (isPresentWithPCMC)
            checkAadharCard(aadharNumber)
            else
                errorMessage.postValue("Registration Not Done In Divyang Nondani Portal PCMC - Pune")
        }else{
           // isPresentWithPCMC = false
          //
        }
    }


    fun checkAadharCard(aadharNumber : String){
        GlobalScope.launch {
            checkAadharCardExist(aadharNumber)
        }
    }

    private fun checkAadharCardExist(aadharNumber : String){
        val response = dataTransfer.accessAPI(Constant.CHECK_AADHAR_URL+aadharNumber, Constant.GET,null)
        if(response.statusCode == 200){
            isAadharCardExists.postValue(true)
        }else{
            isAadharCardExists.postValue(false)
            if (isPresentWithPCMC)
                pcmcData.postValue(pcmcDataLocal)
        }
    }

    fun registerUser(firstName : String,
                     middleName : String,
                     lastName : String,
                     address : String,
                     DOB : String,
                     handicapType : String,
                     mobile : String,
                     lat : String,
                     long : String,
                     addhar : String,
                     pName : String,
                     pAddress : String,
                     pAddhar : String,
                     pMobile : String,
                     UDID : String,
    zone :String,
    ward: String){
        GlobalScope.launch {
            registerUserOnServer(firstName, middleName, lastName, address, DOB, handicapType, mobile, lat, long,addhar, pName, pAddress, pAddhar, pMobile, UDID, zone, ward)
        }
    }

    private fun registerUserOnServer(firstName : String,
                                     middleName : String,
                                     lastName : String,
                                     address : String,
                                     DOB : String,
                                     handicapType : String,
                                     mobile : String,
                                     lat : String,
                                     long : String,
                                     addhar : String,
                                     pName : String,
                                     pAddress : String,
                                     pAddhar : String,
                                     pMobile : String,
                                     UDID : String,
                                     zone :String,
                                     ward: String
    ) {
        val jsonObject = JSONObject()
        jsonObject.put("firstName",firstName)
        jsonObject.put("middleName",middleName)
        jsonObject.put("lastName",lastName)
        jsonObject.put("address",address)
        jsonObject.put("DOB",DOB)
        jsonObject.put("handicapType",handicapType.replace(" ","_"))
        jsonObject.put("mobile",mobile)
        jsonObject.put("lat",lat)
        jsonObject.put("long",long)
        jsonObject.put("city",zone)
        jsonObject.put("zipcode",ward)
        jsonObject.put("email","")

        jsonObject.put("addhar",addhar)
        jsonObject.put("pName",pName)
        jsonObject.put("pDOB","")
        jsonObject.put("pAddress",pAddress)
        jsonObject.put("pAge",0)
        jsonObject.put("pAddhar",pAddhar)
        jsonObject.put("pMobile",pMobile)
        jsonObject.put("UDID",UDID)
        jsonObject.put("wardWardId",1)

        val response = dataTransfer.accessAPI(Constant.ADD_APPLICANT_DATA, Constant.POST, jsonObject.toString())
        if (response.statusCode == 201){
            val json = JSONObject(response.response)
            applicantId = json.getString("applicantId")
            isApplicantDataSubmitted.postValue(true)
        }else{
            isApplicantDataSubmitted.postValue(false)
        }

      /*  {
            "firstName": "Prasad",
            "middleName": "Suryakant",
            "lastName": "More",
            "address": "Baner",
            "DOB": "16/12/1991",
            "city": "Pune",
            "zipcode": "411045",
            "addhar": "121212121212",
            "handicapType": "BLINDNESS",
            "email": "test@test.com",
            "mobile": "9988776655",
            "lat": "12.5553535",
            "long": "12.5553535"
        }*/
    }

    fun submitFaceReg(faceData: String, applicantApplicantId: String, convert: String){
        GlobalScope.launch {
            submitFaceRegToServer(faceData, applicantApplicantId, convert)
        }
    }

    private fun submitFaceRegToServer(faceData: String, applicantId :String, convert: String) {
        /*val jsonface = JSONObject(faceData)
        jsonface.put("image", convert);*/
        val jsonObject = JSONObject()
        jsonObject.put("face",faceData)

        val response = dataTransfer.accessAPI(String.format(Constant.SAVE_BIOMETRIC,applicantId), Constant.POST, jsonObject.toString())
        if (response.statusCode == 201){
            isFaceSubmitted.postValue(true)
        }else{
            isFaceSubmitted.postValue(false)
        }
    }

    fun submitFingerPrint(fpData : String, applicantApplicantId :String){
        GlobalScope.launch {
            submitFingerPrintToServer(fpData, applicantApplicantId)
        }
    }

    private fun submitFingerPrintToServer(fpData: String, applicantId :String) {
        val jsonObject = JSONObject()
        jsonObject.put("fingerprint",fpData)

        val response = dataTransfer.accessAPI(String.format(Constant.SAVE_BIOMETRIC,applicantId), Constant.POST, jsonObject.toString())
        if (response.statusCode == 201){
            isFingerPrintSubmitted.postValue(true)
        }else{
            isFaceSubmitted.postValue(false)
        }
    }

    fun submitIris(irisData : String, applicantApplicantId :String){
        GlobalScope.launch {
            submitIrisToServer(irisData, applicantApplicantId)
        }
    }

    private fun submitIrisToServer(irisData: String, applicantId :String) {
        val jsonObject = JSONObject()
        jsonObject.put("iris",irisData)

        val response = dataTransfer.accessAPI(String.format(Constant.SAVE_BIOMETRIC,applicantId), Constant.POST, jsonObject.toString())
        if (response.statusCode == 201){
            isIrisSubmitted.postValue(true)
        }else{
            isIrisSubmitted.postValue(false)
        }
    }

    fun finalSubmit(){
        isFinalSubmitted.postValue(true)
    }
    
    fun submitProofs(key:String , value : String?){
        GlobalScope.launch {
            submitProofsToServer(key, value)
        }
    }

    private fun submitProofsToServer(key: String, value: String?) {
        val jsonObject = JSONObject()
        jsonObject.put(key,value)

        val response = dataTransfer.accessAPI(String.format(Constant.SAVE_PROOF,applicantId), Constant.POST, jsonObject.toString())
        if (response.statusCode == 201){
            when(key){
                "aadharFront"-> isAadhrFrontSubmitted.postValue(true)
                "aadharBack"-> isAadhrBackSubmitted.postValue(true)
                "uuid"-> isUDIDSubmitted.postValue(true)
                "photo"-> isProfilePicSubmitted.postValue(true)
            }
        }else{
            when(key){
                "aadharFront"-> isAadhrFrontSubmitted.postValue(false)
                "aadharBack"-> isAadhrBackSubmitted.postValue(false)
                "uuid"-> isUDIDSubmitted.postValue(false)
                "photo"-> isProfilePicSubmitted.postValue(false)
            }
        }
    }

}