package by.story_weaver.ridereserve.ui.fragments.driver;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import by.story_weaver.ridereserve.Logic.adapters.DriverTripsAdapter;
import by.story_weaver.ridereserve.Logic.data.enums.TripStatus;
import by.story_weaver.ridereserve.Logic.data.models.Booking;
import by.story_weaver.ridereserve.Logic.data.models.Route;
import by.story_weaver.ridereserve.Logic.data.models.Trip;
import by.story_weaver.ridereserve.Logic.data.models.User;
import by.story_weaver.ridereserve.Logic.data.models.Vehicle;
import by.story_weaver.ridereserve.Logic.viewModels.BookingViewModel;
import by.story_weaver.ridereserve.Logic.viewModels.MainViewModel;
import by.story_weaver.ridereserve.Logic.viewModels.ProfileViewModel;
import by.story_weaver.ridereserve.R;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DriverTripFragment extends Fragment implements DriverTripsAdapter.OnTripClickListener {

    private BookingViewModel bookingViewModel;
    private ProfileViewModel profileViewModel;
    private MainViewModel mainViewModel;

    private DriverTripsAdapter adapter;
    private RecyclerView recyclerView;
    private TextInputEditText etSearch;

    private TextView tvTotalTrips, tvUpcomingTrips, tvCompletedTrips;
    private final Handler dataHandler = new Handler(Looper.getMainLooper());
    private Runnable dataLoadRunnable;

    private List<Trip> originalTrips = new ArrayList<>();
    private List<Route> originalRoutes = new ArrayList<>();
    private List<Vehicle> originalVehicles = new ArrayList<>();
    private List<Booking> originalBookings = new ArrayList<>();
    private List<Trip> filteredTrips = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_driver_trip, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookingViewModel = new ViewModelProvider(requireActivity()).get(BookingViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        initViews(view);
        setupRecyclerView();
        setupClickListeners();

        loadDriverTrips();
        bookingViewModel.getIsSomethingChanged().observe(getViewLifecycleOwner(), flag -> {
            scheduleDataLoading();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        scheduleDataLoading();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rvTrips);
        etSearch = view.findViewById(R.id.etSearch);
        tvTotalTrips = view.findViewById(R.id.tvTotalTrips);
        tvUpcomingTrips = view.findViewById(R.id.tvUpcomingTrips);
        tvCompletedTrips = view.findViewById(R.id.tvCompletedTrips);
    }

    private void setupRecyclerView() {
        adapter = new DriverTripsAdapter(
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                this
        );
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                searchTrips(s.toString());
            }
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            String query = etSearch.getText().toString();
            searchTrips(query);
            return true;
        });
    }

    private void scheduleDataLoading() {
        if (dataLoadRunnable != null) {
            dataHandler.removeCallbacks(dataLoadRunnable);
        }

        dataLoadRunnable = this::loadDriverTrips;

        dataHandler.postDelayed(dataLoadRunnable, 500);
    }

    private void loadDriverTrips() {
        User currentUser = profileViewModel.getProfile();
        if (currentUser != null) {
            List<Trip> allTrips = bookingViewModel.getTriplist();
            List<Trip> driverTrips = filterTripsForDriver(allTrips, currentUser.getId());

            List<Route> routes = bookingViewModel.getRoutelist();
            List<Vehicle> vehicles = bookingViewModel.getVehiclelist();
            List<Booking> bookings = bookingViewModel.getBookinglist();

            originalTrips = new ArrayList<>(driverTrips);
            originalRoutes = new ArrayList<>(routes);
            originalVehicles = new ArrayList<>(vehicles);
            originalBookings = new ArrayList<>(bookings);
            filteredTrips = new ArrayList<>(driverTrips);
            Collections.reverse(filteredTrips);

            adapter.updateAllData(filteredTrips, routes, vehicles, bookings);

            updateStatistics(filteredTrips);
        }
    }

    private List<Trip> filterTripsForDriver(List<Trip> allTrips, long driverId) {
        List<Trip> driverTrips = new ArrayList<>();
        for (Trip trip : allTrips) {
            if (trip.getDriverId() == driverId) {
                driverTrips.add(trip);
            }
        }
        return driverTrips;
    }

    private void updateStatistics(List<Trip> trips) {
        if (trips == null) return;

        int total = trips.size();
        int upcoming = 0;
        int completed = 0;

        for (Trip trip : trips) {
            if (trip.getStatus() == TripStatus.SCHEDULED || trip.getStatus() == TripStatus.IN_PROGRESS) {
                upcoming++;
            } else if (trip.getStatus() == TripStatus.COMPLETED) {
                completed++;
            }
        }

        tvTotalTrips.setText(String.valueOf(total));
        tvUpcomingTrips.setText(String.valueOf(upcoming));
        tvCompletedTrips.setText(String.valueOf(completed));
    }

    private void searchTrips(String searchText) {
        if (searchText.isEmpty()) {
            filteredTrips = new ArrayList<>(originalTrips);
        } else {
            filteredTrips = new ArrayList<>();
            String lowerCaseQuery = searchText.toLowerCase().trim();

            for (Trip trip : originalTrips) {
                Route route = findRouteById(trip.getRouteId());
                if (route != null) {
                    if (containsIgnoreCase(route.getName(), lowerCaseQuery) ||
                            containsIgnoreCase(route.getOrigin(), lowerCaseQuery) ||
                            containsIgnoreCase(route.getDestination(), lowerCaseQuery) ||
                            containsIgnoreCase(String.valueOf(trip.getId()), lowerCaseQuery) ||
                            containsIgnoreCase(trip.getDepartureTime(), lowerCaseQuery)) {
                        filteredTrips.add(trip);
                    }
                } else {
                    if (containsIgnoreCase(String.valueOf(trip.getId()), lowerCaseQuery) ||
                            containsIgnoreCase(trip.getDepartureTime(), lowerCaseQuery)) {
                        filteredTrips.add(trip);
                    }
                }
            }
        }
        adapter.updateTrips(filteredTrips);
        updateStatistics(filteredTrips);
    }

    private boolean containsIgnoreCase(String source, String query) {
        if (source == null || query == null) return false;
        return source.toLowerCase().contains(query);
    }

    private Route findRouteById(long routeId) {
        for (Route route : originalRoutes) {
            if (route.getId() == routeId) {
                return route;
            }
        }
        return null;
    }

    @Override
    public void onTripDetailsClick(Trip trip) {
        openTripDetails(trip);
    }

    @Override
    public void onTripManageClick(Trip trip) {
        manageTrip(trip);
    }

    @Override
    public void onTripClick(Trip trip) {
        openTripDetails(trip);
    }

    private void openTripDetails(Trip trip) {
        TripDetailFragment fragment = TripDetailFragment.newInstance(trip.getId());
        mainViewModel.openFullscreen(fragment);
    }

    private void manageTrip(Trip trip) {
        // TODO: Реализовать управление поездкой
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dataLoadRunnable != null) {
            dataHandler.removeCallbacks(dataLoadRunnable);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dataLoadRunnable != null) {
            dataHandler.removeCallbacks(dataLoadRunnable);
        }
    }
}