package by.story_weaver.ridereserve.ui.fragments.user;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;

import by.story_weaver.ridereserve.Logic.data.enums.UserRole;
import by.story_weaver.ridereserve.Logic.data.models.Booking;
import by.story_weaver.ridereserve.Logic.data.models.User;
import by.story_weaver.ridereserve.Logic.viewModels.AuthViewModel;
import by.story_weaver.ridereserve.Logic.viewModels.BookingViewModel;
import by.story_weaver.ridereserve.Logic.viewModels.ProfileViewModel;
import by.story_weaver.ridereserve.R;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BookingFragment extends Fragment {

    private CheckBox cbUseMyData;
    private TextInputEditText etFullName, etPhone, etEmail;
    private TextInputLayout tilFullName, tilPhone, tilEmail;
    private User currentUser;
    private BookingViewModel bookingViewModel;
    private ProfileViewModel profileViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_booking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bookingViewModel = new ViewModelProvider(requireActivity()).get(BookingViewModel.class);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        initViews(view);
        setupCheckboxListener();
        loadCurrentUser();
    }

    private void initViews(View view) {
        cbUseMyData = view.findViewById(R.id.cbUseMyData);
        etFullName = view.findViewById(R.id.etFullName);
        etPhone = view.findViewById(R.id.etPhone);
        etEmail = view.findViewById(R.id.etEmail);
        tilFullName = view.findViewById(R.id.tilFullName);
        tilPhone = view.findViewById(R.id.tilPhone);
        tilEmail = view.findViewById(R.id.tilEmail);
    }
    private void setupCheckboxListener() {
        cbUseMyData.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                fillUserData();
                setFieldsEnabled(false);
            } else {
                setFieldsEnabled(true);
                clearFields();
            }
        });
    }
    private void loadCurrentUser() {
        currentUser = profileViewModel.getProfile();
    }
    private void fillUserData() {
        if (currentUser != null) {
            etFullName.setText(currentUser.getFullName());
            etPhone.setText(currentUser.getPhone());
            etEmail.setText(currentUser.getEmail());
        }
    }
    private void setFieldsEnabled(boolean enabled) {
        etFullName.setEnabled(enabled);
        etPhone.setEnabled(enabled);
        etEmail.setEnabled(enabled);

        int boxStrokeColor = enabled ? R.color.green : R.color.gray_400;
        int hintColor = enabled ? R.color.gray_600 : R.color.gray_400;
        int textColor = enabled ? R.color.black : R.color.gray_600;

        setFieldAppearance(tilFullName, boxStrokeColor, hintColor, textColor);
        setFieldAppearance(tilPhone, boxStrokeColor, hintColor, textColor);
        setFieldAppearance(tilEmail, boxStrokeColor, hintColor, textColor);
    }
    private void setFieldAppearance(TextInputLayout textInputLayout, int boxStrokeColor, int hintColor, int textColor) {
        textInputLayout.setBoxStrokeColor(ContextCompat.getColor(requireContext(), boxStrokeColor));

        textInputLayout.setHintTextColor(ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), hintColor)));

        EditText editText = textInputLayout.getEditText();
        if (editText != null) {
            editText.setTextColor(ContextCompat.getColor(requireContext(), textColor));
        }
    }
    private void clearFields() {
        etFullName.setText("");
        etPhone.setText("");
        etEmail.setText("");
    }
    private boolean isFormValid() {
        boolean isValid = true;

        if (Objects.requireNonNull(etFullName.getText()).toString().trim().isEmpty()) {
            tilFullName.setError("Введите ФИО");
            isValid = false;
        } else {
            tilFullName.setError(null);
        }

        if (Objects.requireNonNull(etPhone.getText()).toString().trim().isEmpty()) {
            tilPhone.setError("Введите телефон");
            isValid = false;
        } else {
            tilPhone.setError(null);
        }

        if (Objects.requireNonNull(etEmail.getText()).toString().trim().isEmpty()) {
            tilEmail.setError("Введите email");
            isValid = false;
        } else {
            tilEmail.setError(null);
        }

        return isValid;
    }
    private void createNewBooking(){
        if(isFormValid()){
            bookingViewModel.addBooking(null);
        }
    }
}