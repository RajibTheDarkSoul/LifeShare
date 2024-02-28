package com.example.lifeshare;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private List<SearchUser> userList;
    private Context context;

    public SearchAdapter(List<SearchUser> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchUser user = userList.get(position);

        holder.textViewUserName.setText(user.getUserName());
        holder.bloodGroup.setText(user.getBloodType());
        holder.address.setText(user.getAddress());
        holder.lastDonated.setText(user.getLastDonated());

        Picasso.get().load(user.getDpImage()).into(holder.dp);

        holder.textViewUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context,profileActivity1.class);
                i.putExtra("userId",user.getUserId());
                i.putExtra("userName",user.getUserName());
                i.putExtra("profilepic",user.getDpImage());
             //   i.putExtra("phoneNumber",user.getPhoneNumber());
                i.putExtra("track","1");
                context.startActivity(i);
            }
        });
        holder.dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context,profileActivity1.class);
                i.putExtra("userId",user.getUserId());
                i.putExtra("userName",user.getUserName());
                i.putExtra("profilepic",user.getDpImage());
                //   i.putExtra("phoneNumber",user.getPhoneNumber());
                i.putExtra("track","1");

                context.startActivity(i);
            }
        });

        holder.address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context,profileActivity1.class);
                i.putExtra("userId",user.getUserId());
                i.putExtra("userName",user.getUserName());
                i.putExtra("profilepic",user.getDpImage());
                //   i.putExtra("phoneNumber",user.getPhoneNumber());
                context.startActivity(i);
            }
        });

        holder.lastDonated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context,profileActivity1.class);
                i.putExtra("userId",user.getUserId());
                i.putExtra("userName",user.getUserName());
                i.putExtra("profilepic",user.getDpImage());
                //   i.putExtra("phoneNumber",user.getPhoneNumber());
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserName;
        TextView bloodGroup;
        TextView address;
        TextView lastDonated;
        CircleImageView dp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewUserName = itemView.findViewById(R.id.textViewUserNameSearch);
            bloodGroup = itemView.findViewById(R.id.bloodGroupSearch);
            address = itemView.findViewById(R.id.addressSearch);
            lastDonated = itemView.findViewById(R.id.donatedTime1);
            dp=itemView.findViewById(R.id.imageViewProfilePicsearch);

        }
    }
}

