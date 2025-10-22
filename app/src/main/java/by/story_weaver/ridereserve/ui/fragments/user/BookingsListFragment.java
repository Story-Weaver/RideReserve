package by.story_weaver.ridereserve.ui.fragments.user;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import by.story_weaver.ridereserve.Logic.adapters.BookingAdapter;
import by.story_weaver.ridereserve.Logic.data.DataSeeder;
import by.story_weaver.ridereserve.Logic.data.enums.BookingStatus;
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
public class BookingsListFragment extends Fragment {
    private ImageView add;
    private MainViewModel mainViewModel;
    private BookingViewModel bookingViewModel;
    private ProfileViewModel profileViewModel;
    private AuthViewModel authViewModel;
    private RecyclerView recyclerView;
    private BookingAdapter adapter;

    private Handler refreshHandler = new Handler(Looper.getMainLooper());
    private Runnable refreshRunnable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookings_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация ViewModel
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        bookingViewModel = new ViewModelProvider(requireActivity()).get(BookingViewModel.class);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        // Находим View элементы
        findById(view);

        // Настраиваем RecyclerView
        setupRecyclerView();

        // Загружаем начальные данные
        loadInitialData();

        // Настраиваем клики
        setupClickListeners();

        // Наблюдаем за закрытием других фрагментов
        setupObservers();

        addData();
    }
    private void addData(){
        DataSeeder data = new DataSeeder();
        DataSeeder.SeederData finalData = data.generateTestData();
//        for (User i: finalData.users){
//            authViewModel.addUser(i);
//        }
//        for(Vehicle i: finalData.vehicles){
//            bookingViewModel.addVehicle(i);
//        }
//        for(Route i: finalData.routes){
//            bookingViewModel.addRoute(i);
//        }
        //bookingViewModel.addTrip(new Trip());
    }
    @Override
    public void onResume() {
        super.onResume();
        // Обновляем данные при возвращении на фрагмент
        refreshBookings();
    }

    private void findById(View view) {
        add = view.findViewById(R.id.addButton);
        recyclerView = view.findViewById(R.id.bookingList);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        // Инициализируем адаптер с текущими данными
        adapter = new BookingAdapter(
                bookingViewModel.getBookinglist(),
                bookingViewModel.getRoutelist(),
                bookingViewModel.getTriplist()
        );

        // Устанавливаем слушатель кликов
        adapter.setOnItemClickListener(new BookingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Booking booking = adapter.getBookingAt(position);
                if (booking != null) {
                    mainViewModel.openFullscreen(new TripDetailFragment(booking));
                }
            }

            @Override
            public void onItemLongClick(int position) {
                showActionMenu(position);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void loadInitialData() {
        User currentUser = profileViewModel.getProfile();
        if (currentUser != null) {
            List<Booking> userBookings = bookingViewModel.getBookingsForUser(currentUser.getId());
            adapter.updateBookings(userBookings);
        }
    }

    private void setupClickListeners() {
        add.setOnClickListener(v -> {
            mainViewModel.openFullscreen(new BookingFragment());
        });
    }

    private void setupObservers() {
        mainViewModel.closeRequest().observe(getViewLifecycleOwner(), request -> {
            refreshBookings();
        });
    }

    private void refreshBookings() {
        Log.v("list", "check");
        if (refreshRunnable != null) {
            refreshHandler.removeCallbacks(refreshRunnable);
        }
        Log.v("list", "load");
        refreshRunnable = () -> {
            Log.v("list", "start load");
            User currentUser = profileViewModel.getProfile();
            if (currentUser != null && adapter != null) {
                List<Booking> userBookings = bookingViewModel.getBookingsForUser(currentUser.getId());
                adapter.updateBookings(userBookings);
                Log.v("list", "set");
            }
        };
        refreshHandler.postDelayed(refreshRunnable, 1000);
    }

    private void showActionMenu(int position) {
        Booking booking = adapter.getBookingAt(position);
        if (booking == null) return;

        new AlertDialog.Builder(requireActivity())
                .setTitle("Действия с бронью")
                .setItems(new String[]{"Отменить", "Изменить"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            cancelBooking(position);
                            break;
                        case 1:
                            editBooking(position);
                            break;
                    }
                })
                .show();
    }

    private void cancelBooking(int position) {
        Booking booking = adapter.getBookingAt(position);
        if (booking != null) {
            bookingViewModel.changeStatusBooking(booking.getId(), BookingStatus.CANCELLED);
            refreshBookings();
        }
    }

    private void editBooking(int position) {
        Booking booking = adapter.getBookingAt(position);
        if (booking != null) {
            // TODO: Реализовать логику редактирования бронирования
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (refreshHandler != null && refreshRunnable != null) {
            refreshHandler.removeCallbacks(refreshRunnable);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (refreshHandler != null && refreshRunnable != null) {
            refreshHandler.removeCallbacks(refreshRunnable);
        }
    }
}