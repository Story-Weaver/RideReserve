package by.story_weaver.ridereserve.ui.fragments.user;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import by.story_weaver.ridereserve.Logic.adapters.Routes2Adapter;
import by.story_weaver.ridereserve.Logic.data.models.Route;
import by.story_weaver.ridereserve.Logic.utils.UiState;
import by.story_weaver.ridereserve.Logic.viewModels.BookingViewModel;
import by.story_weaver.ridereserve.Logic.viewModels.MainViewModel;
import by.story_weaver.ridereserve.R;
import by.story_weaver.ridereserve.ui.activities.BookingActivity;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RouteFragment extends Fragment implements Routes2Adapter.OnRouteClickListener {

    private ProgressBar progressBar;
    private BookingViewModel bookingViewModel;
    private MainViewModel mainViewModel;
    private Routes2Adapter adapter;
    private RecyclerView recyclerView;
    private TextInputEditText etSearch;
    private List<Route> originalRoutes = new ArrayList<>();
    private List<Route> filteredRoutes = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_route, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bookingViewModel = new ViewModelProvider(requireActivity()).get(BookingViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        initViews(view);
        setupRecyclerView();
        setupObservers();
        setupSearchFilter();

        loadRoutes();
    }

    private void initViews(View view) {
        progressBar = view.findViewById(R.id.progressBar_Route);
        recyclerView = view.findViewById(R.id.rvRoutes);
        etSearch = view.findViewById(R.id.etSearch);
    }

    private void setupRecyclerView() {
        adapter = new Routes2Adapter(new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupObservers() {
        bookingViewModel.getAllRoutes().observe(getViewLifecycleOwner(), routesState -> {
            switch (routesState.status){
                case LOADING:
                    adapter.clear();
                    progressBar.setVisibility(VISIBLE);
                    break;
                case SUCCESS:
                    progressBar.setVisibility(GONE);
                    originalRoutes = routesState.data;
                    filteredRoutes = new ArrayList<>(originalRoutes);
                    adapter.updateRoutes(filteredRoutes);
                    break;
                case ERROR:
                    progressBar.setVisibility(GONE);
                    break;
            }
        });
    }

    private void loadRoutes() {
        bookingViewModel.loadAllRoutes();
    }

    private void setupSearchFilter() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterRoutes(s.toString());
            }
        });
    }

    private void filterRoutes(String searchText) {
        if (searchText.isEmpty()) {
            filteredRoutes = new ArrayList<>(originalRoutes);
        } else {
            filteredRoutes = new ArrayList<>();
            String lowerCaseQuery = searchText.toLowerCase().trim();

            for (Route route : originalRoutes) {
                if (containsIgnoreCase(route.getName(), lowerCaseQuery) ||
                        containsIgnoreCase(route.getOrigin(), lowerCaseQuery) ||
                        containsIgnoreCase(route.getDestination(), lowerCaseQuery)) {
                    filteredRoutes.add(route);
                }
            }
        }
        adapter.updateRoutes(filteredRoutes);
    }

    private boolean containsIgnoreCase(String source, String query) {
        if (source == null || query == null) return false;
        return source.toLowerCase().contains(query);
    }

    @Override
    public void onBookClick(Route route) {
        Log.v("Route", "book");
        openBookingScreen(route);
    }

    @Override
    public void onRouteClick(Route route) {
        Log.v("Route", "click");
        openBookingScreen(route);
    }

    private void openBookingScreen(Route route) {
        Intent intent = BookingActivity.newIntentForRoute(requireActivity(), route.getId());
        startActivity(intent);
        Log.v("Route", "open");
    }
}