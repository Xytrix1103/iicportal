package com.iicportal.adaptor;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.activity.MainActivity;
import com.iicportal.fragments.FeedbackDetailsDialogFragment;
import com.iicportal.models.Feedback;

import java.text.SimpleDateFormat;

public class FeedbackAdaptor extends FirebaseRecyclerAdapter<Feedback, FeedbackAdaptor.FeedbackViewHolder> {
    private Context context;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference usersRef, feedbackRef;

    FragmentManager childFragmentManager;

    private static final String FEEDBACK_ADAPTOR_TAG = "FeedbackAdaptor";

    public FeedbackAdaptor(FirebaseRecyclerOptions<Feedback> options, Context context, FragmentManager childFragmentManager) {
        super(options);
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.currentUser = mAuth.getCurrentUser();
        this.database = MainActivity.database;
        this.usersRef = database.getReference("users/");
        this.feedbackRef = database.getReference("feedback/");
        this.childFragmentManager = childFragmentManager;
    }

    @NonNull
    @Override
    public FeedbackAdaptor.FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_item, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position, Feedback model) {
        MainActivity.loadImage(model.getUserProfilePicture(), holder.userProfilePic, R.drawable.baseline_account_circle_24);
        holder.username.setText(model.getFullName());
        holder.date.setText(new SimpleDateFormat("dd MMM yyyy").format(model.getTimestamp()));
        holder.message.setText(model.getMessage());

        // Check if message is unread
        if (!model.isRead()) {
            holder.username.setTypeface(null, Typeface.BOLD);
            holder.date.setTypeface(null, Typeface.BOLD);
            holder.message.setTypeface(null, Typeface.BOLD);
        } else {
            holder.username.setTypeface(null, Typeface.NORMAL);
            holder.date.setTypeface(null, Typeface.NORMAL);
            holder.message.setTypeface(null, Typeface.NORMAL);
        }

        holder.feedbackBody.setOnClickListener(view -> {
            FeedbackDetailsDialogFragment feedbackDetailsDialogFragment = new FeedbackDetailsDialogFragment(model);
            feedbackDetailsDialogFragment.show(childFragmentManager, "FeedbackDetailsDialogFragment");

            // Change status to read
            if (!model.isRead()) {
                feedbackRef.child(this.getRef(position).getKey()).child("read").setValue(true);
                this.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        notifyDataSetChanged();
    }

    protected class FeedbackViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout feedbackBody;
        ImageView userProfilePic;
        TextView username;
        TextView date;
        TextView message;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            feedbackBody = itemView.findViewById(R.id.feedbackBody);
            userProfilePic = itemView.findViewById(R.id.userProfilePic);
            username = itemView.findViewById(R.id.usernameText);
            date = itemView.findViewById(R.id.dateText);
            message = itemView.findViewById(R.id.messageText);
        }
    }
}
