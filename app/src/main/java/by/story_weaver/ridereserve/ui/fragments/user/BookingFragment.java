package by.story_weaver.ridereserve.ui.fragments.user;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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
import by.story_weaver.ridereserve.Logic.viewModels.MainViewModel;
import by.story_weaver.ridereserve.Logic.viewModels.ProfileViewModel;
import by.story_weaver.ridereserve.R;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BookingFragment extends Fragment {

    private CheckBox cbUseMyData, cbChildSeat, cbPet;
    private TextInputEditText etFullName, etPhone, etEmail;
    private TextInputLayout tilFullName, tilPhone, tilEmail;
    private MaterialButton book, btnSearchRoutes, btnBackToRoutes, btnBackToTrips;
    private User currentUser;
    private Trip currentTrip;
    private Route currentRoute;
    private BookingViewModel bookingViewModel;
    private ProfileViewModel profileViewModel;
    private MainViewModel mainViewModel;
    private TextInputEditText etRouteSearch;
    private Spinner spinnerFrom, spinnerTo;
    private RecyclerView recyclerRoutes, recyclerTrips;
    private ProgressBar progressTrips;
    private CardView cardRoutesList, cardTimeSelection, cardTripInfo, cardServices, cardPassenger, cardTotal;
    private TextView tvSelectedRoute, tvRouteInfo, tvDepartureTime, tvTripPrice, tvTotalPrice;
    private RoutesAdapter routesAdapter;
    private TripsAdapter tripsAdapter;
    private boolean isChild = false;
    private boolean isPet = false;
    private double totalPrice = 0;
    private double petPrice = 200;
    private double childPrice = 300;
    private long spRoute = -1;
    private List<Route> allRoutes = null;
    private static final String ARG_ROUTE = "route";
    private Handler refreshHandler = new Handler(Looper.getMainLooper());
    private Runnable refreshRunnable;

    // Guard to avoid double-registering observers across view re-creations
    private boolean observersInitialized = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            spRoute = getArguments().getLong(ARG_ROUTE, -1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_booking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bookingViewModel = new ViewModelProvider(requireActivity()).get(BookingViewModel.class);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        initViews(view);
        setupAdapters();
        setupButtonListeners();

        // Register observers once per view lifecycle
        if (!observersInitialized) {
            setupObservers();
            observersInitialized = true;
        }

        loadCurrentUser();
        loadInitialData();

        spRoute();
        resetToInitialState();
    }

    @Override
    public void onResume() {
        super.onResume();
        // don't reset state here to avoid wiping user input when returning from other screens
    }

    @Override
    public void onPause() {
        super.onPause();
        // cancel pending callbacks
        if (refreshHandler != null && refreshRunnable != null) {
            refreshHandler.removeCallbacks(refreshRunnable);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (refreshHandler != null && refreshRunnable != null) {
            refreshHandler.removeCallbacks(refreshRunnable);
            refreshRunnable = null;
        }

        if (recyclerRoutes != null) recyclerRoutes.setAdapter(null);
        if (recyclerTrips != null) recyclerTrips.setAdapter(null);

        if (routesAdapter != null) {
            routesAdapter.updateRoutes(new ArrayList<>());
            routesAdapter = null;
        }
        if (tripsAdapter != null) {
            tripsAdapter.updateTrips(new ArrayList<>());
            tripsAdapter = null;
        }

        // clear view refs to help GC
        cbUseMyData = null;
        cbChildSeat = null;
        cbPet = null;
        etFullName = null;
        etPhone = null;
        etEmail = null;
        tilFullName = null;
        tilPhone = null;
        tilEmail = null;
        book = null;
        etRouteSearch = null;
        spinnerFrom = null;
        spinnerTo = null;
        btnSearchRoutes = null;
        btnBackToRoutes = null;
        btnBackToTrips = null;
        recyclerRoutes = null;
        recyclerTrips = null;
        progressTrips = null;
        cardRoutesList = null;
        cardTimeSelection = null;
        cardTripInfo = null;
        cardServices = null;
        cardPassenger = null;
        cardTotal = null;
        tvSelectedRoute = null;
        tvRouteInfo = null;
        tvDepartureTime = null;
        tvTripPrice = null;
        tvTotalPrice = null;

        // allow observers to be re-registered next time view is created
        observersInitialized = false;
    }

    public static BookingFragment newInstance(long spRoute) {
        BookingFragment fragment = new BookingFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ROUTE, spRoute);
        fragment.setArguments(args);
        return fragment;
    }

    private void spRoute() {
        if (spRoute != -1) {
            if (allRoutes != null && !allRoutes.isEmpty()) {
                for (Route i : allRoutes) {
                    if (i != null && i.getId() == spRoute) {
                        onRouteSelected(i);
                        return;
                    }
                }
            }
            // if routes not loaded yet - retry shortly
            refreshRoute();
        }
    }

    private void refreshRoute() {
        if (refreshRunnable != null) {
            refreshHandler.removeCallbacks(refreshRunnable);
        }
        refreshRunnable = this::spRoute;
        refreshHandler.postDelayed(refreshRunnable, 300);
    }

    private void initViews(View view) {
        cbUseMyData = view.findViewById(R.id.cbUseMyData);
        cbChildSeat = view.findViewById(R.id.cbChildSeat);
        cbPet = view.findViewById(R.id.cbPet);
        etFullName = view.findViewById(R.id.etFullName);
        etPhone = view.findViewById(R.id.etPhone);
        etEmail = view.findViewById(R.id.etEmail);
        tilFullName = view.findViewById(R.id.tilFullName);
        tilPhone = view.findViewById(R.id.tilPhone);
        tilEmail = view.findViewById(R.id.tilEmail);
        book = view.findViewById(R.id.bookButton);
        etRouteSearch = view.findViewById(R.id.etRouteSearch);
        spinnerFrom = view.findViewById(R.id.spinnerFrom);
        spinnerTo = view.findViewById(R.id.spinnerTo);
        btnSearchRoutes = view.findViewById(R.id.btnSearchRoutes);
        btnBackToRoutes = view.findViewById(R.id.btnBackToRoutes);
        btnBackToTrips = view.findViewById(R.id.btnBackToTrips);
        recyclerRoutes = view.findViewById(R.id.recyclerRoutes);
        recyclerTrips = view.findViewById(R.id.recyclerTrips);
        progressTrips = view.findViewById(R.id.progressTrips);
        cardRoutesList = view.findViewById(R.id.cardRoutesList);
        cardTimeSelection = view.findViewById(R.id.cardTimeSelection);
        cardTripInfo = view.findViewById(R.id.cardTripInfo);
        cardServices = view.findViewById(R.id.cardServices);
        cardPassenger = view.findViewById(R.id.cardPassenger);
        cardTotal = view.findViewById(R.id.cardTotal);
        tvSelectedRoute = view.findViewById(R.id.tvSelectedRoute);
        tvRouteInfo = view.findViewById(R.id.tvRouteInfo);
        tvDepartureTime = view.findViewById(R.id.tvDepartureTime);
        tvTripPrice = view.findViewById(R.id.tvTripPrice);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
    }

    private void setupAdapters() {
        routesAdapter = new RoutesAdapter(new ArrayList<>(), this::onRouteSelected);
        recyclerRoutes.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerRoutes.setAdapter(routesAdapter);

        tripsAdapter = new TripsAdapter(new ArrayList<>(), this::onTripSelected);
        recyclerTrips.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerTrips.setAdapter(tripsAdapter);

        // initial spinner placeholders — will be replaced when cities arrive
        List<String> placeholder = new ArrayList<>();
        placeholder.add("Выберите город");
        ArrayAdapter<String> fromAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, placeholder);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(fromAdapter);

        ArrayAdapter<String> toAdapter = new ArrayAdapter<>(requireContext(),
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

        cbUseMyData.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                fillUserData();
                setFieldsEnabled(false);
            } else {
                setFieldsEnabled(true);
                clearFields();
            }
        });

        cbPet.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isPet = isChecked;
            updateTotalPrice();
        });

        cbChildSeat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isChild = isChecked;
            updateTotalPrice();
        });

        book.setOnClickListener(v -> createNewBooking());
    }

    private void setupObservers() {
        // Observers tied to view lifecycle so they are removed when view destroyed
        mainViewModel.closeRequest().observe(getViewLifecycleOwner(), request -> {
            resetToInitialState();
        });

        bookingViewModel.getBookingCreated().observe(getViewLifecycleOwner(), flag -> {
            switch (flag.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    Toast.makeText(requireContext(), "Бронь создана!", Toast.LENGTH_SHORT).show();
                    mainViewModel.closeFullscreen();
                    resetToInitialState();
                    break;
                case ERROR:
                    Toast.makeText(requireContext(), "Ошибка при создании брони: " + flag.message, Toast.LENGTH_LONG).show();
                    break;
            }
        });

        bookingViewModel.getFilteredRoutes().observe(getViewLifecycleOwner(), routes -> {
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
                        allRoutes = routes.data;
                        cardRoutesList.setVisibility(VISIBLE);
                    } else {
                        cardRoutesList.setVisibility(GONE);
                        Toast.makeText(requireContext(), "Маршруты не найдены", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case ERROR:
                    Toast.makeText(requireContext(), routes.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        bookingViewModel.getTripsForRoute().observe(getViewLifecycleOwner(), tripsState -> {
            if (progressTrips != null) progressTrips.setVisibility(GONE);

            switch (tripsState.status) {
                case SUCCESS:
                    if (tripsState.data != null && !tripsState.data.isEmpty()) {
                        tripsAdapter.updateTrips(tripsState.data);
                        cardTimeSelection.setVisibility(VISIBLE);

                        if (currentRoute != null) {
                            String origin = currentRoute.getOrigin() != null ? currentRoute.getOrigin() : "";
                            String destination = currentRoute.getDestination() != null ? currentRoute.getDestination() : "";
                            tvSelectedRoute.setText("Маршрут: " + origin + " - " + destination);
                        }

                        hideRemainingCards();
                    } else {
                        handleNoTripsFound();
                    }
                    break;
                case ERROR:
                    Toast.makeText(requireContext(), "Ошибка загрузки рейсов: " + tripsState.message, Toast.LENGTH_SHORT).show();
                    handleNoTripsFound();
                    break;
                case LOADING:
                    break;
            }
        });

        bookingViewModel.getIsHasBooking().observe(getViewLifecycleOwner(), hasBookingState -> {
            if (hasBookingState.status == UiState.Status.SUCCESS && hasBookingState.data != null) {
                if (hasBookingState.data) {
                    Toast.makeText(requireContext(), "Вы уже забронировали этот рейс", Toast.LENGTH_LONG).show();
                } else {
                    proceedWithBooking();
                }
            } else if (hasBookingState.status == UiState.Status.ERROR) {
                Toast.makeText(requireContext(), "Ошибка проверки брони: " + hasBookingState.message, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe all cities (from API) and populate spinners
        bookingViewModel.getAllCitiesLive().observe(getViewLifecycleOwner(), state -> {
            switch (state.status) {
                case LOADING:
                    // optionally show small progress
                    break;
                case SUCCESS:
                    List<String> cities = state.data != null ? state.data : new ArrayList<>();
                    List<String> spinnerItems = new ArrayList<>();
                    spinnerItems.add("Выберите город");
                    spinnerItems.addAll(cities);

                    ArrayAdapter<String> fromAdapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_spinner_item, spinnerItems);
                    fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    if (spinnerFrom != null) spinnerFrom.setAdapter(fromAdapter);

                    ArrayAdapter<String> toAdapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_spinner_item, spinnerItems);
                    toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    if (spinnerTo != null) spinnerTo.setAdapter(toAdapter);
                    break;
                case ERROR:
                    Toast.makeText(requireContext(), "Ошибка загрузки городов: " + state.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        // cache all routes when available
        bookingViewModel.getAllRoutes().observe(getViewLifecycleOwner(), list -> {
            if (list.status == UiState.Status.SUCCESS && list.data != null) {
                allRoutes = list.data;
            }
        });
    }

    private void loadCurrentUser() {
        currentUser = profileViewModel.getProfile();
    }

    private void loadInitialData() {
        bookingViewModel.loadAllRoutes();
        bookingViewModel.loadAllCities(); // <-- загрузка городов с сервера
    }

    // More robust reset that avoids triggering listeners unnecessarily
    private void resetToInitialState() {
        currentRoute = null;
        currentTrip = null;
        totalPrice = 0;
        isChild = false;
        isPet = false;

        if (cardRoutesList != null) cardRoutesList.setVisibility(VISIBLE);
        if (cardTimeSelection != null) cardTimeSelection.setVisibility(GONE);
        hideRemainingCards();

        if (tripsAdapter != null) tripsAdapter.updateTrips(new ArrayList<>());
        if (routesAdapter != null) routesAdapter.updateRoutes(new ArrayList<>());

        clearFields();

        // Avoid triggering checkbox listeners when programmatically changing checked state:
        if (cbUseMyData != null) {
            cbUseMyData.setOnCheckedChangeListener(null);
            cbUseMyData.setChecked(false);
            cbUseMyData.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    fillUserData();
                    setFieldsEnabled(false);
                } else {
                    setFieldsEnabled(true);
                    clearFields();
                }
            });
        }
        if (cbChildSeat != null) {
            cbChildSeat.setOnCheckedChangeListener(null);
            cbChildSeat.setChecked(false);
            cbChildSeat.setOnCheckedChangeListener((buttonView, isChecked) -> {
                isChild = isChecked;
                updateTotalPrice();
            });
        }
        if (cbPet != null) {
            cbPet.setOnCheckedChangeListener(null);
            cbPet.setChecked(false);
            cbPet.setOnCheckedChangeListener((buttonView, isChecked) -> {
                isPet = isChecked;
                updateTotalPrice();
            });
        }

        if (spinnerFrom != null) spinnerFrom.setSelection(0);
        if (spinnerTo != null) spinnerTo.setSelection(0);
        if (etRouteSearch != null) etRouteSearch.setText("");
        if (tvTotalPrice != null) tvTotalPrice.setText(String.format("%.2f BYN", 0.0));
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
            Toast.makeText(requireContext(), "Введите номер маршрута или выберите пункты", Toast.LENGTH_SHORT).show();
        }
    }

    private void onRouteSelected(Route route) {
        if (route == null) return;
        currentRoute = route;
        if (progressTrips != null) progressTrips.setVisibility(VISIBLE);
        bookingViewModel.loadTripsForRoute(route.getId());
        if (cardRoutesList != null) cardRoutesList.setVisibility(GONE);
    }

    private void onTripSelected(Trip trip) {
        if (trip == null) return;
        currentTrip = trip;
        updateTripInfo(trip);
        showRemainingCards();
    }

    private void updateTripInfo(Trip trip) {
        if (currentRoute != null && trip != null) {
            String origin = currentRoute.getOrigin() != null ? currentRoute.getOrigin() : "";
            String destination = currentRoute.getDestination() != null ? currentRoute.getDestination() : "";

            if (tvRouteInfo != null) tvRouteInfo.setText(origin + " - " + destination);
            if (tvDepartureTime != null) tvDepartureTime.setText(formatDateTime(trip.getDepartureTime()));
            if (tvTripPrice != null) tvTripPrice.setText(String.format("%.2f BYN", trip.getPrice()));

            updateTotalPrice();
        }
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
        if (cardRoutesList != null) cardRoutesList.setVisibility(VISIBLE);
        if (cardTimeSelection != null) cardTimeSelection.setVisibility(GONE);
        hideRemainingCards();
        if (tripsAdapter != null) tripsAdapter.updateTrips(new ArrayList<>());
    }

    private void returnToTripSelection() {
        currentTrip = null;
        if (cardTimeSelection != null) cardTimeSelection.setVisibility(VISIBLE);
        hideRemainingCards();
    }

    private void updateTotalPrice() {
        if (currentTrip != null && tvTotalPrice != null) {
            totalPrice = currentTrip.getPrice();
            if (isPet) totalPrice += petPrice;
            if (isChild) totalPrice += childPrice;
            tvTotalPrice.setText(String.format("%.2f BYN", totalPrice));
        }
    }

    private void handleNoTripsFound() {
        if (cardTimeSelection != null) cardTimeSelection.setVisibility(GONE);
        if (cardRoutesList != null) cardRoutesList.setVisibility(VISIBLE);
        hideRemainingCards();
        currentRoute = null;
        showNoTripsDialog();
    }

    private void showNoTripsDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Рейсы не найдены")
                .setMessage("Для выбранного маршрута нет доступных рейсов. Пожалуйста, выберите другой маршрут.")
                .setPositiveButton("OK", (dialog, which) -> {})
                .show();
    }

    private String formatDateTime(String dateTime) {
        if (dateTime == null) return "";
        return dateTime.replace("T", " ").replace("-", ".");
    }

    private void fillUserData() {
        if (currentUser != null) {
            if (etFullName != null) etFullName.setText(currentUser.getFullName() != null ? currentUser.getFullName() : "");
            if (etPhone != null) etPhone.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "");
            if (etEmail != null) etEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "");
        }
    }

    private void setFieldsEnabled(boolean enabled) {
        if (etFullName != null) etFullName.setEnabled(enabled);
        if (etPhone != null) etPhone.setEnabled(enabled);
        if (etEmail != null) etEmail.setEnabled(enabled);

        int boxStrokeColor = enabled ? R.color.green : R.color.gray_400;
        int hintColor = enabled ? R.color.gray_600 : R.color.gray_400;
        int textColor = enabled ? R.color.black : R.color.gray_600;

        if (tilFullName != null) setFieldAppearance(tilFullName, boxStrokeColor, hintColor, textColor);
        if (tilPhone != null) setFieldAppearance(tilPhone, boxStrokeColor, hintColor, textColor);
        if (tilEmail != null) setFieldAppearance(tilEmail, boxStrokeColor, hintColor, textColor);
    }

    private void setFieldAppearance(TextInputLayout textInputLayout, int boxStrokeColor, int hintColor, int textColor) {
        textInputLayout.setBoxStrokeColor(ContextCompat.getColor(requireContext(), boxStrokeColor));
        textInputLayout.setHintTextColor(ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), hintColor)));

        EditText editText = textInputLayout.getEditText();
        if (editText != null) {
            editText.setTextColor(ContextCompat.getColor(requireContext(), textColor));
        }
    }

    private void clearFields() {
        if (etFullName != null) etFullName.setText("");
        if (etPhone != null) etPhone.setText("");
        if (etEmail != null) etEmail.setText("");
    }

    private boolean isFormValid() {
        boolean isValid = true;

        if (etFullName == null || Objects.requireNonNull(etFullName.getText()).toString().trim().isEmpty()) {
            if (tilFullName != null) tilFullName.setError("Введите ФИО");
            isValid = false;
        } else {
            if (tilFullName != null) tilFullName.setError(null);
        }

        if (etPhone == null || Objects.requireNonNull(etPhone.getText()).toString().trim().isEmpty()) {
            if (tilPhone != null) tilPhone.setError("Введите телефон");
            isValid = false;
        } else {
            if (tilPhone != null) tilPhone.setError(null);
        }

        if (etEmail == null || Objects.requireNonNull(etEmail.getText()).toString().trim().isEmpty()) {
            if (tilEmail != null) tilEmail.setError("Введите email");
            isValid = false;
        } else {
            if (tilEmail != null) tilEmail.setError(null);
        }

        return isValid;
    }

    private void createNewBooking() {
        if (currentTrip == null) {
            Toast.makeText(requireContext(), "Выберите рейс", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isFormValid()) {
            return;
        }

        long passengerId;
        if (!cbUseMyData.isChecked()) {
            String name = Objects.requireNonNull(etFullName.getText()).toString();
            String phone = Objects.requireNonNull(etPhone.getText()).toString();
            String email = Objects.requireNonNull(etEmail.getText()).toString();

            // Create guest user locally
            User guestUser = new User(-1, email, "guest_temp_password", name, phone, 1, UserRole.GUEST);
            boolean guestAdded = profileViewModel.addGuest(guestUser);
            if (!guestAdded) {
                Toast.makeText(requireContext(), "Ошибка при создании гостевого аккаунта", Toast.LENGTH_SHORT).show();
                return;
            }

            passengerId = profileViewModel.getIdByEmail(email);
            if (passengerId == -1) {
                Toast.makeText(requireContext(), "Не удалось получить ID пользователя", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            if (currentUser != null) {
                passengerId = currentUser.getId();
            } else {
                Toast.makeText(requireContext(), "Пользователь не найден", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Check if booking already exists
        bookingViewModel.hasBookingForUserAndTrip(passengerId, currentTrip.getId());
    }

    private void proceedWithBooking() {
        long passengerId;
        if (!cbUseMyData.isChecked()) {
            String email = Objects.requireNonNull(etEmail.getText()).toString();
            passengerId = profileViewModel.getIdByEmail(email);
        } else {
            passengerId = currentUser.getId();
        }

        totalPrice = currentTrip.getPrice();
        if (isPet) totalPrice += petPrice;
        if (isChild) totalPrice += childPrice;

        Booking booking = new Booking(currentTrip.getId(), passengerId, 1, isChild, isPet, BookingStatus.PENDING, totalPrice);
        bookingViewModel.createBooking(booking);
    }
}
