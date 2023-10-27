package com.iicportal.adaptor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.activity.MainActivity;
import com.iicportal.fragments.AddToCartDialogFragment;
import com.iicportal.fragments.MessageDetailsDialogFragment;
import com.iicportal.models.Message;

import java.text.SimpleDateFormat;

public class MessageAdaptor extends FirebaseRecyclerAdapter<Message, MessageAdaptor.MessageViewHolder> {
    private Context context;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;

    FragmentManager childFragmentManager;

    private static final String MESSAGE_ADAPTOR_TAG = "MessageAdaptor";

    public MessageAdaptor(FirebaseRecyclerOptions<Message> options, Context context, FragmentManager childFragmentManager) {
        super(options);
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.currentUser = mAuth.getCurrentUser();
        this.database = MainActivity.database;
        this.usersRef = database.getReference("users/");
        this.childFragmentManager = childFragmentManager;
    }

    @NonNull
    @Override
    public MessageAdaptor.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position, Message model) {
        usersRef.child(model.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // TODO: String userProfilePic = ?

                // TODO: Glide.with(context).load()
                holder.username.setText(model.getFullName());
                holder.date.setText(new SimpleDateFormat("dd MMM yyyy").format(model.getTimestamp()));
                holder.message.setText(model.getMessage());

                holder.messageBody.setOnClickListener(view -> {
                    MessageDetailsDialogFragment messageDetailsDialogFragment = new MessageDetailsDialogFragment(model);
                    messageDetailsDialogFragment.show(childFragmentManager, "MessageDetailsDialogFragment");
                });
            } else {
                Log.e(MESSAGE_ADAPTOR_TAG, "Error getting user data", task.getException());
            }
        });
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        notifyDataSetChanged();
    }

    protected class MessageViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout messageBody;
        ImageView userProfilePic;
        TextView username;
        TextView date;
        TextView message;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageBody = itemView.findViewById(R.id.messageBody);
            userProfilePic = itemView.findViewById(R.id.userProfilePic);
            username = itemView.findViewById(R.id.usernameText);
            date = itemView.findViewById(R.id.dateText);
            message = itemView.findViewById(R.id.messageText);
        }
    }
}
