package com.fournineseven.dietstock.ui.food

import android.content.ContentProvider
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.net.toUri
import com.fournineseven.dietstock.App
import com.fournineseven.dietstock.api.RetrofitService
import com.fournineseven.dietstock.model.DefaultResponse
import com.fournineseven.dietstock.model.FoodCamera.DefaultResponseKo
import com.fournineseven.dietstock.model.FoodCamera.GetFoodResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.database.Cursor
import android.provider.OpenableColumns
import android.view.View
import androidx.core.app.ActivityCompat.requestPermissions
import com.fournineseven.dietstock.SplashActivity
import com.fournineseven.dietstock.model.FoodCamera.GetFoodInfo
import com.google.android.gms.common.util.IOUtils
import org.tensorflow.lite.support.common.FileUtil
import java.io.*
import java.lang.Exception
import com.fournineseven.dietstock.ui.food.FoodFragmentCamera
import okhttp3.ResponseBody


//class Food(val uriList: ArrayList<Uri>, val context: Context){
//    private var foodNo: Int = 100
//    private var userNo: Int = 3
//    private var serving: Int = 1
//    fun saveFoodLog(foodNo: Int){
//        try{
//            val fileName = getFileName(uriList.last())
//            val contentResolver = context.getContentResolver()
//            val item = contentResolver.openFile(uriList.last(), "r", null)
//            val inputStream = FileInputStream(item?.fileDescriptor)
//            val file = File(context.cacheDir, fileName)
//            val outputStream = FileOutputStream(file)
//            inputStream.copyTo(outputStream)
//            Log.d("saveFoodLogStart", "start here line 38")
//            Log.d("UriList", uriList.toString())
//            Log.d("file = ", file.length().toString())
//            val requestBody: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
//            val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name, requestBody)
//            val gson: Gson = GsonBuilder().setLenient().create()
//            val retrofit = Retrofit.Builder()
//                    .baseUrl("http://497.iptime.org")
//                    .addConverterFactory(GsonConverterFactory.create(gson))
//                    .build()
//            val connection = retrofit.create(FoodCameraInterface::class.java)
//            connection.saveFoodLog(userNo,foodNo,serving,body).enqueue(object: Callback<DefaultResponseKo> {
//                override fun onFailure(call: Call<DefaultResponseKo>, t: Throwable){
//                    Log.d("result1 - saveFoodLog", t.message.toString())
//                }
//
//                override fun onResponse(call: Call<DefaultResponseKo>, response: Response<DefaultResponseKo>) {
//                    if(response?.isSuccessful){
//                        Log.d("result2 - saveFoodLog", response?.body().toString())
//                    }
//                    else{
//                        Log.d("response error", response?.message())
//                        Log.d("response error", response?.code().toString())
//                        Log.d("response error", response?.errorBody().toString())
//                    }
//                }
//
//            })
//        }catch (e: Exception){
//            Log.d("Error!!!", e.stackTraceToString())
//        }
//
//    }
//
//    fun getFoodInfo(foodName: String): GetFoodResponse?{
//        var result: GetFoodResponse? = null
//        val retrofit = Retrofit.Builder()
//                .baseUrl("http://497.iptime.org")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//        val connection = retrofit.create(FoodCameraInterface::class.java)
//        connection.getFoodInfo(foodName).enqueue(object: Callback<GetFoodResponse>{
//            override fun onFailure(call: Call<GetFoodResponse>, t: Throwable) {
//                Log.d("result1 - getFoodInfo", t.message.toString())
//                result = null
//            }
//
//            override fun onResponse(call: Call<GetFoodResponse>, response: Response<GetFoodResponse>) {
//                if(response?.isSuccessful){
//                    Log.d("result_test", response?.toString())
//                    Log.d("result2 - getFoodInfo", response?.body().toString())
//                    result = response?.body()
//                }else{
//                    Log.d("error line-114", response?.body().toString())
//                    result = null
//                }
//            }
//        })
//        return result
//    }
//
//
//
//    fun getFileName(uri: Uri): String?{
//        Log.d("getFileName", "getFileName start")
//        val contentResolver = context.getContentResolver()
//        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
//        try{
//            if(cursor == null)
//                return null
//            cursor.moveToFirst()
//            val fileName: String = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
//            Log.d("fileName = ", fileName)
//            cursor.close()
//            return fileName
//        }catch (e: Exception){
//            Log.d("error", e.stackTraceToString())
//            cursor?.close()
//            return null
//        }
//        Log.d("getFileName", "getFileName end")
//    }
//
//}
//
//interface FoodCameraInterface{
//    @Multipart
//    @POST("api/food/saveFoodLog")
//    fun saveFoodLog(
//        @Part("user_no") userNo: Int,
//        @Part("food_no") foodNo : Int,
//        @Part("serving") serving : Int,
//        @Part food_image: MultipartBody.Part
//    ): Call<DefaultResponseKo>
//
//
//    @FormUrlEncoded
//    @POST("api/food/getFoodInfo")
//    fun getFoodInfo(
//        @Field("food_name") foodName: String
//    ): Call<GetFoodResponse>
//}
