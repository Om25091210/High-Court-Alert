package in.aryomtech.cgalert.Fragments.admin;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import in.aryomtech.cgalert.R;


public class Entries extends Fragment {

    View view;
    private Context contextNullSafe;
    EditText name_edt,num;
    ImageView back;
    TextView submit_txt,sub_txt,district_txt,pr_roll,admin_num;
    LinearLayout add_admin,add_station;
    AutoCompleteTextView ac_district,policeStation;
    DatabaseReference reference,reference_phone;
    List<String> district,ps_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_entries, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        district =new ArrayList<>();
        ps_list=new ArrayList<>();
        add_admin=view.findViewById(R.id.linearLayout7);
        add_station=view.findViewById(R.id.add_station);
        submit_txt=view.findViewById(R.id.submit_txt);
        sub_txt=view.findViewById(R.id.sub_txt);
        name_edt=view.findViewById(R.id.name_edt);
        district_txt=view.findViewById(R.id.textView11);
        ac_district=view.findViewById(R.id.ac_district);
        pr_roll=view.findViewById(R.id.pr_roll);
        admin_num=view.findViewById(R.id.admin_num);
        num=view.findViewById(R.id.num);
        policeStation=view.findViewById(R.id.policeStation);
        back=view.findViewById(R.id.imageView4);
        back.setOnClickListener(v-> back());

        reference = FirebaseDatabase.getInstance().getReference().child("admin");
        reference_phone = FirebaseDatabase.getInstance().getReference().child("Phone numbers");
        get_districts_phone();
        add_admin.setOnClickListener(v->{
            submit_txt.setVisibility(View.VISIBLE);
            name_edt.setVisibility(View.VISIBLE);
            district_txt.setVisibility(View.GONE);
            admin_num.setVisibility(View.GONE);
            num.setVisibility(View.GONE);
            ac_district.setVisibility(View.GONE);
            pr_roll.setVisibility(View.GONE);
            policeStation.setVisibility(View.GONE);
            sub_txt.setVisibility(View.GONE);

        });

        add_station.setOnClickListener(v->{
            submit_txt.setVisibility(View.GONE);
            name_edt.setVisibility(View.GONE);
            num.setVisibility(View.VISIBLE);
            admin_num.setVisibility(View.VISIBLE);
            district_txt.setVisibility(View.VISIBLE);
            ac_district.setVisibility(View.VISIBLE);
            pr_roll.setVisibility(View.VISIBLE);
            policeStation.setVisibility(View.VISIBLE);
            sub_txt.setVisibility(View.VISIBLE);

        });

        submit_txt.setOnClickListener(v->{
            if(!name_edt.getText().toString().trim().equals("")) {
                reference.child(name_edt.getText().toString().trim()).setValue(name_edt.getText().toString().trim());
                name_edt.setText("");
                Snackbar.make(add_admin,"Number Uploaded!!",Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#171746"))
                        .setTextColor(Color.parseColor("#FF7F5C"))
                        .setBackgroundTint(Color.parseColor("#171746"))
                        .show();
            }
        });
        sub_txt.setOnClickListener(v->{
            if(!num.getText().toString().trim().equals("")
               && !ac_district.getText().toString().trim().equals("")
               && !policeStation.getText().toString().trim().equals("")) {

                reference_phone.child(ac_district.getText().toString().trim())
                        .child(policeStation.getText().toString().trim())
                        .setValue(num.getText().toString().trim());

                num.setText("");
                ac_district.setText("");
                policeStation.setText("");

                Snackbar.make(add_admin,"Number Uploaded!!",Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#171746"))
                        .setTextColor(Color.parseColor("#FF7F5C"))
                        .setBackgroundTint(Color.parseColor("#171746"))
                        .show();
            }
        });
        ac_district.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                get_police_station(ac_district.getText().toString().trim());
            }
        });

        return view;
    }
    private void get_districts_phone() {
        reference_phone.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    district.add(ds.getKey());
                    //Creating the instance of ArrayAdapter containing list of language names
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>
                            (getContextNullSafety(), android.R.layout.select_dialog_item, district);
                    //Getting the instance of AutoCompleteTextView
                    ac_district.setThreshold(1);//will start working from first character
                    ac_district.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
                    ac_district.setTextColor(Color.RED);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void get_police_station(String district) {
        reference_phone.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.child(district).getChildren()) {
                    if (dataSnapshot.getKey().substring(0, 2).equals("PS")) {
                        ps_list.add(dataSnapshot.getKey().substring(3));
                        //Creating the instance of ArrayAdapter containing list of language names
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                                (getContextNullSafety(), android.R.layout.select_dialog_item, ps_list);
                        //Getting the instance of AutoCompleteTextView
                        policeStation.setThreshold(1);//will start working from first character
                        policeStation.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
                        policeStation.setTextColor(Color.RED);
                    }
                }
                Log.e("PS = ",ps_list+"");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    /**CALL THIS IF YOU NEED CONTEXT*/
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
    private void back(){
        FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        if(fm.getBackStackEntryCount()>0) {
            fm.popBackStack();
        }
        ft.commit();
    }
}