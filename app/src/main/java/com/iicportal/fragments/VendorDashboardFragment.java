package com.iicportal.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iicportal.R;
import com.iicportal.activity.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class VendorDashboardFragment extends Fragment {
    AdminDashboardFragment.OpenDrawerInterface openDrawerInterface;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference ordersRef;

    TextView orderCountText;
    BarChart pastFiveDaysOrdersChart;

    static final String VENDOR_DASHBOARD_TAG = "VendorDashboardFragment";

    public VendorDashboardFragment() {
        super(R.layout.vendor_dashboard_fragment);
        this.openDrawerInterface = null;
        mAuth = MainActivity.mAuth;
        user = MainActivity.user;
        database = MainActivity.database;
        ordersRef = database.getReference("orders/");
    }

    public VendorDashboardFragment(AdminDashboardFragment.OpenDrawerInterface openDrawerInterface) {
        super(R.layout.vendor_dashboard_fragment);
        this.openDrawerInterface = openDrawerInterface;
        mAuth = MainActivity.mAuth;
        user = MainActivity.user;
        database = MainActivity.database;
        ordersRef = database.getReference("orders/");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vendor_dashboard_fragment, container, false);

        ImageView menuBtn = view.findViewById(R.id.menuIcon);
        orderCountText = view.findViewById(R.id.orderCountText);
        pastFiveDaysOrdersChart = view.findViewById(R.id.pastFiveDaysOrdersChart);

        // Get the dates of the past 5 school days
        int dayCount = 5;
        Date[] pastFiveDays = new Date[dayCount];
        Calendar calendar = Calendar.getInstance();

        while (dayCount > 0) {
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                pastFiveDays[dayCount - 1] = calendar.getTime();
                dayCount--;
            }

            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Set total order count
                orderCountText.setText(String.valueOf(snapshot.getChildrenCount()));

                // Get past 5 school day order data
                int dayOneOrders = 0, dayTwoOrders = 0, dayThreeOrders = 0, dayFourOrders = 0, dayFiveOrders = 0;

                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Long orderTimestamp = Long.parseLong(orderSnapshot.child("timestamp").getValue().toString());
                    Date orderDate = new Date(orderTimestamp);

                    if (orderDate.getDate() == pastFiveDays[4].getDate()) {
                        dayFiveOrders++;
                    } else if (orderDate.getDate() == pastFiveDays[3].getDate()) {
                        dayFourOrders++;
                    } else if (orderDate.getDate() == pastFiveDays[2].getDate()) {
                        dayThreeOrders++;
                    } else if (orderDate.getDate() == pastFiveDays[1].getDate()) {
                        dayTwoOrders++;
                    } else if (orderDate.getDate() == pastFiveDays[0].getDate()) {
                        dayOneOrders++;
                    }
                }

                // Create BarEntries for the bar chart
                ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
                entries.add(new BarEntry(0, dayOneOrders));
                entries.add(new BarEntry(1, dayTwoOrders));
                entries.add(new BarEntry(2, dayThreeOrders));
                entries.add(new BarEntry(3, dayFourOrders));
                entries.add(new BarEntry(4, dayFiveOrders));

                // Create BarDataSet
                BarDataSet dataSet = new BarDataSet(entries, "Total Orders");
                BarData barData = new BarData(dataSet);

                // Set data to the bar chart
                pastFiveDaysOrdersChart.getDescription().setText("");
                pastFiveDaysOrdersChart.setNoDataText("No data available");
                pastFiveDaysOrdersChart.setData(barData);
                pastFiveDaysOrdersChart.invalidate();

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM");
                ArrayList<String> dateStrings = new ArrayList<String>();
                dateStrings.add(dateFormat.format(pastFiveDays[0]));
                dateStrings.add(dateFormat.format(pastFiveDays[1]));
                dateStrings.add(dateFormat.format(pastFiveDays[2]));
                dateStrings.add(dateFormat.format(pastFiveDays[3]));
                dateStrings.add(dateFormat.format(pastFiveDays[4]));

                ValueFormatter formatter = new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return dateStrings.get((int) value);
                    }
                };

                // Set x-axis
                XAxis xAxis = pastFiveDaysOrdersChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setLabelCount(5);
                xAxis.setGranularity(1f);
                xAxis.setValueFormatter(formatter);
                // Set y-axis
                YAxis yAxis = pastFiveDaysOrdersChart.getAxisLeft();
                yAxis.setAxisMinimum(0f);
                yAxis.setDrawLabels(true);
                yAxis.setLabelCount(5);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(VENDOR_DASHBOARD_TAG, "loadOrders:onCancelled");
            }
        });

        // onClick listeners
        menuBtn.setOnClickListener(v -> openDrawerInterface.openDrawer());

        return view;
    }

    public interface OpenDrawerInterface {
        void openDrawer();
    }
}
