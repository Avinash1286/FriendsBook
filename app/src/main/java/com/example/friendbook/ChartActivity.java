package com.example.friendbook;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartActivity extends AppCompatActivity {


    Toolbar toolbar;
    Button sendTextMessage;
    ImageView sendImageM;
    EditText getMessage;
    RecyclerView recyclerView;
 final    List<Message> userMessage=new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    MessageAdaptor messageAdaptor;
    String messageRecieverName,messageReceiverId;
    CircularImageView profileImageInMessage;
    TextView username,userStatus;
    DatabaseReference databaseReference,rootRef;
    FirebaseAuth mAutMessage;
    String messageSenderId;
    String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        toolbar=(Toolbar)findViewById(R.id.chartTool);
        setSupportActionBar(toolbar);
        mAutMessage=FirebaseAuth.getInstance();
        messageSenderId=mAutMessage.getCurrentUser().getUid();
        sendTextMessage=(Button)findViewById(R.id.sendTextmessage);
        sendImageM=(ImageView)findViewById(R.id.sendImageMessage);
        getMessage=(EditText)findViewById(R.id.messageEditText);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
         rootRef=FirebaseDatabase.getInstance().getReference();
        recyclerView=(RecyclerView)findViewById(R.id.chartRecycle);
        messageRecieverName= getIntent().getExtras().get("friendName").toString();
        messageReceiverId=getIntent().getExtras().get("friendID").toString();
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionview=layoutInflater.inflate(R.layout.custom_action_bar,null);
        actionBar.setCustomView(actionview);
        messageAdaptor=new MessageAdaptor(userMessage);
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(messageAdaptor);
        DisplayFriendInfo();
        sendTextMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                getMessage.setText("");
            }
        });
        FetchMessage();
    }

    private void FetchMessage() {

   rootRef.child("Message").child(messageSenderId).child(messageReceiverId).addChildEventListener(new ChildEventListener() {
       @Override
       public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

           if (dataSnapshot.exists()){
               Message message=dataSnapshot.getValue(Message.class);
               userMessage.add(message);
               messageAdaptor.notifyDataSetChanged();
           }
       }

       @Override
       public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

       }

       @Override
       public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

       }

       @Override
       public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

       }

       @Override
       public void onCancelled(@NonNull DatabaseError databaseError) {

       }
   });

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
        databaseReference.child(messageSenderId).child("Userstatus").updateChildren(currentStatus);
    }

    private void sendMessage() {

        currentUserState("online");
        final String getMess=getMessage.getText().toString();
        if (TextUtils.isEmpty(getMess)){
            Toast.makeText(this, "Please enter a message first..", Toast.LENGTH_SHORT).show();
        }
        else {

            String message_sender_ref="Message/"+messageSenderId+"/"+messageReceiverId;
            String message_receiver_ref="Message/"+messageReceiverId+"/"+messageSenderId;
            DatabaseReference messageRef=rootRef.child("Message").child(messageSenderId).child(messageReceiverId).push();
            String getMessageKey=messageRef.getKey();
            key=getMessageKey;

            Calendar calendar=Calendar.getInstance();
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MMMM-yyyy");
            String date=simpleDateFormat.format(calendar.getTime());
            SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("HH:mm aa");
            String time=simpleDateFormat1.format(calendar.getTime());

            Map messageTextBody=new HashMap();
            messageTextBody.put("message",getMess);
            messageTextBody.put("time",time);
            messageTextBody.put("datee",date);
            messageTextBody.put("type","text");
            messageTextBody.put("from",messageSenderId);

            Map messageBodyDetails=new HashMap();
            messageBodyDetails.put(message_sender_ref+"/"+getMessageKey,messageTextBody);
            messageBodyDetails.put(message_receiver_ref+"/"+getMessageKey,messageTextBody);

            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
               if (task.isSuccessful()){
                   Toast.makeText(ChartActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                   getMessage.setText("");
               }
               else {
                   String error=task.getException().toString();
                   Toast.makeText(ChartActivity.this, "Message not sent :"+error, Toast.LENGTH_SHORT).show();
                   getMessage.setText("");
               }
                }
            });
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);

    }

    private void DisplayFriendInfo() {
        profileImageInMessage=(CircularImageView)findViewById(R.id.containProfile);
        username=(TextView)findViewById(R.id.containUserNameInmessage);
        username.setText(messageRecieverName);
        userStatus=(TextView)findViewById(R.id.userState);
        databaseReference.child(messageReceiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("Userstatus")){
                    String time=dataSnapshot.child("Userstatus").child("currentTime").getValue().toString();
                    String date=dataSnapshot.child("Userstatus").child("currentDate").getValue().toString();
                    String type=dataSnapshot.child("Userstatus").child("type").getValue().toString();
                    if (type.equals("online")){
                        userStatus.setText("online");
                    }
                    else {
                        userStatus.setText("last seen "+time+" "+date);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.child(messageReceiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    String getLink=dataSnapshot.child("profileImage").getValue().toString();
                    Picasso.with(ChartActivity.this).load(getLink).placeholder(R.drawable.profile).into(profileImageInMessage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentUserState("offline");
    }

    @Override
    protected void onStop() {
        super.onStop();
        currentUserState("offline");
    }
}
