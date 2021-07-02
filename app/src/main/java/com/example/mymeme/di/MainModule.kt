package com.example.mymeme.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.mymeme.R
import com.example.mymeme.api.ImgFlipApi
import com.example.mymeme.api.MemeGenApi
import com.example.mymeme.other.Constants.imgFlipBaseUrl
import com.example.mymeme.other.Constants.memeGenBaseUrl
import com.example.mymeme.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Singleton
    @Provides
    fun provideMainRepository(memeGenApi: MemeGenApi, ingFlipApi: ImgFlipApi) =
        MainRepository(memeGenApi, ingFlipApi)

    @Singleton
    @Provides
    fun provideMemeGenApi(): MemeGenApi = Retrofit.Builder()
        .baseUrl(memeGenBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(MemeGenApi::class.java)

    @Singleton
    @Provides
    fun provideImgFlipApi(): ImgFlipApi = Retrofit.Builder()
        .baseUrl(imgFlipBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ImgFlipApi::class.java)

    @Singleton
    @Provides
    fun provideApplicationContext(
        @ApplicationContext context: Context
    ) = context

    @Singleton
    @Provides
    fun provideGlideInstance(
        @ApplicationContext context: Context
    ) = Glide.with(context).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_error)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    )
}