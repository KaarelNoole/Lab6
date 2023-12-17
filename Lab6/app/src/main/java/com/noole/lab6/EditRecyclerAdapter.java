package com.noole.lab6;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.noole.lab6.model.User;

import java.util.ArrayList;

public class EditRecyclerAdapter extends RecyclerView.Adapter<EditRecyclerAdapter.UserViewHolder> {
    ArrayList<User> userArrayList;

    public EditRecyclerAdapter() {
        this.userArrayList = new ArrayList<>();
    }

    @NonNull
    @Override
    public EditRecyclerAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_edit,parent,false);
        return new EditRecyclerAdapter.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userArrayList.get(position);
        holder.firstName.setText(user.getFirstname());
        holder.lastName.setText(user.getLastname());
        holder.email.setText(user.getEmail());
        if (user.getNumber() != null) {
            holder.number.setText(user.getNumber()+"");
        }
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public void updateUserList(final ArrayList<User> userArrayList){
        this.userArrayList = userArrayList;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder{
        private final TextInputEditText firstName;
        private final TextInputEditText lastName;
        private final TextInputEditText email;
        private final TextInputEditText number;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            firstName = itemView.findViewById(R.id.editFirstName);
            lastName = itemView.findViewById(R.id.editLastName);
            email = itemView.findViewById(R.id.editEmailAddress);
            number = itemView.findViewById(R.id.editPhoneNumber);
        }
    }
}
