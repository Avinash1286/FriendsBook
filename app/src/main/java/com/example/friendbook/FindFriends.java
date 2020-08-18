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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class FindFriends extends AppCompatActivity {

    Toolbar findT;
    EditText search;
    Button searchImage;
    RecyclerView searchLists;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
       userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        findT=(Toolbar)findViewById(R.id.findTool);
        setSupportActionBar(findT);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        search=(EditText)findViewById(R.id.searchText);
        searchImage=(Button) findViewById(R.id.startSearch);
        searchLists=(RecyclerView)findViewById(R.id.searchList);
        searchLists.setHasFixedSize(true);
        searchLists.setLayoutManager(new LinearLayoutManager(this));

        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getSearchText=search.getText().toString();
                SearchPeopleAndFrienda(getSearchText);
            }
        });

    }

    private void SearchPeopleAndFrienda(String getSearchText) {
        Toast.makeText(this, "Searching...", Toast.LENGTH_LONG).show();
        Query searchQuery=userRef.orderByChild("FullName").startAt(getSearchText).endAt(getSearchText+"\uf8ff");
        FirebaseRecyclerAdapter<SearchModel,FriendsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<SearchModel, FriendsViewHolder>
                (SearchModel.class,R.layout.search_them,FriendsViewHolder.class,searchQuery)
        {
            @Override
            protected void populateViewHolder(FriendsViewHolder viewHolder, SearchModel model,final int position) {

                viewHolder.setFullName(model.getFullName());
                viewHolder.setProfileStatus(model.getProfileStatus());
                viewHolder.setProfileImage(getApplicationContext(),model.getProfileImage());

               viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       String userId=getRef(position).getKey();
                       Intent intent=new Intent(FindFriends.this,PersonalActivity.class);
                       intent.putExtra("userID",userId);
                       startActivity(intent);

                   }
               });

            }
        };

        searchLists.setAdapter(firebaseRecyclerAdapter);
    }
    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setProfileStatus(String profileStatus) {
            TextView setStatus=(TextView)mView.findViewById(R.id.searchStatus);
            setStatus.setText(profileStatus);
        }
        public void setFullName(String fullName) {
         TextView setName=(TextView)mView.findViewById(R.id.searchusername);
         setName.setText(fullName);
        }

        public void setProfileImage(Context context,String profileImage) {
            CircularImageView userProImage=(CircularImageView)mView.findViewById(R.id.searchImage);
            Picasso.with(context).load(profileImage).placeholder(R.drawable.profile).into(userProImage);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
