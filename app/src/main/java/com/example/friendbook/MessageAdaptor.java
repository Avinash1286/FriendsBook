package com.example.friendbook;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdaptor extends RecyclerView.Adapter<MessageAdaptor.MessageviewHolder> {
     List<Message> userMessageList;
    DatabaseReference getMessageRefere;
    FirebaseAuth mAuthMessage;
    public MessageAdaptor(List<Message> userMessageList) {
        this.userMessageList = userMessageList;

    }

    @NonNull
    @Override
    public MessageviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_stracture,viewGroup,false);
        mAuthMessage=FirebaseAuth.getInstance();
        return new MessageviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageviewHolder messageviewHolder, int i) {
         String messageSenderId=mAuthMessage.getCurrentUser().getUid();
         Message message=userMessageList.get(i);
         String fromUserId=message.getFrom();
         String fromtype=message.getType();
         getMessageRefere= FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);
         getMessageRefere.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if (dataSnapshot.exists()){
                   String profileLink=dataSnapshot.child("profileImage").getValue().toString();
                     Picasso.with(messageviewHolder.receiverProfileHolder.getContext()).load(profileLink).into(messageviewHolder.receiverProfileHolder);
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
         if (fromtype.equals("text")){
             messageviewHolder.receiverProfileHolder.setVisibility(View.INVISIBLE);
             messageviewHolder.receiverMessageHolder.setVisibility(View.INVISIBLE);

             if (fromUserId.equals(messageSenderId)){

                 messageviewHolder.senderMessageHolder.setBackgroundResource(R.drawable.sender_message_back);
                 messageviewHolder.senderMessageHolder.setTextColor(Color.BLACK);
                 messageviewHolder.senderMessageHolder.setGravity(Gravity.LEFT);
                 messageviewHolder.senderMessageHolder.setText(message.getMessage());

             }
             else {
                 messageviewHolder.senderMessageHolder.setVisibility(View.INVISIBLE);
                 messageviewHolder.receiverMessageHolder.setVisibility(View.VISIBLE);
                 messageviewHolder.receiverProfileHolder.setVisibility(View.VISIBLE);
                 messageviewHolder.receiverMessageHolder.setBackgroundResource(R.drawable.receiver_message_back);
                 messageviewHolder.receiverMessageHolder.setTextColor(Color.BLACK);
                 messageviewHolder.receiverMessageHolder.setGravity(Gravity.LEFT);
                 messageviewHolder.receiverMessageHolder.setText(message.getMessage());


             }
         }

    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }

    public class MessageviewHolder extends RecyclerView.ViewHolder{

        TextView receiverMessageHolder,senderMessageHolder;
        CircularImageView receiverProfileHolder;

        public MessageviewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMessageHolder=(TextView)itemView.findViewById(R.id.chartingReceiverText);
            senderMessageHolder=(TextView)itemView.findViewById(R.id.chartingSenderText);
            receiverProfileHolder=(CircularImageView)itemView.findViewById(R.id.chartingprofile);

        }
    }
}
