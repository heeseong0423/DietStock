package com.fournineseven.dietstock.ui.food

import android.Manifest
import android.app.AppOpsManager
import android.content.*
import android.content.ContentValues.TAG
import android.content.Context.CAMERA_SERVICE
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.*
import android.hardware.camera2.*
import android.hardware.camera2.params.SessionConfiguration
import android.media.Image
import android.media.ImageReader
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.os.Environment.getRootDirectory
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.util.Size
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getExternalFilesDirs
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.fournineseven.dietstock.LoginState
import com.fournineseven.dietstock.R
import com.fournineseven.dietstock.databinding.FragmentFoodCameraBinding
import com.fournineseven.dietstock.model.FoodCamera.DefaultResponseKo
import com.fournineseven.dietstock.model.FoodCamera.GetFoodResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.Tensor
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.common.ops.QuantizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.*
import java.lang.Exception
import java.lang.RuntimeException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.Collections.max
import java.util.concurrent.TimeUnit
import kotlin.collections.AbstractList


class FoodFragmentCamera : Fragment() {
    private val requestCameraPermissionCode: Int = 0
    lateinit var cameraDevice: CameraDevice
    lateinit var binding: FragmentFoodCameraBinding
    lateinit var captureRequestBuilder: CaptureRequest.Builder
    lateinit var cameraCaptureSessions: CameraCaptureSession
    private lateinit var windowManager: WindowManager
    private var uriList = ArrayList<Uri>()
    private lateinit var imageDimension: Size
    lateinit var texture: SurfaceTexture
    var fileCount = 0
    private var foodNo: Int = -1
    private var userNo: Int = -1
    private var serving: Int = 1
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_food_camera, container, false)
        binding = FragmentFoodCameraBinding.inflate(inflater, container, false)
        windowManager = requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        binding.takeBtn.setOnClickListener(ButtonListener())
        binding.textureView.surfaceTextureListener = textureListener
        binding.submitBtn.setOnClickListener(SubmitBtnListener())
        sharedPreferences = requireContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        binding.foodSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if(!uriList.isEmpty()){
                    uriList.clear()
                }
                getFoodInfo(query!!)
                return true
            }

        })
        userNo = sharedPreferences.getInt(LoginState.USER_NUMBER, -1)
        return binding.root
    }

    private var textureListener: TextureView.SurfaceTextureListener = object : TextureView.SurfaceTextureListener{
        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            closeCamera()
            return false
        }

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            openCamera()
        }
    }

    inner class ButtonListener: View.OnClickListener{
        override fun onClick(v: View?) {
            takePicture()
        }
    }

    inner class SubmitBtnListener: View.OnClickListener{
        override fun onClick(v: View?) {
            Log.d("foodNo", foodNo.toString())
            saveFoodLog(foodNo = foodNo)
        }
    }

    private fun openCamera(){
        Log.e(TAG, "openCamera(): openCamera()메서드 호출")

        val manager: CameraManager = requireActivity().getSystemService(Context.CAMERA_SERVICE) as CameraManager

        try{
            val cameraId = manager.cameraIdList[0]
            val characteristcs = manager.getCameraCharacteristics(cameraId!!)
            val map = characteristcs.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

            imageDimension = map!!.getOutputSizes<SurfaceTexture>(SurfaceTexture::class.java)[0]

            if(ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), requestCameraPermissionCode)

            }
            manager.openCamera(cameraId!!, stateCallback, null)
        }catch(e: CameraAccessException){
            Log.d("AcessException", "Here Problem")
            e.printStackTrace()
        }
    }

    private val stateCallback = object : CameraDevice.StateCallback(){
        override fun onOpened(camera: CameraDevice){
            Log.d(TAG, "stateCallback: onOpened")
            cameraDevice = camera
            val result = createCameraPreviewSession()
            if(result == false){
                closeCamera()
            }
        }
        override fun onDisconnected(camera : CameraDevice){
            Log.d(TAG, "stateCallback: onDisconnected")
            cameraDevice!!.close()
        }
        override fun onError(camera: CameraDevice, error: Int){
            Log.d(TAG, "stateCallback: OnError")
            cameraDevice!!.close()
        }
    }

    private fun createCameraPreviewSession(): Boolean{
        try{
            if(binding.textureView.isAvailable){
                texture = binding.textureView.surfaceTexture!!
            }
            else{
                return false
            }
            if(texture == null){
                return false
            }
            texture.setDefaultBufferSize(imageDimension!!.width, imageDimension!!.height)
            val surface = Surface(texture)

            captureRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(surface)

            cameraDevice!!.createCaptureSession(listOf(surface), object: CameraCaptureSession.StateCallback(){
                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Log.d(TAG, "Configuration changed")
                }

                override fun onConfigured(session: CameraCaptureSession) {
                    if(cameraDevice == null){
                        return
                    }
                    cameraCaptureSessions = session
                    captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)

                    try {
                        cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, null)
                    }catch (e: CameraAccessException){
                        e.printStackTrace()
                    }
                }
            }, null)
            return true

        }catch (e: CameraAccessException){
            closeCamera()
            Log.d("Error", e.message.toString())
            Log.d("Error", e.stackTraceToString())
        }
        return true
    }

    private fun takePicture(){
        try {
            val manager = requireActivity().getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val characteristics = manager.getCameraCharacteristics(cameraDevice!!.id)
            var jpegSizes: Array<Size>? = null
            jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!.getOutputSizes(ImageFormat.JPEG)

            var width = jpegSizes[0].width
            var height = jpegSizes[0].height

            val imageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1)

            val outputSurface = ArrayList<Surface>(2)
            outputSurface.add(imageReader!!.surface)
            outputSurface.add(Surface(binding.textureView!!.surfaceTexture))

            val captureBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureBuilder.addTarget(imageReader!!.surface)

            val rotation = windowManager.defaultDisplay.rotation
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, rotation)

            val readerListener = object : ImageReader.OnImageAvailableListener{
                override fun onImageAvailable(reader: ImageReader?) {

                    var dir = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                        File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString())
                    }else{
                        File(Environment.getExternalStorageDirectory().absolutePath + "/Pictures")
                    }

                    if(!dir.exists()) {
                        dir.mkdirs()
                    }
                    val dateTime: LocalDateTime = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ofPattern("MMddHHmmss")
                    val fileName = "/pic${dateTime.format(formatter)}.jpg"
                    val file = File(dir, fileName)
                    var image : Image? = null

                    try {
                        image = imageReader!!.acquireLatestImage()
                        val buffer = image!!.planes[0].buffer
                        val bytes = ByteArray(buffer.capacity())
                        buffer.get(bytes)

                        var output: OutputStream? = null
                        try {
                            output = FileOutputStream(file)
                            output.write(bytes)
                            output.flush()
                        }catch (e: FileNotFoundException){
                            Log.d("File Error", e.stackTraceToString())
                        } finally {
                            output?.close()
                            val bitmap: Bitmap = BitmapFactory.decodeFile(file.path)
                            Log.d("bitmap imgae size", bitmap.width.toString() + "x" + bitmap.height.toString())
                            val rotateMatrix = Matrix()
                            rotateMatrix.postRotate(90F)
                            rotateMatrix.postScale((1920f/bitmap.width), (1080f/bitmap.height))
                            val rotatedBitmap: Bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, rotateMatrix, false)
                            Log.d("rotated bitmap", rotatedBitmap.width.toString() + "x" + rotatedBitmap.height.toString())
                            try {
                                val values = ContentValues().apply {
                                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
                                    put(MediaStore.Images.Media.IS_PENDING, 1)
                                }
                                val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                                val contentResolver = requireContext().getContentResolver()
                                val item = contentResolver.insert(collection, values)!!
                                val stream = ByteArrayOutputStream()
                                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
                                val byteImgae = stream.toByteArray()
                                contentResolver.openFileDescriptor(item, "w", null).use{
                                    FileOutputStream(it!!.fileDescriptor).use{
                                            outputStream -> outputStream.write(byteImgae)
                                        outputStream.close()
                                    }
                                }
                                values.clear()
                                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                                contentResolver.update(item, values, null, null)
                                uriList.add(0, item)
                            }catch (e: FileNotFoundException){
                                Log.d("File Not Found", e.stackTraceToString())
                            }finally {
                                output?.close()
                            }
                            binding.resultImage.setImageBitmap(rotatedBitmap)
                            fileCount++
                        }
                    }catch (e: FileNotFoundException){
                        e.printStackTrace()
                    }finally {
                        image?.close()
                    }
                }
            }

            imageReader!!.setOnImageAvailableListener(readerListener, null)

            val captureListener = object : CameraCaptureSession.CaptureCallback(){
                override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
                    super.onCaptureCompleted(session, request, result)

                    Toast.makeText(this@FoodFragmentCamera.requireContext(), "사진이 촬영되었습니다", Toast.LENGTH_SHORT).show()
                    val resultName = tfLiteModel(uriList, getFileName(uriList.last()))
                    if(resultName == "fail"){
                        Log.d("classification", "fail")
                    }else{
                        Log.d("line-300 result is ", resultName)
                        getFoodInfo(resultName)

                    }
                }
            }

            cameraDevice!!.createCaptureSession(outputSurface, object: CameraCaptureSession.StateCallback(){
                override fun onConfigureFailed(session: CameraCaptureSession){}

                override fun onConfigured(session: CameraCaptureSession){
                    try{
                        session.capture(captureBuilder.build(), captureListener, null)
                    }catch (e: CameraAccessException){
                        e.printStackTrace()
                    }
                }
            }, null)
        }catch (e: CameraAccessException){
            e.printStackTrace()
        }
    }

    private fun changeSettings(data: GetFoodResponse, predictedFoodName: String){
        var foodName = binding.foodName
        var leftConstraint = binding.LeftConstraint
        var rightConstraint = binding.RightConstraint
        var carbohydrate = binding.carbohydrate
        var kcal = binding.kcal
        var protein = binding.protein
        var fat = binding.fat
        var cholesterol = binding.cholesterol
        var natrium = binding.natrium
        var btn = binding.takeBtn
        var submitBtn = binding.submitBtn
        foodName.text = predictedFoodName
        leftConstraint.visibility = View.VISIBLE
        rightConstraint.visibility = View.VISIBLE
        carbohydrate.text = "${carbohydrate.text}${ data.result[0].carbs}g"
        kcal.text = "${kcal.text}${ data.result[0].kcal}kcal"
        protein.text = "${protein.text}${ data.result[0].protein}g"
        fat.text = "${fat.text}${ data.result[0].fat}g"
        cholesterol.text = "${cholesterol.text}${ data.result[0].cholesterol}g"
        natrium.text = "${natrium.text}${ data.result[0].natrium}g"
        btn.isClickable = false
        btn.visibility = View.INVISIBLE
        submitBtn.isClickable = true
        submitBtn.visibility = View.VISIBLE
        closeCamera()
    }

    private fun tfLiteModel(uriList: ArrayList<Uri>, fileName: String?): String{
        val tf = testTF()
        val interpreter: Interpreter? = tf.getTfliteInterpreter("test_model2.tflite", this.requireActivity().assets)
        val contentResolver = this@FoodFragmentCamera.requireContext().getContentResolver()
        val item = contentResolver.openFile(uriList.last(), "r", null)
        val inputStream = FileInputStream(item?.fileDescriptor)
        val file = File(this@FoodFragmentCamera.requireContext().cacheDir, fileName)
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        val bitmap: Bitmap = BitmapFactory.decodeFile(file.path)
        val imageProcessor: ImageProcessor = ImageProcessor.Builder()

                .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
                .add(NormalizeOp(0f, 255f))
                .build()

        val tensorProcessor: TensorProcessor = TensorProcessor.Builder().add(NormalizeOp(0.0f, 255.0f)).build()
        var tfImage: TensorImage = TensorImage(DataType.FLOAT32)
        var probabilityBuffer: TensorBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 100), DataType.FLOAT32)
        tfImage.load(bitmap)
        tfImage = imageProcessor.process(tfImage)
        Log.d("test", tfImage.height.toString()+ " " + tfImage.width.toString())
        var output = FloatArray(100)
        Log.d("input shape", interpreter.toString())
        Log.d("output shape", interpreter?.outputTensorCount.toString())
        interpreter?.run(tfImage.buffer, probabilityBuffer.buffer)
        Log.d("buffer", probabilityBuffer.buffer.toString())
        val labelCategoryPath = "test_label1.txt"
        var axisList: List<String>? = null
        try{
            axisList = FileUtil.loadLabels(this.requireContext(), labelCategoryPath)
            Log.d("axlist List", axisList[0] + " " + axisList[99])
        }catch (e: IOException){
            Log.d("tfFile Support Error", e.printStackTrace().toString())
        }

        if(axisList != null){
            val label: TensorLabel = TensorLabel(axisList, probabilityBuffer)
            val floatMap = label.mapWithFloatValue
            Log.d("test", floatMap.toString())
            val values = floatMap.values.toList()
            val res = floatMap.filter { it.value == max(values) }
            Toast.makeText(this@FoodFragmentCamera.requireContext(), res.keys.first(), Toast.LENGTH_SHORT).show()
            return res.keys.first()
        }
        return "fail"
    }

    fun closeCamera(){
        if(null != cameraDevice){
            cameraDevice!!.close()
        }
    }


    fun reOpenCamera(){
        var foodName = binding.foodName
        var leftConstraint = binding.LeftConstraint
        var rightConstraint = binding.RightConstraint
        var btn = binding.takeBtn
        var submitBtn = binding.submitBtn
        var camera = binding.textureView
        var image = binding.resultImage
        foodName.text = "음식을 촬영해 주세요"
        leftConstraint.visibility = View.INVISIBLE
        rightConstraint.visibility = View.INVISIBLE
        btn.isClickable = true
        btn.visibility = View.VISIBLE
        submitBtn.isClickable = false
        submitBtn.visibility = View.INVISIBLE
        image.setImageBitmap(null)
        camera.visibility = View.VISIBLE
        FoodFragmentCamera().openCamera()
    }

    fun saveFoodLog(foodNo: Int){
        try{
            val fileName = getFileName(uriList.last())
            val contentResolver = this.requireContext().getContentResolver()
            val item = contentResolver.openFile(uriList.last(), "r", null)
            val inputStream = FileInputStream(item?.fileDescriptor)
            val file = File(this.requireContext().cacheDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            Log.d("saveFoodLogStart", "start here line 38")
            Log.d("UriList", uriList.toString())
            Log.d("file = ", file.length().toString())
            val requestBody: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name, requestBody)
            val gson: Gson = GsonBuilder().setLenient().create()
            val retrofit = Retrofit.Builder()

                    .baseUrl("http://497.iptime.org")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

            val connection = retrofit.create(FoodCameraInterface::class.java)
            Log.d("info test", userNo.toString() + " " + foodNo.toString())
            connection.saveFoodLog(userNo,foodNo,serving,body).enqueue(object: Callback<DefaultResponseKo> {
                override fun onFailure(call: Call<DefaultResponseKo>, t: Throwable){
                    Log.d("result1 - saveFoodLog", t.message.toString())
                }

                override fun onResponse(call: Call<DefaultResponseKo>, response: Response<DefaultResponseKo>) {
                    if(response?.isSuccessful){
                        Log.d("result2 - saveFoodLog", response?.body().toString())
                    }
                    else{
                        Log.d("response error", response?.message())
                        Log.d("response error", response?.code().toString())
                        Log.d("response error", response?.errorBody().toString())
                    }
                }

            })
        }catch (e: Exception){
            Log.d("Error!!!", e.stackTraceToString())
        }

    }

    fun getFoodInfo(foodName: String){
        val retrofit = Retrofit.Builder()

                .baseUrl("http://497.iptime.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val connection = retrofit.create(FoodCameraInterface::class.java)
        connection.getFoodInfo(foodName).enqueue(object: Callback<GetFoodResponse> {
            override fun onFailure(call: Call<GetFoodResponse>, t: Throwable) {
                Log.d("result1 - getFoodInfo", t.message.toString())
            }

            override fun onResponse(call: Call<GetFoodResponse>, response: Response<GetFoodResponse>) {
                if(response?.isSuccessful){
                    Log.d("result_test", response?.toString())
                    Log.d("result2 - getFoodInfo", response?.body().toString())
                    changeSettings(response.body()!!, foodName)
                    foodNo = response.body()!!.result[0].foodNo
                }else{
                    Log.d("error line-114", response?.body().toString())
                }
            }
        })
    }

    fun getFileName(uri: Uri): String?{
        Log.d("getFileName", "getFileName start")
        val contentResolver = this.requireContext().getContentResolver()
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        try{
            if(cursor == null)
                return null
            cursor.moveToFirst()
            val fileName: String = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            Log.d("fileName = ", fileName)
            cursor.close()
            return fileName
        }catch (e: Exception){
            Log.d("error", e.stackTraceToString())
            cursor?.close()
            return null
        }
        Log.d("getFileName", "getFileName end")
    }
}


interface FoodCameraInterface{
    @Multipart
    @POST("api/food/saveFoodLog")
    fun saveFoodLog(

            @Part("user_no") userNo: Int,
            @Part("food_no") foodNo : Int,
            @Part("serving") serving : Int,
            @Part food_image: MultipartBody.Part

    ): Call<DefaultResponseKo>


    @FormUrlEncoded
    @POST("api/food/getFoodInfo")
    fun getFoodInfo(

            @Field("food_name") foodName: String
    ): Call<GetFoodResponse>
}
