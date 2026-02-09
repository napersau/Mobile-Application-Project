package com.example.fe.network

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.fe.service.DocumentApiService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import java.time.Instant
import com.google.gson.*
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

@RequiresApi(Build.VERSION_CODES.O)
object NetworkManager {
    private const val BASE_URL = "http://10.0.2.2:8080/" // Thay đổi URL này

    @RequiresApi(Build.VERSION_CODES.O)
    private val gson = GsonBuilder()
        .registerTypeAdapter(Instant::class.java, InstantDeserializer())
        .registerTypeAdapter(Instant::class.java, InstantSerializer())
        .create()

    @RequiresApi(Build.VERSION_CODES.O)
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val documentApiService: DocumentApiService by lazy {
        retrofit.create(DocumentApiService::class.java)
    }
}

// Custom serializer và deserializer cho Instant
class InstantDeserializer : JsonDeserializer<Instant> {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Instant {
        return Instant.parse(json.asString)
    }
}

class InstantSerializer : JsonSerializer<Instant> {
    override fun serialize(src: Instant, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.toString())
    }
}