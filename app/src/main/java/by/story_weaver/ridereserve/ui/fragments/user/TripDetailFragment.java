package by.story_weaver.ridereserve.ui.fragments.user;

import static android.view.View.INVISIBLE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import by.story_weaver.ridereserve.Logic.data.enums.TripStatus;
import by.story_weaver.ridereserve.Logic.data.enums.UserRole;
import by.story_weaver.ridereserve.Logic.data.models.Route;
import by.story_weaver.ridereserve.Logic.data.models.Trip;
import by.story_weaver.ridereserve.Logic.data.models.Vehicle;
import by.story_weaver.ridereserve.Logic.utils.UiState;
import by.story_weaver.ridereserve.Logic.viewModels.BookingViewModel;
import by.story_weaver.ridereserve.Logic.viewModels.MainViewModel;
import by.story_weaver.ridereserve.Logic.viewModels.ProfileViewModel;
import by.story_weaver.ridereserve.R;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TripDetailFragment extends Fragment {

    private static final String ARG_TRIP_ID = "trip_id";

    private long tripId;
    private Trip currentTrip;
    private Vehicle currentVehicle;
    private Route currentRoute;
    private BookingViewModel bookingViewModel;
    private ProfileViewModel profileViewModel;
    private MainViewModel mainViewModel;

    private CardView buttonField;
    private TextView tvTripId, tvTripStatus, tvDateTime, tvPrice, tvPassengers, tvDriver, tvDriverNum;
    private TextView tvDeparture, tvDestination, tvVehicleInfo, tvSeats;

    public static TripDetailFragment newInstance(long tripId) {
        TripDetailFragment fragment = new TripDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_TRIP_ID, tripId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tripId = getArguments().getLong(ARG_TRIP_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip_detail2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookingViewModel = new ViewModelProvider(requireActivity()).get(BookingViewModel.class);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        initViews(view);
        setupObservers();

        loadTripData();
    }

    private void initViews(View view) {
        buttonField = view.findViewById(R.id.buttonField_TripDetail);
        tvTripId = view.findViewById(R.id.tvTripId);
        tvTripStatus = view.findViewById(R.id.tvTripStatus);
        tvDateTime = view.findViewById(R.id.tvDateTime);
        tvPrice = view.findViewById(R.id.tvPrice);
        tvPassengers = view.findViewById(R.id.tvPassengers);
        tvDeparture = view.findViewById(R.id.tvDeparture);
        tvDestination = view.findViewById(R.id.tvDestination);
        tvVehicleInfo = view.findViewById(R.id.tvVehicleInfo);
        tvSeats = view.findViewById(R.id.tvSeats);
        tvDriver = view.findViewById(R.id.tvDriver);
        tvDriverNum = view.findViewById(R.id.tvDriverNum);
    }

    private void setupObservers() {
        bookingViewModel.getUserById().observe(getViewLifecycleOwner(), v -> {
            switch (v.status){
                case SUCCESS:
                    if(v.data.getRole() == UserRole.DRIVER){
                        tvDriver.setText(v.data.getFullName());
                        tvDriverNum.setText(v.data.getPhone());
                    }
            }
        });
        // Observe trip data
        bookingViewModel.getTripById().observe(getViewLifecycleOwner(), tripState -> {
            if (tripState.status == UiState.Status.SUCCESS && tripState.data != null) {
                currentTrip = tripState.data;
                updateTripInfo(currentTrip);
                loadRelatedData(currentTrip);

                // Load bookings for this trip
                bookingViewModel.loadBookingsForTrip(currentTrip.getId());
            }
        });

        // Observe vehicle data
        bookingViewModel.getVehicleById().observe(getViewLifecycleOwner(), vehicleState -> {
            if (vehicleState.status == UiState.Status.SUCCESS && vehicleState.data != null) {
                currentVehicle = vehicleState.data;
                updateVehicleInfo();
            }
        });
    }

    private void loadTripData() {
        bookingViewModel.loadTripById(tripId);
    }

    @SuppressLint("SetTextI18n")
    private void updateTripInfo(Trip trip) {
        tvTripId.setText("Поездка #" + trip.getId());
        tvDateTime.setText(formatDateTime(trip.getDepartureTime()));
        tvPrice.setText(formatPrice(trip.getPrice()));
        setupStatus(trip.getStatus());
    }

    @SuppressLint("SetTextI18n")
    private void loadRelatedData(Trip trip) {
        bookingViewModel.loadUserById(trip.getDriverId());
        // Load route data
        bookingViewModel.getAllRoutes().observe(getViewLifecycleOwner(), routesState -> {
            if (routesState.status == UiState.Status.SUCCESS && routesState.data != null) {
                currentRoute = findRouteById(routesState.data, trip.getRouteId());
                if (currentRoute != null) {
                    tvDeparture.setText(currentRoute.getOrigin());
                    tvDestination.setText(currentRoute.getDestination());
                }
            }
        });

        // Load vehicle data
        bookingViewModel.loadVehicleById(trip.getVehicleId());
    }

    private Route findRouteById(List<Route> routes, long routeId) {
        for (Route route : routes) {
            if (route.getId() == routeId) {
                return route;
            }
        }
        return null;
    }

    private void updateVehicleInfo() {
        if (currentVehicle != null) {
            tvVehicleInfo.setText(currentVehicle.getModel() + " (" + currentVehicle.getPlateNumber() + ")");
            tvSeats.setText(currentVehicle.getSeatsCount() + " мест");
        }
    }

    private void setupStatus(TripStatus status) {
        if (status == null) return;

        switch (status) {
            case SCHEDULED:
                tvTripStatus.setText("Запланирована");
                tvTripStatus.setBackgroundResource(R.drawable.bg_status_scheduled);
                break;
            case IN_PROGRESS:
                tvTripStatus.setText("В пути");
                tvTripStatus.setBackgroundResource(R.drawable.bg_status_in_progress);
                break;
            case COMPLETED:
                tvTripStatus.setText("Завершена");
                tvTripStatus.setBackgroundResource(R.drawable.bg_status_completed);
                break;
            case CANCELLED:
                tvTripStatus.setText("Отменена");
                tvTripStatus.setBackgroundResource(R.drawable.bg_status_cancelled);
                break;
            default:
                tvTripStatus.setText("Неизвестно");
                tvTripStatus.setBackgroundResource(R.drawable.bg_status_scheduled);
        }
    }

    private String formatDateTime(String dateTime) {
        return dateTime != null ? dateTime : "Не указано";
    }

    private String formatPrice(double price) {
        return String.format("%.2f BYN", price);
    }
}