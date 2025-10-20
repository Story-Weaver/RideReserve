package by.story_weaver.ridereserve.ui.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import by.story_weaver.ridereserve.Logic.data.enums.UserRole;
import by.story_weaver.ridereserve.Logic.data.models.User;
import by.story_weaver.ridereserve.Logic.viewModels.AuthViewModel;
import by.story_weaver.ridereserve.Logic.viewModels.MainViewModel;
import by.story_weaver.ridereserve.Logic.viewModels.ProfileViewModel;
import by.story_weaver.ridereserve.R;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UserEditFragment extends Fragment {

    private static final String ARG_USER_ID = "user_id";

    private long editingUserId;
    private ProfileViewModel profileViewModel;
    private MainViewModel mainViewModel;

    // Views
    private TextInputEditText etFullName, etEmail, etPhone;
    private TextInputEditText etPassword, etConfirmPassword;
    private TextInputEditText etCurrentRole;
    private AutoCompleteTextView actvRole;
    private TextInputLayout tilPassword, tilConfirmPassword;
    private CardView cardRole;
    private Button btnSave, btnCancel;

    private String[] roleDisplayNames = {"Пассажир", "Водитель", "Администратор", "Гость"};

    public static UserEditFragment newInstance(long userId) {
        UserEditFragment fragment = new UserEditFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            editingUserId = getArguments().getLong(ARG_USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        initViews(view);
        setupRoleDropdown();
        loadUserData();
        setupClickListeners();
        checkAdminPermissions();
    }

    private void initViews(View view) {
        etFullName = view.findViewById(R.id.etFullName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        etCurrentRole = view.findViewById(R.id.etCurrentRole);
        actvRole = view.findViewById(R.id.actvRole);
        tilPassword = view.findViewById(R.id.tilPassword);
        tilConfirmPassword = view.findViewById(R.id.tilConfirmPassword);
        cardRole = view.findViewById(R.id.cardRole);
        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);
    }

    private void setupRoleDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                roleDisplayNames
        );
        actvRole.setAdapter(adapter);
    }

    private void loadUserData() {
        // Получаем данные пользователя для редактирования
        User userToEdit = profileViewModel.getUserById(editingUserId);
        if (userToEdit != null) {
            etFullName.setText(userToEdit.getFullName());
            etEmail.setText(userToEdit.getEmail());
            etPhone.setText(userToEdit.getPhone());
            etCurrentRole.setText(getRoleDisplayName(userToEdit.getRole()));
        }
    }

    private void checkAdminPermissions() {
        // Проверяем, является ли текущий пользователь администратором
        User currentUser = profileViewModel.getProfile();
        if (currentUser != null && currentUser.getRole() == UserRole.ADMIN) {
            cardRole.setVisibility(VISIBLE);
        }
    }

    private String getRoleDisplayName(UserRole role) {
        if (role == null) return "Неизвестно";

        switch (role) {
            case PASSENGER:
                return "Пассажир";
            case DRIVER:
                return "Водитель";
            case ADMIN:
                return "Администратор";
            case GUEST:
                return "Гость";
            default:
                return "Неизвестно";
        }
    }

    private UserRole getRoleFromDisplayName(String displayName) {
        switch (displayName) {
            case "Пассажир":
                return UserRole.PASSENGER;
            case "Водитель":
                return UserRole.DRIVER;
            case "Администратор":
                return UserRole.ADMIN;
            case "Гость":
                return UserRole.GUEST;
            default:
                return UserRole.PASSENGER;
        }
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> {
            if (validateForm()) {
                saveUserData();
            }
        });

        btnCancel.setOnClickListener(v -> {
            mainViewModel.closeFullscreen();
        });

        // Обработка изменения пароля - показываем/скрываем подтверждение
        etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && etPassword.getText().toString().isEmpty()) {
                tilConfirmPassword.setVisibility(GONE);
            } else if (!etPassword.getText().toString().isEmpty()) {
                tilConfirmPassword.setVisibility(VISIBLE);
            }
        });
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Проверка полного имени
        if (etFullName.getText().toString().trim().isEmpty()) {
            etFullName.setError("Введите ФИО");
            isValid = false;
        } else {
            etFullName.setError(null);
        }

        // Проверка email
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            etEmail.setError("Введите email");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Введите корректный email");
            isValid = false;
        } else {
            etEmail.setError(null);
        }

        // Проверка телефона
        String phone = etPhone.getText().toString().trim();
        if (phone.isEmpty()) {
            etPhone.setError("Введите телефон");
            isValid = false;
        } else {
            etPhone.setError(null);
        }

        // Проверка пароля, если он введен
        String password = etPassword.getText().toString();
        if (!password.isEmpty()) {
            if (password.length() < 4) {
                etPassword.setError("Пароль должен содержать минимум 6 символов");
                isValid = false;
            } else {
                etPassword.setError(null);
            }

            String confirmPassword = etConfirmPassword.getText().toString();
            if (!password.equals(confirmPassword)) {
                etConfirmPassword.setError("Пароли не совпадают");
                isValid = false;
            } else {
                etConfirmPassword.setError(null);
            }
        }

        return isValid;
    }

    private void saveUserData() {
        // Собираем данные из формы
        User updatedUser = new User();
        updatedUser.setId(editingUserId);
        updatedUser.setFullName(etFullName.getText().toString().trim());
        updatedUser.setEmail(etEmail.getText().toString().trim());
        updatedUser.setPhone(etPhone.getText().toString().trim());

        String newPassword = etPassword.getText().toString();
        if (!newPassword.isEmpty()) {
            updatedUser.setPassword(newPassword); // В реальном приложении нужно хэшировать
        }

        User currentUser = profileViewModel.getProfile();
        if (currentUser != null && currentUser.getRole() == UserRole.ADMIN) {
            String selectedRole = actvRole.getText().toString();
            if (!selectedRole.isEmpty()) {
                updatedUser.setRole(getRoleFromDisplayName(selectedRole));
            } else {
                User originalUser = profileViewModel.getUserById(editingUserId);
                if (originalUser != null) {
                    updatedUser.setRole(originalUser.getRole());
                }
            }
        } else {
            User originalUser = profileViewModel.getUserById(editingUserId);
            if (originalUser != null) {
                updatedUser.setRole(originalUser.getRole());
            }
        }
        if(profileViewModel.getProfile().getId() == editingUserId){
            updatedUser.setInSystem(1);
        } else updatedUser.setInSystem(0);

        profileViewModel.editUser(updatedUser);

        showSuccessMessage("Данные профиля успешно обновлены");

        mainViewModel.closeFullscreen();
    }

    private void showSuccessMessage(String message) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show();
    }
}