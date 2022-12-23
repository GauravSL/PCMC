package com.example.pcmcdiwyang

object Constant {

    val GET = "GET"
    val POST = "POST"
   // val BASE_URL = "https://187d-117-214-123-0.in.ngrok.io/"
    val BASE_URL = "https://d1e0-1-187-48-35.in.ngrok.io/"
    //val BASE_URL = "http://192.168.29.98:3000/"
    val CHECK_AADHAR_URL = BASE_URL + "applicant/"
    val ADD_APPLICANT_DATA = BASE_URL + "applicant"
    val GET_ALL_APPLICANT = BASE_URL + "applicant/all"
    val SAVE_BIOMETRIC = BASE_URL + "applicant/%s/biometric" //%s is an applicant id
    val GET_BIOMETRIC = BASE_URL + "applicant/%s/biometric"  //%s is an applicant id
    val SAVE_PROOF = BASE_URL + "applicant/%s/document" //%s is an applicant id
    val GET_PROOF = BASE_URL + "applicant/%s/document"  //%s is an applicant id
    val GET_DATA_FROM_PCMC = "http://103.224.247.133:8085/BSUPN/rest/service/getbyAadharNumber?aadharnumber="
}