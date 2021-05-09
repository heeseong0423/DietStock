package com.fournineseven.dietstock.ui.food

import android.Manifest
import android.app.AppOpsManager
import android.content.*
import android.content.ContentValues.TAG
import android.content.Context.CAMERA_SERVICE
import android.content.pm.PackageManager
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
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getExternalFilesDirs
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.fournineseven.dietstock.R
import com.fournineseven.dietstock.databinding.FragmentFoodCameraBinding
import java.io.*
import java.lang.RuntimeException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_food_camera, container, false)
        binding = FragmentFoodCameraBinding.inflate(inflater, container, false)
        windowManager = requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        binding.takeBtn.setOnClickListener(ButtonListener())
        binding.textureView.surfaceTextureListener = textureListener
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
            createCameraPreviewSession()
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

    private fun createCameraPreviewSession(){
        try{
            texture = binding.textureView.surfaceTexture!!
            if(texture == null){
                closeCamera()
                return
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

        }catch (e: CameraAccessException){
            closeCamera()
            Log.d("Error", e.message.toString())
            Log.d("Error", e.stackTraceToString())
        }
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
                    val food = Food(uriList = uriList, context = this@FoodFragmentCamera.requireContext())
                    food.getFoodInfo()
                    food.saveFoodLog()
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
                    foodName.visibility = View.VISIBLE
                    leftConstraint.visibility = View.VISIBLE
                    rightConstraint.visibility = View.VISIBLE
                    foodName.text = "칼국수"
                    carbohydrate.text = "${carbohydrate.text}${ 283}g"
                    kcal.text = "${kcal.text}${ 798}kcal"
                    protein.text = "${protein.text}${ 12.3}g"
                    fat.text = "${fat.text}${ 137}g"
                    cholesterol.text = "${cholesterol.text}${ 122}g"
                    natrium.text = "${natrium.text}${ 42}g"
                    btn.isClickable = false
                    btn.visibility = View.INVISIBLE
                    closeCamera()

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
    private fun closeCamera(){
        if(null != cameraDevice){
            cameraDevice!!.close()
        }
    }

}