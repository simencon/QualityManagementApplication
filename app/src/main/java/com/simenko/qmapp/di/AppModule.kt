package com.simenko.qmapp.di

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.room.Room
import androidx.work.WorkManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.other.Constants.DATABASE_NAME
import com.simenko.qmapp.other.Constants.DEFAULT_REST_API_URL
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.retrofit.entities.NetworkErrorBody
import com.simenko.qmapp.retrofit.implementation.InvestigationsService
import com.simenko.qmapp.retrofit.implementation.ManufacturingService
import com.simenko.qmapp.retrofit.implementation.ProductsService
import com.simenko.qmapp.retrofit.implementation.SystemService
import com.simenko.qmapp.retrofit.implementation.converters.PairConverterFactory
import com.simenko.qmapp.room.implementation.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideQualityDB(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context.applicationContext,
        QualityManagementDB::class.java,
        DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideProductsDao(database: QualityManagementDB) = database.productsDao

    @Singleton
    @Provides
    fun provideMoshiInstance(): Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    @Singleton
    @Provides
    fun provideClient(@Named("firebase_token") token: String): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val newRequest: Request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(newRequest)
            }
            .readTimeout(360, TimeUnit.SECONDS)
            .connectTimeout(360, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofitInstance(moshi: Moshi, client: OkHttpClient, @Named("rest_api_url") url: String): Retrofit = Retrofit
            .Builder()
            .baseUrl(if (url != EmptyString.str) "$url/" else DEFAULT_REST_API_URL)
            .addConverterFactory(
                MoshiConverterFactory.create(
                    moshi
                )
            )
            .addConverterFactory(PairConverterFactory())
            .client(client)
            .build()

    @Singleton
    @Provides
    fun provideSystemService(retrofit: Retrofit): SystemService =
        retrofit.create(SystemService::class.java)

    @Singleton
    @Provides
    fun provideManufacturingService(retrofit: Retrofit): ManufacturingService =
        retrofit.create(ManufacturingService::class.java)

    @Singleton
    @Provides
    fun provideProductsService(retrofit: Retrofit): ProductsService =
        retrofit.create(ProductsService::class.java)

    @Singleton
    @Provides
    fun provideInvestigationsService(retrofit: Retrofit): InvestigationsService =
        retrofit.create(InvestigationsService::class.java)

    @Singleton
    @Provides
    fun provideErrorConverter(retrofit: Retrofit): Converter<ResponseBody, NetworkErrorBody> =
        retrofit.responseBodyConverter(NetworkErrorBody::class.java, arrayOf())

    @Singleton
    @Provides
    fun provideWorkManager(@ApplicationContext context: Context) =
        WorkManager.getInstance(context.applicationContext)

    @Singleton
    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context) =
        NotificationManagerCompat.from(context.applicationContext)

    @Singleton
    @Provides
    fun provideFirebaseAuth() = Firebase.auth

    @Singleton
    @Provides
    fun provideFirebaseFunctions() = Firebase.functions

    @Singleton
    @Provides
    fun provideFirebaseMessaging() = Firebase.messaging

    @Singleton
    @Provides
    @Named("rest_api_url")
    fun provideRestApiUrl(userRepository: UserRepository): String {
        return userRepository.getRestApiUrl
    }

    @Singleton
    @Provides
    @Named("firebase_token")
    fun provideFirebaseToken(userRepository: UserRepository): String {
        return userRepository.authToken
    }
}