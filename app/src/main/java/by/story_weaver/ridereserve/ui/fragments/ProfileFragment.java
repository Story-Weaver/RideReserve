package by.story_weaver.ridereserve.ui.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import by.story_weaver.ridereserve.Logic.data.enums.UserRole;
import by.story_weaver.ridereserve.Logic.data.models.Booking;
import by.story_weaver.ridereserve.Logic.data.models.User;
import by.story_weaver.ridereserve.Logic.viewModels.AuthViewModel;
import by.story_weaver.ridereserve.Logic.viewModels.MainViewModel;
import by.story_weaver.ridereserve.Logic.viewModels.ProfileViewModel;
import by.story_weaver.ridereserve.R;
import by.story_weaver.ridereserve.ui.activities.AuthActivity;
import by.story_weaver.ridereserve.ui.fragments.user.SupportFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private TextView tvUserName, tvUserRole, tvEmail, tvPhone;
    //private View layoutSwitchRole;
    private View layoutVehicleManagement, layoutEditProfile, layoutTripHistory, layoutHelp, layoutLogout;
    private View layoutSlicer1, layoutSlicer2;
    private ProfileViewModel profileViewModel;
    private MainViewModel mainViewModel;
    private AuthViewModel authViewModel;
    private User currentUser;
    private Handler refreshHandler = new Handler(Looper.getMainLooper());
    private Runnable refreshRunnable;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findById(view);
        setupClickListeners();
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        currentUser = profileViewModel.getProfile();
        setupObservers();
        setUserRole(currentUser.getRole());
        setUserData(currentUser);
    }
    private void findById(View view){
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserRole = view.findViewById(R.id.tvUserRole);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhone = view.findViewById(R.id.tvPhone);
        layoutVehicleManagement = view.findViewById(R.id.layoutVehicleManagement);
        layoutEditProfile = view.findViewById(R.id.layoutEditProfile);
        layoutTripHistory = view.findViewById(R.id.layoutTripHistory);
        layoutHelp = view.findViewById(R.id.layoutHelp);
        layoutLogout = view.findViewById(R.id.layoutLogout);
        layoutSlicer1 = view.findViewById(R.id.layoutSlicer1);
        layoutSlicer2 = view.findViewById(R.id.layoutSlicer2);
        //layoutSwitchRole = view.findViewById(R.id.layoutSwitchRole);
    }
    private void setUserRole(UserRole role) {
        if (role.equals(UserRole.DRIVER)) {
            tvUserRole.setText("Водитель");
            //layoutSwitchRole.setVisibility(VISIBLE);
            layoutSlicer1.setVisibility(VISIBLE);
            layoutSlicer2.setVisibility(VISIBLE);
            layoutVehicleManagement.setVisibility(VISIBLE);
        } else {
            tvUserRole.setText("Пассажир");
            layoutSlicer1.setVisibility(GONE);
            layoutSlicer2.setVisibility(GONE);
            //layoutSwitchRole.setVisibility(GONE);
            layoutVehicleManagement.setVisibility(GONE);
        }
    }
    private void setupClickListeners() {
        layoutEditProfile.setOnClickListener(v -> {
            long userIdToEdit = profileViewModel.getProfile().getId();
            UserEditFragment editFragment = UserEditFragment.newInstance(userIdToEdit);
            mainViewModel.openFullscreen(editFragment);
        });

        layoutTripHistory.setOnClickListener(v -> {

        });

//        layoutSwitchRole.setOnClickListener(v -> {
//            switchUserRole();
//        });

        layoutVehicleManagement.setOnClickListener(v -> {
            //openVehicleManagement();
        });

        layoutHelp.setOnClickListener(v -> {
            mainViewModel.openFullscreen(new SupportFragment());
        });

        layoutLogout.setOnClickListener(v -> {
            authViewModel.logout();
            requireActivity().startActivity(new Intent(requireActivity(), AuthActivity.class));
            requireActivity().finish();
        });
    }
    private void setUserData(User user){
        tvUserName.setText(user.getFullName());
        tvEmail.setText(user.getEmail());
        tvPhone.setText(user.getPhone());
    }
    private void setupObservers() {
        mainViewModel.closeRequest().observe(getViewLifecycleOwner(), request -> {
            refreshData();
        });
    }

    private void refreshData() {
        if (refreshRunnable != null) {
            refreshHandler.removeCallbacks(refreshRunnable);
        }
        refreshRunnable = () -> {
            User currentUser = profileViewModel.getProfile();
            setUserData(currentUser);
        };
        refreshHandler.postDelayed(refreshRunnable, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        refreshHandler.removeCallbacks(refreshRunnable);
    }
}