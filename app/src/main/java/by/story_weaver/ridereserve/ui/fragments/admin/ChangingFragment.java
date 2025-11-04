package by.story_weaver.ridereserve.ui.fragments.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import by.story_weaver.ridereserve.Logic.adapters.GenericAdapter;
import by.story_weaver.ridereserve.Logic.data.enums.TripStatus;
import by.story_weaver.ridereserve.Logic.data.enums.UserRole;
import by.story_weaver.ridereserve.Logic.data.models.Route;
import by.story_weaver.ridereserve.Logic.data.models.Trip;
import by.story_weaver.ridereserve.Logic.data.models.User;
import by.story_weaver.ridereserve.Logic.data.models.Vehicle;
import by.story_weaver.ridereserve.Logic.utils.UiState;
import by.story_weaver.ridereserve.Logic.viewModels.AdminViewModel;
import by.story_weaver.ridereserve.R;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChangingFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private MaterialButton btnAdd;
    private Spinner spinnerEntityType;
    private TextView tvEmptyList;
    private GenericAdapter adapter;
    private AdminViewModel viewModel;

    private enum EntityType { USER, ROUTE, TRIP, VEHICLE }
    private EntityType currentType = EntityType.USER;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_changing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        setupAdapter();
        setupSpinner();
        setupClickListeners();
        setupObservers();

        loadEntities();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rvEntities);
        progressBar = view.findViewById(R.id.progressLoading);
        btnAdd = view.findViewById(R.id.btnAddEntity);
        spinnerEntityType = view.findViewById(R.id.spinnerEntityType);
        tvEmptyList = view.findViewById(R.id.tvEmptyList);
    }

    private void setupAdapter() {
        adapter = new GenericAdapter(new GenericAdapter.ItemListener() {
            @Override
            public void onEditObject(Object item) {
                handleEditObject(item);
            }

            @Override
            public void onDeleteObject(Object item) {
                handleDeleteObject(item);
            }

            @Override
            public void onPromoteToDriver(Object item) {
                handlePromoteToDriver(item);
            }

            @Override
            public void onDemoteToPassenger(Object item) {
                handleDemoteToPassenger(item);
            }
        });
        adapter.setType(GenericAdapter.Type.USER);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.entity_types,
                android.R.layout.simple_spinner_item
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEntityType.setAdapter(spinnerAdapter);

        spinnerEntityType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: currentType = EntityType.USER; break;
                    case 1: currentType = EntityType.ROUTE; break;
                    case 2: currentType = EntityType.TRIP; break;
                    case 3: currentType = EntityType.VEHICLE; break;
                }
                adapter.setType(getAdapterType(currentType));
                loadEntities();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private GenericAdapter.Type getAdapterType(EntityType entityType) {
        switch (entityType) {
            case USER: return GenericAdapter.Type.USER;
            case ROUTE: return GenericAdapter.Type.ROUTE;
            case TRIP: return GenericAdapter.Type.TRIP;
            case VEHICLE: return GenericAdapter.Type.VEHICLE;
            default: return GenericAdapter.Type.USER;
        }
    }

    private void setupClickListeners() {
        btnAdd.setOnClickListener(v -> openAddDialog());
    }

    private void setupObservers() {
        //TODO обсерв удаления
        viewModel.getUsers().observe(getViewLifecycleOwner(), usersState -> {
            if (usersState != null) {
                switch (usersState.status) {
                    case LOADING:
                        showLoading(true);
                        break;
                    case SUCCESS:
                        showLoading(false);
                        if (currentType == EntityType.USER) {
                            List<User> userList = usersState.data != null ? usersState.data : new ArrayList<>();
                            adapter.setUsers(userList);
                            updateEmptyState(userList.isEmpty());
                        }
                        break;
                    case ERROR:
                        showLoading(false);
                        Toast.makeText(requireContext(), "Ошибка загрузки пользователей: " + usersState.message,
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        // Наблюдаем за маршрутами
        viewModel.getRoutes().observe(getViewLifecycleOwner(), routesState -> {
            if (routesState != null) {
                switch (routesState.status) {
                    case LOADING:
                        showLoading(true);
                        break;
                    case SUCCESS:
                        showLoading(false);
                        if (currentType == EntityType.ROUTE) {
                            List<Route> routeList = routesState.data != null ? routesState.data : new ArrayList<>();
                            adapter.setRoutes(routeList);
                            updateEmptyState(routeList.isEmpty());
                        }
                        break;
                    case ERROR:
                        showLoading(false);
                        Toast.makeText(requireContext(), "Ошибка загрузки маршрутов: " + routesState.message,
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        // Наблюдаем за поездками
        viewModel.getTrips().observe(getViewLifecycleOwner(), tripsState -> {
            if (tripsState != null) {
                switch (tripsState.status) {
                    case LOADING:
                        showLoading(true);
                        break;
                    case SUCCESS:
                        showLoading(false);
                        if (currentType == EntityType.TRIP) {
                            List<Trip> tripList = tripsState.data != null ? tripsState.data : new ArrayList<>();
                            adapter.setTrips(tripList);
                            updateEmptyState(tripList.isEmpty());
                        }
                        break;
                    case ERROR:
                        showLoading(false);
                        Toast.makeText(requireContext(), "Ошибка загрузки поездок: " + tripsState.message,
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        // Наблюдаем за транспортом
        viewModel.getVehicles().observe(getViewLifecycleOwner(), vehiclesState -> {
            if (vehiclesState != null) {
                switch (vehiclesState.status) {
                    case LOADING:
                        showLoading(true);
                        break;
                    case SUCCESS:
                        showLoading(false);
                        if (currentType == EntityType.VEHICLE) {
                            List<Vehicle> vehicleList = vehiclesState.data != null ? vehiclesState.data : new ArrayList<>();
                            adapter.setVehicles(vehicleList);
                            updateEmptyState(vehicleList.isEmpty());
                        }
                        break;
                    case ERROR:
                        showLoading(false);
                        Toast.makeText(requireContext(), "Ошибка загрузки транспорта: " + vehiclesState.message,
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        // Наблюдаем за результатами операций с пользователями
        viewModel.getUserOperation().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                switch (result.status) {
                    case SUCCESS:
                        Toast.makeText(requireContext(), "Операция с пользователем выполнена успешно", Toast.LENGTH_SHORT).show();
                        break;
                    case ERROR:
                        Toast.makeText(requireContext(), "Ошибка операции с пользователем: " + result.message, Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });

        // Наблюдаем за результатами операций с маршрутами
        viewModel.getRouteOperation().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                switch (result.status) {
                    case SUCCESS:
                        Toast.makeText(requireContext(), "Операция с маршрутом выполнена успешно", Toast.LENGTH_SHORT).show();
                        break;
                    case ERROR:
                        Toast.makeText(requireContext(), "Ошибка операции с маршрутом: " + result.message, Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });

        // Наблюдаем за результатами операций с поездками
        viewModel.getTripOperation().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                switch (result.status) {
                    case SUCCESS:
                        Toast.makeText(requireContext(), "Операция с поездкой выполнена успешно", Toast.LENGTH_SHORT).show();
                        break;
                    case ERROR:
                        Toast.makeText(requireContext(), "Ошибка операции с поездкой: " + result.message, Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });

        // Наблюдаем за результатами операций с транспортом
        viewModel.getVehicleOperation().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                switch (result.status) {
                    case SUCCESS:
                        Toast.makeText(requireContext(), "Операция с транспортом выполнена успешно", Toast.LENGTH_SHORT).show();
                        break;
                    case ERROR:
                        Toast.makeText(requireContext(), "Ошибка операции с транспортом: " + result.message, Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    private void loadEntities() {
        showLoading(true);
        switch (currentType) {
            case USER:
                viewModel.loadAllUsers();
                break;
            case ROUTE:
                viewModel.loadAllRoutes();
                break;
            case TRIP:
                viewModel.loadAllTrips();
                break;
            case VEHICLE:
                viewModel.loadAllVehicles();
                break;
        }
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (loading) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyList.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void updateEmptyState(boolean isEmpty) {
        if (isEmpty) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyList.setVisibility(View.VISIBLE);
            tvEmptyList.setText("Нет данных для отображения");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyList.setVisibility(View.GONE);
        }
    }

    private void openAddDialog() {
        switch (currentType) {
            case USER:
                showUserDialog(null);
                break;
            case ROUTE:
                showRouteDialog(null);
                break;
            case TRIP:
                showTripDialog(null);
                break;
            case VEHICLE:
                showVehicleDialog(null);
                break;
        }
    }
    private void handleDemoteToPassenger(Object obj) {
        if (obj instanceof User) {
            User user = (User) obj;

            new AlertDialog.Builder(requireContext())
                    .setTitle("Понижение водителя")
                    .setMessage("Все будущие поездки этого водителя будут отменены. Продолжить?")
                    .setPositiveButton("Да", (dialog, which) -> {
                        viewModel.cancelDriverFutureTrips(user.getId());
                        user.setRole(UserRole.PASSENGER);
                        viewModel.updateUser(user);
                        Toast.makeText(requireContext(),
                                user.getFullName() + " теперь пассажир",
                                Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Нет", null)
                    .show();
        }
    }
    private void handleEditObject(Object obj) {
        if (obj instanceof User) showUserDialog((User) obj);
        else if (obj instanceof Route) showRouteDialog((Route) obj);
        else if (obj instanceof Trip) showTripDialog((Trip) obj);
        else if (obj instanceof Vehicle) showVehicleDialog((Vehicle) obj);
    }

    private void handleDeleteObject(Object obj) {
        if (obj instanceof User) {
            handleUserDelete((User) obj);
        } else if (obj instanceof Route) {
            handleRouteDelete((Route) obj);
        } else if (obj instanceof Trip) {
            handleTripDelete((Trip) obj);
        } else if (obj instanceof Vehicle) {
            handleVehicleDelete((Vehicle) obj);
        }
    }
    private void handleTripDelete(Trip trip) {
        viewModel.deleteTrip(trip.getId());
    }
    private void handleVehicleDelete(Vehicle vehicle) {
        viewModel.deleteVehicle(vehicle.getId());
    }

    private void handlePromoteToDriver(Object obj) {
        if (obj instanceof User) {
            User user = (User) obj;
            user.setRole(UserRole.DRIVER);
            viewModel.updateUser(user);
            Toast.makeText(requireContext(),
                    user.getFullName() + " назначен водителем",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void handleUserDelete(User user) {
        if (user.getRole() == UserRole.DRIVER) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Удаление водителя")
                    .setMessage("Будут отменены все будущие поездки этого водителя. Продолжить?")
                    .setPositiveButton("Да", (dialog, which) -> {
                        viewModel.deleteUser(user.getId());
                    })
                    .setNegativeButton("Нет", null)
                    .show();
        } else {
            viewModel.deleteUser(user.getId());
        }
    }
    private void handleRouteDelete(Route route) {
        viewModel.deleteRoute(route.getId());
    }

    // ------------------- Dialogs -------------------

    private void showUserDialog(@Nullable User user) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_generic_user);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextInputEditText etFullName = dialog.findViewById(R.id.etFullName);
        TextInputEditText etEmail = dialog.findViewById(R.id.etEmail);
        TextInputEditText etPhone = dialog.findViewById(R.id.etPhone);
        TextInputEditText etPassword = dialog.findViewById(R.id.etPassword);
        MaterialButton btnSave = dialog.findViewById(R.id.btnSave);

        if (user != null) {
            etFullName.setText(user.getFullName());
            etEmail.setText(user.getEmail());
            etPhone.setText(user.getPhone());
            etPassword.setVisibility(View.GONE);
        }

        btnSave.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email)) {
                Toast.makeText(requireContext(), "Заполните обязательные поля", Toast.LENGTH_SHORT).show();
                return;
            }

            if (user == null && TextUtils.isEmpty(password)) {
                Toast.makeText(requireContext(), "Введите пароль", Toast.LENGTH_SHORT).show();
                return;
            }

            if (user == null) {
                User newUser = new User();
                newUser.setFullName(fullName);
                newUser.setEmail(email);
                newUser.setPhone(phone);
                newUser.setPassword(password);
                newUser.setRole(UserRole.PASSENGER);
                viewModel.createUser(newUser);
            } else {
                user.setFullName(fullName);
                user.setEmail(email);
                user.setPhone(phone);
                if (!TextUtils.isEmpty(password)) {
                    user.setPassword(password);
                }
                viewModel.updateUser(user);
            }

            dialog.dismiss();
        });

        dialog.show();
    }

    private void showRouteDialog(@Nullable Route route) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_generic_route);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextInputEditText etName = dialog.findViewById(R.id.etName);
        TextInputEditText etOrigin = dialog.findViewById(R.id.etOrigin);
        TextInputEditText etDestination = dialog.findViewById(R.id.etDestination);
        TextInputEditText etDistance = dialog.findViewById(R.id.etDistance);
        TextInputEditText etTime = dialog.findViewById(R.id.etTime);
        MaterialButton btnSave = dialog.findViewById(R.id.btnSave);

        if (route != null) {
            etName.setText(route.getName());
            etOrigin.setText(route.getOrigin());
            etDestination.setText(route.getDestination());
            etDistance.setText(String.valueOf(route.getDistance()));
            etTime.setText(route.getTime());
        }

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String origin = etOrigin.getText().toString().trim();
            String destination = etDestination.getText().toString().trim();
            String distanceStr = etDistance.getText().toString().trim();
            String time = etTime.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(origin) || TextUtils.isEmpty(destination)) {
                Toast.makeText(requireContext(), "Заполните обязательные поля", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Double distance = TextUtils.isEmpty(distanceStr) ? null : Double.parseDouble(distanceStr);

                if (route == null) {
                    Route newRoute = new Route();
                    newRoute.setName(name);
                    newRoute.setOrigin(origin);
                    newRoute.setDestination(destination);
                    newRoute.setDistance(distance);
                    newRoute.setTime(time);
                    viewModel.createRoute(newRoute);
                } else {
                    route.setName(name);
                    route.setOrigin(origin);
                    route.setDestination(destination);
                    route.setDistance(distance);
                    route.setTime(time);
                    viewModel.updateRoute(route);
                }

                dialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Неверный формат расстояния", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showTripDialog(@Nullable Trip trip) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_generic_trip);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextInputEditText etDeparture = dialog.findViewById(R.id.etDepartureTime);
        TextInputEditText etArrival = dialog.findViewById(R.id.etArrivalTime);
        TextInputEditText etPrice = dialog.findViewById(R.id.etPrice);
        MaterialButton btnSave = dialog.findViewById(R.id.btnSave);

        if (trip != null) {
            etDeparture.setText(trip.getDepartureTime());
            etArrival.setText(trip.getArrivalTime());
            etPrice.setText(String.valueOf(trip.getPrice()));
        }

        btnSave.setOnClickListener(v -> {
            String departure = etDeparture.getText().toString().trim();
            String arrival = etArrival.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();

            if (TextUtils.isEmpty(departure) || TextUtils.isEmpty(arrival) || TextUtils.isEmpty(priceStr)) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double price = Double.parseDouble(priceStr);

                if (trip == null) {
                    Trip newTrip = new Trip();
                    newTrip.setDepartureTime(departure);
                    newTrip.setArrivalTime(arrival);
                    newTrip.setPrice(price);
                    viewModel.createTrip(newTrip);
                } else {
                    trip.setDepartureTime(departure);
                    trip.setArrivalTime(arrival);
                    trip.setPrice(price);
                    viewModel.updateTrip(trip);
                }

                dialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Неверный формат цены", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showVehicleDialog(@Nullable Vehicle vehicle) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_generic_vehicle);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextInputEditText etPlate = dialog.findViewById(R.id.etPlateNumber);
        TextInputEditText etModel = dialog.findViewById(R.id.etModel);
        TextInputEditText etSeats = dialog.findViewById(R.id.etSeatsCount);
        MaterialButton btnSave = dialog.findViewById(R.id.btnSave);

        if (vehicle != null) {
            etPlate.setText(vehicle.getPlateNumber());
            etModel.setText(vehicle.getModel());
            etSeats.setText(String.valueOf(vehicle.getSeatsCount()));
        }

        btnSave.setOnClickListener(v -> {
            String plate = etPlate.getText().toString().trim();
            String model = etModel.getText().toString().trim();
            String seatsStr = etSeats.getText().toString().trim();

            if (TextUtils.isEmpty(plate) || TextUtils.isEmpty(model) || TextUtils.isEmpty(seatsStr)) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int seats = Integer.parseInt(seatsStr);

                if (vehicle == null) {
                    Vehicle newVehicle = new Vehicle();
                    newVehicle.setPlateNumber(plate);
                    newVehicle.setModel(model);
                    newVehicle.setSeatsCount(seats);
                    viewModel.createVehicle(newVehicle);
                } else {
                    vehicle.setPlateNumber(plate);
                    vehicle.setModel(model);
                    vehicle.setSeatsCount(seats);
                    viewModel.updateVehicle(vehicle);
                }

                dialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Неверный формат количества мест", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}