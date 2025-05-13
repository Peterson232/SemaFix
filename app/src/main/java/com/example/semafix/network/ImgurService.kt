package com.example.semafix.network


import com.example.semafix.models.ImgurResponse
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.util.concurrent.TimeUnit

interface ImgurApiService {
    @Multipart
    @POST("image")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<ImgurResponse>

    companion object {
        fun create(): ImgurApiService {
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)  // Increased from default 10s
                .readTimeout(60, TimeUnit.SECONDS)     // Increased from default 10s
                .writeTimeout(60, TimeUnit.SECONDS)    // Increased from default 10s
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Client-ID c9bb848c7b38dc3")
                        .build()
                    chain.proceed(request)
                }
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()

            return Retrofit.Builder()
                .baseUrl("https://api.imgur.com/3/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ImgurApiService::class.java)
        }
    }
}