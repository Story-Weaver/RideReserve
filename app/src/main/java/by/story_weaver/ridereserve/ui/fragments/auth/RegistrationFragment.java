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

import by.story_weaver.ridereserve.Logic.data.models.User;
import by.story_weaver.ridereserve.Logic.viewModels.AuthViewModel;
import by.story_weaver.ridereserve.R;
import by.story_weaver.ridereserve.ui.activities.MainActivity;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegistrationFragment extends Fragment {
    private EditText email;
    private EditText name;
    private EditText phone;
    private EditText pass;
    private Button reg;
    private AuthViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        findById(view);
        observeReg();
        reg.setOnClickListener(v -> {
            User user = new User();
            user.setEmail(email.getText().toString());
            user.setPassword(pass.getText().toString());
            user.setFullName(name.getText().toString());
            user.setPhone(phone.getText().toString());
            if (!email.getText().toString().isEmpty() && !pass.getText().toString().isEmpty()
                    && !name.getText().toString().isEmpty() && !phone.getText().toString().isEmpty()) {
                viewModel.register(user);
            }
        });
    }

    private void findById(View view){
        email = view.findViewById(R.id.regEmail);
        pass = view.findViewById(R.id.regPass);
        name = view.findViewById(R.id.regName);
        phone = view.findViewById(R.id.regPhone);
        reg = view.findViewById(R.id.regButtonReg);
    }

    private void observeReg(){
        viewModel.getUserStateReg().observe(getViewLifecycleOwner(), state -> {
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