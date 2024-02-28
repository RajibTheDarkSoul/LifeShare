package com.example.lifeshare;

import android.app.Notification;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificaViewHolder> {

    private List<Notifica> notificaList;
    private Context context;
   // private NotificaClickListener clickListener;

    public NotificationAdapter(List<Notifica> notificaList, Context context) {
        this.notificaList = notificaList;
        this.context = context;

    }

    public interface NotificaClickListener {
        void onNotificaClick(Notifica notifica);
    }

    public static class NotificaViewHolder extends RecyclerView.ViewHolder {
        CircleImageView senderDp;
        TextView notificaText;
        TextView notificaTime;

        public NotificaViewHolder(@NonNull View itemView) {
            super(itemView);
            senderDp = itemView.findViewById(R.id.senderDp);
            notificaText = itemView.findViewById(R.id.notificaText);
            notificaTime = itemView.findViewById(R.id.notificaTime);
        }
    }

    @NonNull
    @Override
    public NotificaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notifica, parent, false);
        return new NotificaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificaViewHolder holder, int position) {
        Notifica currentNotifica = notificaList.get(position);

        // Set sender DP and username based on senderUserId
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentNotifica.getSenderUserId());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userNa = snapshot.child("userName").getValue(String.class);
                    String dpIm = snapshot.child("dpImage").getValue(String.class);

                    Picasso.get().load(dpIm).into(holder.senderDp);

                    // Set the notification text
                    String notificaText = userNa + " has responded to your request.";

                    //Setting the username to bold and keep others ok
                    SpannableString spannableString = new SpannableString(notificaText);
                    spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, userNa.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                 //   holder.notificaText.setText(notificaText);
                    holder.notificaText.setText(spannableString);

                    // Set the notification time
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    String formattedDate = sdf.format(new Date(currentNotifica.getTimestamp()));
                    holder.notificaTime.setText(formattedDate);

                    // Set click listener
//                    holder.itemView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            clickListener.onNotificaClick(currentNotifica);
//                        }
//                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(context, "Error retrieving user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Toast.makeText(context, "Notifaction clicked at  notifica.", Toast.LENGTH_SHORT).show();

                Intent i=new Intent(context,showPostActivity.class);
                i.putExtra("postId",currentNotifica.getPostId());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificaList.size();
    }
}
