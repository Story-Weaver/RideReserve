package by.story_weaver.ridereserve.ui.fragments.user;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

import by.story_weaver.ridereserve.Logic.adapters.Routes2Adapter;
import by.story_weaver.ridereserve.Logic.data.models.Route;
import by.story_weaver.ridereserve.Logic.viewModels.BookingViewModel;
import by.story_weaver.ridereserve.Logic.viewModels.MainViewModel;
import by.story_weaver.ridereserve.R;
import by.story_weaver.ridereserve.ui.fragments.UserEditFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RouteFragment extends Fragment implements Routes2Adapter.OnRouteClickListener {

    private BookingViewModel bookingViewModel;
    private MainViewModel mainViewModel;
    private Routes2Adapter adapter;
    private RecyclerView recyclerView;
    private TextInputEditText etSearch;

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
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rvRoutes);
        etSearch = view.findViewById(R.id.etSearch);
    }

    private void setupRecyclerView() {
        adapter = new Routes2Adapter(new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }
    private void setupObservers() {
       adapter.updateRoutes(bookingViewModel.getRoutelist());
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
        BookingFragment book = BookingFragment.newInstance(route.getId());
        mainViewModel.openFullscreen(book);
        Log.v("Route", "open");
    }
}