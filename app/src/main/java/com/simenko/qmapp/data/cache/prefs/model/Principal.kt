package com.simenko.qmapp.data.cache.prefs.model

import androidx.annotation.DrawableRes
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Principal(
    @SerialName("fullName") val fullName: String = EmptyString.str, //0
    @SerialName("company") val company: String = EmptyString.str, //1
    @SerialName("department") val department: String = EmptyString.str, //2
    @SerialName("subDepartment") val subDepartment: String = EmptyString.str, //3
    @SerialName("jobRole") val jobRole: String = EmptyString.str, //4
    @SerialName("email") val email: String = EmptyString.str, //5
    @SerialName("phoneNumber") val phoneNumber: String? = null, //6
    @SerialName("restApiUrl") val restApiUrl: String = EmptyString.str, //7
    @SerialName("password_suffix") val password: String = EmptyString.str, //8
    @SerialName("isEmailVerified") val isEmailVerified: Boolean = false, //9
    @SerialName("is_user_log_in") val isUserLoggedIn: Boolean = false, //10
    @SerialName("fb_token") val fbToken: String = EmptyString.str, //11
    @SerialName("epoch_fb_diff") val epochFbDiff: Long = NoRecord.num, //12
    @SerialName("fb_token_exp") val fbTokenExp: Long = NoRecord.num, //13

    @DrawableRes val logo: Int = R.drawable.ic_launcher_round //14
) {

    @ExperimentalSerializationApi
    fun copyWithFireStoreData(data: Map<String, Any>) = this.copy(
        fullName = (data[serializer().descriptor.getElementName(0)] ?: EmptyString.str) as String,
        company = (data[serializer().descriptor.getElementName(1)] ?: EmptyString.str) as String,
        department = (data[serializer().descriptor.getElementName(2)] ?: EmptyString.str) as String,
        subDepartment = (data[serializer().descriptor.getElementName(3)] ?: EmptyString.str) as String,
        jobRole = (data[serializer().descriptor.getElementName(4)] ?: EmptyString.str) as String,
        phoneNumber = data[serializer().descriptor.getElementName(6)]?.let { it as String },
        restApiUrl = (data[serializer().descriptor.getElementName(7)] ?: EmptyString.str) as String,
        isEmailVerified = (data[serializer().descriptor.getElementName(9)] ?: EmptyString.str) as Boolean,
    )

    @ExperimentalSerializationApi
    fun toFireStoreRequest() = hashMapOf(
        serializer().descriptor.getElementName(0) to fullName,
        serializer().descriptor.getElementName(1) to company,
        serializer().descriptor.getElementName(2) to department,
        serializer().descriptor.getElementName(3) to subDepartment,
        serializer().descriptor.getElementName(4) to jobRole,
        serializer().descriptor.getElementName(5) to email,
        serializer().descriptor.getElementName(6) to phoneNumber,
        serializer().descriptor.getElementName(9) to isEmailVerified,
    )
}