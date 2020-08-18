package com.example.friendbook;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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


public class CommentActivity extends AppCompatActivity {


    RecyclerView commRecycle;
    EditText commText;
    ImageView sendComment;
    String postkey,currentUsers,currentTime,currentDate,randomValue;
    DatabaseReference postRef,userRef;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        postkey=getIntent().getExtras().get("PostKey").toString();
        mAuth=FirebaseAuth.getInstance();
        currentUsers=mAuth.getCurrentUser().getUid();
        postRef= FirebaseDatabase.getInstance().getReference().child("Posts").child(postkey).child("Comments");
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");
        commRecycle=(RecyclerView)findViewById(R.id.commentRecycle);
        commText=(EditText)findViewById(R.id.commentText);
        sendComment=(ImageView)findViewById(R.id.postComment);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        commRecycle.setLayoutManager(layoutManager);



        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRef.child(currentUsers).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String fullname=dataSnapshot.child("FullName").getValue().toString();
                        String propic=dataSnapshot.child("profileImage").getValue().toString();
                        ValidationComment(fullname,propic);
                        commText.setText("");
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    private void ValidationComment(String fullname,String propic) {

        String commentText=commText.getText().toString();
        if (commentText.isEmpty()){
            Toast.makeText(this, "Please enter a comment...", Toast.LENGTH_SHORT).show();
            return;
        }

        else {


            Calendar getDate=Calendar.getInstance();
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("ddd-MMMM-yyyy");
            currentDate=simpleDateFormat.format(getDate.getTime());

            Calendar getTimes=Calendar.getInstance();
            SimpleDateFormat simpleDateTime=new SimpleDateFormat("HH:mm:ss");
            currentTime=simpleDateTime.format(getTimes.getTime());
            randomValue=currentUsers+currentDate+currentTime;
            HashMap putPostInfo=new HashMap();
            putPostInfo.put("UserId",currentUsers);
            putPostInfo.put("Date",currentDate);
            putPostInfo.put("Time",currentTime);
            putPostInfo.put("FullName",fullname);
            putPostInfo.put("ProfileImg",propic);
            putPostInfo.put("CommentText",commentText);
            postRef.child(randomValue).updateChildren(putPostInfo).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful()){
                        Toast.makeText(CommentActivity.this, "You Commented Successfully", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String mess=task.getException().getMessage();
                        Toast.makeText(CommentActivity.this, "Error: "+mess, Toast.LENGTH_SHORT).show();
                    }

                }
            });


        }



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<CommentModel,CommentViewHolder> firebaseRecyclerAdapter=new
                FirebaseRecyclerAdapter<CommentModel, CommentViewHolder>(CommentModel.class,R.layout.comment_list,CommentViewHolder.class,postRef) {
                    @Override
                    protected void populateViewHolder(CommentViewHolder viewHolder, CommentModel model, int position) {

                        viewHolder.setProfileImg(getApplicationContext(),model.ProfileImg);
                        viewHolder.setDate(model.getDate());
                        viewHolder.setCommentText(model.getCommentText());
                        viewHolder.setFullName(model.getFullName());
                        viewHolder.setTime(model.getTime());


                    }
                };
        commRecycle.setAdapter(firebaseRecyclerAdapter);
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setDate(String date) {
            TextView setD=(TextView)mView.findViewById(R.id.dateOfComment);
            setD.setText(date);
        }

        public void setTime(String time) {
            TextView setT=(TextView)mView.findViewById(R.id.timeofcomment);
            setT.setText(time);
        }

        public void setFullName(String fullName) {
            TextView setN=(TextView)mView.findViewById(R.id.usernameOfComment);
              setN.setText(fullName);
        }

        public void setProfileImg(Context context,String profileImg) {

            CircularImageView commentPro=(CircularImageView)mView.findViewById(R.id.profilepiconcomment);
            Picasso.with(context).load(profileImg).placeholder(R.drawable.profile).into(commentPro);


        }

        public void setCommentText(String commentText) {

            TextView showComment=(TextView)mView.findViewById(R.id.commentTextoncomment);
            showComment.setText(commentText);
        }
    }
}
