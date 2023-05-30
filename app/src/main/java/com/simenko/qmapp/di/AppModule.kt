package com.simenko.qmapp.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.simenko.qmapp.other.Constants.BASE_URL
import com.simenko.qmapp.other.Constants.DATABASE_NAME
import com.simenko.qmapp.repository.InvestigationsRepository
import com.simenko.qmapp.repository.ManufacturingRepository
import com.simenko.qmapp.repository.ProductsRepository
import com.simenko.qmapp.retrofit.implementation.InvestigationsService
import com.simenko.qmapp.retrofit.implementation.ManufacturingService
import com.simenko.qmapp.retrofit.implementation.ProductsService
import com.simenko.qmapp.room.implementation.InvestigationsDao
import com.simenko.qmapp.room.implementation.ManufacturingDao
import com.simenko.qmapp.room.implementation.ProductsDao
import com.simenko.qmapp.room.implementation.QualityManagementDB
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
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
    fun provideManufacturingDao(database: QualityManagementDB) = database.manufacturingDao

    @Singleton
    @Provides
    fun provideProductsDao(database: QualityManagementDB) = database.productsDao

    @Singleton
    @Provides
    fun provideInvestigationsDao(database: QualityManagementDB) = database.investigationsDao

    @Singleton
    @Provides
    fun provideMoshiInstance(): Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()


    @Singleton
    @Provides
    fun provideRetrofitInstance(moshi: Moshi): Retrofit = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(
            MoshiConverterFactory.create(
                moshi
            )
        )
        .client(
            OkHttpClient.Builder()
                .readTimeout(360,TimeUnit.SECONDS)
                .connectTimeout(360,TimeUnit.SECONDS)
                .build()
        )
        .build()

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
    fun provideManufacturingRepository(dao: ManufacturingDao, service: ManufacturingService) =
        ManufacturingRepository(dao, service)

    @Singleton
    @Provides
    fun provideProductsRepository(dao: ProductsDao, service: ProductsService) =
        ProductsRepository(dao, service)

    @Singleton
    @Provides
    fun provideInvestigationsRepository(dao: InvestigationsDao, service: InvestigationsService) =
        InvestigationsRepository(dao, service)
}