package com.noole.lab6;

import android.app.Application;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.noole.lab6.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AuthRepository {

    //log tag
    private static final String TAG = "Firebase:";
    //firebase variables
    private FirebaseAuth firebaseAuth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    //mutablelivedata variables
    private final MutableLiveData<FirebaseUser> userMutableLiveData;
    private final MutableLiveData<Boolean> loggedOutMutableLiveData;
    private final MutableLiveData<ArrayList<User>> userLiveData;
    private final ArrayList<User> userArrayList = new ArrayList<>();
    private final Application application;

    public AuthRepository(Application application) {
        this.application = application;
        firebaseAuth = FirebaseAuth.getInstance();
        userMutableLiveData = new MutableLiveData<>();
        loggedOutMutableLiveData = new MutableLiveData<>();
        userLiveData = new MutableLiveData<>();

        if (firebaseAuth.getCurrentUser() != null){
            userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
            loggedOutMutableLiveData.postValue(false);
            loadUserData();
        }
    }

    //method for registering user
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void userRegistration(String firstName, String lastName, String email, String password, String number){
        //creating a user with email + password
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(application.getMainExecutor(), task->{
                    //if user was created successfully then save data to firebase firestore
                    if (task.isSuccessful()){
                        if (firebaseAuth.getCurrentUser() != null){
                            //gets the newly created users UID
                            String userId = firebaseAuth.getCurrentUser().getUid();
                            //creates new collection named users if one doesn't exist into it add a new document with UID reference
                            DocumentReference documentReference = db.collection("users").document(userId);
                            //this is the data that will be written into the document
                            Map<String,Object> user = new HashMap<>();
                            user.put("firstname",firstName);
                            user.put("lastname",lastName);
                            user.put("email",email);
                            user.put("number",null);
                            documentReference.set(user)
                                    .addOnSuccessListener(aVoid -> Log.i(TAG, "onSuccess: user data was saved"))
                                    .addOnFailureListener(e -> Log.e(TAG, "onFailure: Error writing to DB document", e));
                            userMutableLiveData.postValue(firebaseAuth.getCurrentUser());

                            firebaseAuth = FirebaseAuth.getInstance();
                            FirebaseUser VerUser = firebaseAuth.getCurrentUser();

                            VerUser.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Email sent.");
                                                Toast.makeText(application, "Email sent. Please verify email", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    }else{
                        Toast.makeText(application, application.getString(R.string.error, task.getException().getMessage())
                                , Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //method for user logout
    public void logOut(){
        firebaseAuth.signOut();
        loggedOutMutableLiveData.postValue(true);
    }

    //method for user log in with email + password
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void login(String email, String password){
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(application.getMainExecutor(), task -> {
                    if (task.isSuccessful()){
                        userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
                    }else{
                        Toast.makeText(application, application.getString(R.string.error, task.getException()
                                        .getMessage())
                                , Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //method for obtaining user data from db
    public void loadUserData(){
        if (firebaseAuth.getCurrentUser() != null){
            String uid = firebaseAuth.getCurrentUser().getUid();
            DocumentReference doc = db.collection("users").document(uid);
            doc.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        User user = documentSnapshot.toObject(User.class);
                        userArrayList.add(user);
                        userLiveData.setValue(userArrayList);
                    }).addOnFailureListener(e ->
                            Toast.makeText(application, application.getString(R.string.error,e.getMessage()), Toast.LENGTH_SHORT).show());
        }
    }

    public void sendPasswordResetEmail(String email) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(application,"Password reset email was successfully sent!",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(application, application.getString(R.string.error, task.getException()
                                            .getMessage())
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void updateEmail(String email) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(application, "Email sent. Please verify email", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                        else{
                            Toast.makeText(application, application.getString(R.string.error, task.getException()
                                            .getMessage())
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void updateDocument(String firstname, String lastname, String email, String number) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference userRef = db.collection("users").document(user.getUid());

        userRef
                .update("firstname", firstname)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
        userRef
                .update("lastname", lastname)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
        userRef
                .update("email", email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
        userRef
                .update("number", number)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    //getters for livedata
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
