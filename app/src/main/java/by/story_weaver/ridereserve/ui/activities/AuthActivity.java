package by.story_weaver.ridereserve.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import by.story_weaver.ridereserve.Logic.viewModels.AuthViewModel;
import by.story_weaver.ridereserve.R;
import by.story_weaver.ridereserve.ui.fragments.auth.EntranceFragment;
import by.story_weaver.ridereserve.ui.fragments.auth.RegistrationFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AuthActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "AuthPrefs";
    private static final String KEY_USER_ID = "user_id";

    private boolean isLoginSelected = true;

    private TextView tabLogin, tabRegister;
    private LinearLayout tabLoginLayout, tabRegisterLayout;
    private CardView authCard;
    private FrameLayout authContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        AuthViewModel authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        if(authViewModel.checkSignedIn() != -1){
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_auth);
        setupWindowInsets();
        initViews();
        setupUI();

        switchToLogin();
        switchToRegister();
        switchToLogin();
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        tabLogin = findViewById(R.id.tabLogin);
        tabRegister = findViewById(R.id.tabRegister);
        tabLoginLayout = findViewById(R.id.tabLoginLayout);
        tabRegisterLayout = findViewById(R.id.tabRegisterLayout);
        authCard = findViewById(R.id.authCard);
        authContainerView = findViewById(R.id.authContainerView);
    }

    private void setupUI() {
        tabLoginLayout.setOnClickListener(v -> switchToLogin());
        tabRegisterLayout.setOnClickListener(v -> switchToRegister());

        authCard.setCardElevation(16f);
        authCard.setMaxCardElevation(24f);
    }

    private void switchToLogin() {
        if (!isLoginSelected) {
            isLoginSelected = true;
            updateTabAppearance(true);
            changeFragment(new EntranceFragment());
        }
    }

    private void switchToRegister() {
        if (isLoginSelected) {
            isLoginSelected = false;
            updateTabAppearance(false);
            changeFragment(new RegistrationFragment());
        }
    }

    private void updateTabAppearance(boolean isLogin) {
        if (isLogin) {
            tabLoginLayout.setBackgroundResource(R.drawable.bg_tab_indicator);
            tabLogin.setTextColor(getColor(R.color.white));

            tabRegisterLayout.setBackgroundResource(android.R.color.transparent);
            tabRegister.setTextColor(getColor(R.color.purple_500));
        } else {
            // Активна вкладка Регистрации
            tabRegisterLayout.setBackgroundResource(R.drawable.bg_tab_indicator);
            tabRegister.setTextColor(getColor(R.color.white));

            tabLoginLayout.setBackgroundResource(android.R.color.transparent);
            tabLogin.setTextColor(getColor(R.color.purple_500));
        }
    }

    private void changeFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        ft.replace(R.id.authContainerView, fragment);
        ft.commit();
    }
}