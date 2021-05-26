package com.fournineseven.dietstock

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class UserSettingDialog(context: Context, myUserSettingDialogInterface: UserSettingDialogInterface,
                        sharedPreferences: SharedPreferences) : Dialog(context),
    View.OnClickListener {


    private val myShared: SharedPreferences by lazy{
        sharedPreferences
    }
    private val cancelButton: Button by lazy {
        findViewById(R.id.dialog_cancel)
    }
    private val okButton: Button by lazy {
        findViewById(R.id.dialog_ok)
    }

    private var myUserSettingDialogInterface: UserSettingDialogInterface? = null

    init {
        this.myUserSettingDialogInterface = myUserSettingDialogInterface
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_setting_dialog)

        //배경 투명
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        cancelButton.setOnClickListener(this)
        okButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            cancelButton -> {
                this.myUserSettingDialogInterface?.onCancelButtonClicked()
                cancel()
            }
            okButton -> {
                val ageTextView: TextView = findViewById(R.id.dialog_age_text)
                val heightTextView:TextView = findViewById(R.id.dialog_height_text)
                val weightTextView:TextView = findViewById(R.id.dialog_weight_text)
                val goalTextView:TextView = findViewById(R.id.dialog_goal_text)

                if(ageTextView.text.toString() == "" ||heightTextView.text.toString() == "" ||
                    weightTextView.text.toString() == "" || goalTextView.text.toString() == ""  ){
                    Log.d("MyTag","ㅇㅇ?")
                    Toast.makeText(context,"빈칸 없이 입력해주세요.", Toast.LENGTH_SHORT).show()
                }else{
                    var age:Int = ageTextView.text.toString().toInt()
                    var height:Float = heightTextView.text.toString().toFloat()
                    var weight:Float = weightTextView.text.toString().toFloat()
                    var goal:Float = goalTextView.text.toString().toFloat()

                    var editor = myShared.edit()
                    editor.putInt(LoginState.AGE_KEY,age)
                    editor.putFloat(LoginState.HEIGHT_KEY,height)
                    editor.putFloat(LoginState.WEIGHT_KEY,weight)
                    editor.putFloat(LoginState.GOAL_KEY,goal)
                    editor.apply()

                    //확인 후 빈칸으로 만들기
                    ageTextView.text = ""
                    weightTextView.text = ""
                    heightTextView.text = ""
                    goalTextView.text = ""
                    cancel()
                }

                this.myUserSettingDialogInterface?.onOkButtonClicked()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d("MyTag","온스탑")
    }
}