package in.aryomtech.cgalert.Writ;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import in.aryomtech.cgalert.CheckRooted.RootUtil;
import in.aryomtech.cgalert.R;
import in.aryomtech.cgalert.Splash;
import in.aryomtech.cgalert.Writ.Fragments.AllWrits;
import in.aryomtech.cgalert.Writ.Fragments.DisposedDismissedWrits;
import in.aryomtech.cgalert.Writ.Fragments.PendingWrits;
import in.aryomtech.cgalert.Writ.Fragments.TodayWrits;
import in.aryomtech.cgalert.Writ.Fragments.UrgentWrits;
import in.aryomtech.cgalert.Writ.Model.WritModel;
import in.aryomtech.myapplication.v4.FragmentPagerItemAdapter;
import in.aryomtech.myapplication.v4.FragmentPagerItems;
import soup.neumorphism.NeumorphCardView;
import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class WritsMain extends AppCompatActivity {

    List<WritModel> list;
    ImageView form,back;
    TextView decided,department;
    int decided_count=0,department_count=0;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference writ_ref;
    NeumorphCardView blue,orange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writs_main);

        Window window = WritsMain.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(WritsMain.this, R.color.use_bg));

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

      /*  stat_name = getSharedPreferences("station_name_K", MODE_PRIVATE)
                .getString("the_station_name2003", "");*/
        list = new ArrayList<>();
        writ_ref= FirebaseDatabase.getInstance().getReference().child("writ");
        form = findViewById(R.id.form);
        blue = findViewById(R.id.blue);
        orange = findViewById(R.id.back);
        decided = findViewById(R.id.textView6);
        department = findViewById(R.id.textView8);
        back = findViewById(R.id.imageView4);

        back.setOnClickListener(v->{
            onBackPressed();
        });
        blue.setOnClickListener(v->{
            WritsMain.this.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.swipe,new WritNeed(),"writform")
                    .addToBackStack(null)
                    .commit();
        });

        orange.setOnClickListener(v->{
            WritsMain.this.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.swipe,new WritDecided(),"writform")
                    .addToBackStack(null)
                    .commit();
        });

        boolean isadmin=getSharedPreferences("isAdmin_or_not", Context.MODE_PRIVATE)
                .getBoolean("authorizing_admin",false);
        if(isadmin){
            form.setVisibility(View.VISIBLE);
        }
        else{
            form.setVisibility(View.GONE);
        }

        FragmentPagerItemAdapter adapter1 = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(WritsMain.this)
                .add("Urgent Writs", UrgentWrits.class)
                .add("Today's Writs", TodayWrits.class)
                .add("All Writs", AllWrits.class)
                .add("Pending Writs", PendingWrits.class)
                .add("Disposed & Dismissed", DisposedDismissedWrits.class)
                .create());

        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter1);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);
        form.setOnClickListener(v->{
            WritsMain.this.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.swipe,new WritForm(),"writform")
                    .addToBackStack(null)
                    .commit();
        });
        findViewById(R.id.imageView4).setOnClickListener(v->{
            finish();
        });
        if(RootUtil.isDeviceRooted()){
            Toast.makeText(this, "Device Rooted", Toast.LENGTH_SHORT).show();
            WritsMain.this.finish();
        }
        get_counts();
    }

    private void get_counts() {
        writ_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(snapshot.child(Objects.requireNonNull(ds.getKey())).child("decisionDate").exists()){
                        if(snapshot.child(ds.getKey()).child("decisionDate").getValue(String.class).equals("") && !snapshot.child(Objects.requireNonNull(ds.getKey())).child("Judgement").getValue(String.class).equals("DISMISSED")){
                            decided_count++;
                        }
                    }
                    if(snapshot.child(Objects.requireNonNull(ds.getKey())).child("decisionDate").exists() || snapshot.child(Objects.requireNonNull(ds.getKey())).child("Judgement").exists()){
                        if(!snapshot.child(ds.getKey()).child("decisionDate").getValue(String.class).equals("") || snapshot.child(Objects.requireNonNull(ds.getKey())).child("Judgement").getValue(String.class).equals("DISMISSED")){
                            department_count++;
                        }
                    }
                }
                decided.setText(decided_count+"");
                department.setText(department_count+"");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment test1 = getSupportFragmentManager().findFragmentByTag("writform");
        if (test1 != null && test1.isVisible()) {
            Log.e("frag","fragment showing");//just for dummy line hehe :)
        }
        else {
            finish();
        }
    }
}
