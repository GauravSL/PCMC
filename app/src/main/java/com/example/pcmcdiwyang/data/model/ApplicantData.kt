package com.example.pcmcdiwyang.data.model

import android.graphics.Bitmap
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
    @SerializedName("isActive"     ) var isActive     : Boolean? = null,
    @SerializedName("UDID"         ) var UDID         : String? = null,
    @SerializedName("pName"         ) var pName         : String? = null,
    @SerializedName("pDOB"         ) var pDOB         : String? = null,
    @SerializedName("pAddress"         ) var pAddress         : String? = null,
    @SerializedName("pAge"         ) var pAge         : String? = null,
    @SerializedName("pAddhar"         ) var pAddhar         : String? = null,
    @SerializedName("pMobile"         ) var pMobile         : String? = null,
    @SerializedName("ward"         ) var ward         : String? = null
) : java.io.Serializable