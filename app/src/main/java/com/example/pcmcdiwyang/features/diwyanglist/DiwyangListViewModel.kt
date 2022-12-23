package com.example.pcmcdiwyang.features.diwyanglist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pcmcdiwyang.Constant
import com.example.pcmcdiwyang.data.model.ApplicantData
import com.example.pcmcdiwyang.data.ServerDataTransfer
import com.example.pcmcdiwyang.data.model.BiometricData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.reflect.Type


class DiwyangListViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
    var dataTransfer = ServerDataTransfer()
    val divyangList = MutableLiveData<List<ApplicantData>>()
    val biometricData = MutableLiveData<BiometricData>()
    var temp = MutableLiveData<String>()

    fun getDivyangList(){
        GlobalScope.launch {
            getDivyangListServer()
        }
    }

    private fun getDivyangListServer() {
        val response = dataTransfer.accessAPI(Constant.GET_ALL_APPLICANT, Constant.GET,null)
        if(response.statusCode == 200){
            val type: Type = object : TypeToken<ArrayList<ApplicantData?>?>() {}.type
            val list = Gson().fromJson<List<ApplicantData>>(response.response, type)
            divyangList.postValue(list)
        }else{
            //isAadharCardExists.postValue(false)
        }
    }

    fun getDivyangTemp(applicantId : String){
        GlobalScope.launch {
            getBiometricData(applicantId)
        }
    }

    private fun getBiometricData(applicantId : String){
        val response = dataTransfer.accessAPI(String.format(Constant.GET_BIOMETRIC, applicantId), Constant.GET,null)
        if(response.statusCode == 200){
            val biometric_Data = Gson().fromJson(response.response, BiometricData::class.java)
            biometricData.postValue(biometric_Data)
        }else{
            //isAadharCardExists.postValue(false)
        }
    }
}