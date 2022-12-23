package com.example.pcmcdiwyang.data.model

import com.google.gson.annotations.SerializedName

data class BiometricData(
    @SerializedName("bioMetricId"  ) var bioMetricId  : Int?     = null,
    @SerializedName("face"    ) var face    : String?  = null,
    @SerializedName("fingerprint"   ) var fingerprint   : String?  = null,
    @SerializedName("iris"     ) var iris     : String?  = null,
)