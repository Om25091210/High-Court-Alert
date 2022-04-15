package in.aryomtech.cgalert;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Locale;

import in.aryomtech.cgalert.policestation.p_Home;

public class Splash extends AppCompatActivity {


    DatabaseReference user_reference;
    FirebaseUser user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Window window = Splash.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(Splash.this, R.color.use_bg));

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();

        user_reference= FirebaseDatabase.getInstance().getReference().child("users");

        new Handler(Looper.myLooper()).postDelayed(() -> {
            if(user!=null){

                String user_is=getSharedPreferences("useris?",MODE_PRIVATE)
                        .getString("the_user_is?","");

                boolean auth_entry=  getSharedPreferences("authorized_entry",MODE_PRIVATE)
                        .getBoolean("entry_done",false);

                if(user_is.equals("p_home") && auth_entry){
                    Intent i = new Intent(Splash.this, p_Home.class);
                    startActivity(i);
                    finish();
                }
                else if(auth_entry){
                    Intent i = new Intent(Splash.this, Home.class);
                    startActivity(i);
                    finish();
                }

            }
            else {
                Intent intent = new Intent(Splash.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        },2100);
    }

}