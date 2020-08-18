package com.example.friendbook;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class MyPostActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    Toolbar toolbar;
    DatabaseReference databaseReference,PostRef,likeRef;
    FirebaseAuth mAuth;
    Boolean likeChecker=false;
    String CurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);
        toolbar=(Toolbar)findViewById(R.id.mypost);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView=(RecyclerView)findViewById(R.id.mypostRecycle);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        mAuth=FirebaseAuth.getInstance();
        CurrentUser=mAuth.getCurrentUser().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        PostRef=FirebaseDatabase.getInstance().getReference().child("Posts");
        likeRef=FirebaseDatabase.getInstance().getReference().child("Likes");
          DisplayAllPost();

    }

    private void DisplayAllPost() {

        Query DecendingStoragePost=PostRef.orderByChild("UserId").startAt(CurrentUser).endAt(CurrentUser+"\uf8ff");

        FirebaseRecyclerAdapter<PostModelClass, MainActivity.PostViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<PostModelClass, MainActivity.PostViewHolder>
                (
                        PostModelClass.class,
                        R.layout.all_post_layout,
                        MainActivity.PostViewHolder.class,
                        DecendingStoragePost
                ) {
            @Override
            protected void populateViewHolder(MainActivity.PostViewHolder viewHolder, PostModelClass model, int position) {

                final  String postkey=getRef(position).getKey();

                viewHolder.setFullName(model.getFullName());
                viewHolder.setProfileImg(getApplicationContext(),model.getProfileImg());
                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setPostImg(getApplicationContext(),model.getPostImg());
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(MyPostActivity.this,ClickActivity.class);
                        intent.putExtra("PostKey",postkey);
                        startActivity(intent);
                    }
                });
                viewHolder.comments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(MyPostActivity.this,CommentActivity.class);
                        intent.putExtra("PostKey",postkey);
                        startActivity(intent);
                    }
                });
                viewHolder.setLIkeButtonStatus(postkey);
                viewHolder.likeB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        likeChecker=true;
                        likeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (likeChecker.equals(true)){
                                    if (dataSnapshot.child(postkey).hasChild(CurrentUser)){
                                        likeRef.child(postkey).child(CurrentUser).removeValue();
                                        likeChecker=false;
                                    }
                                    else {
                                        likeRef.child(postkey).child(CurrentUser).setValue(true);
                                        likeChecker=false;

                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });


            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }
    public static class PostViewHolder extends RecyclerView.ViewHolder
    {
        View view;

        ImageView likeB,comments;
        TextView likeC;
        int likeCount;
        DatabaseReference likeRef;
        String currentUser;


        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            likeB=(ImageView) view.findViewById(R.id.likeButton);
            likeC=(TextView)view.findViewById(R.id.likeCounter);
            comments=(ImageView)view.findViewById(R.id.commentButton);
            likeRef=FirebaseDatabase.getInstance().getReference().child("Likes");
            currentUser=FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        public void setLIkeButtonStatus(final String postkey){

            likeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(postkey).hasChild(currentUser)){
                        likeCount=(int)dataSnapshot.child(postkey).getChildrenCount();
                        likeB.setImageResource(R.drawable.like);
                        likeC.setText((Integer.toString(likeCount))+" Likes");

                    }
                    else {
                        likeCount=(int)dataSnapshot.child(postkey).getChildrenCount();
                        likeB.setImageResource(R.drawable.dislike);
                        likeC.setText((Integer.toString(likeCount))+" Likes");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        public void setFullName(String fullName) {
            TextView setName=(TextView)view.findViewById(R.id.user_post_name);
            setName.setText(fullName);
        }
        public void setPostImg(Context context, String postImg) {
            ImageView setImg=(ImageView) view.findViewById(R.id.user_post_image);
            Picasso.with(context).load(postImg).into(setImg);
        }


        public void setDescription(String description) {
            TextView setDes=(TextView)view.findViewById(R.id.user_post_des);
            setDes.setText(description);
        }

        public void setProfileImg(Context context,String profileImg) {
            CircularImageView setProfile=(CircularImageView)view.findViewById(R.id.user_post_profile);
            Picasso.with(context).load(profileImg).into(setProfile);
        }

        public void setTime(String time) {
            TextView setTime=(TextView)view.findViewById(R.id.user_post_time);
            setTime.setText(time);
        }

        public void setDate(String date) {
            TextView setDate=(TextView)view.findViewById(R.id.user_post_date);
            setDate.setText(date);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
