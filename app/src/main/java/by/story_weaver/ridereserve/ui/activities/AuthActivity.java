package by.story_weaver.ridereserve.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import by.story_weaver.ridereserve.Logic.viewModels.AuthViewModel;
import by.story_weaver.ridereserve.R;
import by.story_weaver.ridereserve.ui.fragments.auth.EntranceFragment;
import by.story_weaver.ridereserve.ui.fragments.auth.RegistrationFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AuthActivity extends AppCompatActivity {
    private Button entrance;
    private Button registration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        AuthViewModel viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        if(viewModel.checkSignedIn() != -1){
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_auth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findAllId();
        entrance.setOnClickListener(v1 -> change(new EntranceFragment()));
        registration.setOnClickListener(v1 -> change(new RegistrationFragment()));
    }

    private void findAllId(){
        entrance = findViewById(R.id.enterButtonAuth);
        registration = findViewById(R.id.regButtonAuth);
    }

    private void change(Fragment f){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fade_in,R.anim.fade_out);
        ft.replace(R.id.authContainerView,f);
        ft.commit();
    }
}