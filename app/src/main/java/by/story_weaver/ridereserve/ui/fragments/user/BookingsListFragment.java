package by.story_weaver.ridereserve.ui.fragments.user;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import by.story_weaver.ridereserve.Logic.adapters.BookingAdapter;
import by.story_weaver.ridereserve.Logic.data.DataSeeder;
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
public class BookingsListFragment extends Fragment {
    private ImageView add;
    private MainViewModel mainViewModel;
    private BookingViewModel bookingViewModel;
    private ProfileViewModel profileViewModel;
    private RecyclerView recyclerView;
    private BookingAdapter adapter;

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
    public void onResume() {
        super.onResume();
        refreshBookings();
    }

    private void refreshBookings() {
        User currentUser = profileViewModel.getProfile();
        if (currentUser != null) {
            List<Booking> userBookings = bookingViewModel.getBookingsForUser(currentUser.getId());
            adapter.updateBookings(userBookings);
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        bookingViewModel = new ViewModelProvider(requireActivity()).get(BookingViewModel.class);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        //addData();
        findById(view);
        setupRecyclerView();
        add.setOnClickListener(v -> {
            mainViewModel.openFullscreen(new BookingFragment());
        });

    }
    private void addData(){
        DataSeeder seeder = new DataSeeder();
        DataSeeder.SeederData testData = seeder.generateTestData();
//        for (User i: testData.users){
//            authViewModel.addUser(i);
//        }
//        for (Route i: testData.routes){
//            bookingViewModel.addRoute(i);
//        }
//        for (Vehicle i: testData.vehicles){
//            bookingViewModel.addVehicle(i);
//        }
        //for (Trip i: testData.trips){
            bookingViewModel.addTrip(new Trip(1,1,19,"sd", "sdf", TripStatus.SCHEDULED,14));
       // }
        //for (Booking i: testData.bookings){
            bookingViewModel.addBooking(new Booking(1, 1, 1, 8,true,true, BookingStatus.CANCELLED, 14000));
        //}
    }
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        adapter = new BookingAdapter(bookingViewModel.getBookinglist(), bookingViewModel.getRoutelist(), bookingViewModel.getTriplist());

        // Устанавливаем слушатель кликов
        adapter.setOnItemClickListener(new BookingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Booking booking = adapter.getBookingAt(position);
                mainViewModel.openFullscreen(new TripDetailFragment(booking));
            }

            @Override
            public void onItemLongClick(int position) {
                showActionMenu(position);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void findById(View view){
        add = view.findViewById(R.id.addButton);;
        recyclerView = view.findViewById(R.id.bookingList);
    }

    private void showActionMenu(int position) {
        new AlertDialog.Builder(requireActivity())
                .setTitle("Действия с бронью")
                .setItems(new String[]{"Отменить", "Изменить", "Поделиться"}, (dialog, which) -> {
                    switch (which) {
                        //case 0: cancelBooking(position); break;
                        //case 1: editBooking(position); break;
                        //case 2: shareBooking(position); break;
                    }
                })
                .show();
    }
}