package by.story_weaver.ridereserve.ui.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import by.story_weaver.ridereserve.Logic.adapters.RoutesAdapter;
import by.story_weaver.ridereserve.Logic.adapters.TripsAdapter;
import by.story_weaver.ridereserve.Logic.data.enums.BookingStatus;
import by.story_weaver.ridereserve.Logic.data.enums.UserRole;
import by.story_weaver.ridereserve.Logic.data.models.Booking;
import by.story_weaver.ridereserve.Logic.data.models.Route;
import by.story_weaver.ridereserve.Logic.data.models.Trip;
import by.story_weaver.ridereserve.Logic.data.models.User;
import by.story_weaver.ridereserve.Logic.utils.UiState;
import by.story_weaver.ridereserve.Logic.viewModels.BookingViewModel;
import by.story_weaver.ridereserve.Logic.viewModels.MainViewModel;
import by.story_weaver.ridereserve.Logic.viewModels.ProfileViewModel;
import by.story_weaver.ridereserve.R;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BookingActivity extends AppCompatActivity {

    public static final String EXTRA_TRIP_ID = "extra_trip_id";
    public static final String EXTRA_ROUTE_ID = "extra_route_id";
    public static final String RESULT_BOOKING_CREATED = "result_booking_created";

    // UI Fields
    private TextInputEditText etFullName, etPhone, etEmail, etRouteSearch, etMaxDistance;
    private Spinner spinnerMaxDurationHours, spinnerMaxDurationMinutes;
    private TextInputLayout tilFullName, tilPhone, tilEmail;
    private MaterialButton book, btnSearchRoutes, btnBackToRoutes, btnBackToTrips;
    private Spinner spinnerFrom, spinnerTo;
    private RecyclerView recyclerRoutes, recyclerTrips;
    private TextView textRouteCard, tvSelectedRoute, tvRouteInfo, tvDepartureTime, tvTripPrice, tvTotalPrice;
    private ProgressBar progressTrips;
    private CardView cardRouteSelection, cardRoutesList, cardTimeSelection, cardTripInfo, cardServices, cardPassenger, cardTotal;

    // Logic Fields
    private RoutesAdapter routesAdapter;
    private TripsAdapter tripsAdapter;
    private BookingViewModel bookingViewModel;
    private ProfileViewModel profileViewModel;
    private MainViewModel mainViewModel;
    private User currentUser;
    private Trip currentTrip;
    private Route currentRoute;

    private boolean isChild = false;
    private boolean isPet = false;
    private double totalPrice = 0;
    private final double petPrice = 200;
    private final double childPrice = 300;
    private long launchRouteId = -1;
    private long launchTripId = -1;

    private long pendingPassengerId = -1;

    private List<Route> allRoutes = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_booking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scroll_booking), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        Intent in = getIntent();
        if (in != null) {
            launchRouteId = in.getLongExtra(EXTRA_ROUTE_ID, -1);
            launchTripId = in.getLongExtra(EXTRA_TRIP_ID, -1);
        }

        initViews();
        setupAdapters();
        setupButtonListeners();
        setupObservers();

        loadCurrentUser();
        bookingViewModel.loadAllRoutes();
        bookingViewModel.loadAllCities();

        setupInitialState();
    }

    private void initViews() {
        // Input Fields
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        tilFullName = findViewById(R.id.tilFullName);
        tilPhone = findViewById(R.id.tilPhone);
        tilEmail = findViewById(R.id.tilEmail);
        etRouteSearch = findViewById(R.id.etRouteSearch);
        etMaxDistance = findViewById(R.id.etMaxDistance);

        // Spinners
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        spinnerMaxDurationHours = findViewById(R.id.spinnerMaxDurationHours);
        spinnerMaxDurationMinutes = findViewById(R.id.spinnerMaxDurationMinutes);

        // Buttons
        btnSearchRoutes = findViewById(R.id.btnSearchRoutes);
        btnBackToRoutes = findViewById(R.id.btnBackToRoutes);
        btnBackToTrips = findViewById(R.id.btnBackToTrips);
        book = findViewById(R.id.bookButton);

        // Lists & Progress
        recyclerRoutes = findViewById(R.id.recyclerRoutes);
        recyclerTrips = findViewById(R.id.recyclerTrips);
        progressTrips = findViewById(R.id.progressTrips);

        // Cards
        cardRouteSelection = findViewById(R.id.cardRouteSelection);
        cardRoutesList = findViewById(R.id.cardRoutesList);
        cardTimeSelection = findViewById(R.id.cardTimeSelection);
        cardTripInfo = findViewById(R.id.cardTripInfo);
        cardServices = findViewById(R.id.cardServices);
        cardPassenger = findViewById(R.id.cardPassenger);
        cardTotal = findViewById(R.id.cardTotal);

        // Text Views
        textRouteCard = findViewById(R.id.textRouteCard);
        tvSelectedRoute = findViewById(R.id.tvSelectedRoute);
        tvRouteInfo = findViewById(R.id.tvRouteInfo);
        tvDepartureTime = findViewById(R.id.tvDepartureTime);
        tvTripPrice = findViewById(R.id.tvTripPrice);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
    }

    private void setupInitialState() {
        if (launchTripId != -1) {
            hideRouteSelection();
            hideTimeSelection();
            bookingViewModel.loadTripById(launchTripId);
        } else if (launchRouteId != -1) {
            hideRouteSelection();
            showTimeSelection();
            bookingViewModel.loadAllRoutes();
            bookingViewModel.loadTripsForRoute(launchRouteId);
        } else {
            showRouteSelection();
            hideTimeSelection();
            hideRemainingCards();
        }
    }

    private void setupAdapters() {
        routesAdapter = new RoutesAdapter(new ArrayList<>(), this::onRouteSelected);
        recyclerRoutes.setLayoutManager(new LinearLayoutManager(this));
        recyclerRoutes.setAdapter(routesAdapter);

        tripsAdapter = new TripsAdapter(new ArrayList<>(), this::onTripSelected);
        recyclerTrips.setLayoutManager(new LinearLayoutManager(this));
        recyclerTrips.setAdapter(tripsAdapter);

        List<String> placeholder = new ArrayList<>();
        placeholder.add("Выберите город");
        ArrayAdapter<String> fromAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, placeholder);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(fromAdapter);

        ArrayAdapter<String> toAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, placeholder);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTo.setAdapter(toAdapter);
        setupDurationSpinners();
    }
    @SuppressLint("DefaultLocale")
    private void setupDurationSpinners() {
        List<String> hoursList = new ArrayList<>();
        hoursList.add("Часы");
        for (int i = 0; i < 24; i++) {
            hoursList.add(String.format("%02d", i));
        }
        ArrayAdapter<String> hoursAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, hoursList);
        hoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaxDurationHours.setAdapter(hoursAdapter);

        List<String> minutesList = new ArrayList<>();
        minutesList.add("Минуты");
        for (int i = 0; i < 60; i++) {
            minutesList.add(String.format("%02d", i));
        }
        ArrayAdapter<String> minutesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, minutesList);
        minutesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaxDurationMinutes.setAdapter(minutesAdapter);
    }

    private void setupButtonListeners() {
        btnSearchRoutes.setOnClickListener(v -> searchRoutes());
        btnBackToRoutes.setOnClickListener(v -> returnToRouteSelection());
        btnBackToTrips.setOnClickListener(v -> returnToTripSelection());

        etRouteSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchRoutes();
                return true;
            }
            return false;
        });

        CheckBox cbUseMyData = findViewById(R.id.cbUseMyData);
        cbUseMyData.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (currentUser != null) {
                    etFullName.setText(currentUser.getFullName());
                    etPhone.setText(currentUser.getPhone());
                    etEmail.setText(currentUser.getEmail());
                }
                setFieldsEnabled(false);
            } else {
                setFieldsEnabled(true);
                etFullName.setText("");
                etPhone.setText("");
                etEmail.setText("");
            }
        });

        CheckBox cbChildSeat = findViewById(R.id.cbChildSeat);
        CheckBox cbPet = findViewById(R.id.cbPet);

        cbChildSeat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isChild = isChecked;
            updateTotalPrice();
        });

        cbPet.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isPet = isChecked;
            updateTotalPrice();
        });

        book.setOnClickListener(v -> createNewBooking());
    }

    private void setFieldsEnabled(boolean enabled) {
        etFullName.setEnabled(enabled);
        etPhone.setEnabled(enabled);
        etEmail.setEnabled(enabled);

        if (!enabled) {
            tilFullName.setError(null);
            tilPhone.setError(null);
            tilEmail.setError(null);
        }
    }

    private void setupObservers() {
        bookingViewModel.getTripById().observe(this, state -> {
            if (state.status == UiState.Status.SUCCESS && state.data != null) {
                currentTrip = state.data;
                bookingViewModel.loadAllRoutes();
                updateTripInfo(currentTrip);
                showRemainingCards();
            }
        });

        bookingViewModel.getAllRoutes().observe(this, list -> {
            if (list.status == UiState.Status.SUCCESS && list.data != null) {
                allRoutes = list.data;
                routesAdapter.updateRoutes(list.data);

                if (launchRouteId != -1 && currentRoute == null) {
                    for (Route r : list.data) {
                        if (r != null && r.getId() == launchRouteId) {
                            currentRoute = r;
                            updateSelectedRouteInfo(r);
                            break;
                        }
                    }
                }
                if (currentTrip != null && currentRoute == null) {
                    for (Route r : list.data) {
                        if (r != null && r.getId() == currentTrip.getRouteId()) {
                            currentRoute = r;
                            updateTripInfo(currentTrip);
                            break;
                        }
                    }
                }

                if (launchTripId == -1 && launchRouteId == -1) {
                    textRouteCard.setText("Все маршруты: ");
                    showRoutesList();
                }
            }
        });

        bookingViewModel.getFilteredRoutes().observe(this, routes -> {
            switch (routes.status) {
                case LOADING:
                    progressTrips.setVisibility(VISIBLE);
                    recyclerTrips.setVisibility(GONE);
                    break;
                case SUCCESS:
                    progressTrips.setVisibility(GONE);
                    recyclerTrips.setVisibility(VISIBLE);
                    if (routes.data != null && !routes.data.isEmpty()) {
                        routesAdapter.updateRoutes(routes.data);
                        textRouteCard.setText("Найденные маршруты: ");
                        showRoutesList();
                    } else {
                        hideRoutesList();
                        Toast.makeText(this, "Маршруты не найдены", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case ERROR:
                    Toast.makeText(this, "Ошибка: " + routes.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        bookingViewModel.getTripsForRoute().observe(this, tripsState -> {
            if (progressTrips != null) progressTrips.setVisibility(GONE);
            if (tripsState.status == UiState.Status.SUCCESS && tripsState.data != null && !tripsState.data.isEmpty()) {
                tripsAdapter.updateTrips(tripsState.data);
                showTimeSelection();
                hideRemainingCards();
            } else if (tripsState.status == UiState.Status.ERROR) {
                Toast.makeText(this, "Ошибка загрузки рейсов: " + tripsState.message, Toast.LENGTH_SHORT).show();
            } else if (tripsState.status == UiState.Status.SUCCESS && (tripsState.data == null || tripsState.data.isEmpty())) {
                showNoTripsDialog();
            }
        });

        bookingViewModel.getIsHasBooking().observe(this, hasBookingState -> {
            if (hasBookingState.status == UiState.Status.SUCCESS && hasBookingState.data != null) {
                if (currentTrip == null) {
                    Toast.makeText(this, "Состояние рейса устарело, выберите рейс снова", Toast.LENGTH_SHORT).show();
                    if (book != null) book.setEnabled(true);
                    return;
                }
                if (hasBookingState.data) {
                    Toast.makeText(this, "Вы уже забронировали этот рейс", Toast.LENGTH_LONG).show();
                    if (book != null) book.setEnabled(true);
                } else {
                    proceedWithBooking();
                }
            } else if (hasBookingState.status == UiState.Status.ERROR) {
                Toast.makeText(this, "Ошибка проверки брони: " + hasBookingState.message, Toast.LENGTH_SHORT).show();
                if (book != null) book.setEnabled(true);
            }
        });

        bookingViewModel.getBookingCreated().observe(this, flag -> {
            switch (flag.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    Toast.makeText(this, "Бронь создана!", Toast.LENGTH_SHORT).show();
                    Intent data = new Intent();
                    data.putExtra(RESULT_BOOKING_CREATED, true);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                    break;
                case ERROR:
                    Toast.makeText(this, "Ошибка при создании брони: " + flag.message, Toast.LENGTH_LONG).show();
                    if (book != null) book.setEnabled(true);
                    break;
            }
        });

        bookingViewModel.getAllCitiesLive().observe(this, state -> {
            if (state.status == UiState.Status.SUCCESS) {
                List<String> cities = state.data != null ? state.data : new ArrayList<>();
                List<String> items = new ArrayList<>();
                items.add("Выберите город");
                items.addAll(cities);

                ArrayAdapter<String> fromAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, items);
                fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFrom.setAdapter(fromAdapter);

                ArrayAdapter<String> toAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, items);
                toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTo.setAdapter(toAdapter);
            } else if (state.status == UiState.Status.ERROR) {
                Toast.makeText(this, "Ошибка загрузки городов: " + state.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRouteSelection() {
        if (cardRouteSelection != null) cardRouteSelection.setVisibility(VISIBLE);
    }

    private void hideRouteSelection() {
        if (cardRouteSelection != null) cardRouteSelection.setVisibility(GONE);
    }

    private void showRoutesList() {
        if (cardRoutesList != null) cardRoutesList.setVisibility(VISIBLE);
    }

    private void hideRoutesList() {
        if (cardRoutesList != null) cardRoutesList.setVisibility(GONE);
    }

    private void showTimeSelection() {
        if (cardTimeSelection != null) cardTimeSelection.setVisibility(VISIBLE);
        if (btnBackToRoutes != null) {
            btnBackToRoutes.setVisibility(launchRouteId == -1 ? VISIBLE : GONE);
        }
    }

    private void hideTimeSelection() {
        if (cardTimeSelection != null) cardTimeSelection.setVisibility(GONE);
    }

    private void loadCurrentUser() {
        currentUser = profileViewModel.getProfile();
        CheckBox cbUseMyData = findViewById(R.id.cbUseMyData);
        if (currentUser != null && cbUseMyData.isChecked()) {
            etFullName.setText(currentUser.getFullName());
            etPhone.setText(currentUser.getPhone());
            etEmail.setText(currentUser.getEmail());
        }
    }

    private void searchRoutes() {
        if (allRoutes == null || allRoutes.isEmpty()) {
            Toast.makeText(this, "Данные маршрутов еще не загружены", Toast.LENGTH_SHORT).show();
            return;
        }

        String routeNumber = etRouteSearch != null ? etRouteSearch.getText().toString().trim().toLowerCase() : "";
        String fromCity = spinnerFrom != null && spinnerFrom.getSelectedItem() != null ? spinnerFrom.getSelectedItem().toString() : "";
        String toCity = spinnerTo != null && spinnerTo.getSelectedItem() != null ? spinnerTo.getSelectedItem().toString() : "";

        String maxDistanceStr = etMaxDistance != null ? Objects.requireNonNull(etMaxDistance.getText()).toString().trim() : "";

        double maxDistance = -1;
        int maxDurationMinutes = -1;

        try {
            if (!maxDistanceStr.isEmpty()) maxDistance = Double.parseDouble(maxDistanceStr);

            String selectedHours = spinnerMaxDurationHours.getSelectedItem().toString();
            String selectedMinutes = spinnerMaxDurationMinutes.getSelectedItem().toString();

            final String durationPlaceholder = "Часы";
            final String minutesPlaceholder = "Минуты";

            if (!selectedHours.equals(durationPlaceholder) || !selectedMinutes.equals(minutesPlaceholder)) {
                int hours = selectedHours.equals(durationPlaceholder) ? 0 : Integer.parseInt(selectedHours);
                int minutes = selectedMinutes.equals(minutesPlaceholder) ? 0 : Integer.parseInt(selectedMinutes);

                if (hours > 0 || minutes > 0) {
                    maxDurationMinutes = hours * 60 + minutes;
                }
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Неверный формат числа для длины", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Route> filteredRoutes = new ArrayList<>(allRoutes);

        final String cityPlaceholder = "Выберите город";

        if (!routeNumber.isEmpty()) {
            filteredRoutes = filteredRoutes.stream()
                    .filter(route -> route.getName().toLowerCase().contains(routeNumber) ||
                            (route.getDestination() != null && route.getDestination().toLowerCase().contains(routeNumber)) ||
                            (route.getOrigin() != null && route.getOrigin().toLowerCase().contains(routeNumber)))
                    .collect(Collectors.toList());
        }

        if (!fromCity.equals(cityPlaceholder)) {
            filteredRoutes = filteredRoutes.stream()
                    .filter(route -> route.getOrigin() != null && route.getOrigin().equals(fromCity))
                    .collect(Collectors.toList());
        }

        if (!toCity.equals(cityPlaceholder)) {
            filteredRoutes = filteredRoutes.stream()
                    .filter(route -> route.getDestination() != null && route.getDestination().equals(toCity))
                    .collect(Collectors.toList());
        }

        if (maxDistance > 0) {
            final double finalMaxDistance = maxDistance;
            filteredRoutes = filteredRoutes.stream()
                    .filter(route -> route.getDistance() <= finalMaxDistance)
                    .collect(Collectors.toList());
        }

        if (maxDurationMinutes > 0) {
            final int finalMaxDurationMinutes = maxDurationMinutes;

            filteredRoutes = filteredRoutes.stream()
                    .filter(route -> {
                        String routeTimeStr = route.getTime();
                        if (routeTimeStr == null || routeTimeStr.isEmpty()) {
                            return false;
                        }

                        try {
                            String[] parts = routeTimeStr.split(":");
                            if (parts.length == 2) {
                                int hours = Integer.parseInt(parts[0]);
                                int minutes = Integer.parseInt(parts[1]);
                                int routeDurationMinutes = hours * 60 + minutes;

                                return routeDurationMinutes <= finalMaxDurationMinutes;
                            }
                        } catch (NumberFormatException e) {
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
        }

        if (!filteredRoutes.isEmpty()) {
            routesAdapter.updateRoutes(filteredRoutes);
            textRouteCard.setText("Найденные маршруты: " + filteredRoutes.size());
            showRoutesList();
        } else {
            routesAdapter.updateRoutes(new ArrayList<>());
            textRouteCard.setText("Маршруты не найдены");
            hideRoutesList();
            Toast.makeText(this, "Маршруты не найдены", Toast.LENGTH_SHORT).show();
        }
    }

    private void onRouteSelected(Route route) {
        if (route == null) return;
        currentRoute = route;
        updateSelectedRouteInfo(route);
        progressTrips.setVisibility(VISIBLE);
        bookingViewModel.loadTripsForRoute(route.getId());
        hideRouteSelection();
        hideRoutesList();
    }

    private void onTripSelected(Trip trip) {
        if (trip == null) return;
        currentTrip = trip;

        if (currentRoute == null && allRoutes != null) {
            for (Route r : allRoutes) {
                if (r != null && r.getId() == currentTrip.getRouteId()) {
                    currentRoute = r;
                    break;
                }
            }
        }

        updateTripInfo(trip);
        hideTimeSelection();
        showRemainingCards();
    }

    private void updateSelectedRouteInfo(Route route) {
        if (tvSelectedRoute != null && route != null) {
            String origin = route.getOrigin() != null ? route.getOrigin() : "";
            String destination = route.getDestination() != null ? route.getDestination() : "";
            tvSelectedRoute.setText("Маршрут: " + origin + " - " + destination);
        }
    }

    private void updateTripInfo(Trip trip) {
        if (trip == null) return;

        if (currentRoute != null) {
            String origin = currentRoute.getOrigin() != null ? currentRoute.getOrigin() : "";
            String destination = currentRoute.getDestination() != null ? currentRoute.getDestination() : "";
            if (tvRouteInfo != null) tvRouteInfo.setText(origin + " - " + destination);

            if(tvSelectedRoute != null) {
                tvSelectedRoute.setText("Маршрут: " + origin + " - " + destination);
            }
        }

        if (tvDepartureTime != null) {
            tvDepartureTime.setText(formatDateTime(trip.getDepartureTime()));
        }
        if (tvTripPrice != null) {
            tvTripPrice.setText(String.format("%.2f BYN", trip.getPrice()));
        }
        updateTotalPrice();
    }

    private void showRemainingCards() {
        if (cardTripInfo != null) cardTripInfo.setVisibility(VISIBLE);
        if (cardServices != null) cardServices.setVisibility(VISIBLE);
        if (cardPassenger != null) cardPassenger.setVisibility(VISIBLE);
        if (cardTotal != null) cardTotal.setVisibility(VISIBLE);
        if (book != null) book.setVisibility(VISIBLE);
    }

    private void hideRemainingCards() {
        if (cardTripInfo != null) cardTripInfo.setVisibility(GONE);
        if (cardServices != null) cardServices.setVisibility(GONE);
        if (cardPassenger != null) cardPassenger.setVisibility(GONE);
        if (cardTotal != null) cardTotal.setVisibility(GONE);
        if (book != null) book.setVisibility(GONE);
    }

    private void returnToRouteSelection() {
        currentRoute = null;
        currentTrip = null;
        launchRouteId = -1;
        showRouteSelection();
        showRoutesList();
        hideTimeSelection();
        hideRemainingCards();
        if (routesAdapter != null && allRoutes != null) {
            routesAdapter.updateRoutes(allRoutes);
            textRouteCard.setText("Все маршруты: ");
        }
        if (tripsAdapter != null) {
            tripsAdapter.updateTrips(new ArrayList<>());
        }
    }

    private void returnToTripSelection() {
        currentTrip = null;
        showTimeSelection();
        hideRemainingCards();

        if (currentRoute != null) {
            updateSelectedRouteInfo(currentRoute);
        }
    }

    private void updateTotalPrice() {
        if (currentTrip == null || tvTotalPrice == null) return;
        totalPrice = currentTrip.getPrice();
        if (isPet) totalPrice += petPrice;
        if (isChild) totalPrice += childPrice;
        tvTotalPrice.setText(String.format("%.2f BYN", totalPrice));
    }

    private String formatDateTime(String dateTime) {
        if (dateTime == null) return "";
        return dateTime.replace("T", " ").replace("-", ".");
    }

    private boolean isFormValid() {
        CheckBox cbUseMyData = findViewById(R.id.cbUseMyData);

        if (cbUseMyData.isChecked()) {
            if (currentUser == null) {
                Toast.makeText(this, "Пользователь не найден", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }

        boolean isValid = true;

        if (etFullName == null || Objects.requireNonNull(etFullName.getText()).toString().trim().isEmpty()) {
            if (tilFullName != null) tilFullName.setError("Введите ФИО");
            isValid = false;
        } else if (tilFullName != null) tilFullName.setError(null);

        if (etPhone == null || Objects.requireNonNull(etPhone.getText()).toString().trim().isEmpty()) {
            if (tilPhone != null) tilPhone.setError("Введите телефон");
            isValid = false;
        } else if (tilPhone != null) tilPhone.setError(null);

        if (etEmail == null || Objects.requireNonNull(etEmail.getText()).toString().trim().isEmpty()) {
            if (tilEmail != null) tilEmail.setError("Введите email");
            isValid = false;
        } else if (tilEmail != null) tilEmail.setError(null);

        return isValid;
    }

    private void createNewBooking() {
        if (currentTrip == null) {
            Toast.makeText(this, "Выберите рейс", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isFormValid()) return;

        book.setEnabled(false);

        CheckBox cbUseMyData = findViewById(R.id.cbUseMyData);
        if (!cbUseMyData.isChecked()) {
            String name = Objects.requireNonNull(etFullName.getText()).toString();
            String phone = Objects.requireNonNull(etPhone.getText()).toString();
            String email = Objects.requireNonNull(etEmail.getText()).toString();

            User guestUser = new User(-1, email, "guest_temp_password", name, phone, 1, UserRole.GUEST);
            boolean guestAdded = profileViewModel.addGuest(guestUser);
            if (!guestAdded) {
                Toast.makeText(this, "Ошибка при создании гостевого аккаунта", Toast.LENGTH_SHORT).show();
                book.setEnabled(true);
                return;
            }
            pendingPassengerId = profileViewModel.getIdByEmail(email);
            if (pendingPassengerId == -1) {
                Toast.makeText(this, "Не удалось получить ID пользователя", Toast.LENGTH_SHORT).show();
                book.setEnabled(true);
                return;
            }
        } else {
            if (currentUser == null) {
                Toast.makeText(this, "Пользователь не найден", Toast.LENGTH_SHORT).show();
                book.setEnabled(true);
                return;
            }
            pendingPassengerId = currentUser.getId();
        }

        bookingViewModel.hasBookingForUserAndTrip(pendingPassengerId, currentTrip.getId());
    }

    private void proceedWithBooking() {
        if (pendingPassengerId == -1) {
            Toast.makeText(this, "Ошибка: ID пассажира потерян", Toast.LENGTH_SHORT).show();
            if (book != null) book.setEnabled(true);
            return;
        }

        if (currentTrip == null) {
            Toast.makeText(this, "Состояние рейса устарело", Toast.LENGTH_SHORT).show();
            if (book != null) book.setEnabled(true);
            return;
        }

        double price = currentTrip.getPrice();
        boolean pet = ((CheckBox) findViewById(R.id.cbPet)).isChecked();
        boolean child = ((CheckBox) findViewById(R.id.cbChildSeat)).isChecked();
        if (pet) price += petPrice;
        if (child) price += childPrice;

        Booking booking = new Booking(currentTrip.getId(), pendingPassengerId, 1, child, pet, BookingStatus.PENDING, price);
        bookingViewModel.createBooking(booking);
    }

    private void showNoTripsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Рейсы не найдены")
                .setMessage("Для выбранного маршрута нет доступных рейсов. Пожалуйста, выберите другой маршрут.")
                .setPositiveButton("OK", (dialog, which) -> {
                    if (launchTripId == -1 && launchRouteId == -1) {
                        returnToRouteSelection();
                    } else if (launchRouteId != -1) {
                        finish();
                    }
                })
                .show();
    }

    public static Intent newIntentForRoute(Activity from, long routeId) {
        Intent i = new Intent(from, BookingActivity.class);
        i.putExtra(EXTRA_ROUTE_ID, routeId);
        return i;
    }

    public static Intent newIntentForTrip(Activity from, long tripId) {
        Intent i = new Intent(from, BookingActivity.class);
        i.putExtra(EXTRA_TRIP_ID, tripId);
        return i;
    }
}