package in.aryomtech.cgalert.Writ;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Objects;

import in.aryomtech.cgalert.R;
import in.aryomtech.cgalert.Writ.Adapter.AppellantAdapter;
import in.aryomtech.cgalert.Writ.Adapter.RespondentAdapter;
import in.aryomtech.cgalert.Writ.Model.WritModel;


public class AppellantFragment extends Fragment {


    View view;
    String  judge_summary, synopsis, decision;
    TextView summary,submit;
    ImageView back;
    int check_;
    boolean isadmin=false;
    RecyclerView respo, appella;
    ArrayList<String> respondents, appellants;
    Context contextNullSafe;
    RespondentAdapter RespondentAdapter;
    AppellantAdapter appellantAdapter;
    DatePickerDialog.OnDateSetListener mDateSetListener;
    DatabaseReference reference,user_ref;
    TextView decisionDate,due,due_date,judgement_date,judgement_date_edt;
    String pushkey;
    LinearLayout layout_checkbox;
    ConstraintLayout layout;
    String type;
    EditText judgement_summary;
    CheckBox allowed, disposed, dismissed;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_appellant, container, false);
        assert getArguments() != null;
        pushkey = getArguments().getString("pushkey");
        judge_summary = getArguments().getString("judge_summary");
        synopsis = getArguments().getString("synopsis");
        decision = getArguments().getString("decision");
        back = view.findViewById(R.id.imageView4);
        judgement_date_edt = view.findViewById(R.id.judgement_date_edt);
        judgement_date = view.findViewById(R.id.judgement_date);
        respo = view.findViewById(R.id.recycler_view);
        due_date = view.findViewById(R.id.due_date);
        appella = view.findViewById(R.id.recycler_view2);
        allowed = view.findViewById(R.id.allowed);
        disposed = view.findViewById(R.id.disposed);
        dismissed = view.findViewById(R.id.dismissed);
        judgement_summary = view.findViewById(R.id.summary_edt);
        summary = view.findViewById(R.id.synop_edt);
        submit = view.findViewById(R.id.submit);
        layout = view.findViewById(R.id.cons_lay);
        due = view.findViewById(R.id.due);
        layout_checkbox = view.findViewById(R.id.linearLayout13);
        reference = FirebaseDatabase.getInstance().getReference().child("writ").child(pushkey);
        user_ref = FirebaseDatabase.getInstance().getReference().child("users");
        decisionDate = view.findViewById(R.id.decision_date);
        decisionDate.setText(decision);
        isadmin=getContextNullSafety().getSharedPreferences("isAdmin_or_not",Context.MODE_PRIVATE)
                .getBoolean("authorizing_admin",false);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(getContext());
        respo.setItemViewCacheSize(500);
        respo.setDrawingCacheEnabled(true);
        respo.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        respo.setLayoutManager(layoutManager3);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        appella.setItemViewCacheSize(500);
        appella.setDrawingCacheEnabled(true);
        appella.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        appella.setLayoutManager(layoutManager2);
        back.setOnClickListener(v->{
          back();
        });
        if(isadmin){
            judgement_summary.setEnabled(true);
            judgement_date.setVisibility(View.VISIBLE);
            layout_checkbox.setVisibility(View.VISIBLE);
            due.setVisibility(View.VISIBLE);
            due_date.setVisibility(View.VISIBLE);
            judgement_date_edt.setVisibility(View.VISIBLE);
        }
        else{
            judgement_summary.setEnabled(false);
            layout_checkbox.setVisibility(View.GONE);
            judgement_date.setVisibility(View.GONE);
            due.setVisibility(View.GONE);
            due_date.setVisibility(View.GONE);
            judgement_date_edt.setVisibility(View.GONE);
        }
        judgement_date_edt.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(
                    getActivity(),
                    mDateSetListener,
                    year, month, day);
            check_ = 1;
            dialog.show();
        });

        decisionDate.setOnClickListener(v->{
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(
                    getActivity(),
                    mDateSetListener,
                    year, month, day);
            check_=0;
            dialog.show();
        });

        allowed.setOnClickListener(v -> {
            dismissed.setChecked(false);
            disposed.setChecked(false);
            type = "Allowed";
        });

        disposed.setOnClickListener(v -> {
            dismissed.setChecked(false);
            allowed.setChecked(false);
            type = "Disposed";
        });

        dismissed.setOnClickListener(v -> {
            allowed.setChecked(false);
            disposed.setChecked(false);
            type = "Dismissed";
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

        if(judge_summary!=null) {
            judgement_summary.setText(judge_summary.toLowerCase());
        }
        else{
            judgement_summary.setText("No Judgement summary for this case yet.");
        }
        if(synopsis!=null)
            summary.setText(synopsis.toLowerCase());

        mDateSetListener = (datePicker, year, month, day) -> {

            String d = String.valueOf(day);
            String m = String.valueOf(month + 1);
            Log.e("month", m + "");
            month = month + 1;
            Log.e("month", month + "");
            if (String.valueOf(day).length() == 1)
                d = "0" + day;
            if (String.valueOf(month).length() == 1)
                m = "0" + month;
            String date = d + "." + m + "." + year;
            if (check_ == 0) {
                decisionDate.setText(date);
            }
            if (check_ == 1) {
                judgement_date_edt.setText(date);
            }
        };

        submit.setOnClickListener(v->{
            if (!decisionDate.getText().toString().equals("")) {
                Snackbar.make(layout,"Deciding Date added successfully.",Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#171746"))
                        .setTextColor(Color.parseColor("#FF7F5C"))
                        .setBackgroundTint(Color.parseColor("#171746"))
                        .show();
                reference.child("decisionDate").setValue(decisionDate.getText().toString().trim());
            }
            else if (!judgement_date_edt.getText().toString().trim().equals("")
                    && type!=null){
                reference.child("judgementDate").setValue(judgement_date_edt.getText().toString().toUpperCase());
                reference.child("Judgement").setValue(type.toUpperCase());
                reference.child("dSummary").setValue(judgement_summary.getText().toString().toUpperCase());
                reference.child("dueDate").setValue(due_date.getText().toString().toUpperCase());
                reference.child("decisionDate").setValue(decisionDate.getText().toString().trim());
                Snackbar.make(layout,"Data added successfully.",Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#171746"))
                        .setTextColor(Color.parseColor("#FF7F5C"))
                        .setBackgroundTint(Color.parseColor("#171746"))
                        .show();
            }
            else
                Toast.makeText(contextNullSafe, "Please Select Deciding Date", Toast.LENGTH_SHORT).show();

        });
        load_data();
        return view;
    }

    private void load_data() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("case_nature").getValue(String.class) != null && snapshot.child("caseNo").getValue(String.class) != null && snapshot.child("dateOfFiling").getValue(String.class) !=null) {
                    respondents=(ArrayList<String>) snapshot.child("respondents").getValue();
                    appellants=(ArrayList<String>) snapshot.child("appellant").getValue();
                    Log.e("Awewae","HEY");
                }
                appellantAdapter = new AppellantAdapter(getContext(), appellants);
                appellantAdapter.notifyDataSetChanged();

                RespondentAdapter = new RespondentAdapter(getContext(), respondents, 0);
                RespondentAdapter.notifyDataSetChanged();

                try {
                    appella.setAdapter(appellantAdapter);
                    respo.setAdapter(RespondentAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    void back(){
        FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        if(fm.getBackStackEntryCount()>0) {
            fm.popBackStack();
        }
        ft.commit();
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