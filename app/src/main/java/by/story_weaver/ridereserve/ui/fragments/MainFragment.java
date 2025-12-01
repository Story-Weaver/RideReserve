package by.story_weaver.ridereserve.ui.fragments;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import by.story_weaver.ridereserve.Logic.data.enums.UserRole;
import by.story_weaver.ridereserve.Logic.data.models.User;
import by.story_weaver.ridereserve.Logic.utils.NetworkHelper;
import by.story_weaver.ridereserve.Logic.viewModels.MainViewModel;
import by.story_weaver.ridereserve.Logic.viewModels.ProfileViewModel;
import by.story_weaver.ridereserve.R;
import by.story_weaver.ridereserve.ui.activities.AuthActivity;
import by.story_weaver.ridereserve.ui.activities.InternetActivity;
import by.story_weaver.ridereserve.ui.fragments.admin.AdminDashboardFragment;
import by.story_weaver.ridereserve.ui.fragments.admin.BookingMonitorFragment;
import by.story_weaver.ridereserve.ui.fragments.admin.ChangingFragment;
import by.story_weaver.ridereserve.ui.fragments.driver.DriverTripFragment;
import by.story_weaver.ridereserve.ui.fragments.user.BookingsListFragment;
import by.story_weaver.ridereserve.ui.fragments.user.RouteFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";

    private boolean isOpeningFullscreen = false;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout fullscreen;
    private FrameLayout container;
    private ProfileViewModel profileViewModel;
    private MainViewModel mainViewModel;
    private User profile;
    private boolean suppressNavSelection = false;

    public MainFragment() { /* required empty ctor */ }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    private void findAllId(View view){
        bottomNavigationView = view.findViewById(R.id.bottomNavigationView);
        fullscreen = view.findViewById(R.id.fullscreen_container);
        container = view.findViewById(R.id.fragmentContainerView);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        NetworkHelper.checkInternetConnection(requireActivity(), hasInternet -> {
            if (hasInternet) {

            } else {
                requireActivity().startActivity(new Intent(requireActivity(), InternetActivity.class));
                requireActivity().finish();
            }
        });
        Log.d(TAG, "onViewCreated - start");
        findAllId(view);

        // Back handling: if fullscreen open -> close it, else default back behavior
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        FragmentManager fm = getChildFragmentManager();
                        if (fm.getBackStackEntryCount() > 0 || fm.findFragmentById(R.id.fullscreen_container) != null) {
                            Log.d(TAG, "Back pressed -> closing fullscreen fragment");
                            closeFullscreenFragment();
                        } else {
                            // allow system/back stack to handle it (exit app or navigate up)
                            Log.d(TAG, "Back pressed -> no fullscreen, delegating to activity");
                            setEnabled(false);
                            requireActivity().onBackPressed();
                        }
                    }
                });

        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        observeData();

        try {
            profile = profileViewModel.getProfile();
            if (profile == null) {
                Log.w(TAG, "Profile is null — redirecting to AuthActivity");
                Toast.makeText(requireContext(), "Неавторизованный пользователь — переход на экран авторизации", Toast.LENGTH_SHORT).show();
                requireActivity().startActivity(new Intent(requireActivity(), AuthActivity.class));
                requireActivity().finish();
                return;
            }
            if (profile.getEmail() == null || profile.getEmail().isEmpty()) {
                Log.w(TAG, "Profile email empty — redirecting to AuthActivity");
                Toast.makeText(requireContext(), "Неавторизованный пользователь — переход на экран авторизации", Toast.LENGTH_SHORT).show();
                requireActivity().startActivity(new Intent(requireActivity(), AuthActivity.class));
                requireActivity().finish();
                return;
            }
            Log.d(TAG, "Profile loaded: email=" + profile.getEmail() + " role=" + profile.getRole());
            setupForRole(profile.getRole());
        } catch (Exception e) {
            Log.e(TAG, "Error getting profile from ProfileViewModel", e);
            Toast.makeText(requireContext(), "Ошибка при получении профиля: " + e.getMessage(), Toast.LENGTH_LONG).show();
            requireActivity().startActivity(new Intent(requireActivity(), AuthActivity.class));
            requireActivity().finish();
            return;
        }

        ensureMainFragmentPresent();
        Log.d(TAG, "onViewCreated - done");
    }

    private void observeData(){
        mainViewModel.openRequest().observe(getViewLifecycleOwner(), fragment -> {
            showFullscreenFragment(fragment,"tag", true);
        });
        mainViewModel.closeRequest().observe(getViewLifecycleOwner(), fragment -> {
            closeFullscreenFragment();
        });
    }
    private void ensureMainFragmentPresent() {
        FragmentManager fm = getChildFragmentManager();
        Fragment current = fm.findFragmentById(R.id.fragmentContainerView);
        if (current == null) {
            Log.d(TAG, "No fragment in fragmentContainerView — loading default for role");
            // load default by role
            UserRole role = profile != null ? profile.getRole() : UserRole.PASSENGER;
            switch (role) {
                case DRIVER:
                    replaceMainFragment(new DriverTripFragment(), "driver_trips");
                    break;
                case ADMIN:
                    replaceMainFragment(new AdminDashboardFragment(), "admin_dashboard");
                    break;
                case PASSENGER:
                default:
                    replaceMainFragment(new BookingsListFragment(), "user_bookings");
                    break;
            }
        } else {
            Log.d(TAG, "Existing fragment in container: " + (current.getTag() != null ? current.getTag() : current.getClass().getSimpleName()));
        }
    }
    private void setupForRole(UserRole role) {
        Log.d(TAG, "setupForRole: " + role);
        if (bottomNavigationView == null) {
            Log.w(TAG, "bottomNavigationView is null in setupForRole");
            return;
        }
//TODO детали брони у админа

        switch (role) {
            case DRIVER:
                inflateMenu(R.menu.menu_driver);
                break;
            case ADMIN:
                inflateMenu(R.menu.menu_admin);
                break;
            case PASSENGER:
            default:
                inflateMenu(R.menu.menu_passenger);
                break;
        }


        bottomNavigationView.post(() -> {
            int menuSize = bottomNavigationView.getMenu().size();
            if (menuSize == 0) return;

            int targetIndex = 1;
            if (targetIndex >= menuSize) targetIndex = 0;
            MenuItem item = null;
            if(role.equals(UserRole.DRIVER)){
                item = bottomNavigationView.getMenu().getItem(0);
            } else {
                item = bottomNavigationView.getMenu().getItem(targetIndex);
            }
            suppressNavSelection = true;
            bottomNavigationView.setSelectedItemId(item.getItemId());
            suppressNavSelection = false;
            handleBottomNavSelection(item, role);
        });


        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (suppressNavSelection) {
                Log.d(TAG, "Selection suppressed for item " + item.getTitle());
                return true;
            }
            Log.d(TAG, "Bottom nav item selected by user: " + item.getTitle());
            handleBottomNavSelection(item, role);
            return true;
        });
    }
    private void inflateMenu(int menuResId) {
        Log.d(TAG, "inflateMenu: " + menuResId);
        bottomNavigationView.getMenu().clear();
        bottomNavigationView.inflateMenu(menuResId);
    }
    private void handleBottomNavSelection(MenuItem item, UserRole role) {
        NetworkHelper.checkInternetConnection(requireActivity(), hasInternet -> {
            if (hasInternet) {

            } else {
                requireActivity().startActivity(new Intent(requireActivity(), InternetActivity.class));
                requireActivity().finish();
            }
        });
        int id = item.getItemId();
        Log.d(TAG, "handleBottomNavSelection role=" + role + " id=" + id);
        if (role == UserRole.PASSENGER) {
            if (id == R.id.menu_routes_user) {
                Log.d(TAG, "Selecting RoutesFragment");
                replaceMainFragment(new RouteFragment(), "routes");
            } else if (id == R.id.menu_bookings_user) {
                Log.d(TAG, "Selecting BookingsListFragment");
                replaceMainFragment(new BookingsListFragment(), "bookings");
            } else if (id == R.id.menu_profile_user) {
                Log.d(TAG, "Selecting ProfileFragment");
                replaceMainFragment(new ProfileFragment(), "profile");
            } else {
                Log.w(TAG, "Unknown passenger menu id: " + id);
                Toast.makeText(requireContext(), "Неподдерживаемое меню", Toast.LENGTH_SHORT).show();
            }
        } else if (role == UserRole.DRIVER) {
            if (id == R.id.menu_trip_board_driver) {
                replaceMainFragment(new DriverTripFragment(), "driver_home");
            } else if (id == R.id.menu_profile_driver) {
                replaceMainFragment(new ProfileFragment(), "profile");
            } else {
                Log.w(TAG, "Unknown driver menu id: " + id);
                Toast.makeText(requireContext(), "Неподдерживаемое меню", Toast.LENGTH_SHORT).show();
            }
        } else if (role == UserRole.ADMIN) {
            if (id == R.id.menu_booking_admin) {
                replaceMainFragment(new BookingMonitorFragment(), "change_data");
            } else if (id == R.id.menu_admin_dashboard_admin) {
                replaceMainFragment(new AdminDashboardFragment(), "admin_dashboard");
            } else if (id == R.id.menu_edit_admin) {
                replaceMainFragment(new ChangingFragment(), "edit_data");
            } else {
                Log.w(TAG, "Unknown admin menu id: " + id);
                Toast.makeText(requireContext(), "Неподдерживаемое меню", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void replaceMainFragment(Fragment fragment, String tag) {
        Log.d(TAG, "replaceMainFragment -> " + tag);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainerView, fragment, tag);
        ft.commit();
    }

    public void showFullscreenFragment(Fragment fragment, String tag, boolean addToBackStack) {
        Log.d(TAG, "showFullscreenFragment tag=" + tag + " addToBackStack=" + addToBackStack);
        if (isOpeningFullscreen) {
            Log.d(TAG, "Already opening fullscreen -> ignoring request");
            return;
        }

        Fragment existing = getChildFragmentManager().findFragmentByTag(tag);
        if (existing != null) {
            Log.d(TAG, "Fullscreen fragment with tag already exists -> ignoring");
            return;
        }

        isOpeningFullscreen = true;
        fullscreen.setVisibility(VISIBLE);
        container.setVisibility(INVISIBLE);

        FragmentTransaction ft = getChildFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fullscreen_container, fragment, tag);

        if (addToBackStack) ft.addToBackStack(tag);
        ft.commit();

        bottomNavigationView.setVisibility(INVISIBLE);
        fullscreen.post(() -> isOpeningFullscreen = false);
    }

    public void closeFullscreenFragment() {
        Log.d(TAG, "closeFullscreenFragment");
        final FragmentManager fm = getChildFragmentManager();

        Runnable restoreUi = () -> {
            Log.d(TAG, "restoreUi -> hide fullscreen, show bottom nav");
            fullscreen.setVisibility(GONE);
            bottomNavigationView.setVisibility(VISIBLE);
            container.setVisibility(VISIBLE);
        };

        if (fm.getBackStackEntryCount() > 0) {
            Log.d(TAG, "Popping back stack");
            fm.popBackStack();
            fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    if (fm.getBackStackEntryCount() == 0) {
                        restoreUi.run();
                        fm.removeOnBackStackChangedListener(this);
                    }
                }
            });
        } else {
            Fragment fragment = fm.findFragmentById(R.id.fullscreen_container);
            if (fragment != null) {
                Log.d(TAG, "Removing fullscreen fragment directly");
                fm.beginTransaction()
                        .remove(fragment)
                        .commitNow();
            } else {
                Log.d(TAG, "No fullscreen fragment to remove");
            }
            restoreUi.run();
        }
    }
}
