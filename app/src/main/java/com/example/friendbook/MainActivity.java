package com.example.friendbook;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference,PostRef,likeRef;
    CircularImageView navProfileImage;
    RecyclerView allUserPost;
    TextView navUserName;
    String CurrentUser;
    ImageButton addPost;
    Boolean likeChecker=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        allUserPost=(RecyclerView)findViewById(R.id.all_user_post_list);
        allUserPost.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        allUserPost.setLayoutManager(layoutManager);
        addPost=(ImageButton)findViewById(R.id.addnewPost);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        PostRef=FirebaseDatabase.getInstance().getReference().child("Posts");
        likeRef=FirebaseDatabase.getInstance().getReference().child("Likes");
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer);
        navigationView=(NavigationView)findViewById(R.id.navigation);
        mAuth=FirebaseAuth.getInstance();
        CurrentUser=mAuth.getCurrentUser().getUid();
        toolbar=(Toolbar)findViewById(R.id.maaintool);
        View view=navigationView.inflateHeaderView(R.layout.nav_header);
        navProfileImage=(CircularImageView)view.findViewById(R.id.navuserprofile);
        navUserName=(TextView)view.findViewById(R.id.contain_username);
        navigationView.getItemIconTintList();
        ActionBarDrawerToggle actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,PostActivity.class));


            }
        });

        DisplayAllPost();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    if(dataSnapshot.child(CurrentUser).hasChild("profileImage")){
                        String navpro=dataSnapshot.child(CurrentUser).child("profileImage").getValue().toString();
                        Picasso.with(MainActivity.this).load(navpro).placeholder(R.drawable.profile).into(navProfileImage);
                    }

                    if(dataSnapshot.child(CurrentUser).hasChild("FullName")){
                        String navUserN=dataSnapshot.child(CurrentUser).child("FullName").getValue().toString();
                        navUserName.setText(navUserN);
                    }
                    else {
                        Toast.makeText(MainActivity.this, "UserName Does not exist..", Toast.LENGTH_SHORT).show();
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){

                    case R.id.add_post:
                        startActivity(new Intent(MainActivity.this,PostActivity.class));
                        checkDrawer();
                        break;

                    case R.id.profile:
                        startActivity(new Intent(MainActivity.this,ProfileActivity.class));
                        checkDrawer();
                        break;
                    case R.id.home:
                        Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        checkDrawer();
                        break;
                    case R.id.friends:
                        startActivity(new Intent(MainActivity.this,FriendList.class));
                        checkDrawer();
                        break;
                    case R.id.find:
                        startActivity(new Intent(MainActivity.this,FindFriends.class));
                        checkDrawer();
                        break;
                    case R.id.mess:
                        startActivity(new Intent(MainActivity.this,FriendList.class));
                        checkDrawer();
                        break;
                    case R.id.set:
                        startActivity(new Intent(MainActivity.this,ActivitySetting.class));
                        checkDrawer();
                        break;
                    case R.id.logout:
                        mAuth.signOut();
                        currentUserState("offline");
                        startActivity(new Intent(MainActivity.this,LogIn.class));
                        checkDrawer();
                        break;

                }

                return true;
            }
        });

    }


    private void DisplayAllPost() {

        Query DecendingStoragePost=PostRef.orderByChild("counter");

        FirebaseRecyclerAdapter<PostModelClass,PostViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<PostModelClass, PostViewHolder>
                (
                        PostModelClass.class,
                        R.layout.all_post_layout,
                        PostViewHolder.class,
                        DecendingStoragePost
                ) {
            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, PostModelClass model, int position) {

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
                        Intent intent=new Intent(MainActivity.this,ClickActivity.class);
                        intent.putExtra("PostKey",postkey);
                        startActivity(intent);
                    }
                });
                viewHolder.comments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(MainActivity.this,CommentActivity.class);
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
        allUserPost.setAdapter(firebaseRecyclerAdapter);
        currentUserState("online");

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
        public void setPostImg(Context context,String postImg) {
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


    public void checkDrawer(){

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onStart() {

        FirebaseUser currentuser=mAuth.getCurrentUser();
        if(currentuser==null){
            startActivity(new Intent(MainActivity.this,LogIn.class));
            finish();
        }
        else {
            CheckUserExistance();
        }

        super.onStart();
    }

    public void currentUserState(String status){
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MMMM-yyy");
        String currentDate=simpleDateFormat.format(calendar.getTime());
        SimpleDateFormat simpleTimeFormat=new SimpleDateFormat("hh:mm a");
        String currentTime=simpleTimeFormat.format(calendar.getTime());
        Map currentStatus=new HashMap();
        currentStatus.put("currentDate",currentDate);
        currentStatus.put("currentTime",currentTime);
        currentStatus.put("type",status);
        databaseReference.child(CurrentUser).child("Userstatus").updateChildren(currentStatus);


    }

    private void CheckUserExistance() {

        final String current_user_id=mAuth.getCurrentUser().getUid();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(current_user_id)){
                    SendToSetUpAct();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void SendToSetUpAct() {
        startActivity(new Intent(MainActivity.this,UserInfo.class));
    }
}
