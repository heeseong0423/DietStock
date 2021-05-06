package com.fournineseven.dietstock.ui.food
import android.app.Activity
import com.fournineseven.dietstock.MainActivity
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import java.io.FileInputStream
import java.lang.Exception
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

object testTF {
    val compatList = CompatibilityList()
    val options = Interpreter.Options().apply {
        if(compatList.isDelegateSupportedOnThisDevice){
            val delegateOptions = compatList.bestOptionsForThisDevice
            this.addDelegate(GpuDelegate(delegateOptions))
        }else{
            this.setNumThreads(4)
        }
    }

    fun loadModelFile(activity : Activity, modelPath : String) : MappedByteBuffer{
        val fileDescriptor = activity.assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        var startOffset = fileDescriptor.startOffset
        var declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun getTfliteInterpreter(modelPath : String) : Interpreter? {
        try {
            return Interpreter(loadModelFile(MainActivity(), modelPath), options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}