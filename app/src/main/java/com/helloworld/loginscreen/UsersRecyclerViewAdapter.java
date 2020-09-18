package com.helloworld.loginscreen;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.helloworld.loginscreen.db.UserAuth;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<UsersRecyclerViewAdapter.UserHolder> {

    private ArrayList<UserAuth> list;

    public UsersRecyclerViewAdapter(ArrayList<UserAuth> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        holder.name.setText(list.get(position).getUsername());
        holder.mail.setText(list.get(position).getMail());
        if(list.get(position).getImage() != null){
            holder.img.setImageBitmap(BitmapFactory.decodeByteArray(list.get(position).getImage(),0,list.get(position).getImage().length));
            holder.img.setBackgroundColor(0);
        }else{
            holder.img.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.ic_account_circle));
            holder.img.setBackground(holder.itemView.getResources().getDrawable(R.drawable.white_oval));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class UserHolder extends RecyclerView.ViewHolder {

        CircleImageView img;
        TextView name,mail;
        public UserHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.users_img);
            name = itemView.findViewById(R.id.username);
            mail = itemView.findViewById(R.id.email);

        }
    }
}
