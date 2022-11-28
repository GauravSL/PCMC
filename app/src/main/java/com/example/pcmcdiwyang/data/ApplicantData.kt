package com.example.pcmcdiwyang.data

import com.google.gson.annotations.SerializedName

data class ApplicantData(

    @SerializedName("applicantId"  ) var applicantId  : Int?     = null,
    @SerializedName("firstName"    ) var firstName    : String?  = null,
    @SerializedName("middleName"   ) var middleName   : String?  = null,
    @SerializedName("lastName"     ) var lastName     : String?  = null,
    @SerializedName("address"      ) var address      : String?  = null,
    @SerializedName("DOB"          ) var DOB          : String?  = null,
    @SerializedName("city"         ) var city         : String?  = null,
    @SerializedName("zipcode"      ) var zipcode      : String?  = null,
    @SerializedName("addhar"       ) var addhar       : String?  = null,
    @SerializedName("handicapType" ) var handicapType : String?  = null,
    @SerializedName("email"        ) var email        : String?  = null,
    @SerializedName("mobile"       ) var mobile       : String?  = null,
    @SerializedName("lat"          ) var lat          : String?  = null,
    @SerializedName("long"         ) var long         : String?  = null,
    @SerializedName("isActive"     ) var isActive     : Boolean? = null

)