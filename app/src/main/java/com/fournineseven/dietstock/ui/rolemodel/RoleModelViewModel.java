package com.fournineseven.dietstock.ui.rolemodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RoleModelViewModel extends ViewModel {
    private MutableLiveData<String> value1 = new MutableLiveData<String>();

    public void setValue1(String str){
        value1.setValue(str);
    }

    public LiveData<String> getValue1(){
        return value1;
    }
}
