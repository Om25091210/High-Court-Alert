package in.aryomtech.cgalert.NoticeVictim;

import android.app.DatePickerDialog;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import in.aryomtech.cgalert.R;


public class NoticeForm extends Fragment {

    View view;
    ImageView back;
    Context contextNullSafe;
    int check_;
    DatabaseReference reference;
    AutoCompleteTextView district,station;
    EditText crime_no,crime_year,case_year,advocate, appellant, caseNo;
    TextView notice_date, hearing_date, submit , crr,cra,mcrc;
    FirebaseAuth auth;
    FirebaseUser user;
    LottieAnimationView done;
    ConstraintLayout lay;
    DatabaseReference reference_phone;
    List<String> district_list,ps_list;
    String pushkey;
    int val = 0;
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
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().remove(NoticeForm.this).commit();
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
            crr.setBackgroundResource(R.drawable.bg_button_focus);
            mcrc.setBackgroundResource(R.drawable.bg_button2);
            cra.setBackgroundResource(R.drawable.bg_button2);
            val = 1;
        });

        cra.setOnClickListener(v->{
            cra.setBackgroundResource(R.drawable.bg_button_focus);
            mcrc.setBackgroundResource(R.drawable.bg_button2);
            crr.setBackgroundResource(R.drawable.bg_button2);
            val = 2;
        });

        mcrc.setOnClickListener(v->{
            mcrc.setBackgroundResource(R.drawable.bg_button_focus);
            cra.setBackgroundResource(R.drawable.bg_button2);
            crr.setBackgroundResource(R.drawable.bg_button2);
            val = 3;
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
                                                if (val == 0) {
                                                    Toast.makeText(getActivity(), "Please select the case type", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    done.setVisibility(View.VISIBLE);
                                                    new Handler(Looper.myLooper()).postDelayed(() -> {
                                                        done.setVisibility(View.GONE);
                                                    }, 800);

                                                    datasend();
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

    public void datasend(){
        reference.child(pushkey).child("district").setValue(district.getText().toString());
        reference.child(pushkey).child("station").setValue(station.getText().toString());
        reference.child(pushkey).child("crimeNo").setValue(crime_no.getText().toString());
        reference.child(pushkey).child("crimeYear").setValue(crime_year.getText().toString());
        reference.child(pushkey).child("caseYear").setValue(case_year.getText().toString());
        reference.child(pushkey).child("noticeDate").setValue(notice_date.getText().toString());
        reference.child(pushkey).child("hearingDate").setValue(hearing_date.getText().toString());
        reference.child(pushkey).child("advocate").setValue(advocate.getText().toString());
        reference.child(pushkey).child("appellant").setValue(appellant.getText().toString());
        reference.child(pushkey).child("notified").setValue("Once");
        reference.child(pushkey).child("caseNo").setValue(caseNo.getText().toString());
        reference.child(pushkey).child("pushkey").setValue(pushkey);

        if (val ==1){
            reference.child(pushkey).child("caseType").setValue("CRR");
        }
        else if (val == 2){
            reference.child(pushkey).child("caseType").setValue("CRA");
        }
        else if(val==3){
            reference.child(pushkey).child("caseType").setValue("MCRC");
        }
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
}