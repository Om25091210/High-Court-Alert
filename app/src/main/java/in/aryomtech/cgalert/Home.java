package in.aryomtech.cgalert;

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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mosio.myapplication2.views.DuoDrawerLayout;
import com.mosio.myapplication2.views.DuoMenuView;
import com.mosio.myapplication2.widgets.DuoDrawerToggle;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import in.aryomtech.cgalert.Writ.WritForm;
import in.aryomtech.cgalert.Fragments.admin.admin_room;
import in.aryomtech.cgalert.Fragments.admin.form;
import in.aryomtech.cgalert.duo_frags.about;

public class Home extends AppCompatActivity implements DuoMenuView.OnMenuClickListener{

    private menuAdapter mMenuAdapter;
    private ViewHolder mViewHolder;
    private ArrayList<String> mTitles = new ArrayList<>();
    DatabaseReference reference;
    FirebaseAuth auth;
    boolean isadmin=false;
    private static final int PERMISSION_SEND_SMS = 123;
    public static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    FirebaseUser user;
    int downspeed;
    int upspeed;
    String DeviceToken;
    //admin
    ImageView admin,entry,phone_num,cases_against_police;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Window window = Home.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(Home.this, R.color.use_bg));

        getSharedPreferences("authorized_entry",MODE_PRIVATE).edit()
                .putBoolean("entry_done",true).apply();

        requestSmsPermission();

        reference=FirebaseDatabase.getInstance().getReference().child("users");
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        admin=findViewById(R.id.admin);
        cases_against_police=findViewById(R.id.cases_against_police);
        entry=findViewById(R.id.entry);
        phone_num=findViewById(R.id.entry2);

        mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menuOptions)));

        // Initialize the views
        mViewHolder = new ViewHolder();

        // Handle menu actions
        handleMenu();

        // Handle drawer actions
        handleDrawer();

        // Show main fragment in container
        goToFragment(new Frag_Home());
        mMenuAdapter.setViewSelected(0);
        setTitle(mTitles.get(0));

        admin.setOnClickListener(v->{
            Home.this.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,new admin_room())
                    .addToBackStack(null)
                    .commit();
        });
        entry.setOnClickListener(v->{
            Home.this.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,new form())
                    .addToBackStack(null)
                    .commit();
        });
        phone_num.setOnClickListener(v->{
            Home.this.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,new DistrictData())
                    .addToBackStack(null)
                    .commit();
        });
        cases_against_police.setOnClickListener(v->{
            Home.this.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,new WritForm())
                    .addToBackStack(null)
                    .commit();
        });
        getting_device_token();
        get_status_of_admin();
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

    protected String hashGenerator(String userName, String senderId, String content, String secureKey) {
        // TODO Auto-generated method stub
        StringBuffer finalString=new StringBuffer();
        finalString.append(userName.trim()).append(senderId.trim()).append(content.trim()).append(secureKey.trim());
        //		logger.info("Parameters for SHA-512 : "+finalString);
        String hashGen=finalString.toString();
        StringBuffer sb = null;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
            md.update(hashGen.getBytes());
            byte byteData[] = md.digest();
            //convert the byte to hex format method 1
            sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sb.toString();
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

    private void get_status_of_admin() {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("admin");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isadmin=snapshot.child(user.getPhoneNumber().substring(3)+"").exists();
                if(isadmin){
                    admin.setVisibility(View.VISIBLE);
                    entry.setVisibility(View.VISIBLE);
                    getSharedPreferences("isAdmin_or_not",MODE_PRIVATE).edit()
                            .putBoolean("authorizing_admin",true).apply();
                }
                else{
                    admin.setVisibility(View.GONE);
                    entry.setVisibility(View.GONE);
                    getSharedPreferences("isAdmin_or_not",MODE_PRIVATE).edit()
                            .putBoolean("authorizing_admin",false).apply();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
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
        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(Home.this,
                    new String[]{Manifest.permission.SEND_SMS},
                    PERMISSION_SEND_SMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length>0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(Home.this, "Permission granted!", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(Home.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
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
        startActivity(new Intent(Home.this , Splash.class));
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
            Home.this.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,new about())
                    .addToBackStack(null)
                    .commit();
            mMenuAdapter.setViewSelected(0);
            mViewHolder.mDuoDrawerLayout.closeDrawer();
        }
        else if(position==2) {
            String title ="*CG High Court Alert*"+"\n\n"+"*उच्च न्यायालय की केश डायरी मंगाने और जमा करने संबंधित सूचना तथा डायरी की स्थिति पता करने के लिए नीचे दिए गए लिंक से Android App download करें।*\n\nDownload this app to stay alerted and notified for the case diaries for both submission and return. Tap on the below link to download"; //Text to be shared
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, title+"\n\n"+"This is a playstore link to download.. " + "https://play.google.com/store/apps/details?id=" + getPackageName());
            startActivity(Intent.createChooser(sharingIntent, "Share using"));

            mMenuAdapter.setViewSelected(0);
            mViewHolder.mDuoDrawerLayout.closeDrawer();
        }
        else if(position==3) {
            Home.this.getSupportFragmentManager()
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
            Intent i = new Intent(Home.this, Login.class);
            startActivity(i);
            finish();
        }
    }
    public static java.util.Map<String, Object> jsonString2Map( String jsonString ) throws org.json.JSONException {
        Map<String, Object> keys = new HashMap<String, Object>();

        org.json.JSONObject jsonObject = new org.json.JSONObject( jsonString ); // HashMap
        java.util.Iterator<?> keyset = jsonObject.keys(); // HM

        while (keyset.hasNext()) {
            String key =  (String) keyset.next();
            Object value = jsonObject.get(key);
            System.out.print("\n Key : "+key);
            if ( value instanceof org.json.JSONObject ) {
                System.out.println("Incomin value is of JSONObject : ");
                keys.put( key, jsonString2Map( value.toString() ));
            } else if ( value instanceof org.json.JSONArray) {
                org.json.JSONArray jsonArray = jsonObject.getJSONArray(key);
                //JSONArray jsonArray = new JSONArray(value.toString());
                keys.put( key, jsonArray2List( jsonArray ));
            } else {
                keyNode( value);
                keys.put( key, value );
            }
        }
        return keys;
    }
    public static java.util.List<Object> jsonArray2List( org.json.JSONArray arrayOFKeys ) throws org.json.JSONException {
        System.out.println("Incoming value is of JSONArray : =========");
        java.util.List<Object> array2List = new java.util.ArrayList<Object>();
        for ( int i = 0; i < arrayOFKeys.length(); i++ )  {
            if ( arrayOFKeys.opt(i) instanceof org.json.JSONObject ) {
                Map<String, Object> subObj2Map = jsonString2Map(arrayOFKeys.opt(i).toString());
                array2List.add(subObj2Map);
            } else if ( arrayOFKeys.opt(i) instanceof org.json.JSONArray ) {
                java.util.List<Object> subarray2List = jsonArray2List((org.json.JSONArray) arrayOFKeys.opt(i));
                array2List.add(subarray2List);
            } else {
                keyNode( arrayOFKeys.opt(i) );
                array2List.add( arrayOFKeys.opt(i) );
            }
        }
        return array2List;
    }
    public static Object keyNode(Object o) {
        if (o instanceof String || o instanceof Character) return (String) o;
        else if (o instanceof Number) return (Number) o;
        else return o;
    }
}