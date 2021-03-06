package com.fournineseven.dietstock;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fournineseven.dietstock.api.DialogService;
import com.fournineseven.dietstock.api.RetrofitService;
import com.fournineseven.dietstock.config.TaskServer;
import com.fournineseven.dietstock.model.DefaultResponse;
import com.fournineseven.dietstock.model.getRanking.GetRankingResponse;
import com.fournineseven.dietstock.model.login.LoginModel;
import com.fournineseven.dietstock.model.login.LoginResponse;
import com.fournineseven.dietstock.model.login.LoginResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignActivity extends BaseActivity {

    private static int PERMISSION_REQUEST_CODE = 2;

    SharedPreferences sharedpreferences;

    Button btn_signin, btn_signup, btn_before_image_register, btn_next_signup;
    LinearLayout ll_sign_main, ll_signin, ll_signup, ll_signup2;
    TextView tv_main_1;
    EditText et_id, et_password, et_id_register, et_name_register, et_password1_register, et_password2_register,
            et_height_register, et_goal_register, et_weight_register, et_age_register;
    RadioGroup radiogroup_sex;
    RadioButton radiobutton_male_register, radiobutton_female_register;
    RadioButton radiobutton_activity1_register, radiobutton_activity2_register,radiobutton_activity3_register;
    ImageView iv_before_image_register;
    Handler mHandler = null;


    File beforeImageFile=null;
    private static final int PICK_FROM_ALBUM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("login", "login ??????");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        init();

        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACTIVITY_RECOGNITION,
                Manifest.permission.CAMERA
        };

        requirePermissions(permissions, PERMISSION_REQUEST_CODE);

        sharedpreferences = getSharedPreferences(LoginState.SHARED_PREFS, Context.MODE_PRIVATE);
        LoginState.INSTANCE.setEmail(sharedpreferences.getString(LoginState.EMAIL_KEY, null));
        LoginState.INSTANCE.setPassword(sharedpreferences.getString(LoginState.PASSWORD_KEY, null));
        RetrofitService loginService = App.retrofit.create(RetrofitService.class);

        et_password.setOnEditorActionListener(
                new TextView.OnEditorActionListener(){
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                String userIdLogin = et_id.getText().toString();
                                String passwordLogin = et_password.getText().toString();
                                if(userIdLogin.equals("")||passwordLogin.equals("")) {
                                    Toast.makeText(SignActivity.this, "????????? ?????? ???????????????", Toast.LENGTH_SHORT).show();
                                }else{
                                    RetrofitService loginService = App.retrofit.create(RetrofitService.class);

                                    Call<LoginResponse> callLogin = loginService.login(new LoginModel(userIdLogin, passwordLogin));
                                    callLogin.enqueue(new Callback<LoginResponse>() {
                                        @Override
                                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                                            try {
                                                Log.d("debug", response.body().toString());
                                                LoginResponse getLoginResponse = (LoginResponse)response.body();
                                                if(getLoginResponse.isSuccess()) {
                                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                                    editor.putString(LoginState.EMAIL_KEY, et_id.getText().toString());
                                                    editor.putString(LoginState.PASSWORD_KEY, et_password.getText().toString());

                                                    int user_no= getLoginResponse.getResult().getUser_no();
                                                    editor.putString(LoginState.USER_NUMBER, String.valueOf(user_no));
                                                    editor.apply();

                                                    Log.d("MyTag","the " + user_no);
                                                    Intent intent = new Intent(SignActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }else{
                                                    DialogService.showDialog(SignActivity.this, "??????", "????????? ???????????? ??????");
                                                }
                                            }catch (NullPointerException e){
                                                DialogService.showDialog(SignActivity.this, "??????", "???????????? ?????? ????????????");
                                                et_password.setText("");
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                                            Log.d("debug", "onFailure: "+t.getMessage());
                                        }
                                    });
                                }
                                return true; // consume.
                            }
                        }
                        return false;
                    }
                }
        );


    }

    void init(){
        tv_main_1 = (TextView)findViewById(R.id.tv_main_1);
        btn_signin = (Button)findViewById(R.id.btn_signin);
        btn_signup = (Button)findViewById(R.id.btn_signup);
        mHandler = new Handler(Looper.getMainLooper());
        ll_sign_main = (LinearLayout)findViewById(R.id.ll_sign_main);
        ll_signin = (LinearLayout)findViewById(R.id.ll_signin);
        ll_signup = (LinearLayout)findViewById(R.id.ll_signup);
        ll_signup2 = (LinearLayout)findViewById(R.id.ll_signup2);
        et_id = (EditText)findViewById(R.id.et_id);
        et_password = (EditText)findViewById(R.id.et_password);
        et_id_register = (EditText)findViewById(R.id.et_id_register);
        et_name_register = (EditText)findViewById(R.id.et_name_register);
        et_password1_register = (EditText)findViewById(R.id.et_password1_register);
        et_password2_register = (EditText)findViewById(R.id.et_password2_register);
        et_height_register = (EditText)findViewById(R.id.et_height_register);
        et_goal_register = (EditText)findViewById(R.id.et_goal_register);
        et_weight_register = (EditText)findViewById(R.id.et_weight_register);
        et_age_register = (EditText)findViewById(R.id.et_age_register);
        radiobutton_male_register = (RadioButton)findViewById(R.id.radiobutton_male_register);
        radiobutton_female_register = (RadioButton)findViewById(R.id.radiobutton_female_register);
        btn_before_image_register = (Button)findViewById(R.id.btn_before_image_register);
        iv_before_image_register = (ImageView)findViewById(R.id.iv_before_image_register);
        btn_next_signup = (Button)findViewById(R.id.btn_next_signup);
        radiobutton_activity1_register = (RadioButton)findViewById(R.id.radiobutton_activity1_register);
        radiobutton_activity2_register = (RadioButton)findViewById(R.id.radiobutton_activity2_register);
        radiobutton_activity3_register = (RadioButton)findViewById(R.id.radiobutton_activity3_register);

    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_signin:
                ll_sign_main.setVisibility(View.GONE);
                ll_signin.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_signup:
                tv_main_1.setVisibility(View.GONE);
                ll_sign_main.setVisibility(View.GONE);
                ll_signup.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_before_image_register:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM);
                break;
            case R.id.btn_submit_signin:
                String userIdLogin = et_id.getText().toString();
                String passwordLogin = et_password.getText().toString();

                if(userIdLogin.equals("")||passwordLogin.equals("")) {
                    DialogService.showDialog(SignActivity.this, "??????", "????????? ?????? ???????????????");
                    break;
                }else{
                    try{Thread.sleep(1000);}catch (InterruptedException e){e.printStackTrace();}
                }
                RetrofitService loginService = App.retrofit.create(RetrofitService.class);

                Call<LoginResponse> callLogin = loginService.login(new LoginModel(userIdLogin, passwordLogin));
                callLogin.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        try {
                            Log.d("debug", response.body().toString());
                            LoginResponse getLoginResponse = (LoginResponse)response.body();
                            if(getLoginResponse.isSuccess()) {
                                //Context context = SignActivity.this;
                                //SharedPreferences sharedPref = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                //SharedPreferences.Editor editor = sharedPref.edit();
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(LoginState.EMAIL_KEY, et_id.getText().toString());
                                editor.putString(LoginState.PASSWORD_KEY, et_password.getText().toString());

                                int user_no= getLoginResponse.getResult().getUser_no();
                                editor.putString(LoginState.USER_NUMBER, String.valueOf(user_no));
                                //editor.commit();
                                editor.apply();

                                Log.d("MyTag","the " + user_no);
                                Intent intent = new Intent(SignActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }catch (NullPointerException e){
                            DialogService.showDialog(SignActivity.this, "??????", "????????? ????????? ??????????????????");
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        DialogService.showDialog(SignActivity.this, "??????", "?????? ?????? ??????");
                    }
                });
                break;
            case R.id.btn_next_signup:
                Log.d("debug",  "" + et_password1_register.getText().toString());
                Log.d("debug",  "" + et_password2_register.getText().toString());
                if(et_password1_register.getText().toString().equals(et_password2_register.getText().toString())){
                    ll_signup.setVisibility(View.GONE);
                    ll_signup2.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(SignActivity.this, "??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_submit_signup:
                Log.d("debug", "???????????? ??????1");
                String typedUserId = et_id_register.getText().toString();
                String typedPassword = et_password1_register.getText().toString();
                String typedName = et_name_register.getText().toString();
                String typedHeight = et_height_register.getText().toString();
                String typedGoal = et_goal_register.getText().toString();
                String typedWeight = et_weight_register.getText().toString();
                String typedAge = et_age_register.getText().toString();
                int typedSex=0;
                if(radiobutton_male_register.isChecked()) typedSex = 1;
                else if(radiobutton_female_register.isChecked()) typedSex = 2;
                int typedActivity=0;
                if(radiobutton_activity1_register.isChecked()) typedActivity=1;
                else if(radiobutton_activity2_register.isChecked()) typedActivity=2;
                else typedActivity=3;
                if(typedUserId.equals("")||typedPassword.equals("")||typedName.equals("")||typedHeight.equals("")||typedGoal.equals("")||typedSex==0||typedActivity==0||
                        typedWeight.equals("")||typedAge.equals("")) {
                    DialogService.showDialog(SignActivity.this, "??????", "????????? ?????? ???????????????");
                    break;
                }
                /*if(beforeImageFile==null)
                    break;*/
                RetrofitService saveUserService = App.retrofit.create(RetrofitService.class);
                RequestBody user_id = RequestBody.create( okhttp3.MultipartBody.FORM,typedUserId);
                RequestBody password = RequestBody.create(okhttp3.MultipartBody.FORM,typedPassword);
                RequestBody name = RequestBody.create(okhttp3.MultipartBody.FORM,typedName);
                RequestBody height = RequestBody.create(okhttp3.MultipartBody.FORM,typedHeight);
                RequestBody goal = RequestBody.create(okhttp3.MultipartBody.FORM,typedGoal);
                RequestBody weight = RequestBody.create(okhttp3.MultipartBody.FORM,typedWeight);
                RequestBody age = RequestBody.create(okhttp3.MultipartBody.FORM,typedAge);
                RequestBody sex = RequestBody.create(okhttp3.MultipartBody.FORM,String.valueOf(typedSex));
                RequestBody activity = RequestBody.create(okhttp3.MultipartBody.FORM,String.valueOf(typedActivity));
                /*RequestBody beforeimage = RequestBody.create(MediaType.parse("multipart/form-data"),beforeImageFile);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", beforeImageFile.getName(), beforeimage);*/

                RequestBody beforeimage;
                MultipartBody.Part body;
                if(beforeImageFile==null){
                    /*beforeimage = RequestBody.create(MediaType.parse("multipart/form-data"),beforeImageFile);
                    body = MultipartBody.Part.createFormData("file", beforeImageFile.getName(), beforeimage);*/
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hindoongi);
                    File myfile = bitmapToFile(getBaseContext(),bitmap,"default.jpg");

                    beforeimage = RequestBody.create(MediaType.parse("multipart/form-data"),myfile);
                    body = MultipartBody.Part.createFormData("file", myfile.getName(), beforeimage);
                    /*Toast.makeText(SignActivity.this, "??????????????? ???????????????.", Toast.LENGTH_SHORT).show();
                    break;*/
                }else{
                    beforeimage = RequestBody.create(MediaType.parse("multipart/form-data"),beforeImageFile);
                    body = MultipartBody.Part.createFormData("file", beforeImageFile.getName(), beforeimage);
                }
                Call<DefaultResponse> callRegister = saveUserService.saveUser(user_id, password, name, height, goal,weight,age,sex,activity, body);
                callRegister.enqueue(new Callback<DefaultResponse>() {
                    @Override
                    public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                        Log.d("debug", "onSuccess");
                        if(response.body().isSuccess()) {
                            tv_main_1.setVisibility(View.VISIBLE);
                            ll_signup2.setVisibility(View.GONE);
                            ll_sign_main.setVisibility(View.VISIBLE);
                        }else{
                            DialogService.showDialog(SignActivity.this, "??????", "????????? ???????????? ??????");
                        }
                        /*tv_main_1.setVisibility(View.VISIBLE);
                        ll_signup.setVisibility(View.GONE);
                        ll_sign_main.setVisibility(View.VISIBLE);*/
                    }

                    @Override
                    public void onFailure(Call<DefaultResponse> call, Throwable t) {
                        DialogService.showDialog(SignActivity.this, "??????", "?????? ?????? ??????");
                    }
                });
                break;
        }
    }

    public static File bitmapToFile(Context context,Bitmap bitmap, String fileNameToSave) { // File name like "image.png"
        //create a file to write bitmap data
        File file = null;
        try {
            file = new File(Environment.getExternalStorageDirectory() + File.separator + fileNameToSave);
            file.createNewFile();

//Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 , bos); // YOU can also save it in JPEG
            byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return file;
        }catch (Exception e){
            e.printStackTrace();
            return file; // it will return null
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_FROM_ALBUM){
            Uri photoUri = data.getData();
            Cursor cursor = null;

            try{
                String[] proj = {MediaStore.Images.Media.DATA};
                assert photoUri != null;
                cursor = getContentResolver().query(photoUri, proj, null, null, null);
                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();

                beforeImageFile = new File(cursor.getString(column_index));

            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap originalBm = BitmapFactory.decodeFile(beforeImageFile.getAbsolutePath(), options);
            iv_before_image_register.setImageBitmap(originalBm);
        }
    }

    @Override
    public void onBackPressed() {
        if(ll_sign_main.getVisibility()==View.VISIBLE)
        {
            super.onBackPressed();
        }else if(ll_signup.getVisibility() == View.VISIBLE){
            ll_signup.setVisibility(View.GONE);
            ll_sign_main.setVisibility(View.VISIBLE);
        }else if(ll_signin.getVisibility() ==View.VISIBLE){
            ll_signin.setVisibility(View.GONE);
            ll_sign_main.setVisibility(View.VISIBLE);
        }else if(ll_signup2.getVisibility() == View.VISIBLE){
            ll_signup2.setVisibility(View.GONE);
            ll_signup.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void permissionGranted(int requestCode) {

    }

    @Override
    public void permissionDenied(int requestCode) {
        Toast.makeText(SignActivity.this, "?????? ???????????????~", Toast.LENGTH_SHORT).show();
        finish();
    }
}
