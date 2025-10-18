package by.story_weaver.ridereserve.ui.fragments.user;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import by.story_weaver.ridereserve.Logic.viewModels.AuthViewModel;
import by.story_weaver.ridereserve.R;
import by.story_weaver.ridereserve.ui.activities.AuthActivity;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {
    private ImageView logOut;
    private AuthViewModel authViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        findById(view);
        logOut.setOnClickListener(v -> {
            authViewModel.logout();
            requireActivity().startActivity(new Intent(requireActivity(), AuthActivity.class));
            requireActivity().finish();
        });
    }

    private void findById(View view){
        logOut = view.findViewById(R.id.logoutButtonProfile);
    }
}