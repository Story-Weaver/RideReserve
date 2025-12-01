package by.story_weaver.ridereserve.ui.fragments.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import by.story_weaver.ridereserve.Logic.adapters.BookingManagementAdapter;
import by.story_weaver.ridereserve.Logic.data.enums.BookingStatus;
import by.story_weaver.ridereserve.Logic.data.models.Booking;
import by.story_weaver.ridereserve.Logic.viewModels.BookingViewModel;
import by.story_weaver.ridereserve.Logic.viewModels.MainViewModel;
import by.story_weaver.ridereserve.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import dagger.hilt.android.AndroidEntryPoint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@AndroidEntryPoint
public class BookingMonitorFragment extends Fragment {

    private BookingViewModel viewModel;
    private MainViewModel mainViewModel;
    private BookingManagementAdapter bookingAdapter;
    private List<Booking> allBookings = new ArrayList<>();

    private TextInputEditText etSearchBooking;
    private Spinner spinnerStatusFilter;
    private MaterialButton btnApplyFilters, btnClearFilters;
    private TextView tvPendingCount, tvConfirmedCount, tvCancelledCount, tvTotalCount, tvEmptyList;
    private RecyclerView rvBookings;
    private ProgressBar progressBookings;
    private boolean messageShowed = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_booking_monitor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        viewModel = new ViewModelProvider(requireActivity()).get(BookingViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        setupViews();
        setupObservers();
        loadData();
    }

    private void initViews(View view) {
        etSearchBooking = view.findViewById(R.id.etSearchBooking);
        spinnerStatusFilter = view.findViewById(R.id.spinnerStatusFilter);
        btnApplyFilters = view.findViewById(R.id.btnApplyFilters);
        btnClearFilters = view.findViewById(R.id.btnClearFilters);
        tvPendingCount = view.findViewById(R.id.tvPendingCount);
        tvConfirmedCount = view.findViewById(R.id.tvConfirmedCount);
        tvCancelledCount = view.findViewById(R.id.tvCancelledCount);
        tvTotalCount = view.findViewById(R.id.tvTotalCount);
        tvEmptyList = view.findViewById(R.id.tvEmptyList);
        rvBookings = view.findViewById(R.id.rvBookings);
        progressBookings = view.findViewById(R.id.progressBookings);
    }

    private void setupViews() {
        setupFilters();
        setupRecyclerView();
        setupClickListeners();
    }

    private void setupFilters() {
        List<String> statusFilters = Arrays.asList(
                "Все статусы",
                "Ожидает подтверждения",
                "Подтверждено",
                "Отменено",
                "Завершено"
        );

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                statusFilters
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatusFilter.setAdapter(statusAdapter);
    }

    private void setupRecyclerView() {
        bookingAdapter = new BookingManagementAdapter(new BookingManagementAdapter.BookingActionListener() {
            @Override
            public void onConfirmBooking(long bookingId) {
                viewModel.changeStatusBooking(bookingId, BookingStatus.CONFIRMED);
            }

            @Override
            public void onCancelBooking(long bookingId) {
                viewModel.changeStatusBooking(bookingId, BookingStatus.CANCELLED);
            }

            @Override
            public void onShowDetails(long bookingId) {
                showBookingDetails(bookingId);
            }
        });

        rvBookings.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBookings.setAdapter(bookingAdapter);
    }

    private void setupClickListeners() {
        btnApplyFilters.setOnClickListener(v -> applyFilters());
        btnClearFilters.setOnClickListener(v -> clearFilters());
    }

