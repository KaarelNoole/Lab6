package com.noole.lab6.fragments;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.noole.lab6.R;
import com.noole.lab6.viewmodel.LoginRegisterViewModel;
import com.noole.lab6.viewmodel.UserViewModel;

import java.util.Objects;

public class RegisterFragment extends Fragment {

    private TextInputEditText firstName;
    private TextInputEditText lastName;
    private TextInputEditText user_email;
    private TextInputEditText user_password;
    private TextInputEditText passwordConfirmation;
    private CoordinatorLayout contained;
    private LoginRegisterViewModel loginRegisterViewModel;
    private UserViewModel userViewModel;

    public static boolean isAnyStringNullOrEmpty(String... strings) {
        for (String s : strings)
            if (s == null || s.isEmpty())
                return true;
        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginRegisterViewModel = new ViewModelProvider(this).get(LoginRegisterViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getUserMutableLiveData().observe(this,firebaseUser -> {
            if(firebaseUser != null && firebaseUser.isEmailVerified()){
                Toast.makeText(getContext(), "User logged in", Toast.LENGTH_SHORT).show();
                //TODO: problemo numbero uno user is logged in -> shouldn't be able to come here
            }
        });

        userViewModel.getLoggedOutMutableLiveData().observe(this, loggedOut -> {
            if (loggedOut){
                if(getView() != null) Navigation.findNavController(getView())
                        .navigate(R.id.action_registerFragment_to_loginFragment);
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register,container,false);

        setHasOptionsMenu(true);
        requireActivity().setTitle(getString(R.string.us_reg));

        contained = view.findViewById(R.id.register_coordinator);
        firstName = view.findViewById(R.id.etFirstName);
        lastName = view.findViewById(R.id.etLastName);
        user_email = view.findViewById(R.id.etEmailRegister);
        user_password = view.findViewById(R.id.etPasswordRegister);
        passwordConfirmation = view.findViewById(R.id.etPasswordConfirm);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_register, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.register_menu) {
            String first_name = Objects.requireNonNull(firstName.getText()).toString().trim();
            String last_name = Objects.requireNonNull(lastName.getText()).toString().trim();
            String email = Objects.requireNonNull(user_email.getText()).toString().trim();
            String password = Objects.requireNonNull(user_password.getText()).toString().trim();
            String confirmed_password = Objects.requireNonNull(passwordConfirmation.getText()).toString().trim();

            if (isAnyStringNullOrEmpty(first_name,last_name,email,password,confirmed_password)){
                Snackbar.make(contained, getString(R.string.field_err), Snackbar.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(user_email.getText().toString()).matches()){
                Snackbar.make(contained, "Email is not valid", Snackbar.LENGTH_SHORT).show();
            }
            else{
                if (password.length() < 6 || passwordConfirmation.length() < 6){
                    Snackbar.make(contained, getString(R.string.max_pass), Snackbar.LENGTH_SHORT).show();
                }else{
                    if (password.equals(confirmed_password)){
                        //creates user and saves data by default firebase logs the user in after creating account
                        loginRegisterViewModel.userRegistration(first_name,last_name,email,password,null);
                        //sign the user out
                        userViewModel.logOut();
                    }else{
                        Snackbar.make(contained, getString(R.string.noMatch), Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        }
        return false;
    }
}