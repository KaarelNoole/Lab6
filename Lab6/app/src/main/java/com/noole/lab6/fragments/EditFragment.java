package com.noole.lab6.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.noole.lab6.EditRecyclerAdapter;
import com.noole.lab6.R;
import com.noole.lab6.viewmodel.UserViewModel;

import java.util.Objects;

public class EditFragment extends Fragment {

    private UserViewModel userViewModel;
    private EditRecyclerAdapter editRecyclerAdapter;
    private final String TAG = "";
    NavController navController;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(getString(R.string.us_info));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit,container,false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewEdit);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        editRecyclerAdapter = new EditRecyclerAdapter();
        recyclerView.setAdapter(editRecyclerAdapter);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), userArrayList ->
                editRecyclerAdapter.updateUserList(userArrayList));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_update, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void delay() {
        Navigation.findNavController(getView()).navigate(R.id.action_editFragment_to_userFragment);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        TextInputEditText firstName = requireView().findViewById(R.id.editFirstName);
        TextInputEditText lastName = requireView().findViewById(R.id.editLastName);
        TextInputEditText email = requireView().findViewById(R.id.editEmailAddress);
        TextInputEditText number = requireView().findViewById(R.id.editPhoneNumber);
        if (item.getItemId() == R.id.update){
            Log.i(TAG, email.getText().toString());
            if (email.getText().toString().equals("") || firstName.getText().toString().equals("") || lastName.getText().toString().equals("")) {
                Toast.makeText(getContext(), "Fields are missing", Toast.LENGTH_LONG).show();
            }
            else {
                userViewModel.getUserMutableLiveData().observe(this,firebaseUser -> {
                    if (Objects.equals(firebaseUser.getEmail(), email.getText().toString())) {
                        Toast.makeText(getContext(), "Fields were updated successfully", Toast.LENGTH_LONG).show();
                        userViewModel.updateDocument(firstName.getText().toString(),lastName.getText().toString(),email.getText().toString(), number.getText().toString());
                        (new Handler()).postDelayed(this::delay, 500);
                    }
                    else {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.updateEmail(email.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            user.sendEmailVerification()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {

                                                        public void delay() {
                                                            userViewModel.logOut();
                                                            Navigation.findNavController(getView()).navigate(R.id.action_editFragment_to_loginFragment);
                                                        }
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(getContext(), "Email sent. Please verify email", Toast.LENGTH_LONG).show();
                                                                userViewModel.updateDocument(firstName.getText().toString(),lastName.getText().toString(),email.getText().toString(), number.getText().toString());
                                                                //userViewModel.updateEmail(email.getText().toString());
                                                                Toast.makeText(getContext(), "Fields were updated successfully", Toast.LENGTH_LONG).show();

                                                                (new Handler()).postDelayed(this::delay, 800);
                                                            }
                                                        }
                                                    });
                                        }
                                        else{
                                            Toast.makeText(getContext(), getContext().getString(R.string.error, task.getException()
                                                            .getMessage())
                                                    , Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
            }
        }
        return false;
    }
}