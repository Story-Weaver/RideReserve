package by.story_weaver.ridereserve.ui.fragments.user;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import by.story_weaver.ridereserve.Logic.adapters.BookingAdapter;
import by.story_weaver.ridereserve.Logic.data.enums.BookingStatus;
import by.story_weaver.ridereserve.Logic.data.models.Booking;
import by.story_weaver.ridereserve.Logic.data.models.Route;
import by.story_weaver.ridereserve.Logic.data.models.Trip;
import by.story_weaver.ridereserve.Logic.utils.UiState;
import by.story_weaver.ridereserve.Logic.viewModels.BookingViewModel;
import by.story_weaver.ridereserve.Logic.viewModels.MainViewModel;
import by.story_weaver.ridereserve.R;
import by.story_weaver.ridereserve.ui.activities.BookingActivity;
import by.story_weaver.ridereserve.ui.fragments.user.TripDetailFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BookingsListFragment extends Fragment {
    private ProgressBar progressBar;
    private ImageView add;
    private MainViewModel mainViewModel;
    private BookingViewModel bookingViewModel;
    private RecyclerView recyclerView;
    private BookingAdapter adapter;

    private List<Booking> currentBookings = new ArrayList<>();
    private List<Route> currentRoutes = new ArrayList<>();
    private List<Trip> currentTrips = new ArrayList<>();

    private ActivityResultLauncher<Intent> bookingLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getBooleanExtra(BookingActivity.RESULT_BOOKING_CREATED, false)) {
                        refreshBookings();
                    }
                }
            }
    );

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

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        bookingViewModel = new ViewModelProvider(requireActivity()).get(BookingViewModel.class);

        findById(view);
        setupRecyclerView();
        setupClickListeners();
        setupObservers();

        loadInitialData();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshBookings();
    }

    private void findById(View view) {
        progressBar = view.findViewById(R.id.progressBar_BookingList);
        add = view.findViewById(R.id.addButton);
        recyclerView = view.findViewById(R.id.bookingList);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        adapter = new BookingAdapter(
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        adapter.setOnItemClickListener(new BookingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Booking booking = adapter.getBookingAt(position);
                if (booking != null) {
                    // Find the trip for this booking
                    Trip trip = findTripById(booking.getTripId());
                    if (trip != null) {
                        openTripDetails(trip);
                    }
                }
            }

            @Override
            public void onItemLongClick(int position) {
                showActionMenu(position);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        add.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), BookingActivity.class);
            bookingLauncher.launch(intent);
        });
    }

    private void setupObservers() {
        mainViewModel.closeRequest().observe(getViewLifecycleOwner(), request -> {
            refreshBookings();
        });

        bookingViewModel.getBookingsForUser().observe(getViewLifecycleOwner(), list -> {
            switch (list.status){
                case SUCCESS:
                    progressBar.setVisibility(GONE);
                    if (list.data != null) {
                        currentBookings = list.data;
                        updateAdapterData();
                    }
                    break;
                case ERROR:
                    progressBar.setVisibility(GONE);
                    break;
                case LOADING:
                    progressBar.setVisibility(VISIBLE);
                    break;
            }
        });

        bookingViewModel.getAllRoutes().observe(getViewLifecycleOwner(), list -> {
            switch (list.status){
                case SUCCESS:
                    if (list.data != null) {
                        currentRoutes = list.data;
                        updateAdapterData();
                    }
                    break;
                case ERROR:
                case LOADING:
                    break;
            }
        });

        bookingViewModel.getAllTrips().observe(getViewLifecycleOwner(), list -> {
            switch (list.status){
                case SUCCESS:
                    if (list.data != null) {
                        currentTrips = list.data;
                        updateAdapterData();
                    }
                    break;
                case ERROR:
                case LOADING:
                    break;
            }
        });

        bookingViewModel.getBookingStatusChanged().observe(getViewLifecycleOwner(), bookingState -> {
            if (bookingState.status == UiState.Status.SUCCESS) {
                refreshBookings();
            }
        });
    }

    private void loadInitialData() {
        bookingViewModel.loadBookingsForUser();
        bookingViewModel.loadAllRoutes();
        bookingViewModel.loadAllTrips();
    }

    private void refreshBookings() {
        bookingViewModel.loadBookingsForUser();
    }

    private void updateAdapterData() {
        adapter.updateAdapterBookings(currentBookings);
        adapter.updateAdapterRoutes(currentRoutes);
        adapter.updateAdapterTrips(currentTrips);
    }

    private Trip findTripById(long tripId) {
        for (Trip trip : currentTrips) {
            if (trip.getId() == tripId) {
                return trip;
            }
        }
        return null;
    }

    private void showActionMenu(int position) {
        Booking booking = adapter.getBookingAt(position);
        if (booking == null) return;

        new AlertDialog.Builder(requireActivity())
                .setItems(new String[]{"Отменить"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            cancelBooking(booking);
                            break;
                    }
                })
                .show();
    }

    private void cancelBooking(Booking booking) {
        if (booking != null) {
            bookingViewModel.changeStatusBooking(booking.getId(), BookingStatus.CANCELLED);
        }
    }

    private void openTripDetails(Trip trip) {
        TripDetailFragment fragment = TripDetailFragment.newInstance(trip.getId());
        mainViewModel.openFullscreen(fragment);
    }
}