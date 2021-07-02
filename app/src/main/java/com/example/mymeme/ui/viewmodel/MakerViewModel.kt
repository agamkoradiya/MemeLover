package com.example.mymeme.ui.viewmodel

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.mymeme.data.entities.ImgFlipResponse
import com.example.mymeme.other.Constants.memeGenBaseUrl
import com.example.mymeme.other.Resource
import com.example.mymeme.other.snack
import com.example.mymeme.repository.MainRepository
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

private const val TAG = "MakerViewModel"

@HiltViewModel
class MakerViewModel @Inject constructor(
    private val repository: MainRepository,
) : ViewModel() {

    fun getFormattedUrlForMemeGen(id: String, vararg text: String): String {

        val result = StringBuilder()
        result.append(memeGenBaseUrl + "images/$id")
        for (t in text) {
            result.append("/${usingRegex(t)}")
        }
        result.append(".png")
        return result.toString()
    }

    private fun usingRegex(text: String): String {
        var inputtedText = text
        var pattern = Regex("_")
        inputtedText = pattern.replace(inputtedText, "__")
        pattern = Regex(" ")
        inputtedText = pattern.replace(inputtedText, "_")
        pattern = Regex("-")
        inputtedText = pattern.replace(inputtedText, "--")
        pattern = Regex("\\?")
        inputtedText = pattern.replace(inputtedText, "~q")
        pattern = Regex("&")
        inputtedText = pattern.replace(inputtedText, "~a")
        pattern = Regex("%")
        inputtedText = pattern.replace(inputtedText, "~p")
        pattern = Regex("#")
        inputtedText = pattern.replace(inputtedText, "~h")
        pattern = Regex("/")
        inputtedText = pattern.replace(inputtedText, "~s")
        pattern = Regex("\\\\")
        inputtedText = pattern.replace(inputtedText, "~b")
        Log.d(TAG, "usingRegex: $inputtedText")
        return inputtedText
    }

    fun downloadCustomMemeForMemeGen(url: String, height: Int, width: Int): String {
        val memeGenUrl = "$url?height=$height&width=$width"
        Log.d(TAG, "downloadCustomMemeForMemeGen: $memeGenUrl")
        return memeGenUrl
    }

    fun downloadCustomMemeForImgFlip(url: String, height: Int, width: Int): String {
        val imgFlipUrl =
            "https://api.memegen.link/images/custom/_/_.png?background=$url&height=$height&width=$width"
        Log.d(TAG, "downloadCustomMemeForMemeGen: $imgFlipUrl")
        return imgFlipUrl
    }

    private val _getPostImgFlipMemeResult = MutableLiveData<Resource<ImgFlipResponse>>()
    val getPostImgFlipMemeResult: LiveData<Resource<ImgFlipResponse>> = _getPostImgFlipMemeResult

    private fun loadPostImgFlipMemeReq(
        templateId: String,
        boxes: Map<String, String>
    ) {
        _getPostImgFlipMemeResult.postValue(Resource.Loading())
        viewModelScope.launch {
            val result = repository.postImgFlipMemeReq(templateId, boxes)
            _getPostImgFlipMemeResult.postValue(result)
        }
    }

    fun imgFlipMemeReq(id: String, vararg texts: String) {
        val boxes = HashMap<String, String>()
        for ((boxesLength, text) in texts.withIndex()) {
            boxes["boxes[$boxesLength][text]"] = text
        }
        loadPostImgFlipMemeReq(id, boxes)
    }

    fun getBitmapFromUrl(url: String, context: Context, view: View) {
        viewModelScope.launch {
            Glide.with(context)
                .asBitmap().load(url)
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        view.snack("Something went wrong! Try again")
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        resource?.let {
                            saveMediaToStorage(it, context, view)
                        }
                        return false
                    }
                }).submit()
        }
    }

    private fun saveMediaToStorage(bitmap: Bitmap, context: Context, view: View) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES
                    )
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            view.snack("Meme Saved Into Pictures Folder", Snackbar.LENGTH_SHORT)
        }
    }

}