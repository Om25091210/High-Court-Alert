package in.aryomtech.cgalert;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;

import in.aryomtech.cgalert.CasesAgainstPolice.CasesAgainPolice;

public class entry_actiivity extends AppCompatActivity {

    LinearLayout case_diary, police_contacts, notice_victim, writ_police;
    LottieAnimationView toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_actiivity);

        Window window = entry_actiivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(entry_actiivity.this, R.color.use_bg));

        police_contacts = findViewById(R.id.linearLayout);
        case_diary = findViewById(R.id.linearLayout8);
        notice_victim = findViewById(R.id.linearLayout7);
        writ_police = findViewById(R.id.linearLayout9);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setOnClickListener(v -> {
            /*entry_actiivity.this.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,new about())
                    .addToBackStack(null)
                    .commit();*/

        });

        writ_police.setOnClickListener(v -> {
            entry_actiivity.this.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer, new CasesAgainPolice())
                    .addToBackStack(null)
                    .commit();
        });

        police_contacts.setOnClickListener(v -> {
            entry_actiivity.this.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer, new DistrictData())
                    .addToBackStack(null)
                    .commit();
        });

        notice_victim.setOnClickListener(v -> {
            entry_actiivity.this.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer, new NoticeVictim())
                    .addToBackStack(null)
                    .commit();
        });

        case_diary.setOnClickListener(v -> {
            Intent intent = new Intent(entry_actiivity.this, Home.class);
            startActivity(intent);
        });
    }
}