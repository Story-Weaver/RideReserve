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
import by.story_weaver.ridereserve.Logic.viewModels.AuthViewModel;
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
        loadTripData();
        setupClickListeners();
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
    }

    private void loadTripData() {
        currentTrip = findTripById(tripId);
        if (currentTrip != null) {
            updateTripInfo(currentTrip);
            loadRelatedData(currentTrip);
            loadPassengers(currentTrip.getId());
            updateActionButtons(currentTrip.getStatus());
        }
    }

    private Trip findTripById(long tripId) {
        List<Trip> allTrips = bookingViewModel.getTriplist();
        for (Trip trip : allTrips) {
            if (trip.getId() == tripId) {
                return trip;
            }
        }
        return null;
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
        Route route = findRouteById(trip.getRouteId());
        if (route != null) {
            tvDeparture.setText(route.getOrigin());
            tvDestination.setText(route.getDestination());
        }

        currentVehicle = findVehicleById(trip.getVehicleId());
        if (currentVehicle != null) {
            tvVehicleInfo.setText(currentVehicle.getModel() + " (" + currentVehicle.getPlateNumber() + ")");
            tvSeats.setText(currentVehicle.getSeatsCount() + " мест");
        }
    }

    private Route findRouteById(long routeId) {
        List<Route> allRoutes = bookingViewModel.getRoutelist();
        for (Route route : allRoutes) {
            if (route.getId() == routeId) {
                return route;
            }
        }
        return null;
    }

    private Vehicle findVehicleById(long vehicleId) {
        List<Vehicle> allVehicles = bookingViewModel.getVehiclelist();
        for (Vehicle vehicle : allVehicles) {
            if (vehicle.getId() == vehicleId) {
                return vehicle;
            }
        }
        return null;
    }

    private void loadPassengers(long tripId) {
        List<Booking> tripBookings = getBookingsForTrip(tripId);
        List<User> passengers = getPassengersFromBookings(tripBookings);

        Log.v("TripDetail", "Найдено бронирований: " + tripBookings.size());
        Log.v("TripDetail", "Найдено пассажиров: " + passengers.size());

        if (passengers.isEmpty()) {
            cardPassengers.setVisibility(View.GONE);
        } else {
            cardPassengers.setVisibility(View.VISIBLE);
            passengerAdapter.updatePassengers(passengers, tripBookings);
        }

        updatePassengersInfo(tripBookings);
        updateTotalPrice(tripBookings);
    }

    private List<Booking> getBookingsForTrip(long tripId) {
        List<Booking> tripBookings = new ArrayList<>();
        List<Booking> allBookings = bookingViewModel.getBookinglist();

        Log.v("TripDetail", "Всего бронирований в системе: " + allBookings.size());

        for (Booking booking : allBookings) {
            Log.v("TripDetail", "Бронирование: tripId=" + booking.getTripId() + ", passengerId=" + booking.getPassengerId());
            if (booking.getTripId() == tripId && booking.getStatus() != BookingStatus.CANCELLED) {
                tripBookings.add(booking);
                Log.v("TripDetail", "Добавлено бронирование для поездки " + tripId);
            }
        }

        Log.v("TripDetail", "Итоговое количество бронирований для поездки " + tripId + ": " + tripBookings.size());
        return tripBookings;
    }

    private List<User> getPassengersFromBookings(List<Booking> bookings) {
        List<User> passengers = new ArrayList<>();
        for (Booking booking : bookings) {
            User passenger = profileViewModel.getUserById(booking.getPassengerId());
            if (passenger != null) {
                passengers.add(passenger);
                Log.v("TripDetail", "Добавлен пассажир: " + passenger.getFullName() + " (ID: " + passenger.getId() + ")");
            } else {
                Log.v("TripDetail", "Пассажир с ID " + booking.getPassengerId() + " не найден");
            }
        }
        return passengers;
    }

    @SuppressLint("SetTextI18n")
    private void updatePassengersInfo(List<Booking> bookings) {
        if (currentVehicle != null) {
            int passengerCount = bookings.size();
            int maxPassengers = currentVehicle.getSeatsCount();

            tvPassengers.setText(passengerCount + "/" + maxPassengers + " пассажиров");
            Log.v("TripDetail", "Обновлена информация о пассажирах: " + passengerCount + "/" + maxPassengers);
        } else {
            tvPassengers.setText(bookings.size() + " пассажиров");
            Log.v("TripDetail", "Транспорт не найден, только количество пассажиров: " + bookings.size());
        }
    }

    private void updateTotalPrice(List<Booking> bookings) {
        double total = 0;
        for (Booking booking : bookings) {
            total += booking.getPrice();
        }
        tvTotalPrice.setText(formatPrice(total));
        Log.v("TripDetail", "Общая стоимость: " + total);
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

        Log.v("TripDetail", "Статус поездки установлен: " + status);
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

        Log.v("TripDetail", "Кнопки обновлены для статуса: " + status);
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
            currentTrip.setStatus(TripStatus.IN_PROGRESS);
            bookingViewModel.changeStatusTrip(currentTrip.getId(), TripStatus.IN_PROGRESS);
            updateTripInfo(currentTrip);
            updateActionButtons(TripStatus.IN_PROGRESS);
            showMessage("Поездка начата");
            Log.v("TripDetail", "Поездка начата, ID: " + currentTrip.getId());
        } else if (currentTrip.getStatus() == TripStatus.IN_PROGRESS) {
            currentTrip.setStatus(TripStatus.COMPLETED);
            bookingViewModel.changeStatusTrip(currentTrip.getId(), TripStatus.COMPLETED);
            updateTripInfo(currentTrip);
            updateActionButtons(TripStatus.COMPLETED);
            buttonField.setVisibility(INVISIBLE);
            showMessage("Поездка завершена");
            Log.v("TripDetail", "Поездка завершена, ID: " + currentTrip.getId());
        }
    }

    private void cancelTrip() {
        currentTrip.setStatus(TripStatus.CANCELLED);
        bookingViewModel.changeStatusTrip(currentTrip.getId(), TripStatus.CANCELLED);

        List<Booking> tripBookings = getBookingsForTrip(currentTrip.getId());
        for (Booking booking : tripBookings) {
            bookingViewModel.changeStatusBooking(booking.getId(), BookingStatus.CANCELLED);
            Log.v("TripDetail", "Бронирование отменено, ID: " + booking.getId());
        }

        updateTripInfo(currentTrip);
        updateActionButtons(TripStatus.CANCELLED);
        showMessage("Поездка отменена");
        Log.v("TripDetail", "Поездка отменена, ID: " + currentTrip.getId());

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