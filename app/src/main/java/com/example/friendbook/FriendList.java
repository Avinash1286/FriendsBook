package com.example.friendbook;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FriendList extends AppCompatActivity {

    FirebaseAuth mAuths;
    DatabaseReference friendRef, userRef;
    String onLineUser;
    RecyclerView friendlistRecycle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        mAuths = FirebaseAuth.getInstance();
        onLineUser = mAuths.getCurrentUser().getUid();
        friendRef = FirebaseDatabase.getInstance().getReference().child("FriendsList").child(onLineUser);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        friendlistRecycle = (RecyclerView) findViewById(R.id.showfriends);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        friendlistRecycle.setLayoutManager(layoutManager);

        DisplayAllFriends();


    }


    private void DisplayAllFriends() {

        FirebaseRecyclerAdapter<FriendListModel,FriendsListViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<FriendListModel, FriendsListViewHolder>
                (FriendListModel.class,R.layout.search_them,FriendsListViewHolder.class,friendRef) {
            @Override
            protected void populateViewHolder(final FriendsListViewHolder viewHolder, FriendListModel model, int position) {
                final String usersId=getRef(position).getKey();
                userRef.child(usersId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   if (dataSnapshot.exists()){
                       final String userName=dataSnapshot.child("FullName").getValue().toString();
                       String proImgae=dataSnapshot.child("profileImage").getValue().toString();
                       viewHolder.setFullName(userName);
                       viewHolder.setProfileImage(getApplicationContext(),proImgae);
                       String state;
                       if (dataSnapshot.hasChild("Userstatus")){
                           state=dataSnapshot.child("Userstatus").child("type").getValue().toString();
                           if (state.equals("online")){
                               viewHolder.stateMentioned.setVisibility(View.VISIBLE);
                           }
                           else {
                               viewHolder.stateMentioned.setVisibility(View.INVISIBLE);
                           }
                       }
                       viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               CharSequence option[]=new CharSequence[]{
                                "View Profile",
                                "Send Message"
                               };
                               AlertDialog.Builder builder=new AlertDialog.Builder(FriendList.this);
                               builder.setTitle("Select Option");
                               builder.setItems(option, new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {

                                       if (which==0){

                                           Intent intent=new Intent(FriendList.this,PersonalActivity.class);
                                           intent.putExtra("userID",usersId);
                                           startActivity(intent);
                                       }
                                       if (which==1){
                                           Intent intent=new Intent(FriendList.this,ChartActivity.class);
                                           intent.putExtra("friendID",usersId);
                                           intent.putExtra("friendName",userName);
                                           startActivity(intent);

                                       }

                                   }
                               });
                               builder.show();
                           }
                       });
                   }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                viewHolder.setDate(model.getDate());

            }
        };
        friendlistRecycle.setAdapter(firebaseRecyclerAdapter);


    }


    public static class FriendsListViewHolder extends RecyclerView.ViewHolder {

            View mView;
           ImageView stateMentioned;
            public FriendsListViewHolder(@NonNull View itemView) {
                super(itemView);
                mView = itemView;
                 stateMentioned=(ImageView)itemView.findViewById(R.id.userStateIcon);
            }


            public void setFullName(String fullName) {
                TextView setName = (TextView) mView.findViewById(R.id.searchusername);
                setName.setText(fullName);
            }

            public void setProfileImage(Context context, String profileImage) {
                CircularImageView userProImage = (CircularImageView) mView.findViewById(R.id.searchImage);
                Picasso.with(context).load(profileImage).placeholder(R.drawable.profile).into(userProImage);
            }

            public void setDate(String date) {
                TextView setStatus = (TextView) mView.findViewById(R.id.searchStatus);
                setStatus.setText("Friend Since " + date);
            }


        }
    public void currentUserState(String status) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM-yyy");
        String currentDate = simpleDateFormat.format(calendar.getTime());
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm a");
        String currentTime = simpleTimeFormat.format(calendar.getTime());
        Map currentStatus = new HashMap();
        currentStatus.put("currentDate", currentDate);
        currentStatus.put("currentTime", currentTime);
        currentStatus.put("type", status);
        userRef.child(onLineUser).child("Userstatus").updateChildren(currentStatus);
    }

    @Override
    protected void onStop() {
        super.onStop();
        currentUserState("offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentUserState("offline");
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUserState("online");
    }
}
