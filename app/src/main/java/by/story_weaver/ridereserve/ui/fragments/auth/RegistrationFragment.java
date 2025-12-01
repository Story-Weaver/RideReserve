package by.story_weaver.ridereserve.ui.fragments.auth;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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
import android.widget.ProgressBar;
import android.widget.Toast;

import by.story_weaver.ridereserve.Logic.data.models.User;
import by.story_weaver.ridereserve.Logic.viewModels.AuthViewModel;
import by.story_weaver.ridereserve.R;
import by.story_weaver.ridereserve.ui.activities.MainActivity;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegistrationFragment extends Fragment {
    private ProgressBar progressBar;
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
            String mail = email.getText().toString().trim();
            String password = pass.getText().toString().trim();
            String fullName = name.getText().toString().trim();
            String phoneNumber = phone.getText().toString().trim();

            if (mail.isEmpty() || password.isEmpty() || fullName.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidEmail(mail)) {
                Toast.makeText(requireContext(), "Введите корректный email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(requireContext(), "Пароль должен содержать минимум 6 символов", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User();
            user.setEmail(mail);
            user.setPassword(password);
            user.setFullName(fullName);
            user.setPhone(phoneNumber);

            viewModel.register(user);
        });
    }
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private void findById(View view){
        progressBar = view.findViewById(R.id.progressBar_Registration);
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
                    progressBar.setVisibility(VISIBLE);
                    break;
                case ERROR:
                    progressBar.setVisibility(GONE);
                    break;
                case SUCCESS:
                    progressBar.setVisibility(GONE);
                    viewModel.setUserInSystem(state.data.getId());
                    startActivity(new Intent(requireActivity(), MainActivity.class));
                    requireActivity().finish();
                    break;
            }
        });
    }
}