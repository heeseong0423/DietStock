package com.fournineseven.dietstock.Utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;

public class HttpConnectUtil {
    static public HashMap<String, String> sendGetData(String serverURL){
        HashMap<String, String> result = new HashMap<>();
        HttpURLConnection con = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(serverURL);
            con = (HttpURLConnection) url.openConnection();

            con.setReadTimeout(10000);
            con.setConnectTimeout(10000);
            con.setRequestMethod("GET");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "text/html");

            con.setDoOutput(false);
            con.setDoInput(true);
            con.connect();
            int responseStatusCode = con.getResponseCode();

            InputStream inputStream;
            if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                inputStream = con.getInputStream();
            } else {
                inputStream = con.getErrorStream();
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer buffer = new StringBuffer();

            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            System.out.println(buffer.toString());
            JSONObject resultJsonObject = new JSONObject(buffer.toString());
            boolean res = resultJsonObject.getBoolean("success");
            if(!res) {
                int errcode = resultJsonObject.getInt("errcode");
                switch (errcode){
                    case 101:
                        result.put("error", "계정이 없습니다.");
                        return result;
                    case 102:
                        result.put("error", "비밀번호가 틀렸습니다.");
                        return result;
                    default:
                        result.put("error", "알 수 없는 오류");
                        return result;
                }
            }else{
                result.put("result", buffer.toString());
                return result;
            }
        }catch(MalformedURLException e){
            e.printStackTrace();
            result.put("error", "DB 접속 오류");
            return result;
        }catch (UnknownHostException e) {
            e.printStackTrace();
            result.put("error", "인터넷 연결을 확인해주세요");
            return result;
        }catch(IOException e){
            e.printStackTrace();
            result.put("error", "다시 시도");
            return result;
        }catch (Exception e) {
            e.printStackTrace();
            result.put("error", "오류! 다시 시도해 주세요");
            return result;
        }finally
        {
            if (con != null){
                Log.d("debug", "con 연결 끊음");
                con.disconnect();
            }
            try{
                if(reader != null){
                    Log.d("debug", "reader 끊음");
                    reader.close();
                }
            }catch(IOException e){
                e.printStackTrace();
                result.put("error", "입출력 오류");
                return result;
            }
        }
    }
    static public HashMap<String, String> sendPostData(String serverURL, JSONObject jsonObject) {
        HashMap<String, String> result = new HashMap<>();
        HttpURLConnection con = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(serverURL);
            con = (HttpURLConnection) url.openConnection();

            con.setReadTimeout(10000);
            con.setConnectTimeout(10000);
            con.setRequestMethod("POST");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "text/html");

            con.setDoOutput(true);
            con.setDoInput(true);
            con.connect();

            OutputStream outputStream = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(jsonObject.toString());
            writer.flush();
            writer.close();

            int responseStatusCode = con.getResponseCode();

            InputStream inputStream;
            if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                inputStream = con.getInputStream();
            } else {
                inputStream = con.getErrorStream();
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer buffer = new StringBuffer();

            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            JSONObject resultJsonObject = new JSONObject(buffer.toString());
            boolean res = resultJsonObject.getBoolean("success");
            if(!res) {
                int errcode = resultJsonObject.getInt("errcode");
                switch (errcode){
                    case 101:
                        result.put("error", "계정이 없습니다.");
                        return result;
                    case 102:
                        result.put("error", "비밀번호가 틀렸습니다.");
                        return result;
                    default:
                        result.put("error", "알 수 없는 오류");
                        return result;
                }
            }else{
                result.put("result", buffer.toString());
                return result;
            }
        }catch(MalformedURLException e){
            e.printStackTrace();
            result.put("error", "DB 접속 오류");
            return result;
        }catch (UnknownHostException e) {
            e.printStackTrace();
            result.put("error", "인터넷 연결을 확인해주세요");
            return result;
        }catch(IOException e){
            e.printStackTrace();
            result.put("error", "다시 시도");
            return result;
        }catch (Exception e) {
            e.printStackTrace();
            result.put("error", "오류! 다시 시도해 주세요");
            return result;
        }finally
        {
            if (con != null){
                Log.d("debug", "con 연결 끊음");
                con.disconnect();
            }
            try{
                if(reader != null){
                    Log.d("debug", "reader 끊음");
                    reader.close();
                }
            }catch(IOException e){
                e.printStackTrace();
                result.put("error", "입출력 오류");
                return result;
            }
        }
    }
}
