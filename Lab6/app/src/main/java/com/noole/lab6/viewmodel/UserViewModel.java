package com.noole.lab6.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.noole.lab6.AuthRepository;
import com.noole.lab6.model.User;

import java.util.ArrayList;

// when user is logged in
public class UserViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<FirebaseUser> userMutableLiveData;
    private final MutableLiveData<Boolean> loggedOutMutableLiveData;
    private final MutableLiveData<ArrayList<User>> userLiveData;
    public UserViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
        userMutableLiveData = authRepository.getUserMutableLiveData();
        loggedOutMutableLiveData = authRepository.getLoggedOutMutableLiveData();
        userLiveData = authRepository.getUserLiveData();
    }
    public void logOut(){ authRepository.logOut();}
    public void updateEmail(String email){ authRepository.updateEmail(email);}
    public void updateDocument(String firstname,String lastname,String email,String number){ authRepository.updateDocument(firstname,lastname,email,number);}
    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }
    public MutableLiveData<Boolean> getLoggedOutMutableLiveData() {
        return loggedOutMutableLiveData;
    }
    public MutableLiveData<ArrayList<User>> getUserLiveData() {
        return userLiveData;
    }
}