package com.example.mymeme.repository

import android.util.Log
import com.example.mymeme.api.ImgFlipApi
import com.example.mymeme.api.MemeGenApi
import com.example.mymeme.data.entities.ImgFlipResponse
import com.example.mymeme.data.entities.ImgFlipTemplateResponse
import com.example.mymeme.data.entities.ItemInfo
import com.example.mymeme.data.entities.MemeGenTemplateResponse
import com.example.mymeme.other.Resource
import com.example.mymeme.other.safeCall
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

const val TAG = "MainRepository"

class MainRepository @Inject constructor(
    private val memeGenApi: MemeGenApi,
    private val imgFlipApi: ImgFlipApi
) {

    private val fireStore = FirebaseFirestore.getInstance()
    private val hotTopicsCollection = fireStore.collection("hotTopics")
    private val instagramCollection = fireStore.collection("instagram")
    private val discordCollection = fireStore.collection("discord")
    private val telegramCollection = fireStore.collection("telegram")

    suspend fun getHotTopics() = withContext(Dispatchers.IO) {
        safeCall {
            val result = hotTopicsCollection.get().await().toObjects(ItemInfo::class.java)
            Log.d(TAG, "getHotTopics: $result")
            Resource.Success(result)
        }
    }

    suspend fun getInstagramAccounts() = withContext(Dispatchers.IO) {
        safeCall {
            val result = instagramCollection.get().await().toObjects(ItemInfo::class.java)
            Log.d(TAG, "getInstagramAccounts: $result")
            Resource.Success(result)
        }
    }

    suspend fun getTelegramChannels() = withContext(Dispatchers.IO) {
        safeCall {
            val result = telegramCollection.get().await().toObjects(ItemInfo::class.java)
            Log.d(TAG, "getTelegramChannels: $result")
            Resource.Success(result)
        }
    }

    suspend fun getDiscordServers() = withContext(Dispatchers.IO) {
        safeCall {
            val result = discordCollection.get().await().toObjects(ItemInfo::class.java)
            Log.d(TAG, "getDiscordServers: $result")
            Resource.Success(result)
        }
    }

    suspend fun getMemeGenTemplates(): Resource<MemeGenTemplateResponse> {
        return try {
            val response = memeGenApi.getMemeGenTemplates()
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Log.d(TAG, "getMemeGenTemplates: $result")
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occured")
        }
    }

    suspend fun getImgFlipTemplates(): Resource<ImgFlipTemplateResponse> {
        return try {
            val response = imgFlipApi.getImgFlipTemplates()
            val result = response.body()
            if (response.isSuccessful && result != null && result.success) {
                Log.d(TAG, "getImgFlipTemplates: $result")
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occured")
        }
    }

    suspend fun postImgFlipMemeReq(
        templateId: String,
        boxes: Map<String, String>
    ): Resource<ImgFlipResponse> {
        return try {
            val response = imgFlipApi.postImgFlipMemeReq(templateId = templateId, boxes = boxes)
            val result = response.body()
            if (response.isSuccessful && result != null && result.success) {
                Log.d(TAG, "postImgFlipMemeData: $result")
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occured")
        }
    }
}