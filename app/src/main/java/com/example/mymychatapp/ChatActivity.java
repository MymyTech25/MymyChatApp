package com.example.mymychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.mymychatapp.adapter.ChatAdapter;
import com.example.mymychatapp.adapter.SearchUserAdapter;
import com.example.mymychatapp.model.ChatMessageModel;
import com.example.mymychatapp.model.ChatRoomModel;
import com.example.mymychatapp.model.UserModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;
    ChatRoomModel chatRoomModel;
    EditText chatInput;
    ImageButton backBtn, sendMessageBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    ChatAdapter adapter;

    String chatRoomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //get userModel
        otherUser = Utility.getUserFromIntent(getIntent());
        chatRoomId = Utility.getChatRoomId(Utility.currentUserId(),otherUser.getUserID());

        chatInput = findViewById(R.id.chat_input);
        backBtn = findViewById(R.id.backBtn);
        sendMessageBtn = findViewById(R.id.send_messageBtn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chatRV);

        backBtn.setOnClickListener((v)->{
            onBackPressed();
        });
        otherUsername.setText(otherUser.getUsername());

        sendMessageBtn.setOnClickListener((v -> {
            String message = chatInput.getText().toString().trim();
            if (message.isEmpty())
                return;
            sendMessageToUser(message);
        }));

        createChatRoomModel();
        setUpChatRV();

    }

    void setUpChatRV(){
        //To search by username in Firebase
        Query query = Utility.getChatRoomMessageRef(chatRoomId).orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>().setQuery(query, ChatMessageModel.class).build();

        adapter = new ChatAdapter(options,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });


    }
    void sendMessageToUser(String message){
        chatRoomModel.setLastMessageTimestamp(Timestamp.now());
        chatRoomModel.setLastMessageSenderId(Utility.currentUserId());
        Utility.getChatRoomRef(chatRoomId).set(chatRoomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message, Utility.currentUserId(), Timestamp.now());
        Utility.getChatRoomMessageRef(chatRoomId).add(chatMessageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()){
                    chatInput.setText("");
                }
            }
        });
    }
    void createChatRoomModel(){
        Utility.getChatRoomRef(chatRoomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                chatRoomModel = task.getResult().toObject(ChatRoomModel.class);
                if (chatRoomModel==null){
                    //first time chat
                    chatRoomModel = new ChatRoomModel(
                            chatRoomId, Arrays.asList(Utility.currentUserId(),otherUser.getUserID()),
                            Timestamp.now(),
                            ""
                    );
                    Utility.getChatRoomRef(chatRoomId).set(chatRoomModel);
                }
            }
        });

    }


}