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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
    private ProgressBar progressBar;


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
            String mail = email.getText().toString().trim();
            String password = pass.getText().toString().trim();

            if (mail.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidEmail(mail)) {
                Toast.makeText(requireContext(), "Введите корректный email", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.login(mail, password);
        });

    }
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private void findById(View view){
        progressBar = view.findViewById(R.id.progressBar_Entrance);
        email = view.findViewById(R.id.enterEmail);
        pass = view.findViewById(R.id.enterPass);
        enter = view.findViewById(R.id.enterButtonEnter);
    }
    private void observeLogin(){
        viewModel.getUserStateEnter().observe(getViewLifecycleOwner(), state -> {
            switch (state.status){
                case LOADING:
                    progressBar.setVisibility(VISIBLE);
                    break;
                case ERROR:
                    progressBar.setVisibility(GONE);
                    //TODO
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