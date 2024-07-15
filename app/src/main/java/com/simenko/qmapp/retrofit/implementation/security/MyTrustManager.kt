package com.simenko.qmapp.retrofit.implementation.security

import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager
import javax.security.cert.CertificateException


class MyTrustManager : X509TrustManager {
    @Throws(CertificateException::class)
    override fun checkClientTrusted(chain: Array<out java.security.cert.X509Certificate>?, authType: String?) {
    }

    @Throws(CertificateException::class)
    override fun checkServerTrusted(chain: Array<out java.security.cert.X509Certificate>?, authType: String?) {
    }

    override fun getAcceptedIssuers(): Array<out java.security.cert.X509Certificate> {
        return emptyArray()
    }

    private val sslContext = SSLContext.getInstance("SSL")

    fun getFactory(): SSLSocketFactory {
        sslContext.init(null, arrayOf(this), java.security.SecureRandom())
        return sslContext.socketFactory
    }
}