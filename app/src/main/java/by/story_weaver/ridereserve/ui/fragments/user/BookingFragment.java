package by.story_weaver.ridereserve.ui.fragments.user;

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
import by.story_weaver.ridereserve.Logic.viewModels.BookingViewModel;
import by.story_weaver.ridereserve.Logic.viewModels.MainViewModel;
import by.story_weaver.ridereserve.Logic.viewModels.ProfileViewModel;
import by.story_weaver.ridereserve.R;
import by.story_weaver.ridereserve.ui.fragments.UserEditFragment;
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


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            spRoute = getArguments().getLong(ARG_ROUTE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_booking, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        resetToInitialState();
    }

    public static BookingFragment newInstance(long spRoute) {
        BookingFragment fragment = new BookingFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ROUTE, spRoute);
        fragment.setArguments(args);
        return fragment;
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
        setupObservers();
        loadCurrentUser();
        loadInitialData();

        spRoute();
        resetToInitialState();
    }
    private void spRoute(){
        if(spRoute != -1){
            if(allRoutes != null){
                for (Route i: allRoutes) {
                    if(i.getId() == spRoute){
                        onRouteSelected(i);
                    }
                }
            } else {
                refreshRoute();
            }
        }
    }

    private void refreshRoute() {
        if (refreshRunnable != null) {
            refreshHandler.removeCallbacks(refreshRunnable);
        }
        refreshRunnable = this::spRoute;
        refreshHandler.postDelayed(refreshRunnable, 500);
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

        ArrayAdapter<String> fromAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, getCities());
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(fromAdapter);

        ArrayAdapter<String> toAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, getCities());
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTo.setAdapter(toAdapter);
    }

    private void setupButtonListeners() {
        btnSearchRoutes.setOnClickListener(v -> searchRoutes());

        // Кнопки возврата
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

    private void loadCurrentUser() {
        currentUser = profileViewModel.getProfile();
    }

    private void setupObservers() {
        bookingViewModel.getRoutes().observe(getViewLifecycleOwner(), routes -> {
            if (routes != null && !routes.isEmpty()) {
                routesAdapter.updateRoutes(routes);
                allRoutes = routes;
                cardRoutesList.setVisibility(View.VISIBLE);
            } else {
                cardRoutesList.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Маршруты не найдены", Toast.LENGTH_SHORT).show();
            }
        });

        // Наблюдаем за списком рейсов
        bookingViewModel.getTrips().observe(getViewLifecycleOwner(), trips -> {
            progressTrips.setVisibility(View.GONE);

            if (trips != null && !trips.isEmpty()) {
                tripsAdapter.updateTrips(trips);
                cardTimeSelection.setVisibility(View.VISIBLE);

                // БЕЗОПАСНОЕ обновление - проверяем что currentRoute не null
                if (currentRoute != null) {
                    String origin = currentRoute.getOrigin() != null ? currentRoute.getOrigin() : "";
                    String destination = currentRoute.getDestination() != null ? currentRoute.getDestination() : "";
                    tvSelectedRoute.setText("Маршрут: " + origin + " - " + destination);
                }

                // Скрываем остальные карточки пока не выбран конкретный рейс
                hideRemainingCards();
            } else {
                // Если рейсов нет - возвращаемся к выбору маршрута
                handleNoTripsFound();
            }
        });

        // Наблюдаем за состоянием загрузки
        bookingViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && isLoading) {
                progressTrips.setVisibility(View.VISIBLE);
                recyclerTrips.setVisibility(View.GONE);
            } else {
                progressTrips.setVisibility(View.GONE);
                recyclerTrips.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadInitialData() {
        // Загрузка начальных данных (города и т.д.)
        bookingViewModel.loadAllRoutes();
    }

    private void resetToInitialState() {
        // Сбрасываем все состояния и показываем только выбор маршрута
        currentRoute = null;
        currentTrip = null;
        cardRoutesList.setVisibility(View.VISIBLE);
        cardTimeSelection.setVisibility(View.GONE);
        hideRemainingCards();
        tripsAdapter.updateTrips(new ArrayList<>());
        clearFields();
        cbUseMyData.setChecked(false);
        cbChildSeat.setChecked(false);
        cbPet.setChecked(false);
        etRouteSearch.setText("");
    }

    private void searchRoutes() {
        String routeNumber = etRouteSearch.getText().toString().trim();
        String from = spinnerFrom.getSelectedItem().toString();
        String to = spinnerTo.getSelectedItem().toString();

        if (!routeNumber.isEmpty()) {
            // Поиск по номеру маршрута
            bookingViewModel.searchRoutesByNumber(routeNumber);
        } else if (!from.equals("Выберите город") && !to.equals("Выберите город")) {
            // Поиск по пунктам отправления/назначения
            bookingViewModel.searchRoutesByPoints(from, to);
        } else {
            Toast.makeText(requireContext(), "Введите номер маршрута или выберите пункты", Toast.LENGTH_SHORT).show();
        }
    }

    private void onRouteSelected(Route route) {
        currentRoute = route;
        progressTrips.setVisibility(View.VISIBLE);

        bookingViewModel.loadTripsForRoute(route.getId());

        cardRoutesList.setVisibility(View.GONE);
    }

    private void onTripSelected(Trip trip) {
        currentTrip = trip;
        // Показываем информацию о выбранном рейсе
        updateTripInfo(trip);
        showRemainingCards();
    }

    private void updateTripInfo(Trip trip) {
        // БЕЗОПАСНОЕ обновление - проверяем что currentRoute не null
        if (currentRoute != null && trip != null) {
            String origin = currentRoute.getOrigin() != null ? currentRoute.getOrigin() : "";
            String destination = currentRoute.getDestination() != null ? currentRoute.getDestination() : "";

            tvRouteInfo.setText(origin + " - " + destination);
            tvDepartureTime.setText(formatDateTime(trip.getDepartureTime()));
            tvTripPrice.setText(String.format("%.2f ₽", trip.getPrice()));

            // Обновляем общую стоимость
            updateTotalPrice();
        }
    }

    private void showRemainingCards() {
        cardTripInfo.setVisibility(View.VISIBLE);
        cardServices.setVisibility(View.VISIBLE);
        cardPassenger.setVisibility(View.VISIBLE);
        cardTotal.setVisibility(View.VISIBLE);
        book.setVisibility(View.VISIBLE);
    }

    private void hideRemainingCards() {
        cardTripInfo.setVisibility(View.GONE);
        cardServices.setVisibility(View.GONE);
        cardPassenger.setVisibility(View.GONE);
        cardTotal.setVisibility(View.GONE);
        book.setVisibility(View.GONE);
    }

    private void returnToRouteSelection() {
        // Возврат к выбору маршрута
        currentRoute = null;
        currentTrip = null;
        cardRoutesList.setVisibility(View.VISIBLE);
        cardTimeSelection.setVisibility(View.GONE);
        hideRemainingCards();
        tripsAdapter.updateTrips(new ArrayList<>());
    }

    private void returnToTripSelection() {
        // Возврат к выбору рейса
        currentTrip = null;
        cardTimeSelection.setVisibility(View.VISIBLE);
        hideRemainingCards();
    }

    private void updateTotalPrice() {
        if (currentTrip != null) {
            totalPrice = currentTrip.getPrice();
            if (isPet) totalPrice += petPrice;
            if (isChild) totalPrice += childPrice;
            tvTotalPrice.setText(String.format("%.2f ₽", totalPrice));
        }
    }

    private List<String> getCities() {
        List<String> cities = new ArrayList<>();
        cities.add("Выберите город");
        cities.add("Москва");
        cities.add("Санкт-Петербург");
        cities.add("Казань");
        cities.add("Сочи");
        cities.add("Минск");
        cities.add("Вильнюс");
        return cities;
    }

    private void handleNoTripsFound() {
        // Скрываем карточку выбора времени
        cardTimeSelection.setVisibility(View.GONE);

        // Показываем снова список маршрутов
        cardRoutesList.setVisibility(View.VISIBLE);

        // Скрываем все остальные карточки
        hideRemainingCards();

        // Сбрасываем выбранный маршрут
        currentRoute = null;

        // Показываем сообщение пользователю
        showNoTripsDialog();
    }

    private void showNoTripsDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Рейсы не найдены")
                .setMessage("Для выбранного маршрута нет доступных рейсов. Пожалуйста, выберите другой маршрут.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Диалог закрывается, пользователь остается в выборе маршрута
                })
                .show();
    }

    private String formatDateTime(String dateTime) {
        if (dateTime == null) return "";
        // Простое форматирование
        return dateTime.replace("T", " ").replace("-", ".");
    }

    private void fillUserData() {
        if (currentUser != null) {
            etFullName.setText(currentUser.getFullName() != null ? currentUser.getFullName() : "");
            etPhone.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "");
            etEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "");
        }
    }

    private void setFieldsEnabled(boolean enabled) {
        etFullName.setEnabled(enabled);
        etPhone.setEnabled(enabled);
        etEmail.setEnabled(enabled);

        int boxStrokeColor = enabled ? R.color.green : R.color.gray_400;
        int hintColor = enabled ? R.color.gray_600 : R.color.gray_400;
        int textColor = enabled ? R.color.black : R.color.gray_600;

        setFieldAppearance(tilFullName, boxStrokeColor, hintColor, textColor);
        setFieldAppearance(tilPhone, boxStrokeColor, hintColor, textColor);
        setFieldAppearance(tilEmail, boxStrokeColor, hintColor, textColor);
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
        etFullName.setText("");
        etPhone.setText("");
        etEmail.setText("");
    }

    private boolean isFormValid() {
        boolean isValid = true;

        if (Objects.requireNonNull(etFullName.getText()).toString().trim().isEmpty()) {
            tilFullName.setError("Введите ФИО");
            isValid = false;
        } else {
            tilFullName.setError(null);
        }

        if (Objects.requireNonNull(etPhone.getText()).toString().trim().isEmpty()) {
            tilPhone.setError("Введите телефон");
            isValid = false;
        } else {
            tilPhone.setError(null);
        }

        if (Objects.requireNonNull(etEmail.getText()).toString().trim().isEmpty()) {
            tilEmail.setError("Введите email");
            isValid = false;
        } else {
            tilEmail.setError(null);
        }

        return isValid;
    }

    private void createNewBooking(){
        if (currentTrip == null) {
            Toast.makeText(requireContext(), "Выберите рейс", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isFormValid()) {
            return;
        }

        String name = null;
        String phone = null;
        String email = null;
        long passengerId = -1;

        if(!cbUseMyData.isChecked()){
            name = Objects.requireNonNull(etFullName.getText()).toString();
            phone = Objects.requireNonNull(etPhone.getText()).toString();
            email = Objects.requireNonNull(etEmail.getText()).toString();
            currentUser = new User(-1, email, "nul2345gfvsgfsvdfavf4etsdfwdfvfgbsfdgfjsfgdsfhl", name, phone, -1, UserRole.GUEST);

            // Сохраняем гостя и получаем его ID
            boolean guestAdded = profileViewModel.addGuest(currentUser);
            if (!guestAdded) {
                Toast.makeText(requireContext(), "Ошибка при создании гостевого аккаунта", Toast.LENGTH_SHORT).show();
                return;
            }

            passengerId = profileViewModel.getIdByEmail(currentUser.getEmail());
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

        if (passengerId == -1) {
            Toast.makeText(requireContext(), "Неверный ID пользователя", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentTrip.getId() == -1) {
            Toast.makeText(requireContext(), "Неверный рейс", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean hasExistingBooking = bookingViewModel.hasBookingForUserAndTrip(passengerId, currentTrip.getId());
        if (hasExistingBooking) {
            Toast.makeText(requireContext(), "Вы уже забронировали этот рейс", Toast.LENGTH_LONG).show();
            return;
        }

        totalPrice = currentTrip.getPrice();
        if(isPet){
            totalPrice += petPrice;
        }
        if (isChild){
            totalPrice += childPrice;
        }

        try {
            Booking booking = new Booking(-1, currentTrip.getId(), passengerId, 1,
                    isChild, isPet, BookingStatus.PENDING, totalPrice);

            bookingViewModel.addBooking(booking);
            Toast.makeText(requireContext(), "Бронь создана!", Toast.LENGTH_SHORT).show();

            mainViewModel.closeFullscreen();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Ошибка при создании брони: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        refreshHandler.removeCallbacks(refreshRunnable);
    }
}