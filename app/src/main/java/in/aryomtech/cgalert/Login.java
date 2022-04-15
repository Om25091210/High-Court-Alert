package in.aryomtech.cgalert;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.simform.customcomponent.SSCustomEdittextOutlinedBorder;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import in.aryomtech.cgalert.policestation.p_Home;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class Login extends AppCompatActivity {

    LinearLayout linearLayout,logo_layout;
    TextView send_otp;
    ImageView p_back;
    String DeviceToken;
    int downspeed;
    int upspeed;
    PinView pinView;
    SSCustomEdittextOutlinedBorder edtEmail;
    int count=0;
    // variable for FirebaseAuth class
    private FirebaseAuth mAuth;
    String station_name;
    // string for storing our verification ID
    private String verificationId;
    DatabaseReference user_reference;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSharedPreferences("authorized_entry", MODE_PRIVATE).edit()
                    .putBoolean("entry_done", false).apply();

        getSharedPreferences("isAdmin_or_not",MODE_PRIVATE).edit()
                .putBoolean("authorizing_admin",false).apply();

        // below line is for getting instance
        // of our FirebaseAuth.
        mAuth=FirebaseAuth.getInstance();
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/myTopic3")
                .addOnCompleteListener(task -> {
                    String msg = "Done";
                    if (!task.isSuccessful()) {
                        msg = "Failed";
                    }
                    Log.d("topic_log", msg);
                });
        user_reference= FirebaseDatabase.getInstance().getReference().child("users");
        getting_device_token();
        linearLayout=findViewById(R.id.sign_in);
        logo_layout=findViewById(R.id.logo_layout);
        edtEmail=findViewById(R.id.edtEmail);
        send_otp=findViewById(R.id.textView23);
        p_back=findViewById(R.id.p_back);
        pinView = findViewById(R.id.pin_view);
        upAnimate(logo_layout);

        linearLayout.setOnClickListener(v->{
            if(edtEmail.getGetTextValue().trim().length()==10){
                offanimate(edtEmail);
                p_back.setVisibility(View.VISIBLE);
                send_otp.setText("Verify");
                onAnimate(pinView);
                pinView.setVisibility(View.VISIBLE);
                String phone = "+91" + edtEmail.getGetTextValue();
                sendVerificationCode(phone);

            }
            else{
                Toast.makeText(Login.this, "Enter 10 digit mobile number.", Toast.LENGTH_SHORT).show();
            }
        });

        p_back.setOnClickListener(v->{
            onAnimate(edtEmail);
            pinView.setText("");
            p_back.setVisibility(View.GONE);
            send_otp.setText("Send OTP");
            offanimate(pinView);
        });

        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String ch=s+"";
                if(ch.length()==6){
                    String otp_text= Objects.requireNonNull(pinView.getText()).toString().trim();
                    Log.e("pinView","==========");
                    verifyCode(otp_text);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // callback method is called on Phone auth provider.
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks

            // initializing our callbacks for on
            // verification callback method.
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        // below method is used when
        // OTP is sent from Firebase
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            // when we receive the OTP it
            // contains a unique id which
            // we are storing in our string
            // which we have already created.
            verificationId = s;
        }

        // this method is called when user
        // receive OTP from Firebase.
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            // below line is used for getting OTP code
            // which is sent in phone auth credentials.
            final String code = phoneAuthCredential.getSmsCode();

            // checking if the code
            // is null or not.
            if (code != null) {
                // if the code is not null then
                // we are setting that code to
                // our OTP edittext field.
                pinView.setText(code);
                Log.e("inside code block","==========");
                // after setting this code
                // to OTP edittext field we
                // are calling our verifycode method.
                verifyCode(code);
            }
        }

        // this method is called when firebase doesn't
        // sends our OTP code due to any error or issue.
        @Override
        public void onVerificationFailed(FirebaseException e) {
            // displaying error message with firebase exception.
            Log.e("error",e+"");
        }
    };
    // below method is use to verify code from Firebase.
    private void verifyCode(String code) {
        // below line is used for getting getting
        // credentials from our verification id and code.
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        // after getting credential we are
        // calling sign in method.
        signInWithCredential(credential);
    }
    private void signInWithCredential(PhoneAuthCredential credential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // if the code is correct and the task is successful
                            // we are sending our user to new activity.
                            Log.e("task successfull","Success");
                            update_ui();
                        } else {
                            // if the code is not correct then we are
                            // displaying an error message to the user.
                            Log.e("task result",task.getException().getMessage());
                        }
                    }
                });
    }

    private void update_ui() {
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        String pkey=user_reference.push().getKey();

        //TODO: Check number whether it exists in our database if yes then take it to home otherwise show him the toast.
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Phone numbers");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    for (DataSnapshot ds_1 : ds.getChildren()){
                        if (edtEmail.getGetTextValue().trim().equals(ds_1.getValue(String.class))){
                            count=1;
                            station_name=ds_1.getKey();
                            getSharedPreferences("station_name_K",MODE_PRIVATE).edit()
                                    .putString("the_station_name2003",station_name).apply();
                            break;
                        }
                    }
                    if(count==1)
                        break;
                }
                if(count==1 && station_name.substring(0,2).equals("PS")){
                    Log.e("police station if","entered");
                    getSharedPreferences("useris?",MODE_PRIVATE).edit()
                            .putString("the_user_is?","p_home").apply();

                    if(DeviceToken!=null){
                        user_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {//.child(user.getUid()).child("token")
                                int c=0;
                                if(snapshot.child(user.getUid()).child("token").exists()) {
                                    for (DataSnapshot ds : snapshot.child(user.getUid()).child("token").getChildren()) {
                                        Log.e("forloop", "YES");
                                        if (Objects.requireNonNull(snapshot.child(user.getUid()).child("token").child(Objects.requireNonNull(ds.getKey())).getValue(String.class)).equals(DeviceToken)) {
                                            c = 1;
                                            Log.e("loop if", "YES");
                                        }
                                    }
                                    if (c == 0) {
                                        user_reference.child(user.getUid()).child("token").child(pkey).setValue(DeviceToken);
                                    }
                                }
                                else{
                                    user_reference.child(user.getUid()).child("token").child(pkey).setValue(DeviceToken);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }

                    user=mAuth.getCurrentUser();
                    user_reference.child(user.getUid()).child("phone").setValue(user.getPhoneNumber());
                    user_reference.child(user.getUid()).child("name").setValue(station_name);
                    Intent i = new Intent(Login.this, p_Home.class);
                    i.putExtra("station_name",station_name);
                    startActivity(i);
                    finish();
                }
                else if(count==1){
                    Log.e("non-admin","entered");
                    getSharedPreferences("useris?",MODE_PRIVATE).edit()
                            .putString("the_user_is?","home").apply();

                    if(DeviceToken!=null){
                        user_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {//.child(user.getUid()).child("token")
                                int c=0;
                                if(snapshot.child(user.getUid()).child("token").exists()) {
                                    for (DataSnapshot ds : snapshot.child(user.getUid()).child("token").getChildren()) {
                                        Log.e("forloop", "YES");
                                        if (Objects.requireNonNull(snapshot.child(user.getUid()).child("token").child(Objects.requireNonNull(ds.getKey())).getValue(String.class)).equals(DeviceToken)) {
                                            c = 1;
                                            Log.e("loop if", "YES");
                                        }
                                    }
                                    if (c == 0) {
                                        user_reference.child(user.getUid()).child("token").child(pkey).setValue(DeviceToken);
                                    }
                                }
                                else{
                                    user_reference.child(user.getUid()).child("token").child(pkey).setValue(DeviceToken);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }

                    user=mAuth.getCurrentUser();
                    user_reference.child(user.getUid()).child("phone").setValue(user.getPhoneNumber());
                    user_reference.child(user.getUid()).child("name").setValue(station_name);
                    Intent i = new Intent(Login.this, Home.class);
                    startActivity(i);
                    finish();
                }
                else
                    check_for_admin();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void check_for_admin() {
        String pkey=user_reference.push().getKey();
        user=mAuth.getCurrentUser();
        DatabaseReference admin_ref=FirebaseDatabase.getInstance().getReference().child("admin");
        admin_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(ds.getKey().equals(user.getPhoneNumber().substring(3))){
                        count=2;
                        break;
                    }
                }
                if(count==2){
                    Log.e("admin entry","entered");
                    getSharedPreferences("useris?",MODE_PRIVATE).edit()
                            .putString("the_user_is?","home").apply();

                    if(DeviceToken!=null){

                        user_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {//.child(user.getUid()).child("token")
                                int c=0;
                                if(snapshot.child(user.getUid()).child("token").exists()) {
                                    for (DataSnapshot ds : snapshot.child(user.getUid()).child("token").getChildren()) {
                                        Log.e("forloop", "YES");
                                        if (Objects.requireNonNull(snapshot.child(user.getUid()).child("token").child(Objects.requireNonNull(ds.getKey())).getValue(String.class)).equals(DeviceToken)) {
                                            c = 1;
                                            Log.e("loop if", "YES");
                                        }
                                    }
                                    if (c == 0) {
                                        user_reference.child(user.getUid()).child("token").child(pkey).setValue(DeviceToken);
                                    }
                                }
                                else{
                                    Log.e("tokn",pkey+"");
                                    user_reference.child(user.getUid()).child("token").child(pkey).setValue(DeviceToken);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }

                    user=mAuth.getCurrentUser();
                    user_reference.child(user.getUid()).child("phone").setValue(user.getPhoneNumber());
                    user_reference.child(user.getUid()).child("name").setValue("admin");
                    Intent i = new Intent(Login.this, Home.class);
                    startActivity(i);
                    finish();
                }
                else{
                    MotionToast.Companion.darkColorToast(Login.this,
                            "Failed ☹️",
                            "Phone No. Is Not In Our Database",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(Login.this, R.font.helvetica_regular));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void sendVerificationCode(String number) {
        // this method is used for getting
        // OTP on user phone number.
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)            // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)           // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    void offanimate(View view){
        ObjectAnimator move=ObjectAnimator.ofFloat(view, "translationX",-800f);
        move.setDuration(1000);
        ObjectAnimator alpha2= ObjectAnimator.ofFloat(view, "alpha",0);
        alpha2.setDuration(500);
        AnimatorSet animset=new AnimatorSet();
        animset.play(alpha2).with(move);
        animset.start();
    }
    void onAnimate(View view){
        ObjectAnimator move=ObjectAnimator.ofFloat(view, "translationX",0f);
        move.setDuration(1000);
        ObjectAnimator alpha2= ObjectAnimator.ofFloat(view, "alpha",100);
        alpha2.setDuration(500);
        AnimatorSet animset=new AnimatorSet();
        animset.play(alpha2).with(move);
        animset.start();
    }

    void upAnimate(View view){
        ObjectAnimator move=ObjectAnimator.ofFloat(view, "translationY",-200f);
        move.setDuration(500);
        ObjectAnimator alpha2= ObjectAnimator.ofFloat(view, "alpha",100);
        alpha2.setDuration(500);
        AnimatorSet animset=new AnimatorSet();
        animset.play(alpha2).with(move);
        animset.start();
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
}