    private void setupObservers() {
        viewModel.getAllBookings().observe(getViewLifecycleOwner(), bookingsState -> {
            if (bookingsState == null) return;

            switch (bookingsState.status) {
                case LOADING:
                    showLoading(true);
                    tvEmptyList.setVisibility(View.GONE);
                    break;

                case SUCCESS:
                    showLoading(false);
                    if (bookingsState.data != null) {
                        allBookings = bookingsState.data;
                        applyFilters();
                        updateStats(bookingsState.data);
                    }
                    break;

                case ERROR:
                    showLoading(false);
                    Toast.makeText(requireContext(), "Ошибка загрузки: " + bookingsState.message,
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        viewModel.getBookingStatusChanged().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            switch (result.status) {
                case SUCCESS:
                    if(!messageShowed){
                        Toast.makeText(requireActivity(), "Статус бронирования обновлен",
                                Toast.LENGTH_SHORT).show();
                        messageShowed = true;
                    }
                    viewModel.loadAllBookings();
                    break;

                case ERROR:
                    if(!messageShowed) {
                        Toast.makeText(requireActivity(), "Ошибка: " + result.message,
                                Toast.LENGTH_SHORT).show();
                        messageShowed = true;
                    }
                    break;
                case LOADING:
                    messageShowed = false;
                    break;
            }
        });
    }

    private void loadData() {
        viewModel.loadAllBookings();
    }

    private void applyFilters() {
        String searchQuery = etSearchBooking.getText().toString().trim();
        String selectedStatus = spinnerStatusFilter.getSelectedItem().toString();

        Log.d("BookingMonitor", "Applying filters - Status: " + selectedStatus + ", Search: " + searchQuery);
        Log.d("BookingMonitor", "Total bookings before filter: " + allBookings.size());

        List<Booking> filtered = new ArrayList<>(allBookings);

        // Фильтрация по статусу
        if (!selectedStatus.equals("Все статусы")) {
            BookingStatus status = mapStatusFilter(selectedStatus);
            Log.d("BookingMonitor", "Filtering by status: " + status);
            filtered.removeIf(booking -> {
                boolean shouldRemove = booking.getStatus() != status;
                if (shouldRemove) {
                    Log.d("BookingMonitor", "Removing booking #" + booking.getId() + " with status: " + booking.getStatus());
                }
                return shouldRemove;
            });
        } else {
            Log.d("BookingMonitor", "Showing all statuses");
        }

        if (!searchQuery.isEmpty()) {
            Log.d("BookingMonitor", "Filtering by search: " + searchQuery);
            filtered.removeIf(booking -> {
                boolean matchesId = String.valueOf(booking.getId()).contains(searchQuery);
                boolean matchesPassenger = String.valueOf(booking.getPassengerId()).contains(searchQuery);
                boolean shouldRemove = !(matchesId || matchesPassenger);

                if (shouldRemove) {
                    Log.d("BookingMonitor", "Removing booking #" + booking.getId() + " - no match for search");
                }
                return shouldRemove;
            });
        }

        Log.d("BookingMonitor", "Bookings after filters: " + filtered.size());

        // СОРТИРОВКА по статусу: PENDING -> CONFIRMED -> CANCELLED
        Collections.sort(filtered, new Comparator<Booking>() {
            @Override
            public int compare(Booking b1, Booking b2) {
                return getStatusPriority(b1.getStatus()) - getStatusPriority(b2.getStatus());
            }
        });

        // Отладочная информация о статусах после фильтрации
        for (Booking booking : filtered) {
            Log.d("BookingMonitor", "Final booking #" + booking.getId() + " - Status: " + booking.getStatus());
        }

        bookingAdapter.submitList(filtered);

        if (filtered.isEmpty()) {
            rvBookings.setVisibility(View.GONE);
            tvEmptyList.setVisibility(View.VISIBLE);
            tvEmptyList.setText("Бронирования не найдены");
            Log.d("BookingMonitor", "No bookings to display");
        } else {
            rvBookings.setVisibility(View.VISIBLE);
            tvEmptyList.setVisibility(View.GONE);
            Log.d("BookingMonitor", "Displaying " + filtered.size() + " bookings");
        }
    }

    private int getStatusPriority(BookingStatus status) {
        if (status == null) return 4;

        switch (status) {
            case PENDING: return 1;
            case CONFIRMED: return 2;
            case CANCELLED: return 3;
            case COMPLETED: return 4; // Добавляем поддержку COMPLETED
            default: return 5;
        }
    }

    private void clearFilters() {
        etSearchBooking.setText("");
        spinnerStatusFilter.setSelection(0);
        applyFilters();
    }

    private BookingStatus mapStatusFilter(String statusFilter) {
        Log.d("BookingMonitor", "Mapping status filter: " + statusFilter);
        switch (statusFilter) {
            case "Ожидает подтверждения":
                return BookingStatus.PENDING;
            case "Подтверждено":
                return BookingStatus.CONFIRMED;
            case "Отменено":
                return BookingStatus.CANCELLED;
            case "Завершено":
                return BookingStatus.COMPLETED;
            default:
                Log.w("BookingMonitor", "Unknown status filter: " + statusFilter);
                return null;
        }
    }

    private void updateStats(List<Booking> bookings) {
        int pending = 0, confirmed = 0, cancelled = 0;

        for (Booking booking : bookings) {
            switch (booking.getStatus()) {
                case PENDING:
                    pending++;
                    break;
                case CONFIRMED:
                    confirmed++;
                    break;
                case CANCELLED:
                    cancelled++;
                    break;
            }
        }

        tvPendingCount.setText(String.valueOf(pending));
        tvConfirmedCount.setText(String.valueOf(confirmed));
        tvCancelledCount.setText(String.valueOf(cancelled));
        tvTotalCount.setText(String.valueOf(bookings.size()));
    }

    private void showLoading(boolean show) {
        progressBookings.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            rvBookings.setVisibility(View.GONE);
            tvEmptyList.setVisibility(View.GONE);
        } else {
            rvBookings.setVisibility(View.VISIBLE);
        }
    }

    private void showBookingDetails(long bookingId) {
        // mainViewModel.openFullscreen(BookingDetailFragment);
        Toast.makeText(requireContext(), "Детали бронирования #" + bookingId,
                Toast.LENGTH_SHORT).show();
    }
}