package by.story_weaver.ridereserve.ui.fragments.driver;

import static android.view.View.INVISIBLE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import by.story_weaver.ridereserve.Logic.adapters.PassengerAdapter;
import by.story_weaver.ridereserve.Logic.data.enums.BookingStatus;
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
    private TextView tvTripId, tvTripStatus, tvDateTime, tvPrice, tvPassengers;
    private TextView tvDeparture, tvDestination, tvVehicleInfo, tvSeats, tvTotalPrice;
    private Button btnStartTrip, btnCancelTrip;
    private CardView cardPassengers;
    private RecyclerView rvPassengers;

    private PassengerAdapter passengerAdapter;
    private List<Booking> currentBookings = new ArrayList<>();
    private List<User> currentPassengers = new ArrayList<>();

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
        return inflater.inflate(R.layout.fragment_trip_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookingViewModel = new ViewModelProvider(requireActivity()).get(BookingViewModel.class);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        initViews(view);
        setupPassengersRecyclerView();
        setupObservers();
        setupClickListeners();

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
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        btnStartTrip = view.findViewById(R.id.btnStartTrip);
        btnCancelTrip = view.findViewById(R.id.btnCancelTrip);
        cardPassengers = view.findViewById(R.id.cardPassengers);
        rvPassengers = view.findViewById(R.id.rvPassengers);
    }

    private void setupPassengersRecyclerView() {
        passengerAdapter = new PassengerAdapter(new ArrayList<>());
        rvPassengers.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPassengers.setAdapter(passengerAdapter);
        //TODO проверить правиильность загрузки пассажиров
    }

    private void setupObservers() {
        // Observe trip data
        bookingViewModel.getTripById().observe(getViewLifecycleOwner(), tripState -> {
            if (tripState.status == UiState.Status.SUCCESS && tripState.data != null) {
                currentTrip = tripState.data;
                updateTripInfo(currentTrip);
                loadRelatedData(currentTrip);
                updateActionButtons(currentTrip.getStatus());

                // Load bookings for this trip
                bookingViewModel.loadBookingsForTrip(currentTrip.getId());
            }
        });

        // Observe bookings for trip
        bookingViewModel.getBookingsForTrip().observe(getViewLifecycleOwner(), bookingsState -> {
            if (bookingsState.status == UiState.Status.SUCCESS && bookingsState.data != null) {
                currentBookings = filterActiveBookings(bookingsState.data);
                loadPassengersData();
            }
        });

        // Observe vehicle data
        bookingViewModel.getVehicleById().observe(getViewLifecycleOwner(), vehicleState -> {
            if (vehicleState.status == UiState.Status.SUCCESS && vehicleState.data != null) {
                currentVehicle = vehicleState.data;
                updateVehicleInfo();
                updatePassengersInfo();
            }
        });

        // Observe user data for passengers
        bookingViewModel.getUserById().observe(getViewLifecycleOwner(), userState -> {
            if (userState.status == UiState.Status.SUCCESS && userState.data != null) {
                // Check if we already have this user in the list
                boolean userExists = false;
                for (User passenger : currentPassengers) {
                    if (passenger.getId() == userState.data.getId()) {
                        userExists = true;
                        break;
                    }
                }

                if (!userExists) {
                    currentPassengers.add(userState.data);
                    passengerAdapter.updatePassengers(currentPassengers, currentBookings);
                    updatePassengersInfo();
                }
            }
        });

        // Observe trip status changes
        bookingViewModel.getTripStatusChanged().observe(getViewLifecycleOwner(), tripState -> {
            if (tripState.status == UiState.Status.SUCCESS && tripState.data != null) {
                currentTrip = tripState.data;
                updateTripInfo(currentTrip);
                updateActionButtons(currentTrip.getStatus());
            }
        });

        // Observe booking status changes
        bookingViewModel.getBookingStatusChanged().observe(getViewLifecycleOwner(), bookingState -> {
            if (bookingState.status == UiState.Status.SUCCESS) {
                // Refresh bookings after status change
                bookingViewModel.loadBookingsForTrip(tripId);
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

    private void loadPassengersData() {
        currentPassengers.clear();

        if (currentBookings.isEmpty()) {
            cardPassengers.setVisibility(View.GONE);
        } else {
            cardPassengers.setVisibility(View.VISIBLE);
            // Load each passenger's data
            for (Booking booking : currentBookings) {
                bookingViewModel.loadUserById(booking.getPassengerId());
            }
        }

        updatePassengersInfo();
        updateTotalPrice();
    }

    private List<Booking> filterActiveBookings(List<Booking> bookings) {
        List<Booking> activeBookings = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getStatus() != BookingStatus.CANCELLED) {
                activeBookings.add(booking);
            }
        }
        return activeBookings;
    }

    @SuppressLint("SetTextI18n")
    private void updatePassengersInfo() {
        if (currentVehicle != null) {
            int passengerCount = currentBookings.size();
            int maxPassengers = currentVehicle.getSeatsCount();
            tvPassengers.setText(passengerCount + "/" + maxPassengers + " пассажиров");
        } else {
            tvPassengers.setText(currentBookings.size() + " пассажиров");
        }
    }

    private void updateTotalPrice() {
        double total = 0;
        for (Booking booking : currentBookings) {
            total += booking.getPrice();
        }
        tvTotalPrice.setText(formatPrice(total));
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

    private void updateActionButtons(TripStatus status) {
        switch (status) {
            case SCHEDULED:
                btnStartTrip.setVisibility(View.VISIBLE);
                btnStartTrip.setText("Начать поездку");
                btnCancelTrip.setVisibility(View.VISIBLE);
                break;
            case IN_PROGRESS:
                btnStartTrip.setVisibility(View.VISIBLE);
                btnStartTrip.setText("Завершить поездку");
                btnCancelTrip.setVisibility(View.GONE);
                break;
            case COMPLETED:
            case CANCELLED:
                btnStartTrip.setVisibility(View.GONE);
                btnCancelTrip.setVisibility(View.GONE);
                break;
        }
    }

    private void setupClickListeners() {
        btnStartTrip.setOnClickListener(v -> {
            if (currentTrip != null) {
                startOrCompleteTrip();
            }
        });

        btnCancelTrip.setOnClickListener(v -> {
            if (currentTrip != null) {
                cancelTrip();
            }
        });
    }

    private void startOrCompleteTrip() {
        if (currentTrip.getStatus() == TripStatus.SCHEDULED) {
            bookingViewModel.changeStatusTrip(currentTrip.getId(), TripStatus.IN_PROGRESS);
            showMessage("Поездка начата");
        } else if (currentTrip.getStatus() == TripStatus.IN_PROGRESS) {
            bookingViewModel.changeStatusTrip(currentTrip.getId(), TripStatus.COMPLETED);
            buttonField.setVisibility(INVISIBLE);
            showMessage("Поездка завершена");
        }
    }

    private void cancelTrip() {
        bookingViewModel.changeStatusTrip(currentTrip.getId(), TripStatus.CANCELLED);

        // Cancel all bookings for this trip
        for (Booking booking : currentBookings) {
            bookingViewModel.changeStatusBooking(booking.getId(), BookingStatus.CANCELLED);
        }

        showMessage("Поездка отменена");
    }

    private String formatDateTime(String dateTime) {
        return dateTime != null ? dateTime : "Не указано";
    }

    private String formatPrice(double price) {
        return String.format("%.2f BYN", price);
    }

    private void showMessage(String message) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show();
    }
}