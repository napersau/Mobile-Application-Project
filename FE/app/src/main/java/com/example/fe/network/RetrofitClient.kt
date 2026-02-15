package com.example.fe.network

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    @Volatile
    private var retrofit: Retrofit? = null

    @Volatile
    private var authRetrofit: Retrofit? = null

    private fun createGson() = GsonBuilder()
        .registerTypeAdapter(Instant::class.java, JsonDeserializer { json, _, _ ->
            Instant.parse(json.asString)
        })
        .registerTypeAdapter(Instant::class.java, JsonSerializer<Instant> { src, _, _ ->
            JsonPrimitive(src.toString())
        })
        .create()

    fun initialize(context: Context) {
        if (retrofit == null) {
            synchronized(this) {
                if (retrofit == null) {
                    val loggingInterceptor = HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }

                    val okHttpClient = OkHttpClient.Builder()
                        .addInterceptor(AuthInterceptor(context.applicationContext))
                        .addInterceptor(loggingInterceptor)
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(120, TimeUnit.SECONDS) // Increased for AI API
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .build()

                    retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create(createGson()))
                        .build()

                    // Auth retrofit without AuthInterceptor
                    val authOkHttpClient = OkHttpClient.Builder()
                        .addInterceptor(loggingInterceptor)
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .build()

                    authRetrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(authOkHttpClient)
                        .addConverterFactory(GsonConverterFactory.create(createGson()))
                        .build()
                }
            }
        }
    }

    private fun getRetrofit(): Retrofit {
        return retrofit ?: throw IllegalStateException("RetrofitClient not initialized. Call initialize() first.")
    }

    private fun getAuthRetrofit(): Retrofit {
        return authRetrofit ?: throw IllegalStateException("RetrofitClient not initialized. Call initialize() first.")
    }

    val authApi: AuthApi
        get() = getAuthRetrofit().create(AuthApi::class.java)

    val userApi: UserApi
        get() = getRetrofit().create(UserApi::class.java)

    val decksApi: DecksApi
        get() = getRetrofit().create(DecksApi::class.java)

    val flashcardsApi: FlashcardsApi
        get() = getRetrofit().create(FlashcardsApi::class.java)

    val documentApi: DocumentApi
        get() = getRetrofit().create(DocumentApi::class.java)

    val courseApi: CourseApiService
        get() = getRetrofit().create(CourseApiService::class.java)

    val examApi: ExamApi
        get() = getRetrofit().create(ExamApi::class.java)

    val examResultApi: ExamResultApi
        get() = getRetrofit().create(ExamResultApi::class.java)

    val aiApi: AIApi
        get() = getRetrofit().create(AIApi::class.java)
}
