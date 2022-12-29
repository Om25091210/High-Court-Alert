package in.aryomtech.cgalert.policestation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mosio.myapplication2.views.DuoDrawerLayout;
import com.mosio.myapplication2.views.DuoMenuView;
import com.mosio.myapplication2.widgets.DuoDrawerToggle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import in.aryomtech.cgalert.DistrictData;
import in.aryomtech.cgalert.Home;
import in.aryomtech.cgalert.Login;
import in.aryomtech.cgalert.R;
import in.aryomtech.cgalert.Splash;
import in.aryomtech.cgalert.about_dev;
import in.aryomtech.cgalert.duo_frags.about;

public class p_Home extends AppCompatActivity implements DuoMenuView.OnMenuClickListener{

    TextView station_name_txt;
    String stat_name;
    FirebaseAuth auth;
    FirebaseUser user;
    private ArrayList<String> mTitles = new ArrayList<>();
    DatabaseReference reference;
    private static final int PERMISSION_SEND_SMS = 123;
    public static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    int downspeed;
    menuAdapter mMenuAdapter;
    private ViewHolder mViewHolder;
    int upspeed;
    String DeviceToken;
    ImageView phone_num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phome);

        Window window = p_Home.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(p_Home.this, R.color.use_bg));

        requestSmsPermission();
        reference= FirebaseDatabase.getInstance().getReference().child("users");


        getSharedPreferences("authorized_entry",MODE_PRIVATE).edit()
                .putBoolean("entry_done",true).apply();

        station_name_txt=findViewById(R.id.textView4);
        phone_num=findViewById(R.id.entry2);
        stat_name= getSharedPreferences("station_name_K",MODE_PRIVATE)
                .getString("the_station_name2003","");

        station_name_txt.setText(stat_name);

        mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menuOptions)));

        // Initialize the views
        mViewHolder = new ViewHolder();

        // Handle menu actions
        handleMenu();

        // Handle drawer actions
        handleDrawer();

        // Show main fragment in container
        goToFragment(new Frag_p_Home());
        mMenuAdapter.setViewSelected(0);
        setTitle(mTitles.get(0));

        getting_device_token();
        check_if_token();
        findViewById(R.id.card_fb).setOnClickListener(s-> {
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
        });

    }

    private void check_if_token() {
        String pkey=reference.push().getKey();
        if(DeviceToken!=null){
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {//
                    int c=0;
                    if(snapshot.child(user.getUid()).child("token").exists()) {
                        for (DataSnapshot ds : snapshot.child(user.getUid()).child("token").getChildren()) {
                            Log.e("forloop", "YES");
                            if (snapshot.child(user.getUid()).child("token").child(Objects.requireNonNull(ds.getKey())).child(DeviceToken).exists()) {
                                c = 1;
                                Log.e("loop if", "YES");
                            }
                        }
                        if (c == 0) {
                            reference.child(user.getUid()).child("token").child(pkey).setValue(DeviceToken);
                        }
                    }
                    else{
                        reference.child(user.getUid()).child("token").child(pkey).setValue(DeviceToken);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }
    private void getting_device_token() {
        ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkCapabilities nc = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        if(nc!=null) {
            downspeed = nc.getLinkDownstreamBandwidthKbps()/1000;
            upspeed = nc.getLinkUpstreamBandwidthKbps()/1000;
        }else{
            downspeed=0;
            upspeed=0;
        }

        if((upspeed!=0 && downspeed!=0) || getWifiLevel()!=0) {
            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                if (!TextUtils.isEmpty(token)) {
                    Log.d("token", "retrieve token successful : " + token);
                } else {
                    Log.w("token121", "token should not be null...");
                }
            }).addOnFailureListener(e -> {
                //handle e
            }).addOnCanceledListener(() -> {
                //handle cancel
            }).addOnCompleteListener(task ->
            {
                try {
                    DeviceToken = task.getResult();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
    private void requestSmsPermission() {

        // check permission is given
        if (ContextCompat.checkSelfPermission(p_Home.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(p_Home.this,
                    new String[]{Manifest.permission.SEND_SMS},
                    PERMISSION_SEND_SMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length>0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(p_Home.this, "Permission granted!", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(p_Home.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }

    }
    public int getWifiLevel()
    {
        try {
            WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            int linkSpeed = wifiManager.getConnectionInfo().getRssi();
            return WifiManager.calculateSignalLevel(linkSpeed, 5);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void handleMenu() {
        mMenuAdapter = new menuAdapter(mTitles);

        mViewHolder.mDuoMenuView.setOnMenuClickListener(this);
        mViewHolder.mDuoMenuView.setAdapter(mMenuAdapter);
    }

    private void handleDrawer() {
        DuoDrawerToggle duoDrawerToggle = new DuoDrawerToggle(this,
                mViewHolder.mDuoDrawerLayout,
                mViewHolder.mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mViewHolder.mDuoDrawerLayout.setDrawerListener(duoDrawerToggle);
        duoDrawerToggle.syncState();

    }
    private void goToFragment(Fragment fragment) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, fragment,"mainFrag").commit();
    }
    @Override
    public void onFooterClicked() {

        auth.signOut();
        startActivity(new Intent(p_Home.this , Splash.class));
        finish();
    }

    @Override
    public void onHeaderClicked() {

    }

    @Override
    public void onOptionClicked(int position, Object objectClicked) {
        // Set the toolbar title


        // Set the right options selected
        mMenuAdapter.setViewSelected(position);

        // Navigate to the right fragment
        if(position==1) {
            p_Home.this.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,new about())
                    .addToBackStack(null)
                    .commit();
            mMenuAdapter.setViewSelected(0);
            mViewHolder.mDuoDrawerLayout.closeDrawer();
        }
        else if(position==2) {
            String title ="*High Court Alert*"+"\n\n"+"*उच्च न्यायालय की केस डायरी को जामा और वपास लेजाने के लिए सतार्क और समय बतने वाले ऐप को डाउनलोड करे नीचे दीये गए लिंक से।*\n\nDownload this app to stay alerted and notified for the case diaries both for submission and return.Link below"; //Text to be shared
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, title+"\n\n"+"This is a playstore link to download.. " + "https://play.google.com/store/apps/details?id=" + getPackageName());
            startActivity(Intent.createChooser(sharingIntent, "Share using"));

            mMenuAdapter.setViewSelected(0);
            mViewHolder.mDuoDrawerLayout.closeDrawer();
        }
        else if(position==3) {
            p_Home.this.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,new about_dev())
                    .addToBackStack(null)
                    .commit();
            mMenuAdapter.setViewSelected(0);
            mViewHolder.mDuoDrawerLayout.closeDrawer();
        }
        else if(position==4) {
            DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("handles");
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
            mMenuAdapter.setViewSelected(0);
            mViewHolder.mDuoDrawerLayout.closeDrawer();
        }
        else if(position==5){
            DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("handles");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
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
            mMenuAdapter.setViewSelected(0);
            mViewHolder.mDuoDrawerLayout.closeDrawer();
        }
        else if(position==6){

            mMenuAdapter.setViewSelected(0);
            mViewHolder.mDuoDrawerLayout.closeDrawer();
        }
        else {
            mViewHolder.mDuoDrawerLayout.closeDrawer();
        }
        // Close the drawer

    }

    private class ViewHolder {
        private final DuoDrawerLayout mDuoDrawerLayout;
        private final DuoMenuView mDuoMenuView;
        private final ImageView mToolbar;

        ViewHolder() {
            mDuoDrawerLayout =findViewById(R.id.drawer);
            mDuoMenuView = (DuoMenuView) mDuoDrawerLayout.getMenuView();
            mToolbar = findViewById(R.id.toolbar);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        if(user==null){
            Intent i = new Intent(p_Home.this, Login.class);
            startActivity(i);
            finish();
        }
    }
}