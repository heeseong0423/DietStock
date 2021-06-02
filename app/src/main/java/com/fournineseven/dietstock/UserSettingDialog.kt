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
import com.fournineseven.dietstock.retrofitness.RetrofitBuilder
import com.fournineseven.dietstock.retrofitness.UpdateGoalResponse
import com.fournineseven.dietstock.retrofitness.UpdateHeightResponse
import com.fournineseven.dietstock.retrofitness.UpdateWeightResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val TAG = "ResponseTest"

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

                var userNumber = myShared.getString(LoginState.USER_NUMBER,"0")!!.toInt()
                //val ageTextView: TextView = findViewById(R.id.dialog_age_text)
                val heightTextView:TextView = findViewById(R.id.dialog_height_text)
                val weightTextView:TextView = findViewById(R.id.dialog_weight_text)
                val goalTextView:TextView = findViewById(R.id.dialog_goal_text)

                if(heightTextView.text.toString() == "" ||
                    weightTextView.text.toString() == "" || goalTextView.text.toString() == ""  ){
                    Log.d("MyTag","ㅇㅇ?")
                    Toast.makeText(context,"빈칸 없이 입력해주세요.", Toast.LENGTH_SHORT).show()
                }else{
                    //var age:Int = ageTextView.text.toString().toInt()
                    var height:Float = heightTextView.text.toString().toFloat()
                    var weight:Float = weightTextView.text.toString().toFloat()
                    var goal:Float = goalTextView.text.toString().toFloat()

                    var editor = myShared.edit()
                    //editor.putInt(LoginState.AGE_KEY,age)
                    editor.putFloat(LoginState.HEIGHT_KEY,height)
                    editor.putFloat(LoginState.WEIGHT_KEY,weight)
                    editor.putFloat(LoginState.GOAL_KEY,goal)
                    editor.apply()

                    RetrofitBuilder.api.updateWeight(UpdateWeightRequest(user_no = userNumber, weight = weight))
                        .enqueue(object : Callback<UpdateWeightResponse> {
                            override fun onResponse(
                                call: Call<UpdateWeightResponse>,
                                response: Response<UpdateWeightResponse>
                            ) {
                                Log.d(TAG,"몸무게 업데이트${response.body()?.success}")
                            }
                            override fun onFailure(call: Call<UpdateWeightResponse>, t: Throwable) {

                            }

                        })


                    RetrofitBuilder.api.updateHeight(UpdateHeightRequest(user_no = userNumber, height = height))
                        .enqueue(object : Callback<UpdateHeightResponse> {
                            override fun onResponse(
                                call: Call<UpdateHeightResponse>,
                                response: Response<UpdateHeightResponse>
                            ) {
                                Log.d(TAG,"키 업데이트${response.body()?.success}")
                            }

                            override fun onFailure(call: Call<UpdateHeightResponse>, t: Throwable) {
                                TODO("Not yet implemented")
                            }

                        })

                    RetrofitBuilder.api.updateGoal(UpdateGoalRequest(user_no = userNumber, goal = goal))
                        .enqueue(object : Callback<UpdateGoalResponse> {
                            override fun onResponse(
                                call: Call<UpdateGoalResponse>,
                                response: Response<UpdateGoalResponse>
                            ) {
                                Log.d(TAG,"목표 업데이트${response.body()?.success}")
                            }

                            override fun onFailure(call: Call<UpdateGoalResponse>, t: Throwable) {
                                TODO("Not yet implemented")
                            }
                        })


                    //확인 후 빈칸으로 만들기
                    //ageTextView.text = ""
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