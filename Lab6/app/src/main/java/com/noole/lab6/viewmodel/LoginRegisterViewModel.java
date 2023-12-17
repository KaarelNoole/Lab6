package com.noole.lab6.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.noole.lab6.AuthRepository;

//when user logs in and registers
public class LoginRegisterViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<FirebaseUser> userMutableLiveData;
    public LoginRegisterViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
        userMutableLiveData = authRepository.getUserMutableLiveData();
    }
    public void userRegistration(String firstName, String lastName, String email, String password,String number){
        authRepository.userRegistration(firstName, lastName, email, password,number);
    }
    public void login(String email, String password){
        authRepository.login(email, password);
    }
    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }
}
