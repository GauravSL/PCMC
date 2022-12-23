package com.example.example

import com.google.gson.annotations.SerializedName


data class PcmcData (

  @SerializedName("code"    ) var code    : String?            = null,
  @SerializedName("message" ) var message : String?            = null,
  @SerializedName("allData" ) var allData : ArrayList<AllData> = arrayListOf()

)