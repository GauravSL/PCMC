package com.example.example

import com.google.gson.annotations.SerializedName


data class AllData (

  @SerializedName("avakNo"    ) var avakNo    : String? = null,
  @SerializedName("adharNo"   ) var adharNo   : String? = null,
  @SerializedName("name"      ) var name      : String? = null,
  @SerializedName("mobileNo"  ) var mobileNo  : String? = null,
  @SerializedName("uniqueKey" ) var uniqueKey : String? = null

)