package in.aryomtech.cgalert;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


import in.aryomtech.cgalert.NoticeVictim.NoticemainAdmin;
import in.aryomtech.cgalert.Writ.WritForm;
import in.aryomtech.cgalert.duo_frags.about;
import in.aryomtech.cgalert.policestation.p_Home;

public class Dashboard extends AppCompatActivity {

    LinearLayout case_diary, police_contacts, notice_victim, writ_police;
    //LottieAnimationView toolbar;
    FirebaseAuth auth;
    FirebaseUser user;
    boolean isadmin=false;
    String redirect_to="";
    Toolbar toolbar;
    NavigationView navView;
    OnBackPressedListener onBackpressedListener;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_actiivity);

       /* Window window = Dashboard.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(Dashboard.this, R.color.use_bg));*/

        setStatusBarTransparent();

        getSharedPreferences("authorized_entry",MODE_PRIVATE).edit()
                .putBoolean("entry_done",true).apply();

        redirect_to=getSharedPreferences("useris?",MODE_PRIVATE)
                .getString("the_user_is?","");
        auth= FirebaseAuth.getInstance();
        user=auth.getCurrentUser();



        police_contacts = findViewById(R.id.linearLayout);
        case_diary = findViewById(R.id.linearLayout8);
        notice_victim = findViewById(R.id.linearLayout7);
        writ_police = findViewById(R.id.linearLayout9);

        toolbar = findViewById(R.id.toolbar);
        navView = findViewById(R.id.navView);
        drawer = findViewById(R.id.drawer1);

        setSupportActionBar(toolbar);

        //set default home fragment and its title
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        navView.setCheckedItem(R.id.nav_home);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.use_orange));
        toggle.syncState();


       /* findViewById(R.id.card_fb).setOnClickListener(s-> {
            String facebookUrl ="https://www.facebook.com/chhattisgarh.police";
            Intent facebookAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl));
            facebookAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            startActivity(facebookAppIntent);
        });
        findViewById(R.id.card_twitter).setOnClickListener(s->{
            String url = "https://twitter.com/CG_Police";
            Intent twitterAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            twitterAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            startActivity(twitterAppIntent);
        });

        findViewById(R.id.card_whatsapp).setOnClickListener(s->{
            String url = "https://api.whatsapp.com/send?phone=" +"+91"+ "8269737971";
            try {
                PackageManager pm = getPackageManager();
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            } catch (PackageManager.NameNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
        findViewById(R.id.card_insta).setOnClickListener(s->{
            Intent insta_in;
            String scheme = "http://instagram.com/_u/"+"chhattisgarhpolice_";
            String path = "https://instagram.com/"+"chhattisgarhpolice_";
            String nomPackageInfo ="com.instagram.android";
            try {
                getPackageManager().getPackageInfo(nomPackageInfo, 0);
                insta_in = new Intent(Intent.ACTION_VIEW, Uri.parse(scheme));
            } catch (Exception e) {
                insta_in = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
            }
            startActivity(insta_in);
        });*/


        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            Fragment fragment = null;

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_about:
                        fragment = new about();
                        drawer.closeDrawer(GravityCompat.START);
                        callFragment(fragment);
                        break;

                    case R.id.nav_share:
                        String title ="*CG High Court Alert*"+"\n\n"+"*उच्च न्यायालय की केश डायरी मंगाने और जमा करने संबंधित सूचना तथा डायरी की स्थिति पता करने के लिए नीचे दिए गए लिंक से Android App download करें।*\n\nDownload this app to stay alerted and notified for the case diaries for both submission and return. Tap on the below link to download"; //Text to be shared
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, title+"\n\n"+"This is a playstore link to download.. " + "https://play.google.com/store/apps/details?id=" + getPackageName());
                        startActivity(Intent.createChooser(sharingIntent, "Share using"));
                        break;

                    case R.id.nav_developer:
                        fragment = new about_dev();
                        drawer.closeDrawer(GravityCompat.START);
                        //  getSupportActionBar().setTitle("About US");
                        callFragment(fragment);
                        break;

                    case R.id.nav_privacy:
                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("handles");
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String url = snapshot.child("privacy_policy").getValue(String.class);
                                Intent twitterAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                twitterAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                                startActivity(twitterAppIntent);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                        break;

                    case R.id.nav_terms:
                        DatabaseReference reference1 =FirebaseDatabase.getInstance().getReference().child("handles");
                        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String url = snapshot.child("terms_condition").getValue(String.class);
                                Intent twitterAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                twitterAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                                startActivity(twitterAppIntent);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                        break;
                    case R.id.nav_logout:
                        fragment = new about_dev();
                        drawer.closeDrawer(GravityCompat.START);
                        callFragment(fragment);
                        break;

                }
                return true;
            }
        });

        notice_victim.setOnClickListener(v->{
            Intent intent = new Intent(Dashboard.this, NoticemainAdmin.class);
            startActivity(intent);
        });

        writ_police.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, WritsMain.class);
            startActivity(intent);
        });

        police_contacts.setOnClickListener(v -> {
            Dashboard.this.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer, new DistrictData(), "data")
                    .addToBackStack(null)
                    .commit();
        });

        case_diary.setOnClickListener(v -> {
            if(redirect_to.equals("home")) {
                Intent intent = new Intent(Dashboard.this, Home.class);
                startActivity(intent);
            }
            else if(redirect_to.equals("p_home")){
                Intent intent = new Intent(Dashboard.this, p_Home.class);
                startActivity(intent);
            }
        });
    }

    private void callFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.drawer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setStatusBarTransparent () {
        Window window = Dashboard.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    //on backpress
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment test = getSupportFragmentManager().findFragmentByTag("data");
        if (test != null && test.isVisible()) {
            Log.e("frag","fragment showing");//just for dummy line hehe :)
        }
        else {
            if (onBackpressedListener != null) {
                navView.setCheckedItem(R.id.nav_home);
                onBackpressedListener.doBack();
                drawer.closeDrawer(GravityCompat.START);
            } else if (onBackpressedListener == null) {
                finish();
                super.onBackPressed();
            }
        }

    }

    public interface OnBackPressedListener {
        void doBack();
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackpressedListener = onBackPressedListener;
    }

    @Override
    protected void onDestroy() {
        onBackpressedListener = null;
        super.onDestroy();
    }

    /*private void get_status_of_admin() {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("admin");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isadmin=snapshot.child(user.getPhoneNumber().substring(3)+"").exists();
                if(isadmin){
                    notice_victim.setOnClickListener(v -> Dashboard.this.getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                            .add(R.id.drawer, new NoticemainAdmin())
                            .addToBackStack(null)
                            .commit());
                }
                else{
                    getSharedPreferences("isAdmin_or_not",MODE_PRIVATE).edit()
                            .putBoolean("authorizing_admin",false).apply();

                    notice_victim.setOnClickListener(v -> Dashboard.this.getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                            .add(R.id.drawer, new UrgentNTV())
                            .addToBackStack(null)
                            .commit());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        auth= FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        if(user==null){
            Intent i = new Intent(Dashboard.this, Login.class);
            startActivity(i);
            finish();
        }
    }
}