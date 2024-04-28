package com.example.mymychatapp;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.mymychatapp.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Utility {

   public static void showToast(Context context, String message){
       Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

    }
    public static DocumentReference currentUserDetails(){
       return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    public static String currentUserId(){
       return FirebaseAuth.getInstance().getUid();
    }

    public static boolean alreadyLoggedIn(){
       if (currentUserId()!=null){
           return true;
       }
       return false;
    }

    public static CollectionReference allUserCollectionReference(){
       return FirebaseFirestore.getInstance().collection("users");
    }

    public static void passUserModel(Intent intent, UserModel model ){
       intent.putExtra("username", model.getUsername());
       intent.putExtra("phone", model.getPhone());
       intent.putExtra("userID", model.getUserID());
    }

    public  static UserModel getUserFromIntent(Intent intent){
       UserModel userModel = new UserModel();
       userModel.setUsername(intent.getStringExtra("username"));
       userModel.setPhone(intent.getStringExtra("phone"));
       userModel.setUserID(intent.getStringExtra("userID"));
       return userModel;
    }

    public static DocumentReference getChatRoomRef(String chatRoomId){
       return FirebaseFirestore.getInstance().collection("chatrooms").document(chatRoomId);
    }

    public static String getChatRoomId(String userID1, String userID2){
       if (userID1.hashCode()<userID2.hashCode()){
           return userID1+"_"+userID2;

       } else {
           return userID2+"_"+userID1;
       }
    }

    public static CollectionReference getChatRoomMessageRef(String chatRoomId){
       return getChatRoomRef(chatRoomId).collection("chats");
    }
}
