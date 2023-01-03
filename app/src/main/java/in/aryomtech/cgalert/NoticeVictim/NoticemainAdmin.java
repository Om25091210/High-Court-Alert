package in.aryomtech.cgalert.NoticeVictim;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import in.aryomtech.cgalert.NoticeVictim.Fragments.AllNTV;
import in.aryomtech.cgalert.NoticeVictim.Fragments.TodayNTV;
import in.aryomtech.cgalert.NoticeVictim.Fragments.UrgentNTV;
import in.aryomtech.cgalert.NoticeVictim.model.Notice_model;
import in.aryomtech.cgalert.R;
import in.aryomtech.myapplication.v4.FragmentPagerItemAdapter;
import in.aryomtech.myapplication.v4.FragmentPagerItems;

public class NoticemainAdmin extends AppCompatActivity {

    View view;
    List<Notice_model> list;
    ImageView form;
    String stat_name;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_noticemain_admin);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        stat_name = getSharedPreferences("station_name_K", MODE_PRIVATE)
                .getString("the_station_name2003", "");
        list = new ArrayList<>();

        form = findViewById(R.id.form);

        FragmentPagerItemAdapter adapter1 = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(NoticemainAdmin.this)
                .add("Urgent Notices", UrgentNTV.class)
                .add("Today Notices", TodayNTV.class)
                .add("All Notices", AllNTV.class)
                .create());

        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter1);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);
        boolean isadmin=getSharedPreferences("isAdmin_or_not", Context.MODE_PRIVATE)
                .getBoolean("authorizing_admin",false);
        if(isadmin){
            form.setVisibility(View.VISIBLE);
        }
        else{
            form.setVisibility(View.GONE);
        }
        form.setOnClickListener(v->{
            NoticemainAdmin.this.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.constraint,new NoticeForm(),"noticeform")
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
}
