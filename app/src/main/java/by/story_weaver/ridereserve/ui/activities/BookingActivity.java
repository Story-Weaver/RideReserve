package by.story_weaver.ridereserve.ui.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
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
import by.story_weaver.ridereserve.Logic.viewModels.ProfileViewModel;
import by.story_weaver.ridereserve.Logic.viewModels.MainViewModel;
import by.story_weaver.ridereserve.R;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BookingActivity extends AppCompatActivity {

    public static final String EXTRA_TRIP_ID = "extra_trip_id";
    public static final String EXTRA_ROUTE_ID = "extra_route_id";
    public static final String RESULT_BOOKING_CREATED = "result_booking_created";

    private TextInputEditText etFullName, etPhone, etEmail, etRouteSearch;
    private TextInputLayout tilFullName, tilPhone, tilEmail;
    private MaterialButton book, btnSearchRoutes, btnBackToRoutes, btnBackToTrips;
    private Spinner spinnerFrom, spinnerTo;
    private RecyclerView recyclerRoutes, recyclerTrips;
    private TextView textRouteCard;
    private ProgressBar progressTrips;
    private CardView cardRoutesList, cardTimeSelection, cardTripInfo, cardServices, cardPassenger, cardTotal;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_booking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scroll_booking), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
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

        if (launchTripId != -1) {
            bookingViewModel.loadTripById(launchTripId);
        } else if (launchRouteId != -1) {
            bookingViewModel.loadAllRoutes();
        }
    }

    private void initViews() {
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        tilFullName = findViewById(R.id.tilFullName);
        tilPhone = findViewById(R.id.tilPhone);
        tilEmail = findViewById(R.id.tilEmail);
        textRouteCard = findViewById(R.id.textRouteCard);

        etRouteSearch = findViewById(R.id.etRouteSearch);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);

        btnSearchRoutes = findViewById(R.id.btnSearchRoutes);
        btnBackToRoutes = findViewById(R.id.btnBackToRoutes);
        btnBackToTrips = findViewById(R.id.btnBackToTrips);

        recyclerRoutes = findViewById(R.id.recyclerRoutes);
        recyclerTrips = findViewById(R.id.recyclerTrips);
        progressTrips = findViewById(R.id.progressTrips);

        cardRoutesList = findViewById(R.id.cardRoutesList);
        cardTimeSelection = findViewById(R.id.cardTimeSelection);
        cardTripInfo = findViewById(R.id.cardTripInfo);
        cardServices = findViewById(R.id.cardServices);
        cardPassenger = findViewById(R.id.cardPassenger);
        cardTotal = findViewById(R.id.cardTotal);

        tvHelperInitTotal();

        book = findViewById(R.id.bookButton);
    }

    private void tvHelperInitTotal() {
        // ensure tvTotalPrice exists in layout (it does in fragment layout)
        // nothing special to do here
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

        // Добавьте этот обработчик для чекбокса
        CheckBox cbUseMyData = findViewById(R.id.cbUseMyData);
        cbUseMyData.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && currentUser != null) {
                etFullName.setText(currentUser.getFullName());
                etPhone.setText(currentUser.getPhone());
                etEmail.setText(currentUser.getEmail());

                etFullName.setEnabled(false);
                etPhone.setEnabled(false);
                etEmail.setEnabled(false);

                tilFullName.setError(null);
                tilPhone.setError(null);
                tilEmail.setError(null);
            } else {
                etFullName.setEnabled(true);
                etPhone.setEnabled(true);
                etEmail.setEnabled(true);

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
                routesAdapter.updateRoutes(list.data);
                if (launchRouteId != -1) {
                    for (Route r : list.data) {
                        if (r != null && r.getId() == launchRouteId) {
                            onRouteSelected(r);
                            break;
                        }
                    }
                } else {
                    textRouteCard.setText("Все маршруты: ");
                    cardRoutesList.setVisibility(VISIBLE);
                }
            }
        });
        if (launchRouteId != -1) {
            bookingViewModel.loadTripsForRoute(launchRouteId);
        }
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
                        cardRoutesList.setVisibility(VISIBLE);
                    } else {
                        cardRoutesList.setVisibility(GONE);
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
                cardTimeSelection.setVisibility(VISIBLE);
                hideRemainingCards();
            } else if (tripsState.status == UiState.Status.ERROR) {
                Toast.makeText(this, "Ошибка загрузки рейсов: " + tripsState.message, Toast.LENGTH_SHORT).show();
            }
        });

        bookingViewModel.getIsHasBooking().observe(this, hasBookingState -> {
            if (hasBookingState.status == UiState.Status.SUCCESS && hasBookingState.data != null) {
                if (currentTrip == null) {
                    // race: trip was cleared or not selected — show message and re-enable button
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

        bookingViewModel.getAllRoutes().observe(this, ignored -> {});
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
        String routeNumber = etRouteSearch != null ? etRouteSearch.getText().toString().trim() : "";
        String from = spinnerFrom != null && spinnerFrom.getSelectedItem() != null ? spinnerFrom.getSelectedItem().toString() : "";
        String to = spinnerTo != null && spinnerTo.getSelectedItem() != null ? spinnerTo.getSelectedItem().toString() : "";

        if (!routeNumber.isEmpty()) {
            bookingViewModel.loadRoutesByNumber(routeNumber);
        } else if (!from.equals("Выберите город") && !to.equals("Выберите город")) {
            bookingViewModel.loadRoutesByPoints(from, to);
        } else {
            bookingViewModel.loadAllRoutes();
        }
    }

    private void onRouteSelected(Route route) {
        if (route == null) return;
        currentRoute = route;
        progressTrips.setVisibility(VISIBLE);
        bookingViewModel.loadTripsForRoute(route.getId());
        cardRoutesList.setVisibility(GONE);
    }

    private void onTripSelected(Trip trip) {
        if (trip == null) return;
        currentTrip = trip;
        updateTripInfo(trip);
        showRemainingCards();
    }

    private void updateTripInfo(Trip trip) {
        if (trip == null || currentRoute == null) return;
        findViewById(R.id.tvRouteInfo).setVisibility(VISIBLE);
        String origin = currentRoute.getOrigin() != null ? currentRoute.getOrigin() : "";
        String destination = currentRoute.getDestination() != null ? currentRoute.getDestination() : "";
        ((android.widget.TextView) findViewById(R.id.tvRouteInfo)).setText(origin + " - " + destination);
        ((android.widget.TextView) findViewById(R.id.tvDepartureTime)).setText(formatDateTime(trip.getDepartureTime()));
        ((android.widget.TextView) findViewById(R.id.tvTripPrice)).setText(String.format("%.2f BYN", trip.getPrice()));
        updateTotalPrice();
    }

    private void showRemainingCards() {
        cardTripInfo.setVisibility(VISIBLE);
        cardServices.setVisibility(VISIBLE);
        cardPassenger.setVisibility(VISIBLE);
        cardTotal.setVisibility(VISIBLE);
        book.setVisibility(VISIBLE);
    }

    private void hideRemainingCards() {
        cardTripInfo.setVisibility(GONE);
        cardServices.setVisibility(GONE);
        cardPassenger.setVisibility(GONE);
        cardTotal.setVisibility(GONE);
        book.setVisibility(GONE);
    }

    private void returnToRouteSelection() {
        currentRoute = null;
        currentTrip = null;
        cardRoutesList.setVisibility(VISIBLE);
        cardTimeSelection.setVisibility(GONE);
        hideRemainingCards();
        tripsAdapter.updateTrips(new ArrayList<>());
    }

    private void returnToTripSelection() {
        currentTrip = null;
        cardTimeSelection.setVisibility(VISIBLE);
        hideRemainingCards();
    }

    private void updateTotalPrice() {
        if (currentTrip == null) return;
        totalPrice = currentTrip.getPrice();
        if (isPet) totalPrice += petPrice;
        if (isChild) totalPrice += childPrice;
        ((android.widget.TextView) findViewById(R.id.tvTotalPrice)).setText(String.format("%.2f BYN", totalPrice));
    }

    private String formatDateTime(String dateTime) {
        if (dateTime == null) return "";
        return dateTime.replace("T", " ").replace("-", ".");
    }

    private boolean isFormValid() {
        boolean isValid = true;

        CheckBox cbUseMyData = findViewById(R.id.cbUseMyData);

        if (cbUseMyData.isChecked()) {
            if (currentUser == null) {
                Toast.makeText(this, "Пользователь не найден", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }

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

        long passengerId;
        if (!((android.widget.CheckBox) findViewById(R.id.cbUseMyData)).isChecked()) {
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
            passengerId = profileViewModel.getIdByEmail(email);
            if (passengerId == -1) {
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
            passengerId = currentUser.getId();
        }

        bookingViewModel.hasBookingForUserAndTrip(passengerId, currentTrip.getId());
    }

    private void proceedWithBooking() {
        if (currentTrip == null) {
            Toast.makeText(this, "Состояние рейса устарело, повторите выбор рейса", Toast.LENGTH_SHORT).show();
            if (book != null) book.setEnabled(true);
            return;
        }

        long passengerId;
        if (!((android.widget.CheckBox) findViewById(R.id.cbUseMyData)).isChecked()) {
            String email = Objects.requireNonNull(etEmail.getText()).toString();
            passengerId = profileViewModel.getIdByEmail(email);
        } else {
            if (currentUser == null) {
                Toast.makeText(this, "Пользователь не найден", Toast.LENGTH_SHORT).show();
                if (book != null) book.setEnabled(true);
                return;
            }
            passengerId = currentUser.getId();
        }

        double price = currentTrip.getPrice();
        boolean pet = ((android.widget.CheckBox) findViewById(R.id.cbPet)).isChecked();
        boolean child = ((android.widget.CheckBox) findViewById(R.id.cbChildSeat)).isChecked();
        if (pet) price += petPrice;
        if (child) price += childPrice;

        Booking booking = new Booking(currentTrip.getId(), passengerId, 1, child, pet, BookingStatus.PENDING, price);
        bookingViewModel.createBooking(booking);
    }

    private void showNoTripsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Рейсы не найдены")
                .setMessage("Для выбранного маршрута нет доступных рейсов. Пожалуйста, выберите другой маршрут.")
                .setPositiveButton("OK", (dialog, which) -> {})
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
