package in.aryomtech.cgalert.Fragments;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.DIRECTORY_PICTURES;

import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import in.aryomtech.cgalert.R;

public class form extends Fragment {

    View view;
    TextView submit_txt;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    TextView rm,before,diary;
    int check_;
    EditText case_no_edt,name_edt,case_year_edt,crime_no_edt,crime_year_edt;
    CheckBox checkBox_RM_call,checkBox_RM_return;
    List<String> district,ps_list;
    String rm_Date,fd_dot;
    ConstraintLayout lay;
    AutoCompleteTextView ac_district,policeStation,ac_caseType;
    DatabaseReference reference,reference_phone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.activity_form, container, false);
        district =new ArrayList<>();
        ps_list=new ArrayList<>();

        ac_district = view.findViewById(R.id.ac_district);
        policeStation = view.findViewById(R.id.policeStation);
        ac_caseType = view.findViewById(R.id.ac_case_type);
        case_no_edt=view.findViewById(R.id.case_no_edt);
        name_edt=view.findViewById(R.id.name_edt);
        case_year_edt=view.findViewById(R.id.case_year_edt);
        crime_no_edt=view.findViewById(R.id.crime_no_edt);
        crime_year_edt=view.findViewById(R.id.crime_year_edt);
        checkBox_RM_call=view.findViewById(R.id.checkBox_RM_call);
        checkBox_RM_return=view.findViewById(R.id.checkBox_RM_return);
        submit_txt=view.findViewById(R.id.submit_txt);
        lay=view.findViewById(R.id.lay);

        String[] caseType = {"MCRC", "MCRCA"};
        //Creating the instance of ArrayAdapter containing list of language names
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>
                (getContext(), android.R.layout.select_dialog_item, caseType);
        //Getting the instance of AutoCompleteTextView
        ac_caseType.setThreshold(1);//will start working from first character
        ac_caseType.setAdapter(adapter1);//setting the adapter data into the AutoCompleteTextView
        ac_caseType.setTextColor(Color.RED);

        reference = FirebaseDatabase.getInstance().getReference().child("data");
        reference_phone = FirebaseDatabase.getInstance().getReference().child("Phone numbers");
        get_districts_phone();
        rm=view.findViewById(R.id.rm_date);
        diary=view.findViewById(R.id.diary);
        before=view.findViewById(R.id.before);
        rm.setOnClickListener(v->{

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
        diary.setOnClickListener(v->{

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
        before.setOnClickListener(v->{

            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    getActivity(),
                    mDateSetListener,
                    year,month,day);
            check_=2;
            dialog.show();
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
            rm_Date=year+m+d;
            fd_dot=year+"-"+m+"-"+d;
            if(check_==0)
                rm.setText(date);
            else if(check_==1)
                diary.setText(date);
            else if(check_==2)
                before.setText(date);
        };
        checkBox_RM_call.setOnClickListener(v->{
            checkBox_RM_return.setChecked(false);
        });
        checkBox_RM_return.setOnClickListener(v->{
            checkBox_RM_call.setChecked(false);
        });
        submit_txt.setOnClickListener(v->{
            if(!ac_district.getText().toString().trim().equals("")){
                if(!policeStation.getText().toString().trim().equals("")){
                    if(!crime_no_edt.getText().toString().trim().equals("")){
                        if(!crime_year_edt.getText().toString().trim().equals("")){
                            if(!ac_caseType.getText().toString().trim().equals("")){
                                if(!case_no_edt.getText().toString().trim().equals("")){
                                    if(!case_year_edt.getText().toString().trim().equals("")){
                                        if(!name_edt.getText().toString().trim().equals("")){
                                            if(!rm.getText().toString().equals("")){
                                                if(!before.getText().toString().equals("")){
                                                    if(checkBox_RM_call.isChecked()){
                                                        push_to_database_and_excel("RM CALL");
                                                    }
                                                    else if(checkBox_RM_return.isChecked()){
                                                        push_to_database_and_excel("RM RETURN");
                                                    }
                                                    else{
                                                        Snackbar.make(lay,"Please Select Sheet Name.",Snackbar.LENGTH_LONG)
                                                                .setActionTextColor(Color.parseColor("#171746"))
                                                                .setTextColor(Color.parseColor("#FF7F5C"))
                                                                .setBackgroundTint(Color.parseColor("#171746"))
                                                                .show();
                                                    }
                                                }
                                                else{
                                                    before.setError("Empty");
                                                    Snackbar.make(lay,"Please Add Before Date.",Snackbar.LENGTH_LONG)
                                                            .setActionTextColor(Color.parseColor("#171746"))
                                                            .setTextColor(Color.parseColor("#FF7F5C"))
                                                            .setBackgroundTint(Color.parseColor("#171746"))
                                                            .show();
                                                }
                                            }
                                            else{
                                                rm.setError("Empty");
                                                Snackbar.make(lay,"Please Add RM Date.",Snackbar.LENGTH_LONG)
                                                        .setActionTextColor(Color.parseColor("#171746"))
                                                        .setTextColor(Color.parseColor("#FF7F5C"))
                                                        .setBackgroundTint(Color.parseColor("#171746"))
                                                        .show();
                                            }
                                        }
                                        else{
                                            name_edt.setError("Empty");
                                            Snackbar.make(lay,"Please Add Criminal Name.",Snackbar.LENGTH_LONG)
                                                    .setActionTextColor(Color.parseColor("#171746"))
                                                    .setTextColor(Color.parseColor("#FF7F5C"))
                                                    .setBackgroundTint(Color.parseColor("#171746"))
                                                    .show();
                                        }
                                    }
                                    else{
                                        case_year_edt.setError("Empty");
                                        Snackbar.make(lay,"Please Add Case Year.",Snackbar.LENGTH_LONG)
                                                .setActionTextColor(Color.parseColor("#171746"))
                                                .setTextColor(Color.parseColor("#FF7F5C"))
                                                .setBackgroundTint(Color.parseColor("#171746"))
                                                .show();
                                    }
                                }
                                else{
                                    case_no_edt.setError("Empty");
                                    Snackbar.make(lay,"Please Add Case No.",Snackbar.LENGTH_LONG)
                                            .setActionTextColor(Color.parseColor("#171746"))
                                            .setTextColor(Color.parseColor("#FF7F5C"))
                                            .setBackgroundTint(Color.parseColor("#171746"))
                                            .show();
                                }
                            }
                            else{
                                ac_caseType.setError("Empty");
                                Snackbar.make(lay,"Please Add Case Type.",Snackbar.LENGTH_LONG)
                                        .setActionTextColor(Color.parseColor("#171746"))
                                        .setTextColor(Color.parseColor("#FF7F5C"))
                                        .setBackgroundTint(Color.parseColor("#171746"))
                                        .show();
                            }
                        }
                        else{
                            crime_year_edt.setError("Empty");
                            Snackbar.make(lay,"Please Add Crime Year.",Snackbar.LENGTH_LONG)
                                    .setActionTextColor(Color.parseColor("#171746"))
                                    .setTextColor(Color.parseColor("#FF7F5C"))
                                    .setBackgroundTint(Color.parseColor("#171746"))
                                    .show();
                        }
                    }
                    else{
                        crime_no_edt.setError("Empty");
                        Snackbar.make(lay,"Please Add Crime no.",Snackbar.LENGTH_LONG)
                                .setActionTextColor(Color.parseColor("#171746"))
                                .setTextColor(Color.parseColor("#FF7F5C"))
                                .setBackgroundTint(Color.parseColor("#171746"))
                                .show();
                    }
                }
                else{
                    policeStation.setError("Empty");
                    Snackbar.make(lay,"Please Add Police Station.",Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.parseColor("#171746"))
                            .setTextColor(Color.parseColor("#FF7F5C"))
                            .setBackgroundTint(Color.parseColor("#171746"))
                            .show();
                }
            }
            else{
                ac_district.setError("Empty");
                Snackbar.make(lay,"Please Add District.",Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#171746"))
                        .setTextColor(Color.parseColor("#FF7F5C"))
                        .setBackgroundTint(Color.parseColor("#171746"))
                        .show();
            }
        });
        getFileUrl();
        return view;
    }

    private void push_to_database_and_excel(String sheet) {
        String pushkey=rm_Date
                       +policeStation.getText().toString().trim()
                       +ac_district.getText().toString().trim()
                       +ac_caseType.getText().toString().trim()
                       +case_no_edt.getText().toString().trim()
                       +case_year_edt.getText().toString().trim()
                       +crime_no_edt.getText().toString().trim()
                       +crime_year_edt.getText().toString().trim();
        Map<String,String> data_packet=new HashMap<>();
        data_packet.put("A","");
        data_packet.put("B",policeStation.getText().toString().trim());
        data_packet.put("C",ac_district.getText().toString().trim());
        data_packet.put("D",ac_caseType.getText().toString().trim());
        data_packet.put("E",case_no_edt.getText().toString().trim());
        data_packet.put("F",name_edt.getText().toString().trim());
        data_packet.put("G",case_year_edt.getText().toString().trim());
        data_packet.put("H",crime_no_edt.getText().toString().trim());
        data_packet.put("I",crime_year_edt.getText().toString().trim());
        if(diary.getText().toString().trim().equals(""))
            data_packet.put("J","None");
        else
            data_packet.put("J",diary.getText().toString().trim()+"");
        data_packet.put("K",rm.getText().toString().trim());
        data_packet.put("L",before.getText().toString().trim());
        data_packet.put("date",fd_dot);
        data_packet.put("pushkey",pushkey);
        data_packet.put("type",sheet);

        reference.child(pushkey).setValue(data_packet);

    }
    private void getFileUrl(){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("downloaded.xlsx");
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            String url = uri.toString();
            downloadFile(getContext(), "downloaded", ".xlsx", DIRECTORY_PICTURES, url);
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        });
    }


    private void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {

        DownloadManager downloadmanager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadmanager.enqueue(request);
    }

    private void get_districts_phone() {
        reference_phone.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    district.add(ds.getKey());
                    //Creating the instance of ArrayAdapter containing list of language names
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>
                            (getContext(), android.R.layout.select_dialog_item, district);
                    //Getting the instance of AutoCompleteTextView
                    ac_district.setThreshold(1);//will start working from first character
                    ac_district.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
                    ac_district.setTextColor(Color.RED);
                }
                get_police_station(district);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void get_police_station(List<String> district) {
        reference_phone.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(int i=0;i<district.size();i++){
                    for(DataSnapshot dataSnapshot:snapshot.child(district.get(i)).getChildren()){
                        if(dataSnapshot.getKey().substring(0,2).equals("PS")){
                            ps_list.add(dataSnapshot.getKey().substring(3));
                            //Creating the instance of ArrayAdapter containing list of language names
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                                    (getContext(), android.R.layout.select_dialog_item, ps_list);
                            //Getting the instance of AutoCompleteTextView
                            policeStation.setThreshold(1);//will start working from first character
                            policeStation.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
                            policeStation.setTextColor(Color.RED);
                        }
                    }
                }
                Log.e("PS = ",ps_list+"");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}