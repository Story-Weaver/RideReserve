package by.story_weaver.ridereserve.ui.fragments.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import by.story_weaver.ridereserve.Logic.viewModels.MainViewModel;
import by.story_weaver.ridereserve.Logic.viewModels.ProfileViewModel;
import by.story_weaver.ridereserve.R;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SupportFragment extends Fragment {

    private MainViewModel mainViewModel;
    private ProfileViewModel profileViewModel;

    // Views
    private Spinner spinnerFeedbackType;
    private TextInputEditText etSubject, etMessage, etContactEmail;
    private Button btnAttach, btnSend;
    private TextView tvAttachedFiles;

    private List<Uri> attachedFiles = new ArrayList<>();
    private String[] feedbackTypes = {
            "Предложение по улучшению",
            "Сообщение об ошибке",
            "Вопрос по использованию",
            "Жалоба на сервис",
            "Другое"
    };

    private boolean isSubjectAutoSet = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_support, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        initViews(view);
        setupFeedbackTypeDropdown();
        loadUserData();
        setupClickListeners();
    }

    private void initViews(View view) {
        spinnerFeedbackType = view.findViewById(R.id.spinnerFeedbackType);
        etSubject = view.findViewById(R.id.etSubject);
        etMessage = view.findViewById(R.id.etMessage);
        etContactEmail = view.findViewById(R.id.etContactEmail);
        btnAttach = view.findViewById(R.id.btnAttach);
        btnSend = view.findViewById(R.id.btnSend);
        tvAttachedFiles = view.findViewById(R.id.tvAttachedFiles);
    }

    private void setupFeedbackTypeDropdown() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                feedbackTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFeedbackType.setAdapter(adapter);

        // Добавляем слушатель для автоматического заполнения темы
        spinnerFeedbackType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = feedbackTypes[position];
                autoFillSubject(selectedType);
                autoFillMessageHint(selectedType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ничего не делаем
            }
        });
    }

    private void autoFillSubject(String selectedType) {
        // Автоматически заполняем тему только если она пустая или была установлена автоматически ранее
        if (etSubject.getText().toString().trim().isEmpty() || isSubjectAutoSet) {
            String autoSubject = getAutoSubject(selectedType);
            etSubject.setText(autoSubject);
            isSubjectAutoSet = true;
        }
    }

    private void autoFillMessageHint(String selectedType) {
        // Предлагаем шаблон сообщения в зависимости от типа обращения
        String hint = getMessageHint(selectedType);
        etMessage.setHint(hint);
    }

    private String getAutoSubject(String type) {
        switch (type) {
            case "Предложение по улучшению":
                return "Предложение по улучшению сервиса";
            case "Сообщение об ошибке":
                return "Сообщение об ошибке в приложении";
            case "Вопрос по использованию":
                return "Вопрос по использованию приложения";
            case "Жалоба на сервис":
                return "Жалоба на качество сервиса";
            case "Другое":
                return "Обращение в поддержку";
            default:
                return "";
        }
    }

    private String getMessageHint(String type) {
        switch (type) {
            case "Предложение по улучшению":
                return "Опишите ваше предложение по улучшению сервиса. Что именно можно улучшить и как это поможет пользователям?";
            case "Сообщение об ошибке":
                return "Опишите ошибку, которую вы обнаружили. Укажите, какие действия привели к ошибке, и что вы ожидали увидеть. Если возможно, прикрепите скриншоты.";
            case "Вопрос по использованию":
                return "Опишите ваш вопрос по использованию приложения. Что именно вам непонятно или вызывает затруднения?";
            case "Жалоба на сервис":
                return "Опишите ситуацию, которая вызвала ваше недовольство. Укажите дату, время и обстоятельства произошедшего.";
            case "Другое":
                return "Опишите ваше обращение подробно...";
            default:
                return "Опишите ваше предложение, проблему или вопрос...";
        }
    }

    private void loadUserData() {
        // Загружаем email пользователя из профиля
        String userEmail = profileViewModel.getProfile().getEmail();
        if (userEmail != null && !userEmail.isEmpty()) {
            etContactEmail.setText(userEmail);
        }
    }

    private void setupClickListeners() {
        btnAttach.setOnClickListener(v -> {
            attachFile();
        });

        btnSend.setOnClickListener(v -> {
            if (validateForm()) {
                sendFeedback();
            }
        });

        // Сбрасываем флаг автоматической установки темы, если пользователь начал редактировать
        etSubject.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && isSubjectAutoSet) {
                // Если пользователь начинает редактировать автоматически установленную тему,
                // сбрасываем флаг, чтобы не перезаписывать его изменения
                isSubjectAutoSet = false;
            }
        });
    }

    private void attachFile() {
        // Создаем intent для выбора файлов
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Выберите файлы"),
                    1001
            );
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(requireContext(), "Установите файловый менеджер", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && data != null) {
            if (data.getClipData() != null) {
                // Multiple files selected
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri fileUri = data.getClipData().getItemAt(i).getUri();
                    attachedFiles.add(fileUri);
                }
            } else if (data.getData() != null) {
                // Single file selected
                Uri fileUri = data.getData();
                attachedFiles.add(fileUri);
            }
            updateAttachedFilesUI();
        }
    }

    private void updateAttachedFilesUI() {
        if (attachedFiles.isEmpty()) {
            tvAttachedFiles.setText("Прикрепленные файлы: нет");
        } else {
            tvAttachedFiles.setText(String.format("Прикрепленные файлы: %d", attachedFiles.size()));
        }
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Проверка темы
        if (etSubject.getText().toString().trim().isEmpty()) {
            etSubject.setError("Введите тему обращения");
            isValid = false;
        } else {
            etSubject.setError(null);
        }

        // Проверка сообщения
        if (etMessage.getText().toString().trim().isEmpty()) {
            etMessage.setError("Введите сообщение");
            isValid = false;
        } else if (etMessage.getText().toString().trim().length() < 10) {
            etMessage.setError("Сообщение должно содержать минимум 10 символов");
            isValid = false;
        } else {
            etMessage.setError(null);
        }

        // Проверка email
        String email = etContactEmail.getText().toString().trim();
        if (email.isEmpty()) {
            etContactEmail.setError("Введите email для ответа");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etContactEmail.setError("Введите корректный email");
            isValid = false;
        } else {
            etContactEmail.setError(null);
        }

        return isValid;
    }

    private void sendFeedback() {
        String subject = etSubject.getText().toString().trim();
        String message = etMessage.getText().toString().trim();
        String contactEmail = etContactEmail.getText().toString().trim();

        // Создаем intent для отправки email
        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@ridereserve.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[Обратная связь] " + subject);

        // Формируем тело письма
        String emailBody = createEmailBody(subject, message, contactEmail);
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);

        // Добавляем прикрепленные файлы
        if (!attachedFiles.isEmpty()) {
            emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, new ArrayList<>(attachedFiles));
        }

        try {
            startActivity(Intent.createChooser(emailIntent, "Отправить обращение через..."));
            showSuccessMessage("Выберите почтовое приложение для отправки");

            clearForm();
        } catch (android.content.ActivityNotFoundException ex) {
            showErrorMessage("Не найдено почтовое приложение");
        }
    }

    private String createEmailBody(String subject, String message, String contactEmail) {
        return  "Тема: " + subject + "\n\n" +
                "Сообщение:\n" + message + "\n\n" +
                "---\n" +
                "Контактный email: " + contactEmail + "\n" +
                "Отправлено из приложения RideReserve\n" +
                "Версия: 1.0\n" +
                "Устройство: " + android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL + "\n" +
                "Android: " + android.os.Build.VERSION.RELEASE;
    }

    private void clearForm() {
        etSubject.setText("");
        etMessage.setText("");
        attachedFiles.clear();
        updateAttachedFilesUI();
        isSubjectAutoSet = false;

        etMessage.setHint("Опишите ваше предложение, проблему или вопрос...");
    }

    private void showSuccessMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showErrorMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }
}