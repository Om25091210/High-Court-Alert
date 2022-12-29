package in.aryomtech.cgalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.aryomtech.cgalert.CasesAgainstPolice.CasesAgainPoliceForm;
import in.aryomtech.cgalert.NoticeVictim.NoticeMain;
import in.aryomtech.cgalert.NoticeVictim.NoticemainAdmin;

public class entry_actiivity extends AppCompatActivity {

    LinearLayout case_diary, police_contacts, notice_victim, writ_police;
    LottieAnimationView toolbar;
    FirebaseAuth auth;
    FirebaseUser user;
    boolean isadmin=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_actiivity);

        Window window = entry_actiivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(entry_actiivity.this, R.color.use_bg));

        getSharedPreferences("authorized_entry",MODE_PRIVATE).edit()
                .putBoolean("entry_done",true).apply();

        auth= FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        police_contacts = findViewById(R.id.linearLayout);
        case_diary = findViewById(R.id.linearLayout8);
        notice_victim = findViewById(R.id.linearLayout7);
        writ_police = findViewById(R.id.linearLayout9);
        toolbar = findViewById(R.id.toolbar);

        get_status_of_admin();
        toolbar.setOnClickListener(v -> {
            /*entry_actiivity.this.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,new about())
                    .addToBackStack(null)
                    .commit();*/

        });

        notice_victim.setOnClickListener(v -> entry_actiivity.this.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .add(R.id.drawer, new NoticeMain())
                .addToBackStack(null)
                .commit());

        writ_police.setOnClickListener(v -> entry_actiivity.this.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .add(R.id.drawer, new CasesAgainPoliceForm())
                .addToBackStack(null)
                .commit());

        police_contacts.setOnClickListener(v -> entry_actiivity.this.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .add(R.id.drawer, new DistrictData())
                .addToBackStack(null)
                .commit());



        case_diary.setOnClickListener(v -> {
            Intent intent = new Intent(entry_actiivity.this, Home.class);
            startActivity(intent);
        });
    }

    private void get_status_of_admin() {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("admin");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isadmin=snapshot.child(user.getPhoneNumber().substring(3)+"").exists();
                if(isadmin){
                    notice_victim.setOnClickListener(v -> entry_actiivity.this.getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                            .add(R.id.drawer, new NoticemainAdmin())
                            .addToBackStack(null)
                            .commit());
                }
                else{
                    getSharedPreferences("isAdmin_or_not",MODE_PRIVATE).edit()
                            .putBoolean("authorizing_admin",false).apply();

                    notice_victim.setOnClickListener(v -> entry_actiivity.this.getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                            .add(R.id.drawer, new NoticeMain())
                            .addToBackStack(null)
                            .commit());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth= FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        if(user==null){
            Intent i = new Intent(entry_actiivity.this, Login.class);
            startActivity(i);
            finish();
        }
    }
}