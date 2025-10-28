package by.story_weaver.ridereserve.ui.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import by.story_weaver.ridereserve.Logic.adapters.TripsAdapter;
import by.story_weaver.ridereserve.Logic.data.models.AdminStats;
import by.story_weaver.ridereserve.Logic.data.models.Trip;
import by.story_weaver.ridereserve.Logic.viewModels.AdminViewModel;
import by.story_weaver.ridereserve.R;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AdminDashboardFragment extends Fragment {

    private AdminViewModel adminViewModel;
    private TripsAdapter tripsAdapter;

    private TextView tvTotalUsers, tvTotalRoutes, tvTotalTrips, tvTotalBookings, tvTotalVehicles, tvTotalDrivers;
    private TextView tvActiveTripsCount, tvScheduledTripsCount, tvConfirmedBookingsCount, tvCancelledBookingsCount;
    private RecyclerView rvActiveTrips;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        initViews(view);
        setupRecyclerView();
        observeData();
        loadData();
    }

    private void initViews(View view) {
        tvTotalUsers = view.findViewById(R.id.tvTotalUsers);
        tvTotalRoutes = view.findViewById(R.id.tvTotalRoutes);
        tvTotalTrips = view.findViewById(R.id.tvTotalTrips);
        tvTotalBookings = view.findViewById(R.id.tvTotalBookings);
        tvTotalVehicles = view.findViewById(R.id.tvTotalVehicles);
        tvTotalDrivers = view.findViewById(R.id.tvTotalDrivers);

        tvActiveTripsCount = view.findViewById(R.id.tvActiveTripsCount);
        tvScheduledTripsCount = view.findViewById(R.id.tvScheduledTripsCount);
        tvConfirmedBookingsCount = view.findViewById(R.id.tvConfirmedBookingsCount);
        tvCancelledBookingsCount = view.findViewById(R.id.tvCancelledBookingsCount);

        rvActiveTrips = view.findViewById(R.id.rvActiveTrips);
    }

    private void setupRecyclerView() {
        tripsAdapter = new TripsAdapter(new ArrayList<>(), this::onTripClick, true);
        rvActiveTrips.setLayoutManager(new LinearLayoutManager(getContext()));
        rvActiveTrips.setAdapter(tripsAdapter);
    }

    private void observeData() {
        adminViewModel.getAdminStats().observe(getViewLifecycleOwner(), this::updateStatistics);
        adminViewModel.getActiveTrips().observe(getViewLifecycleOwner(), this::updateTrips);
    }

    private void loadData() {
        adminViewModel.loadAdminData();
    }

    private void updateStatistics(AdminStats stats) {
        if (stats == null) return;

        tvTotalUsers.setText(String.valueOf(stats.totalUsers));
        tvTotalRoutes.setText(String.valueOf(stats.totalRoutes));
        tvTotalTrips.setText(String.valueOf(stats.totalTrips));
        tvTotalBookings.setText(String.valueOf(stats.totalBookings));
        tvTotalVehicles.setText(String.valueOf(stats.totalVehicles));
        tvTotalDrivers.setText(String.valueOf(stats.totalDrivers));

        tvActiveTripsCount.setText(String.valueOf(stats.activeTrips));
        tvScheduledTripsCount.setText(String.valueOf(stats.scheduledTrips));
        tvConfirmedBookingsCount.setText(String.valueOf(stats.confirmedBookings));
        tvCancelledBookingsCount.setText(String.valueOf(stats.cancelledBookings));
    }

    private void updateTrips(List<Trip> trips) {
        if (trips != null) {
            tripsAdapter.updateTrips(trips);
        }
    }

    private void onTripClick(Trip trip) {

    }

    @Override
    public void onResume() {
        super.onResume();
        adminViewModel.loadAdminData();
    }
}