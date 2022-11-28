package com.example.pcmcdiwyang.features.registerDiwyang

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pcmcdiwyang.Constant
import com.example.pcmcdiwyang.data.Response
import com.example.pcmcdiwyang.data.ServerDataTransfer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class RegisterDiwyangViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text
    var dataTransfer = ServerDataTransfer()
    var isAadharCardExists = MutableLiveData<Boolean>()
    var isApplicantDataSubmitted = MutableLiveData<Boolean>()


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
                     long : String){
        GlobalScope.launch {
            registerUserOnServer(firstName, middleName, lastName, address, DOB, handicapType, mobile, lat, long)
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
                                     long : String) {
        val jsonObject = JSONObject()
        jsonObject.put("firstName",firstName)
        jsonObject.put("middleName",middleName)
        jsonObject.put("lastName",lastName)
        jsonObject.put("address",address)
        jsonObject.put("DOB",DOB)
        jsonObject.put("handicapType",handicapType)
        jsonObject.put("mobile",mobile)
        jsonObject.put("lat",lat)
        jsonObject.put("long",long)
        jsonObject.put("city","")
        jsonObject.put("zipcode","")
        jsonObject.put("email","")

        val response = dataTransfer.accessAPI(Constant.ADD_APPLICANT_DATA, Constant.POST, jsonObject.toString())
        if (response.statusCode == 200){
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
}