package by.story_weaver.ridereserve.ui.fragments.driver;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import by.story_weaver.ridereserve.Logic.adapters.DriverTripsAdapter;
import by.story_weaver.ridereserve.Logic.data.enums.TripStatus;
import by.story_weaver.ridereserve.Logic.data.models.Booking;
import by.story_weaver.ridereserve.Logic.data.models.Route;
import by.story_weaver.ridereserve.Logic.data.models.Trip;
import by.story_weaver.ridereserve.Logic.data.models.User;
import by.story_weaver.ridereserve.Logic.data.models.Vehicle;
import by.story_weaver.ridereserve.Logic.utils.UiState;
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

    private List<Trip> originalTrips = new ArrayList<>();
    private List<Route> originalRoutes = new ArrayList<>();
    private List<Vehicle> originalVehicles = new ArrayList<>();
    private List<Booking> originalBookings = new ArrayList<>();
    private List<Trip> filteredTrips = new ArrayList<>();

    private User currentUser;

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
        setupObservers();

        loadCurrentUser();
        loadInitialData();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
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
    }

    private void setupObservers() {
        // Observe driver trips
        bookingViewModel.getDriverTrips().observe(getViewLifecycleOwner(), tripsState -> {
            switch (tripsState.status){
                case SUCCESS:
                    originalTrips = tripsState.data;
                    filteredTrips = new ArrayList<>(originalTrips);
                    Collections.reverse(filteredTrips);
                    adapter.updateTrips(filteredTrips);
                    updateStatistics(filteredTrips);
                    break;
                case ERROR:
                case LOADING:
                    break;
            }
        });

        // Observe routes for search functionality
        bookingViewModel.getAllRoutes().observe(getViewLifecycleOwner(), routesState -> {
            if (routesState.status == UiState.Status.SUCCESS && routesState.data != null) {
                originalRoutes = routesState.data;
                updateAdapterData();
            }
        });

        // Observe bookings
        bookingViewModel.getAllBookings().observe(getViewLifecycleOwner(), bookingsState -> {
            if (bookingsState.status == UiState.Status.SUCCESS && bookingsState.data != null) {
                originalBookings = bookingsState.data;
                updateAdapterData();
            }
        });

        // Observe vehicles
        bookingViewModel.getAllVehicles().observe(getViewLifecycleOwner(), vehiclesState -> {
            if (vehiclesState.status == UiState.Status.SUCCESS && vehiclesState.data != null) {
                originalVehicles = vehiclesState.data;
                updateAdapterData();
            }
        });

        // Observe trip status changes to refresh data
        bookingViewModel.getTripStatusChanged().observe(getViewLifecycleOwner(), tripState -> {
            if (tripState.status == UiState.Status.SUCCESS) {
                refreshData();
            }
        });
    }

    private void loadCurrentUser() {
        currentUser = profileViewModel.getProfile();
    }

    private void loadInitialData() {
        if (currentUser != null) {
            bookingViewModel.loadDriverTrips(currentUser.getId());
            bookingViewModel.loadAllRoutes();
            bookingViewModel.loadAllBookings();
            bookingViewModel.loadAllVehicles();
        }
    }

    private void refreshData() {
        if (currentUser != null) {
            bookingViewModel.loadDriverTrips(currentUser.getId());
        }
    }

    private void updateAdapterData() {
        adapter.updateAllData(filteredTrips, originalRoutes, originalVehicles, originalBookings);
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
    public void onTripClick(Trip trip) {
        openTripDetails(trip);
    }

    private void openTripDetails(Trip trip) {
        TripDetailFragment fragment = TripDetailFragment.newInstance(trip.getId());
        mainViewModel.openFullscreen(fragment);
    }
}