package com.simenko.qmapp.data.cache.prefs.model

import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FcmToken(
    @SerialName("fcmTimeStamp") var fcmTimeStamp: Long = NoRecord.num,
    @SerialName("fcmEmail") var fcmEmail: String = EmptyString.str,
    @SerialName("fcmToken") var fcmToken: String = EmptyString.str,
) {
    @OptIn(ExperimentalSerializationApi::class)
    fun copyWithFireStoreData(result: Map<String, Any>) = this.copy(
        fcmEmail = (result[serializer().descriptor.getElementName(1)] ?: EmptyString.str) as String,
        fcmToken = (result[serializer().descriptor.getElementName(2)] ?: EmptyString.str) as String,
    )

    @OptIn(ExperimentalSerializationApi::class)
    fun dataToFirebase() = hashMapOf(
        serializer().descriptor.getElementName(0) to fcmTimeStamp,
        serializer().descriptor.getElementName(1) to fcmEmail,
        serializer().descriptor.getElementName(2) to fcmToken
    )
}