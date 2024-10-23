package com.simenko.qmapp.di

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.room.Room
import androidx.work.WorkManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.simenko.qmapp.BaseApplication
import com.simenko.qmapp.BuildConfig
import com.simenko.qmapp.other.Constants.DATABASE_NAME
import com.simenko.qmapp.other.Constants.DEFAULT_REST_API_URL
import com.simenko.qmapp.data.repository.UserRepository
import com.simenko.qmapp.data.remote.entities.NetworkErrorBody
import com.simenko.qmapp.data.remote.implementation.InvestigationsService
import com.simenko.qmapp.data.remote.implementation.ManufacturingService
import com.simenko.qmapp.data.remote.implementation.ProductsService
import com.simenko.qmapp.data.remote.implementation.SystemService
import com.simenko.qmapp.data.remote.implementation.converters.PairConverterFactory
import com.simenko.qmapp.data.remote.implementation.interceptors.AuthorizationInterceptor
import com.simenko.qmapp.data.remote.implementation.interceptors.error_handler.ErrorHandlerInterceptor
import com.simenko.qmapp.data.remote.implementation.interceptors.error_handler.ErrorManager
import com.simenko.qmapp.data.remote.implementation.interceptors.error_handler.ErrorManagerImpl
import com.simenko.qmapp.data.remote.implementation.security.MyTrustManager
import com.simenko.qmapp.data.cache.db.implementation.*
import com.simenko.qmapp.data.remote.serializer.JsonSingleton
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
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
    ) = Room.databaseBuilder(context.applicationContext, QualityManagementDB::class.java, DATABASE_NAME)
//        .addMigrations(
//            MigrationUtil.MIGRATION_1_2
//        )
        .build()

    @Singleton
    @Provides
    fun provideClient(
        @Named("authorization_interceptor") authInterceptor: Interceptor,
        @Named("error_handler_interceptor") errorHandlerInterceptor: Interceptor
    ): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            okHttpClientBuilder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
        }

        val tm = MyTrustManager()
        return if (BuildConfig.IS_API_LOCAL_HOST)
            okHttpClientBuilder
                .addInterceptor(authInterceptor)
                .addInterceptor(errorHandlerInterceptor)
                .readTimeout(360, TimeUnit.SECONDS)
                .connectTimeout(360, TimeUnit.SECONDS)
                .sslSocketFactory(tm.getFactory(), tm)
                .hostnameVerifier { _, _ -> true }
                .build()
        else
            okHttpClientBuilder
                .addInterceptor(authInterceptor)
                .addInterceptor(errorHandlerInterceptor)
                .readTimeout(360, TimeUnit.SECONDS)
                .connectTimeout(360, TimeUnit.SECONDS)
                .build()
    }

    @Singleton
    @Provides
    fun provideRetrofitInstance(client: OkHttpClient): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl(DEFAULT_REST_API_URL)
            .addConverterFactory(PairConverterFactory())
            .addConverterFactory(JsonSingleton.networkJson.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
    }

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
    fun provideBaseApplicationInstance(@ApplicationContext context: Context) = context as BaseApplication

    @Singleton
    @Provides
    fun provideWorkManager(app: BaseApplication) = WorkManager.getInstance(app.applicationContext)

    @Singleton
    @Provides
    fun provideNotificationManager(app: BaseApplication) = NotificationManagerCompat.from(app.applicationContext)

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
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig {
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        return remoteConfig
    }

    @Singleton
    @Provides
    @Named("authorization_interceptor")
    fun provideAuthorizationInterceptor(userRepository: UserRepository): Interceptor {
        return AuthorizationInterceptor(userRepository)
    }

    @Singleton
    @Provides
    fun provideErrorHandlerManager(@ApplicationContext context: Context): ErrorManager {
        return ErrorManagerImpl(context as BaseApplication)
    }

    @Singleton
    @Provides
    @Named("error_handler_interceptor")
    fun provideErrorHandlerInterceptor(errorManager: ErrorManager): Interceptor {
        return ErrorHandlerInterceptor(errorManager)
    }
}