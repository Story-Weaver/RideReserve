package by.story_weaver.ridereserve.ui.fragments.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import by.story_weaver.ridereserve.Logic.viewModels.AuthViewModel;
import by.story_weaver.ridereserve.R;
import by.story_weaver.ridereserve.ui.activities.MainActivity;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EntranceFragment extends Fragment {
    private EditText email;
    private EditText pass;
    private Button enter;
    private AuthViewModel viewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entrance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        findById(view);
        observeLogin();
        enter.setOnClickListener(v -> {
            String mail = email.getText().toString();
            String password = pass.getText().toString();
            if (!mail.isEmpty() && !password.isEmpty()) {
                viewModel.login(mail, password);
            }
        });
    }

    private void findById(View view){
        email = view.findViewById(R.id.enterEmail);
        pass = view.findViewById(R.id.enterPass);
        enter = view.findViewById(R.id.enterButtonEnter);
    }
    private void observeLogin(){
        viewModel.getCurrentUserStateEnter().observe(getViewLifecycleOwner(), state -> {
            switch (state.status){
                case LOADING:
                case ERROR:
                    break;

                case SUCCESS:
                    viewModel.setUserInSystem(state.data.getId());
                    startActivity(new Intent(requireActivity(), MainActivity.class));
                    requireActivity().finish();
                    break;
            }
        });
    }
}