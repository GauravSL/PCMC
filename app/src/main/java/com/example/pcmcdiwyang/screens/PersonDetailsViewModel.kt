package com.example.pcmcdiwyang.screens

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pcmcdiwyang.Constant
import com.example.pcmcdiwyang.data.ServerDataTransfer
import com.example.pcmcdiwyang.data.model.ApplicantData
import com.example.pcmcdiwyang.data.model.BiometricData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.reflect.Type

class PersonDetailsViewModel : ViewModel() {

    var dataTransfer = ServerDataTransfer()
    var isBioMetricDataReceived = MutableLiveData<Boolean>()
    var biometricData = MutableLiveData<BiometricData>()
    var picData = MutableLiveData<String>()

    fun getPersonBiometricDetails(applicantId:String?){
        GlobalScope.launch {
            getBiometricData(applicantId)
        }
    }

    private fun getBiometricData(applicantId : String?){
        val response = dataTransfer.accessAPI(String.format(Constant.GET_BIOMETRIC, applicantId), Constant.GET,null)
        if(response.statusCode == 200){
            val biometric_Data = Gson().fromJson(response.response, BiometricData::class.java)
            biometricData.postValue(biometric_Data)
        }else{
            //isAadharCardExists.postValue(false)
        }
    }

    fun getProfilePic(applicantId:String?){
        GlobalScope.launch {
            getProfilePicFromServer(applicantId)
        }
    }

    private fun getProfilePicFromServer(applicantId : String?){
        val response = dataTransfer.accessAPI(String.format(Constant.GET_PROOF+"/PH", applicantId), Constant.GET,null)
        if(response.statusCode == 200){
            val json = JSONObject(response.response)
            val pic = json.getString("photo")
            picData.postValue(pic)
        }else{
            //isAadharCardExists.postValue(false)
        }
    }
}