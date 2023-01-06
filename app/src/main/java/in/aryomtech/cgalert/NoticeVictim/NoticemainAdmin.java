package in.aryomtech.cgalert.NoticeVictim;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import in.aryomtech.cgalert.DB.TinyDB;
import in.aryomtech.cgalert.NoticeVictim.Adapter.NoticeAdapter;
import in.aryomtech.cgalert.NoticeVictim.Fragments.AllNTV;
import in.aryomtech.cgalert.NoticeVictim.Fragments.PendingNTV;
import in.aryomtech.cgalert.NoticeVictim.Fragments.TodayNTV;
import in.aryomtech.cgalert.NoticeVictim.Fragments.UrgentNTV;
import in.aryomtech.cgalert.NoticeVictim.Fragments.Served;
import in.aryomtech.cgalert.NoticeVictim.model.Notice_model;
import in.aryomtech.cgalert.R;
import in.aryomtech.myapplication.v4.FragmentPagerItemAdapter;
import in.aryomtech.myapplication.v4.FragmentPagerItems;
import soup.neumorphism.NeumorphCardView;

public class NoticemainAdmin extends AppCompatActivity {

    String stat_name;
    List<Notice_model> list;
    ImageView form;
    TinyDB tinyDB;
    TextView pending_txt,served_txt;
    int total_pending=0,total_served=0;
    DatabaseReference reference;
    NeumorphCardView pending,served;
    FirebaseAuth auth;
    String ps_or_admin="";
    FirebaseUser user;
    String sp_of;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_noticemain_admin);

        Window window = NoticemainAdmin.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(NoticemainAdmin.this, R.color.use_bg));
        pending_txt=findViewById(R.id.textView6);
        served_txt=findViewById(R.id.textView8);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        stat_name= getSharedPreferences("station_name_K",MODE_PRIVATE)
                .getString("the_station_name2003","");

        list = new ArrayList<>();
        tinyDB=new TinyDB(getApplicationContext());
        form = findViewById(R.id.form);
        served = findViewById(R.id.served);
        pending = findViewById(R.id.pending);
        reference= FirebaseDatabase.getInstance().getReference().child("notice");
        FragmentPagerItemAdapter adapter1 = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(NoticemainAdmin.this)
                .add("Today Notices", TodayNTV.class)
                .add("Urgent Notices", UrgentNTV.class)
                .add("All Notices", AllNTV.class)
                .create());

        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter1);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);
        ps_or_admin=getSharedPreferences("useris?",MODE_PRIVATE)
                .getString("the_user_is?","");
        boolean isadmin=getSharedPreferences("isAdmin_or_not", Context.MODE_PRIVATE)
                .getBoolean("authorizing_admin",false);
        if(isadmin){
            form.setVisibility(View.VISIBLE);
        }
        else{
            form.setVisibility(View.GONE);
        }
        sp_of=getSharedPreferences("Is_SP",MODE_PRIVATE)
                .getString("Yes_of","none");
        if(sp_of.equals("none")) {
            if(tinyDB.getInt("num_station")==0){
                getDataForIG();
            }
            else if(tinyDB.getInt("num_station")==10){
                getDataForSDOP();
            }
            else{
                if(ps_or_admin.equals("home")) {
                    get_data();
                }
                else if(ps_or_admin.equals("p_home")){
                    get_ps_data();
                }
            }
        }
        else
            getdata_for_sp();
        form.setOnClickListener(v->{
            NoticemainAdmin.this.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.constraint,new NoticeForm(),"noticeform")
                    .addToBackStack(null)
                    .commit();
        });
        served.setOnClickListener(v->{
            NoticemainAdmin.this.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.constraint,new Served(),"noticeform")
                    .addToBackStack(null)
                    .commit();
        });
        pending.setOnClickListener(v->{
            NoticemainAdmin.this.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.constraint,new PendingNTV(),"noticeform")
                    .addToBackStack(null)
                    .commit();
        });
        findViewById(R.id.imageView4).setOnClickListener(v->{
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment test = getSupportFragmentManager().findFragmentByTag("noticeform");
        if (test != null && test.isVisible()) {
            Log.e("frag","fragment showing");//just for dummy line hehe :)
        }
        else {
            finish();
        }
    }
    private void get_data() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    if (!snapshot.child(Objects.requireNonNull(ds.getKey())).child("uploaded_date").exists()) {
                        total_pending++;
                    }
                    else{
                        total_served++;
                    }
                }
                pending_txt.setText(total_pending+"");
                served_txt.setText(total_served+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getdata_for_sp() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()) {
                        if (snapshot.child(Objects.requireNonNull(ds.getKey())).child("district").getValue(String.class).trim().toUpperCase().equals(sp_of)) {
                            if (!snapshot.child(Objects.requireNonNull(ds.getKey())).child("uploaded_date").exists()) {
                                total_pending++;
                            }
                            else{
                                total_served++;
                            }
                        }
                }
                pending_txt.setText(total_pending+"");
                served_txt.setText(total_served+"");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDataForSDOP() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()) {
                        if (tinyDB.getListString("districts_list").contains(Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("district").getValue(String.class)).trim().toUpperCase())
                                && tinyDB.getListString("stations_list").contains("PS " + Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("station").getValue(String.class)).trim().toUpperCase())) {
                            if (!snapshot.child(Objects.requireNonNull(ds.getKey())).child("uploaded_date").exists()) {
                                total_pending++;
                            }
                            else{
                                total_served++;
                            }
                        }
                }
                pending_txt.setText(total_pending+"");
                served_txt.setText(total_served+"");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void getDataForIG() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()) {
                        if (tinyDB.getListString("districts_list").contains(Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("district").getValue(String.class)).trim().toUpperCase())) {
                            if (!snapshot.child(Objects.requireNonNull(ds.getKey())).child("uploaded_date").exists()) {
                                total_pending++;
                            }
                            else{
                                total_served++;
                            }
                        }
                }
                pending_txt.setText(total_pending+"");
                served_txt.setText(total_served+"");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private void get_ps_data() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()) {
                    if ((Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("station").getValue(String.class)).trim()).toUpperCase().equals(stat_name.substring(3).trim())) {
                        if (!snapshot.child(Objects.requireNonNull(ds.getKey())).child("uploaded_date").exists()) {
                            total_pending++;
                        }
                        else{
                            total_served++;
                        }
                    }
                }
                pending_txt.setText(total_pending+"");
                served_txt.setText(total_served+"");

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


}
