package in.aryomtech.cgalert.Writ;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import in.aryomtech.cgalert.R;
import in.aryomtech.cgalert.Writ.Fragments.AllWrits;
import in.aryomtech.cgalert.Writ.Fragments.DisposedDismissedWrits;
import in.aryomtech.cgalert.Writ.Fragments.PendingWrits;
import in.aryomtech.cgalert.Writ.Fragments.TodayWrits;
import in.aryomtech.cgalert.Writ.Fragments.UrgentWrits;
import in.aryomtech.cgalert.Writ.Model.WritModel;
import in.aryomtech.myapplication.v4.FragmentPagerItemAdapter;
import in.aryomtech.myapplication.v4.FragmentPagerItems;
import soup.neumorphism.NeumorphCardView;

public class WritsMain extends AppCompatActivity {

    List<WritModel> list;
    ImageView form;
    String stat_name;
    FirebaseAuth auth;
    FirebaseUser user;
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

        form = findViewById(R.id.form);
        blue = findViewById(R.id.blue);
        orange = findViewById(R.id.back);

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
