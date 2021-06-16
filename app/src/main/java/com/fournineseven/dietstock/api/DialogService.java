package com.fournineseven.dietstock.api;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

public class DialogService {
    static public void showDialog(Context context,String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
