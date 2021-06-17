    package com.fournineseven.dietstock.ui.food

import android.Manifest
import android.app.AlertDialog
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
import android.text.InputFilter
import android.util.Log
import android.util.Size
import android.view.*
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getExternalFilesDirs
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.fournineseven.dietstock.LoginState
import com.fournineseven.dietstock.R
import com.fournineseven.dietstock.User
import com.fournineseven.dietstock.databinding.FragmentFoodCameraBinding
import com.fournineseven.dietstock.model.FoodCamera.DefaultResponseKo
import com.fournineseven.dietstock.model.FoodCamera.GetFoodResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
import java.lang.NullPointerException
import java.lang.NumberFormatException
import java.lang.RuntimeException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.Collections.max
import java.util.concurrent.TimeUnit
import kotlin.collections.AbstractList


class FoodFragmentCamera : Fragment(){
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
    private lateinit var onBackCallback: OnBackPressedCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                    Log.d("OnBack", "1")
                    if(!binding.foodSearch.isIconified){
                        Log.d("OnBack", "2")
                        binding.foodSearch.isIconified = true
                    } else if(binding.textureView.visibility == View.INVISIBLE) {
                        Log.d("OnBack", "4")
                        flipVisibility(true)
                        openCamera()
                    } else{
                        onDetach()
                    }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackCallback)
    }

    override fun onDetach() {
        super.onDetach()
        onBackCallback.remove()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        Log.d("JJJ","카메라 onCreateView")
        val root = inflater.inflate(R.layout.fragment_food_camera, container, false)
        binding = FragmentFoodCameraBinding.inflate(inflater, container, false)
        windowManager = requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        binding.takeBtn.setOnClickListener(ButtonListener())
        binding.textureView.surfaceTextureListener = textureListener
        binding.submitBtn.setOnClickListener(SubmitBtnListener())
        sharedPreferences = requireContext().getSharedPreferences(LoginState.SHARED_PREFS, Context.MODE_PRIVATE)

        binding.foodSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if(!binding.foodSearch.isIconified){
                    binding.foodName.visibility = View.VISIBLE
                    binding.foodSearch.isIconified = true
                    binding.foodSearch.isIconified = true
                }
                if(!uriList.isEmpty()){
                    uriList.clear()
                }
                getFoodInfo(query!!)
                return true
            }
        })
        binding.foodSearch.setOnSearchClickListener(VisibilityListener())
        binding.foodSearch.setOnCloseListener(object : SearchView.OnCloseListener{
            override fun onClose(): Boolean {
                if(!binding.foodSearch.isIconified){
                    binding.foodName.visibility = View.VISIBLE
                    if(binding.resultImage.visibility == View.VISIBLE){
                        binding.SubConstraint.visibility = View.VISIBLE
                        binding.submitBtn.visibility = View.VISIBLE
                    }
                }
                return false
            }
        })
        binding.addBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(requireContext())
                builder.setTitle("추가할 음식을 입력해 주세요")
                val input = EditText(requireContext())
                val maxLength = 30
                input.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
                builder.setView(input)
                builder.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                    // Here you get get input text from the Edittext
                    var name = input.text.toString()
                    val gson: Gson = GsonBuilder().setLenient().create()
                    val retrofit = Retrofit.Builder()
                        .baseUrl("http://497.iptime.org")
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build()

                    val connection = retrofit.create(FoodCameraInterface::class.java)
                    connection.addFood(name).enqueue(object: Callback<DefaultResponseKo> {
                        override fun onFailure(call: Call<DefaultResponseKo>, t: Throwable){
                            Log.d("result1 - addFood", t.message.toString())
                        }

                        override fun onResponse(call: Call<DefaultResponseKo>, response: Response<DefaultResponseKo>) {
                            if(response?.isSuccessful){
                                val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(requireContext())
                                builder.setTitle("저장 성공")
                                builder.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->

                                }).show()
                            }
                            else{
                                Log.d("response error", response?.message())
                            }
                        }

                    })
                })
                builder.setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
                builder.show()

            }
        })
        
        userNo = sharedPreferences.getString(LoginState.USER_NUMBER, "0")!!.toInt()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        Log.d("JJJ","onStart.>.sdf")
    }


    private var textureListener: TextureView.SurfaceTextureListener = object : TextureView.SurfaceTextureListener{
        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
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
            Log.d("HELLO","먹은 칼로리는 ${User.foodKcal}")

            var sharedPreferences = context?.getSharedPreferences(LoginState.SHARED_PREFS, Context.MODE_PRIVATE);
            var editor = sharedPreferences?.edit()
            var lowKcal = sharedPreferences?.getFloat(LoginState.LOW_KEY,0.0f)
            var highKcal = sharedPreferences?.getFloat(LoginState.HIGH_KEY,0.0f)
            var intake = sharedPreferences?.getFloat(LoginState.INTAKE_KEY,0.0f)
            var natrium = sharedPreferences?.getFloat(LoginState.NATRIUM_KEY,0.0f)
            var danbaekjil = sharedPreferences?.getFloat(LoginState.DANBAEKJIL_KEY,0.0f)
            var tansuhwamul = sharedPreferences?.getFloat(LoginState.TANSUHWAMUL_KEY,0.0f)
            var zibang = sharedPreferences?.getFloat(LoginState.ZIBANG_KEY,0.0f)


            var userCurrentKcal = User.PKcal + User.kcal - (intake!! * User.foodNo)
            var minusFoodKcal = userCurrentKcal - User.foodKcal
            var currentIntakeKcal = sharedPreferences?.getFloat(LoginState.INTAKE_KEY,0.0f)
            editor?.putFloat(LoginState.INTAKE_KEY, currentIntakeKcal!! + (User.foodKcal * User.foodNo))

            User.UserIntakeKcal += (User.foodKcal * User.foodNo)

            if(highKcal!! < userCurrentKcal){
                Log.d(TAG,"User current kcal = ${userCurrentKcal}")
                editor?.putFloat(LoginState.HIGH_KEY,userCurrentKcal)
            }

            if(lowKcal!! > minusFoodKcal){
                Log.d(TAG,"User low kcal = ${lowKcal}")
                editor?.putFloat(LoginState.LOW_KEY,minusFoodKcal)
            }

            editor?.putFloat(LoginState.NATRIUM_KEY,natrium!! + (User.natrium * User.foodNo))
            editor?.putFloat(LoginState.DANBAEKJIL_KEY,danbaekjil!! + (User.danbaekjil * User.foodNo))
            editor?.putFloat(LoginState.TANSUHWAMUL_KEY,tansuhwamul!! + (User.tansuhwamul * User.foodNo))
            editor?.putFloat(LoginState.ZIBANG_KEY,zibang!! + (User.zibang * User.foodNo))
            editor?.apply()

            Log.d("KKKKKK","dd ${User.foodNo}")
        }
    }

    inner class VisibilityListener: View.OnClickListener{
        override fun onClick(v: View?) {
            if(!binding.foodSearch.isIconified){
                binding.foodName.visibility = View.INVISIBLE
                if(binding.resultImage.visibility == View.VISIBLE){
                    binding.SubConstraint.visibility = View.INVISIBLE
                    binding.submitBtn.visibility = View.INVISIBLE
                }
            }
        }
    }

    fun openCamera(){

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
            flipVisibility(true)
        }catch(e: CameraAccessException){
            Log.d("AcessException", "Here Problem")
            e.printStackTrace()
        }
    }

    private val stateCallback = object : CameraDevice.StateCallback(){
        override fun onOpened(camera: CameraDevice){
            cameraDevice = camera
            val result = createCameraPreviewSession()
            if(result == false){
                closeCamera()
            }
        }
        override fun onDisconnected(camera : CameraDevice){
//            Log.d(TAG, "stateCallback: onDisconnected")
//            flipVisibility(false)
//            closeCamera()
        }
        override fun onError(camera: CameraDevice, error: Int){
//            Log.d(TAG, "stateCallback: OnError")
//            flipVisibility(false)
//            closeCamera()
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
                        } finally {
                            output?.close()
                            val bitmap: Bitmap = BitmapFactory.decodeFile(file.path)

                            val rotateMatrix = Matrix()
                            rotateMatrix.postRotate(90F)
                            rotateMatrix.postScale((1920f/bitmap.width), (1080f/bitmap.height))
                            val rotatedBitmap: Bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, rotateMatrix, false)
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
                                uriList.add(uriList.size, item)
                            }catch (e: FileNotFoundException){
                            }finally {
                                output?.close()
                            }
                            binding.resultImage.setImageBitmap(rotatedBitmap)
                            binding.resultImage.visibility = View.VISIBLE
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

                    GlobalScope.launch {
                        delay(500)
                    }
                    val resultName = tfLiteModel(uriList, getFileName(uriList.last()))
                    if(resultName[0].first == "fail"){
                    }else{
                        var selectList: MutableList<String> = arrayListOf()
                        for(i in resultName){
                            selectList.add(i.first)
                        }
                        var finalResultName: String = ""
                        val arraySelect = selectList.toTypedArray()
                        var selectDialog = AlertDialog.Builder(requireContext()).setSingleChoiceItems(arraySelect, -1){
                            dialog, which-> finalResultName = arraySelect[which]
                        }.setPositiveButton("확인"){
                            dialog, which -> getFoodInfo((finalResultName))
                        }.setTitle("선택해 주세요").show()
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
        var carbohydrate = binding.carbohydrate
        var kcal = binding.kcal
        var protein = binding.protein
        var fat = binding.fat
        var cholesterol = binding.cholesterol
        var natrium = binding.natrium
        foodName.text = predictedFoodName

        carbohydrate.text = "${ data.result[0].carbs}g"
        kcal.text = "${ data.result[0].kcal}kcal"
        protein.text = "${ data.result[0].protein}g"
        fat.text = "${ data.result[0].fat}g"
        cholesterol.text = "${ data.result[0].cholesterol}g"
        natrium.text = "${ data.result[0].natrium}g"

        User.foodKcal = data.result[0].kcal
        User.natrium = data.result[0].natrium
        User.danbaekjil = data.result[0].protein
        User.zibang = data.result[0].fat
        User.tansuhwamul = data.result[0].carbs
    }

    private fun tfLiteModel(uriList: ArrayList<Uri>, fileName: String?): List<Pair<String, Float>>{
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
        var output = FloatArray(100)
        interpreter?.run(tfImage.buffer, probabilityBuffer.buffer)
        val labelCategoryPath = "test_label1.txt"
        var axisList: List<String>? = null
        try{
            axisList = FileUtil.loadLabels(this.requireContext(), labelCategoryPath)

        }catch (e: IOException){
            Log.d("tfFile Support Error", e.printStackTrace().toString())
        }

        if(axisList != null){
            val label: TensorLabel = TensorLabel(axisList, probabilityBuffer)
            val floatMap = label.mapWithFloatValue
            val sortedList = floatMap.toList().sortedWith(compareBy({it.second})).reversed()
            return sortedList.subList(0, 4)
        }
        return listOf(Pair<String, Float>("fail", -1f))
    }

    fun closeCamera(){
        if(null != cameraDevice){
            cameraDevice!!.close()
        }
    }

    fun flipVisibility(cameraOn: Boolean){
        var foodName = binding.foodName
        var subConstraint = binding.SubConstraint
        var btn = binding.takeBtn
        var submitBtn = binding.submitBtn
        var camera = binding.textureView
        var image = binding.resultImage
        var servingConstraint = binding.servingConstraint
        if(cameraOn){
            foodName.text = "음식을 촬영해 주세요"
            subConstraint.visibility = View.INVISIBLE
            submitBtn.visibility = View.INVISIBLE
            servingConstraint.visibility = View.INVISIBLE
            image.visibility = View.INVISIBLE
            btn.visibility = View.VISIBLE
            camera.visibility = View.VISIBLE
            btn.isClickable = true
            submitBtn.isClickable = false
            image.setImageBitmap(null)
        }else{
            btn.visibility = View.INVISIBLE
            camera.visibility = View.INVISIBLE
            subConstraint.visibility = View.VISIBLE
            submitBtn.visibility = View.VISIBLE
            servingConstraint.visibility = View.VISIBLE
            image.visibility = View.VISIBLE
            btn.isClickable = false
            submitBtn.isClickable = true
        }
    }

    fun saveFoodLog(foodNo: Int){
        try{
            serving = binding.serving.text.toString().toInt()
        }catch (e: NumberFormatException){
            val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(requireContext())
            builder.setMessage("식사량을 입력 해 주십시오")
            builder.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->

            }).show()
            return
        }
        User.foodNo = serving
        try{
            var body: MultipartBody.Part
            try{
                val fileName = getFileName(uriList.last())
                val contentResolver = this.requireContext().getContentResolver()
                val item = contentResolver.openFile(uriList.last(), "r", null)
                val inputStream = FileInputStream(item?.fileDescriptor)
                val file = File(this.requireContext().cacheDir, fileName)
                val outputStream = FileOutputStream(file)
                inputStream.copyTo(outputStream)
                val requestBody: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                body = MultipartBody.Part.createFormData("file", file.name, requestBody)
                val gson: Gson = GsonBuilder().setLenient().create()
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://497.iptime.org")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

                //serving -  몇 인분
                val connection = retrofit.create(FoodCameraInterface::class.java)
                connection.saveFoodLog2(userNo, foodNo, serving, body).enqueue(object: Callback<DefaultResponseKo> {
                    override fun onFailure(call: Call<DefaultResponseKo>, t: Throwable){
                        Log.d("result1 - saveFoodLog", t.message.toString())
                    }

                    override fun onResponse(call: Call<DefaultResponseKo>, response: Response<DefaultResponseKo>) {
                        if(response?.isSuccessful){
                            val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(requireContext())
                            builder.setTitle("저장 성공")
                            builder.setMessage("식단을 저장하였습니다.")
                            builder.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                                flipVisibility(true)
                                openCamera()
                            }).show()
                        }
                        else{
                            Log.d("response error", response?.message())
                            Log.d("response error", response?.code().toString())
                            Log.d("response error", response?.errorBody().toString())
                        }
                    }

                })
            }catch (e: Exception) {
                val gson: Gson = GsonBuilder().setLenient().create()
                val retrofit = Retrofit.Builder()

                    .baseUrl("http://497.iptime.org")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

                val connection = retrofit.create(FoodCameraInterface::class.java)
                connection.saveFoodLog1(userNo, foodNo, serving).enqueue(object: Callback<DefaultResponseKo> {
                    override fun onFailure(call: Call<DefaultResponseKo>, t: Throwable){
                        Log.d("result1 - saveFoodLog", t.message.toString())
                    }

                    override fun onResponse(call: Call<DefaultResponseKo>, response: Response<DefaultResponseKo>) {
                        if(response?.isSuccessful){
                            val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(requireContext())
                            builder.setTitle("저장 성공")
                            builder.setMessage("식단을 저장하였습니다.")
                            builder.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                                flipVisibility(true)
                                openCamera()
                            }).show()
                        }
                        else{
                            Log.d("response error", response?.message())
                            Log.d("response error", response?.code().toString())
                            Log.d("response error", response?.errorBody().toString())
                        }
                    }

                })
            }
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
                    if(response?.body()!!.result?.isEmpty()){
                        val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(requireContext())
                        builder.setTitle("No Result")
                        builder.setMessage("검색 결과가 없습니다.")
                        builder.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                            openCamera()
                        }).show()
                    }else{
                        changeSettings(response.body()!!, foodName)
                        flipVisibility(false)
                        closeCamera()
                        foodNo = response.body()!!.result[0].foodNo
                    }

                }else{
                    Log.d("error line-114", response?.body().toString())
                }
            }
        })
    }

    fun getFileName(uri: Uri): String?{
        val contentResolver = this.requireContext().getContentResolver()
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        try{
            if(cursor == null)
                return null
            cursor.moveToFirst()
            val fileName: String = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
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

    @FormUrlEncoded
    @POST("api/food/saveFoodLog/1")
    fun saveFoodLog1(
        @Field("user_no") userNo: Int,
        @Field("food_no") foodNo : Int,
        @Field("serving") serving : Int,
    ): Call<DefaultResponseKo>

    @Multipart
    @POST("api/food/saveFoodLog/2")
    fun saveFoodLog2(
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

    @FormUrlEncoded
    @POST("api/food/addFood")
    fun addFood(
        @Field("name") name: String,
    ): Call<DefaultResponseKo>
}
