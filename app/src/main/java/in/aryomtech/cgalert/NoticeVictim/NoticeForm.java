package in.aryomtech.cgalert.NoticeVictim;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import in.aryomtech.cgalert.R;
import in.aryomtech.cgalert.fcm.Specific;


public class NoticeForm extends Fragment {

    View view;
    ImageView back;
    Context contextNullSafe;
    int check_;
    DatabaseReference reference,user_ref;
    Dialog dialog1;
    AutoCompleteTextView district,station;
    EditText crime_no,crime_year,case_year,advocate, appellant, caseNo;
    TextView notice_date, hearing_date, submit , crr,cra,mcrc;
    FirebaseAuth auth;
    FirebaseUser user;
    int c=-1;
    LottieAnimationView done;
    ConstraintLayout lay;
    DatabaseReference reference_phone,gs_ref;
    List<String> district_list,ps_list;
    String pushkey,gsID="";
    String case_type = "";
    String notice;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_notice_form, container, false);
        back = view.findViewById(R.id.imageView4);

        district_list =new ArrayList<>();
        ps_list=new ArrayList<>();

        auth= FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference().child("notice");
        user_ref = FirebaseDatabase.getInstance().getReference().child("users");
        gs_ref = FirebaseDatabase.getInstance().getReference().child("gskey");
        pushkey = reference.push().getKey();
        reference_phone = FirebaseDatabase.getInstance().getReference().child("Phone numbers");
        district = view.findViewById(R.id.ac_district);
        station = view.findViewById(R.id.policeStation);
        get_districts_phone();
        caseNo = view.findViewById(R.id.case_no_edt);
        done = view.findViewById(R.id.animation_view);
        crime_no = view.findViewById(R.id.crime_no_edt);
        crime_year = view.findViewById(R.id.crime_year_edt);
        case_year = view.findViewById(R.id.case_year_edt);
        appellant = view.findViewById(R.id.appellant_edt);
        notice_date = view.findViewById(R.id.notice_date);
        hearing_date = view.findViewById(R.id.next_hearing_date);
        advocate = view.findViewById(R.id.name_edt);
        submit = view.findViewById(R.id.submit_txt);

        crr = view.findViewById(R.id.crr);
        cra = view.findViewById(R.id.cra);
        mcrc = view.findViewById(R.id.mcrc);

        lay = view.findViewById(R.id.lay1);

        notice_date.setOnClickListener(v->{
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    getActivity(),
                    mDateSetListener,
                    year,month,day);
            check_=0;
            dialog.show();
    });

        hearing_date.setOnClickListener(v->{

            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    getActivity(),
                    mDateSetListener,
                    year,month,day);
            check_=1;
            dialog.show();
        });

        back.setOnClickListener(v->{
            back();
        });

        district.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                get_police_station(district.getText().toString().trim());
            }
        });


        mDateSetListener = (datePicker, year, month, day) -> {

            String d=String.valueOf(day);
            String m=String.valueOf(month+1);
            Log.e("month",m+"");
            month = month + 1;
            Log.e("month",month+"");
            if(String.valueOf(day).length()==1)
                d="0"+ day;
            if(String.valueOf(month).length()==1)
                m="0"+ month;
            String date = d + "." + m + "." + year;
            if(check_==0) {
                notice_date.setText(date);
                notice=year+m+d;
               // fd_dot=year+"-"+m+"-"+d;
            }
            else if(check_==1)
                hearing_date.setText(date);
        };

        crr.setOnClickListener(v->{
            crr.setBackgroundResource(R.drawable.bg_button2);
            mcrc.setBackgroundResource(R.drawable.bg_selector);
            cra.setBackgroundResource(R.drawable.bg_selector);
            case_type = "CRR";
        });

        cra.setOnClickListener(v->{
            cra.setBackgroundResource(R.drawable.bg_button2);
            mcrc.setBackgroundResource(R.drawable.bg_selector);
            crr.setBackgroundResource(R.drawable.bg_selector);
            case_type = "CRA";
        });

        mcrc.setOnClickListener(v->{
            mcrc.setBackgroundResource(R.drawable.bg_button2);
            cra.setBackgroundResource(R.drawable.bg_selector);
            crr.setBackgroundResource(R.drawable.bg_selector);
            case_type = "MCRC";
        });

        gs_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gsID=snapshot.getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        submit.setOnClickListener(v-> {
            if(!district.getText().toString().trim().equals("")){
                if(!station.getText().toString().trim().equals("")){
                    if(!crime_no.getText().toString().trim().equals("")){
                        if(!crime_year.getText().toString().trim().equals("")){
                            if(!case_year.getText().toString().trim().equals("")){
                                if(!notice_date.getText().toString().trim().equals("")){
                                    if(!hearing_date.getText().toString().trim().equals("")){
                                        if(!advocate.getText().toString().trim().equals("")) {
                                            if (!appellant.getText().toString().trim().equals("")) {
                                                if (!caseNo.getText().toString().trim().equals("")) {
                                                    if (case_type.trim().equals("")) {
                                                        Toast.makeText(getActivity(), "Please select the case type", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        dialog1 = new Dialog(getContextNullSafety());
                                                        dialog1.setCancelable(false);
                                                        dialog1.setContentView(R.layout.loading_dialog);
                                                        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                                        LottieAnimationView lottieAnimationView=dialog1.findViewById(R.id.animate);
                                                        lottieAnimationView.setAnimation("loader.json");
                                                        dialog1.show();
                                                        send_data_to_sheets();
                                                    }
                                                } else {
                                                    caseNo.setError("Empty");
                                                    Snackbar.make(lay, "Please Add Case Number", Snackbar.LENGTH_LONG)
                                                            .setActionTextColor(Color.parseColor("#171746"))
                                                            .setTextColor(Color.parseColor("#FF7F5C"))
                                                            .setBackgroundTint(Color.parseColor("#171746"))
                                                            .show();
                                                }
                                            }
                                                else {
                                                    appellant.setError("Empty");
                                                    Snackbar.make(lay, "Please Add Appellant Name", Snackbar.LENGTH_LONG)
                                                            .setActionTextColor(Color.parseColor("#171746"))
                                                            .setTextColor(Color.parseColor("#FF7F5C"))
                                                            .setBackgroundTint(Color.parseColor("#171746"))
                                                            .show();
                                                }
                                            }
                                        else{
                                            advocate.setError("Empty");
                                            Snackbar.make(lay,"Please Add Advocate Name",Snackbar.LENGTH_LONG)
                                                    .setActionTextColor(Color.parseColor("#171746"))
                                                    .setTextColor(Color.parseColor("#FF7F5C"))
                                                    .setBackgroundTint(Color.parseColor("#171746"))
                                                    .show();
                                        }
                                    }
                                    else{
                                        hearing_date.setError("Empty");
                                        Snackbar.make(lay,"Please Add Hearing Date.",Snackbar.LENGTH_LONG)
                                                .setActionTextColor(Color.parseColor("#171746"))
                                                .setTextColor(Color.parseColor("#FF7F5C"))
                                                .setBackgroundTint(Color.parseColor("#171746"))
                                                .show();
                                    }
                                }
                                else{
                                    notice_date.setError("Empty");
                                    Snackbar.make(lay,"Please Add Notice Date",Snackbar.LENGTH_LONG)
                                            .setActionTextColor(Color.parseColor("#171746"))
                                            .setTextColor(Color.parseColor("#FF7F5C"))
                                            .setBackgroundTint(Color.parseColor("#171746"))
                                            .show();
                                }
                            }
                            else{
                                case_year.setError("Empty");
                                Snackbar.make(lay,"Please Add Case Year.",Snackbar.LENGTH_LONG)
                                        .setActionTextColor(Color.parseColor("#171746"))
                                        .setTextColor(Color.parseColor("#FF7F5C"))
                                        .setBackgroundTint(Color.parseColor("#171746"))
                                        .show();
                            }
                        }
                        else{
                            crime_year.setError("Empty");
                            Snackbar.make(lay,"Please Add Crime Year.",Snackbar.LENGTH_LONG)
                                    .setActionTextColor(Color.parseColor("#171746"))
                                    .setTextColor(Color.parseColor("#FF7F5C"))
                                    .setBackgroundTint(Color.parseColor("#171746"))
                                    .show();
                        }
                    }
                    else{
                        crime_no.setError("Empty");
                        Snackbar.make(lay,"Please Add Crime no.",Snackbar.LENGTH_LONG)
                                .setActionTextColor(Color.parseColor("#171746"))
                                .setTextColor(Color.parseColor("#FF7F5C"))
                                .setBackgroundTint(Color.parseColor("#171746"))
                                .show();
                    }
                }
                else{
                    station.setError("Empty");
                    Snackbar.make(lay,"Please Add Police Station.",Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.parseColor("#171746"))
                            .setTextColor(Color.parseColor("#FF7F5C"))
                            .setBackgroundTint(Color.parseColor("#171746"))
                            .show();
                }
            }
            else{
                district.setError("Empty");
                Snackbar.make(lay,"Please Add District.",Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#171746"))
                        .setTextColor(Color.parseColor("#FF7F5C"))
                        .setBackgroundTint(Color.parseColor("#171746"))
                        .show();
            }
        });


        OnBackPressedCallback callback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                FragmentTransaction ft=fm.beginTransaction();
                if(fm.getBackStackEntryCount()>0) {
                    fm.popBackStack();
                }
                ft.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);
        return view;
    }

    private void back() {
        FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        if(fm.getBackStackEntryCount()>0) {
            fm.popBackStack();
        }
        ft.commit();
    }

    private void  send_data_to_sheets(){
        Snackbar.make(lay,"Uploading data...",Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.parseColor("#ea4a1f"))
                .setTextColor(Color.parseColor("#000000"))
                .setBackgroundTint(Color.parseColor("#D9F5F8"))
                .show();

        Log.e("sdf","SDF");
        String prev_keygen=district.getText().toString().toUpperCase()+"-"
                +station.getText().toString().toUpperCase()+"-"
                +caseNo.getText().toString().toUpperCase()+"-"
                +case_year.getText().toString().toUpperCase();
        Log.e("GS",gsID);
        String URL = "https://script.google.com/macros/s/"
                + gsID+"/exec?"
                +"noticeDate="+notice_date.getText().toString().toUpperCase()
                +"&policeStation="+station.getText().toString().toUpperCase()
                +"&district="+district.getText().toString().toUpperCase()
                +"&caseNo="+caseNo.getText().toString().toUpperCase()
                +"&caseType="+case_type
                +"&caseYear="+case_year.getText().toString().toUpperCase()
                +"&appealant="+appellant.getText().toString().toUpperCase()
                +"&crimeNo="+crime_no.getText().toString().toUpperCase()
                +"&crimeYear="+crime_year.getText().toString().toUpperCase()
                +"&hearingDate="+hearing_date.getText().toString().toUpperCase()
                +"&advocateName="+advocate.getText().toString().toUpperCase()
                +"&keygen="+hashGenerator(prev_keygen)
                +"&action=victimData";

        RequestQueue queue = Volley.newRequestQueue(getContextNullSafety());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String code="",url="";
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            code=jsonObj.get("code")+"";
                            url=jsonObj.get("url")+"";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(code.equals("202")){
                            datasend(url);
                            Snackbar.make(lay,"Data Uploaded.",Snackbar.LENGTH_LONG)
                                    .setActionTextColor(Color.parseColor("#171746"))
                                    .setTextColor(Color.parseColor("#FF7F5C"))
                                    .setBackgroundTint(Color.parseColor("#171746"))
                                    .show();
                            send_notification();
                        }
                        else{
                            Snackbar.make(lay,"Failed to Upload.",Snackbar.LENGTH_LONG)
                                    .setActionTextColor(Color.parseColor("#000000"))
                                    .setTextColor(Color.parseColor("#000000"))
                                    .setBackgroundTint(Color.parseColor("#FF5252"))
                                    .show();
                            LottieAnimationView lottieAnimationView=dialog1.findViewById(R.id.animate);
                            lottieAnimationView.setAnimation("error.json");
                            dialog1.show();
                            new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialog1.dismiss();
                                }
                            },2000);
                        }
                        Log.e("BULK code", response +"");
                        Log.e("BULK response",response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // enjoy your error status
                Log.e("Status of code = ","Wrong"+" "+error.toString());
                Snackbar.make(lay,"Failed to Upload.",Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#000000"))
                        .setTextColor(Color.parseColor("#000000"))
                        .setBackgroundTint(Color.parseColor("#FF5252"))
                        .show();
                LottieAnimationView lottieAnimationView=dialog1.findViewById(R.id.animate);
                lottieAnimationView.setAnimation("error.json");
                dialog1.show();
                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog1.dismiss();
                    }
                },2000);
            }
        });

        queue.add(stringRequest);
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
    }

    private void send_notification() {
        LottieAnimationView lottieAnimationView=dialog1.findViewById(R.id.animate);
        lottieAnimationView.setAnimation("message_sent.json");
        dialog1.show();
        c=-1;
        String body="अतः उक्त अपराध क्रमांक के पीड़ित को स्वयं मय वैध पहचान पत्र के साथ या अपने अधिवक्ता के माध्यम से दिनांक - " + notice_date.getText().toString().trim() + " को उच्च न्यायालय, बिलासपुर के हेल्प डेस्क या संबंधित जिले के (DLSA) " +
                "DISTRICT LEGAL SERVICES AUTHORITY में उपस्थित होकर अपना प्रतिनिधित्व सुनिश्चित करने हेतु सूचित करने का कष्ट करें।";
        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds:snapshot.getChildren()){
                        if(Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("name").getValue(String.class)).equals("SP "+district.getText().toString().trim().toUpperCase())){
                            c=0;
                            for(DataSnapshot ds_token:snapshot.child(ds.getKey()).child("token").getChildren()){
                                String token=snapshot.child(ds.getKey()).child("token").child(Objects.requireNonNull(ds_token.getKey())).getValue(String.class);
                                Specific specific=new Specific();
                                Log.e("logging_info",pushkey+"");
                                specific.noti("CG Sangyan", body, Objects.requireNonNull(token),pushkey,"notice");
                                Snackbar.make(lay,"Notification Sent to SP.",Snackbar.LENGTH_LONG)
                                        .setActionTextColor(Color.parseColor("#171746"))
                                        .setTextColor(Color.parseColor("#FF7F5C"))
                                        .setBackgroundTint(Color.parseColor("#171746"))
                                        .show();
                            }
                        }
                        if(Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("name").getValue(String.class)).equals("PS "+station.getText().toString().trim().toUpperCase())){
                            c=1;
                            reference.child(pushkey).child("number").setValue(snapshot.child(Objects.requireNonNull(ds.getKey())).child("phone").getValue(String.class));
                            for(DataSnapshot ds_token:snapshot.child(ds.getKey()).child("token").getChildren()){
                                String token=snapshot.child(ds.getKey()).child("token").child(Objects.requireNonNull(ds_token.getKey())).getValue(String.class);
                                Specific specific=new Specific();
                                specific.noti("CG Sangyan", body, Objects.requireNonNull(token),pushkey,"notice");
                            }
                            Snackbar.make(lay,"Notification Sent to Police Station.",Snackbar.LENGTH_LONG)
                                    .setActionTextColor(Color.parseColor("#171746"))
                                    .setTextColor(Color.parseColor("#FF7F5C"))
                                    .setBackgroundTint(Color.parseColor("#171746"))
                                    .show();
                        }
                    }
                    if(c==-1){
                        Snackbar.make(lay,"Notification not sent. Check SP and PS numbers",Snackbar.LENGTH_LONG)
                                .setActionTextColor(Color.parseColor("#171746"))
                                .setTextColor(Color.parseColor("#FF7F5C"))
                                .setBackgroundTint(Color.parseColor("#171746"))
                                .show();
                    }
                    dialog1.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    protected String hashGenerator(String str_hash) {
        // TODO Auto-generated method stub
        StringBuffer finalString=new StringBuffer();
        finalString.append(str_hash);
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
    public void datasend(String url){
        reference.child(pushkey).child("district").setValue(district.getText().toString().trim().toUpperCase());
        reference.child(pushkey).child("station").setValue(station.getText().toString().trim().toUpperCase());
        reference.child(pushkey).child("crimeNo").setValue(crime_no.getText().toString().trim().toUpperCase());
        reference.child(pushkey).child("crimeYear").setValue(crime_year.getText().toString().trim().toUpperCase());
        reference.child(pushkey).child("caseYear").setValue(case_year.getText().toString().trim().toUpperCase());
        reference.child(pushkey).child("noticeDate").setValue(notice_date.getText().toString().trim().toUpperCase());
        reference.child(pushkey).child("hearingDate").setValue(hearing_date.getText().toString().trim().toUpperCase());
        reference.child(pushkey).child("advocate").setValue(advocate.getText().toString().trim().toUpperCase());
        reference.child(pushkey).child("appellant").setValue(appellant.getText().toString().trim().toUpperCase());
        reference.child(pushkey).child("reminded").setValue("once");
        reference.child(pushkey).child("caseNo").setValue(caseNo.getText().toString().trim().toUpperCase());
        reference.child(pushkey).child("pushkey").setValue(pushkey);
        reference.child(pushkey).child("doc_url").setValue(url.trim());
        reference.child(pushkey).child("caseType").setValue(case_type);
    }

    private void get_districts_phone() {
        reference_phone.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    district_list.add(ds.getKey());
                    //Creating the instance of ArrayAdapter containing list of language names
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>
                            (getContextNullSafety(), android.R.layout.select_dialog_item, district_list);
                    //Getting the instance of AutoCompleteTextView
                    district.setThreshold(1);//will start working from first character
                    district.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
                    district.setTextColor(Color.RED);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void get_police_station(String dis) {
        ps_list.clear();
        reference_phone.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.child(dis).getChildren()) {
                    if (dataSnapshot.getKey().substring(0, 2).equals("PS")) {
                        ps_list.add(dataSnapshot.getKey().substring(2));
                        //Creating the instance of ArrayAdapter containing list of language names
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                                (getContextNullSafety(), android.R.layout.select_dialog_item, ps_list);
                        //Getting the instance of AutoCompleteTextView
                        station.setThreshold(1);//will start working from first character
                        station.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
                        station.setTextColor(Color.RED);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public Context getContextNullSafety() {
        if (getContext() != null) return getContext();
        if (getActivity() != null) return getActivity();
        if (contextNullSafe != null) return contextNullSafe;
        if (getView() != null && getView().getContext() != null) return getView().getContext();
        if (requireContext() != null) return requireContext();
        if (requireActivity() != null) return requireActivity();
        if (requireView() != null && requireView().getContext() != null)
            return requireView().getContext();

        return null;

    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextNullSafe = context;
    }
}