package com.iicportal.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.adaptor.BookingHistoryAdaptor;
import com.iicportal.models.BookingItem;

public class BookingHistoryActivity extends AppCompatActivity {
    Context context = this;
    RecyclerView bookingRecyclerView;
    BookingHistoryAdaptor BookingHistoryAdaptor;

    FirebaseDatabase database;
    DatabaseReference bookingRef;

    FirebaseAuth mAuth;
    FirebaseUser user;

    TextView noBookingText;
    ImageView backBtnIcon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        bookingRef = database.getReference("bookings/");
        bookingRef.keepSynced(true);

        backBtnIcon = findViewById(R.id.backBtnIcon);
        backBtnIcon.setOnClickListener(v -> finish());

        bookingRecyclerView = findViewById(R.id.bookingHistoryRecyclerView);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        bookingRecyclerView.setLayoutManager(mLayoutManager);

        FirebaseRecyclerOptions<BookingItem> options = new FirebaseRecyclerOptions.Builder<BookingItem>()
                .setLifecycleOwner(this)
                .setQuery(bookingRef.orderByChild("userId").equalTo(user.getUid()), BookingItem.class)
                .build();

        BookingHistoryAdaptor = new BookingHistoryAdaptor(options, context);
        bookingRecyclerView.setAdapter(BookingHistoryAdaptor);

        noBookingText = findViewById(R.id.bookingHistoryEmptyText);

        BookingHistoryAdaptor.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (BookingHistoryAdaptor.getItemCount() == 0) {
                    noBookingText.setVisibility(View.VISIBLE);
                } else {
                    noBookingText.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        BookingHistoryAdaptor.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        BookingHistoryAdaptor.stopListening();
    }


}
