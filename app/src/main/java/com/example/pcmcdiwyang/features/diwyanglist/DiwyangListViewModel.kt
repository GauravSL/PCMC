package com.example.pcmcdiwyang.features.diwyanglist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pcmcdiwyang.Constant
import com.example.pcmcdiwyang.data.ApplicantData
import com.example.pcmcdiwyang.data.ServerDataTransfer
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

    fun getDivyangList(){
        GlobalScope.launch {
            getDivyangListServer()
        }
    }

    private fun getDivyangListServer() {
        val response = dataTransfer.accessAPI(Constant.GET_ALL_APPLICANT, Constant.GET,null)
        if(response.statusCode == 200){
            val type: Type = object : TypeToken<ArrayList<ApplicantData?>?>() {}.type
            val doctorAppointmentsList = Gson().fromJson<List<ApplicantData>>(response.response, type)
            //divyangList.value = doctorAppointmentsList as List<ApplicantData>
            divyangList.postValue(doctorAppointmentsList)
            //isAadharCardExists.postValue(true)
        }else{
            //isAadharCardExists.postValue(false)
        }
    }
}