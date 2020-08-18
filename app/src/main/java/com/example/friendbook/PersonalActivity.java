package com.example.friendbook;

import android.support.annotation.NonNull;
import android.support.v4.app.INotificationSideChannel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

public class PersonalActivity extends AppCompatActivity {


    TextView profileDob,profileUserName,profileFullName,profileGender,profileRelation,profileCountry;
    TextView profileStatus;
    CircularImageView profileImage;
    FirebaseAuth mAuth;
    DatabaseReference userDataBase,Friendreref,listofFri;
    Button sendRe,decline;
    String currentUser,sender,Current_State;
     String cuDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        mAuth=FirebaseAuth.getInstance();
        sendRe=(Button)findViewById(R.id.sendfriend);
        sender=mAuth.getCurrentUser().getUid();
        decline=(Button)findViewById(R.id.declinfriend);
        currentUser=getIntent().getExtras().get("userID").toString();
        userDataBase= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);
        Friendreref=FirebaseDatabase.getInstance().getReference().child("FriendRef");
        listofFri=FirebaseDatabase.getInstance().getReference().child("FriendsList");
        profileImage=(CircularImageView)findViewById(R.id.personalImage);
        profileDob=(TextView)findViewById(R.id.personalDob);
        profileUserName=(TextView)findViewById(R.id.personalUserName);
        profileFullName=(TextView)findViewById(R.id.personalFullName);
        profileGender=(TextView)findViewById(R.id.personalgender);
        profileRelation=(TextView)findViewById(R.id.personalRelation);
        profileCountry=(TextView)findViewById(R.id.personalcountry);
        profileStatus=(TextView) findViewById(R.id.personalProfileStatus);
        Current_State="not_Friends";
       if (!currentUser.equals(sender)){

           sendRe.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if (Current_State.equals("not_Friends")){

                       Friendreref.child(sender).child(currentUser).child("request_type").setValue("sent")
                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {

                                       if (task.isSuccessful()){
                                           Friendreref.child(currentUser).child(sender).child("request_type").setValue("received")
                                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<Void> task) {
                                                           if (task.isSuccessful()){
                                                               Current_State="reqsent";
                                                                sendRe.setText("cancel friend request");
                                                                decline.setVisibility(View.INVISIBLE);

                                                           }


                                                       }
                                                   })      ;
                                       }


                                   }
                               });

                   }
                   if (Current_State.equals("reqsent")){
                       Friendreref.child(sender).child(currentUser).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                          if (task.isSuccessful()){
                              Friendreref.child(currentUser).child(sender).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                  @Override
                                  public void onComplete(@NonNull Task<Void> task) {
                                 if (task.isSuccessful()){
                                     Current_State="not_Friends";
                                     sendRe.setText("Send Friend Request");
                                 }
                                  }
                              });
                          }
                           }
                       });
                   }
                   if (Current_State.equals("req_rec")){
                     Calendar calendar=Calendar.getInstance();
                       SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MMMM-yyyy");
                       cuDate=simpleDateFormat.format(calendar.getTime());
                       listofFri.child(sender).child(currentUser).child("date").setValue(cuDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                          if (task.isSuccessful()){
                              listofFri.child(currentUser).child(sender).child("date").setValue(cuDate)
                                      .addOnCompleteListener(new OnCompleteListener<Void>() {
                                          @Override
                                          public void onComplete(@NonNull Task<Void> task) {
                                         if (task.isSuccessful()){
                                             Friendreref.child(sender).child(currentUser).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                 @Override
                                                 public void onComplete(@NonNull Task<Void> task) {
                                                     if (task.isSuccessful()){
                                                         Friendreref.child(currentUser).child(sender).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                             @Override
                                                             public void onComplete(@NonNull Task<Void> task) {
                                                                 if (task.isSuccessful()){
                                                                     Current_State="Friends";
                                                                     sendRe.setText("UnFriend This Person");
                                                                     decline.setVisibility(View.INVISIBLE);
                                                                 }
                                                             }
                                                         });
                                                     }
                                                 }
                                             });
                                         }
                                          }
                                      });
                          }
                           }
                       });
                   }
                   if (Current_State.equals("friends")){

                       listofFri.child(sender).child(currentUser).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if (task.isSuccessful()){
                                listofFri.child(currentUser).child(sender).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           if (task.isSuccessful()){
                                               Current_State="not_Friends";
                                               sendRe.setText("Send Friend Request");
                                           }
                                       }
                                   });
                               }
                           }
                       });

                   }
               }
           });


       }
       else {
           sendRe.setVisibility(View.INVISIBLE);
       }

        userDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String userN=dataSnapshot.child("UserName").getValue().toString();
                    String userFN=dataSnapshot.child("FullName").getValue().toString();
                    String userC=dataSnapshot.child("Country").getValue().toString();
                    String userDob=dataSnapshot.child("Dob").getValue().toString();
                    String userRSS=dataSnapshot.child("RelationShipStatus").getValue().toString();
                    String userG=dataSnapshot.child("Gender").getValue().toString();
                    String userPS=dataSnapshot.child("ProfileStatus").getValue().toString();
                    String userProfileImg=dataSnapshot.child("profileImage").getValue().toString();
                    profileDob.setText("Date of birth: "+userDob);
                    profileRelation.setText("RelationShip Status: "+userRSS);
                    profileGender.setText("Gender: "+userG);
                    profileStatus.setText(userPS);
                    profileUserName.setText("@"+userN);
                    profileFullName.setText(userFN);
                    profileCountry.setText("Country: "+userC);
                    Picasso.with(getApplicationContext()).load(userProfileImg).placeholder(R.drawable.profile).into(profileImage);
                    decline.setVisibility(View.INVISIBLE);
                    Friendreref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(sender).child(currentUser).exists()){
                                String state=dataSnapshot.child(sender).child(currentUser).child("request_type").getValue().toString();
                                if (state.equals("sent")){
                                    sendRe.setText("cancel friend request");
                                    Current_State="reqsent";
                                }
                                if (state.equals("received")){
                                    sendRe.setText("Accept Friend Request");
                                    decline.setVisibility(View.VISIBLE);
                                    Current_State="req_rec";
                                    decline.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Friendreref.child(sender).child(currentUser).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Friendreref.child(currentUser).child(sender).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    Current_State="not_Friends";
                                                                    sendRe.setText("Send Friend Request");
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }


                            }
                            else {

                                listofFri.child(sender).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.child(currentUser).exists()){
                                            Current_State="friends";
                                            sendRe.setText("UnFriend This Person");
                                            decline.setVisibility(View.INVISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



}
