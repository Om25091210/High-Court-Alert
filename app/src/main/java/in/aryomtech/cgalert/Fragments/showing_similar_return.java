package in.aryomtech.cgalert.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import in.aryomtech.cgalert.Fragments.Adapter.Excel_Adapter;
import in.aryomtech.cgalert.Fragments.model.Excel_data;
import in.aryomtech.cgalert.R;
import in.aryomtech.cgalert.fcm.Specific;
import soup.neumorphism.NeumorphButton;


public class showing_similar_return extends Fragment {

    View view;
    private Context contextNullSafe;
    Bundle b;
    List<String> case_data_list=new ArrayList<>();
    List<String> case_data_list_filter=new ArrayList<>();
    RecyclerView mRecyclerView;
    Query query;
    List<Excel_data> excel_data;
    List<Excel_data> filter_excel_data=new ArrayList<>();
    ArrayList<String> added_list;
    CheckBox select_all;
    Excel_Adapter excel_adapter;

    List<String> not_sent_sms_list=new ArrayList<>();
    List<String> keys_selected=new ArrayList<>();
    List<String> keys_copy_selected_phone=new ArrayList<>();
    DatabaseReference reference;
    NeumorphButton join;
    boolean isadmin=false;
    EditText search;
    DatabaseReference user_ref;
    Dialog dialog,dialog1;
    TextView message, notification,phone_sms;
    DatabaseReference phone_numbers_ref;
    List<String> station_name_list=new ArrayList<>();
    List<String> noti_keys_copy_selected_phone=new ArrayList<>();
    List<String> district_name_list=new ArrayList<>();
    List<Excel_data> filter_excel_data_mylist=new ArrayList<>();
    List<String> phone_numbers=new ArrayList<>();
    private onClickInterface onClickInterface;
    private onAgainClickInterface onAgainClickInterface;
    String data_case_type,data_case_number,data_station_name,data_district_name,data_year;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_showing_similar_return, container, false);

        b=getArguments();
        if(b!=null){
            data_case_type=b.getString("data_case_type");
            data_case_number=b.getString("data_case_number");
            data_station_name=b.getString("data_station_name");
            data_district_name=b.getString("data_district_name");
            data_year=b.getString("data_case_year");
            excel_data= (List<Excel_data>) b.getSerializable("data_array_list");
        }
        //Hide the keyboard
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        added_list=new ArrayList<>();
        join=view.findViewById(R.id.join);
        select_all=view.findViewById(R.id.checkBox4);
        search=view.findViewById(R.id.search);
        //Initialize RecyclerView
        mRecyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager mManager = new LinearLayoutManager(getContextNullSafety());
        mRecyclerView.setItemViewCacheSize(500);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecyclerView.setLayoutManager(mManager);
        excel_adapter= new Excel_Adapter(getContextNullSafety(),excel_data,onClickInterface,onAgainClickInterface);

        isadmin=getContextNullSafety().getSharedPreferences("isAdmin_or_not",Context.MODE_PRIVATE)
                .getBoolean("authorizing_admin",false);
        if(isadmin) {
            join.setVisibility(View.VISIBLE);
            select_all.setVisibility(View.VISIBLE);
        }
        else {
            join.setVisibility(View.GONE);
            select_all.setVisibility(View.GONE);
        }

        //Initialize Database
        reference = FirebaseDatabase.getInstance().getReference().child("data");
        user_ref=FirebaseDatabase.getInstance().getReference().child("users");
        query = FirebaseDatabase.getInstance().getReference().child("data").orderByChild("type").equalTo("RM RETURN");
        phone_numbers_ref=FirebaseDatabase.getInstance().getReference().child("Phone numbers");
        onClickInterface = position -> {
            if(search.getText().toString().equals("")) {
                added_list.add(filter_excel_data.get(position).getPushkey());
                String txt = "Send " + "(" + added_list.size() + ")";
                select_all.setChecked(added_list.size() == filter_excel_data.size());
                join.setText(txt);
            }
            else{
                added_list.add(filter_excel_data_mylist.get(position).getPushkey());
                String txt = "Send " + "(" + added_list.size() + ")";
                select_all.setChecked(added_list.size() == filter_excel_data_mylist.size());
                join.setText(txt);
            }
        };

        onAgainClickInterface=removePosition -> {
            if(search.getText().toString().equals("")) {
                added_list.remove(filter_excel_data.get(removePosition).getPushkey());
                String txt = "Send " + "(" + added_list.size() + ")";
                select_all.setChecked(false);
                join.setText(txt);
            }
            else{
                added_list.remove(filter_excel_data_mylist.get(removePosition).getPushkey());
                String txt = "Send " + "(" + added_list.size() + ")";
                select_all.setChecked(false);
                join.setText(txt);
            }
        };
        select_all.setOnClickListener(v->{
            if (select_all.isChecked()){
                if(search.getText().toString().equals("")) {
                    for (int i = 0; i < filter_excel_data.size(); i++) {
                        added_list.add(filter_excel_data.get(i).getPushkey());
                    }
                    String txt = "Send " + "(" + added_list.size() + ")";
                    join.setText(txt);
                    excel_adapter.selectAll();
                }
                else{
                    for (int i = 0; i < filter_excel_data_mylist.size(); i++) {
                        added_list.add(filter_excel_data_mylist.get(i).getPushkey());
                    }
                    String txt = "Send " + "(" + added_list.size() + ")";
                    join.setText(txt);
                    excel_adapter.selectAll();
                }
            }
            else{
                added_list.clear();
                String txt="Send "+"("+added_list.size()+")";
                join.setText(txt);
                excel_adapter.unselectall();
            }
            excel_adapter.notifyDataSetChanged();
            Log.e("added_peeps",added_list+"");
        });
        //adapter
        //Initialize Database
        getdata();
        //Set listener to SwipeRefreshLayout for refresh action
        //mSwipeRefreshLayout.setOnRefreshListener(this::getdata);
        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                select_all.setChecked(false);
                added_list.clear();
                String txt="Send "+"("+added_list.size()+")";
                join.setText(txt);
                search(s+"");
            }
        });
        join.setOnClickListener(v-> {
            dialog = new Dialog(getContextNullSafety());
            dialog.setContentView(R.layout.message_dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
            message = dialog.findViewById(R.id.message);
            notification = dialog.findViewById(R.id.notification);
            phone_sms = dialog.findViewById(R.id.phone_sms);

            message.setOnClickListener(v1->{
                dialog.dismiss();
                gather_number("sms");
            });

            notification.setOnClickListener(v2->{
                dialog.dismiss();
                gather_number("notify");
            });

            phone_sms.setOnClickListener(v3->{
                dialog.dismiss();
                gather_number("phonesms");
            });

        });
        return view;
    }

    private void search(String str) {
        filter_excel_data_mylist.clear();
        for(Excel_data object:filter_excel_data) {
            if (object.getB().toLowerCase().contains(str.toLowerCase().trim())) {
                filter_excel_data_mylist.add(object);
            } else if (object.getC().toLowerCase().contains(str.toLowerCase().trim())) {
                filter_excel_data_mylist.add(object);
            } else if (object.getE().toLowerCase().contains(str.toLowerCase().trim())) {
                filter_excel_data_mylist.add(object);
            } else if (object.getH().toLowerCase().contains(str.toLowerCase().trim())) {
                filter_excel_data_mylist.add(object);
            }
            else if(object.getK().toLowerCase().contains(str.toLowerCase().trim())){
                filter_excel_data_mylist.add(object);
            }
            else if(object.getJ().toLowerCase().contains(str.toLowerCase().trim())){
                filter_excel_data_mylist.add(object);
            }
            else if(object.getDate().toLowerCase().contains(str.toLowerCase().trim())){
                filter_excel_data_mylist.add(object);
            }
        }
        excel_adapter=new Excel_Adapter(getContextNullSafety(),filter_excel_data_mylist,onClickInterface,onAgainClickInterface);
        excel_adapter.notifyDataSetChanged();
        if(mRecyclerView!=null)
            mRecyclerView.setAdapter(excel_adapter);
    }

    public void gather_number(String type) {
        dialog1 = new Dialog(getContextNullSafety());
        dialog1.setCancelable(true);
        dialog1.setContentView(R.layout.loading_dialog);
        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.show();
        Snackbar.make(mRecyclerView,"Gathering number of stations...",Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.parseColor("#ea4a1f"))
                .setTextColor(Color.parseColor("#000000"))
                .setBackgroundTint(Color.parseColor("#D9F5F8"))
                .show();
        station_name_list.clear();
        district_name_list.clear();
        case_data_list.clear();
        not_sent_sms_list.clear();
        keys_selected.clear();
        keys_copy_selected_phone.clear();
        case_data_list_filter.clear();
        phone_numbers.clear();
        Log.e("added_pushkey",added_list+"");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (added_list.size() != 0) {
                    for (int h = 0; h < added_list.size(); h++) {
                        String station_name = "PS " + snapshot.child(added_list.get(h)).child("B").getValue(String.class).trim();
                        String district_name = snapshot.child(added_list.get(h)).child("C").getValue(String.class).trim();

                        String K = snapshot.child(added_list.get(h)).child("K").getValue(String.class).trim();
                        String C = snapshot.child(added_list.get(h)).child("C").getValue(String.class).trim();
                        String D = snapshot.child(added_list.get(h)).child("D").getValue(String.class).trim();
                        String E = snapshot.child(added_list.get(h)).child("E").getValue(String.class).trim();
                        String G = snapshot.child(added_list.get(h)).child("G").getValue(String.class).trim();
                        String H = snapshot.child(added_list.get(h)).child("H").getValue(String.class).trim();
                        String I = snapshot.child(added_list.get(h)).child("I").getValue(String.class).trim();
                        String B = snapshot.child(added_list.get(h)).child("B").getValue(String.class).trim();
                        String type = snapshot.child(added_list.get(h)).child("type").getValue(String.class).trim();

                        case_data_list.add(type+"~"+K+"~"+C+"~"+D+"~"+E+"~"+G+"~"+H+"~"+I+"~"+B+"~");
                        district_name_list.add(district_name);
                        station_name_list.add(station_name);
                        keys_selected.add(added_list.get(h));

                    }
                    Log.e("district_name_list = ", district_name_list + "");
                    Log.e("station_name_list = ", station_name_list + "");
                    filter_by_district(type);
                }
                else{
                    Snackbar.make(mRecyclerView,"Zero selections...",Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.parseColor("#ea4a1f"))
                            .setTextColor(Color.parseColor("#000000"))
                            .setBackgroundTint(Color.parseColor("#D9F5F8"))
                            .show();
                    dialog1.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    private void filter_by_district(String type) {
        Snackbar.make(mRecyclerView,"Filtering data...",Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.parseColor("#ea4a1f"))
                .setTextColor(Color.parseColor("#000000"))
                .setBackgroundTint(Color.parseColor("#D9F5F8"))
                .show();
        phone_numbers_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(int i=0;i<district_name_list.size();i++){
                    for(DataSnapshot ds_key:snapshot.getChildren()){
                        if(district_name_list.get(i).toLowerCase().trim().equals(ds_key.getKey().toLowerCase().trim())){
                            if(snapshot.child(ds_key.getKey().trim()).child(station_name_list.get(i)).exists()){
                                phone_numbers.add(snapshot.child(ds_key.getKey()).child(station_name_list.get(i)).getValue(String.class));
                                case_data_list_filter.add(case_data_list.get(i));
                                keys_copy_selected_phone.add(keys_selected.get(i));
                            }
                            else{
                                not_sent_sms_list.add(keys_selected.get(i));
                            }
                        }
                    }
                }
                Log.e("phone_numbers = ",phone_numbers+"");
                if(phone_numbers.size()!=0) {
                    for (int pos = 0; pos < phone_numbers.size(); pos++) {
                        if (type.equals("sms")) {
                            //httpCall("https://2factor.in/API/R1/?module=TRANS_SMS&apikey=89988543-35b9-11ec-a13b-0200cd936042&to="+phone_numbers.get(pos)+"&from=OMSAIT&templatename=TESTING&var1="+"Himanshi"+"&var2="+"OM is Love");
                        } else if (type.equals("phonesms")) {
                            send_phone_sms(phone_numbers);
                            break;
                        } else {
                            send_notification(phone_numbers);
                            break;
                        }
                    }
                }
                else{
                    Toast.makeText(getContextNullSafety(), "No data found.", Toast.LENGTH_SHORT).show();
                    dialog1.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private void send_phone_sms(List<String> phone_numbers) {
        SmsManager sms = SmsManager.getDefault();
        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(phone_numbers.contains(Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("phone").getValue(String.class)).substring(3))){
                        int index=phone_numbers.indexOf(Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("phone").getValue(String.class)).substring(3));
                        reference.child(keys_copy_selected_phone.get(index)).child("reminded").setValue("once");
                        String body=extract_data(index);
                        ArrayList<String> list=sms.divideMessage(body);
                        sms.sendMultipartTextMessage(phone_numbers.get(index), null, list, null, null);
                    }
                }
                //TODO :Sent to next section.
                Log.e("number does not exist = ",not_sent_sms_list+"");
                getContextNullSafety().getSharedPreferences("saving_RM_showing_return_not_sms",Context.MODE_PRIVATE).edit()
                        .putString("RM_showing_return_list",not_sent_sms_list+"").apply();

                Snackbar.make(mRecyclerView,"Notified successfully...",Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#ea4a1f"))
                        .setTextColor(Color.parseColor("#000000"))
                        .setBackgroundTint(Color.parseColor("#D9F5F8"))
                        .show();
                dialog1.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void send_notification(List<String> phone_numbers) {
        noti_keys_copy_selected_phone.clear();
        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(phone_numbers.contains(Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("phone").getValue(String.class)).substring(3))){
                        int index=phone_numbers.indexOf(Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("phone").getValue(String.class)).substring(3));
                        String body=extract_data(index);
                        reference.child(keys_copy_selected_phone.get(index)).child("reminded").setValue("once");
                        keys_copy_selected_phone.remove(index);
                        if(snapshot.child(ds.getKey()).child("token").exists()) {
                            for (DataSnapshot dd : snapshot.child(ds.getKey()).child("token").getChildren()) {
                                String token = snapshot.child(ds.getKey()).child("token").child(Objects.requireNonNull(dd.getKey())).getValue(String.class);
                                if (token != null) {
                                    Specific specific = new Specific();
                                    specific.noti("High Court Alert", body, token);
                                }
                            }
                        }
                        else{
                            noti_keys_copy_selected_phone.add(keys_copy_selected_phone.get(index));
                        }
                    }
                }
                //TODO :Sent to next section.
                Log.e("number does not exist = ",not_sent_sms_list+"");
                Log.e("keys copy selected phone = ",noti_keys_copy_selected_phone+"");

                getContextNullSafety().getSharedPreferences("saving_RM_showing_return_not_noti",Context.MODE_PRIVATE).edit()
                        .putString("RM_showing_return_list",noti_keys_copy_selected_phone+"").apply();

                getContextNullSafety().getSharedPreferences("saving_RM_showing_return_not_sms",Context.MODE_PRIVATE).edit()
                        .putString("RM_showing_return_list",not_sent_sms_list+"").apply();

                Snackbar.make(mRecyclerView,"Notified successfully...",Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#ea4a1f"))
                        .setTextColor(Color.parseColor("#000000"))
                        .setBackgroundTint(Color.parseColor("#D9F5F8"))
                        .show();
                dialog1.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void httpCall(String url) {

        RequestQueue queue = Volley.newRequestQueue(getContextNullSafety());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // enjoy your response
                        Snackbar.make(mRecyclerView,"SMS sent successfully...",Snackbar.LENGTH_LONG)
                                .setActionTextColor(Color.parseColor("#ea4a1f"))
                                .setTextColor(Color.parseColor("#000000"))
                                .setBackgroundTint(Color.parseColor("#D9F5F8"))
                                .show();
                        dialog1.dismiss();
                        Log.e("Status of code = ","Success");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // enjoy your error status
                Log.e("Status of code = ","Wrong");
            }
        });

        queue.add(stringRequest);
    }
    private String extract_data(int index) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());
        Date date = new Date();
        String format=case_data_list_filter.get(index);
        String C=null,B=null,K=null,D=null,E=null,G=null,H=null,I=null,type=null;
        int temp=0;
        for(int i=0;i<format.length();i++){
            if(format.charAt(i)=='~'){
                if(type==null){
                    type=format.substring(0,i);
                    temp=i+1;
                }
                else if(K==null){
                    K=format.substring(temp,i);
                    temp=i+1;
                }
                else if (C==null){
                    C=format.substring(temp,i);
                    temp=i+1;
                }
                else if(D==null){
                    D=format.substring(temp,i);
                    temp=i+1;
                }
                else if(E==null){
                    E=format.substring(temp,i);
                    temp=i+1;
                }
                else if(G==null){
                    G=format.substring(temp,i);
                    temp=i+1;
                }
                else if(H==null){
                    H=format.substring(temp,i);
                    temp=i+1;
                }
                else if(I==null){
                    I=format.substring(temp,i);
                    temp=i+1;
                }
                else if(B==null){
                    B=format.substring(temp,i);

                }
            }
        }
        if(type.equals("RM RETURN")){
            String current=formatter.format(date);
            return "हाईकोर्ट अलर्ट:-डायरी वापसी"+"\nदिनाँक:- "+current+" \n"
                    +"\n"+C+"\n"+D+" No. "+E+"/"+G+"\n"
                    +"Crime No. "+H+"/"+I+"\n"
                    +"Police station: "+B+"\n"
                    +"1)उपरोक्त मूल केश डायरी  महाधिवक्ता कार्यालय द्वारा दी गयी मूल पावती लाने पर ही दी जाएगी।\n"
                    +"2) उपरोक्त मूल केश डायरी "+K+" से पांच दिवस के भीतर बेल शाखा, कार्यालय महाधिवक्ता,उच्च न्यायालय से वापिस ले जावें।";
        }
        else{
            String current=formatter.format(date);
            return "हाईकोर्ट अलर्ट:-डायरी माँग"+"\nदिनाँक:- "+current+" \n"
                    +"\n"+C+"\n"+D+" No. "+E+"/"+G+"\n"
                    +"Crime No. "+H+"/"+I+"\n"
                    +"Police station: "+B+"\n"
                    +"उपरोक्त मूल केश डायरी दिनाँक "+K+" तक बेल शाखा, कार्यालय महाधिवक्ता,उच्च न्यायालय छतीसगढ़ में  अनिवार्यतः जमा करें।";
        }
    }
    private void getdata() {
        search.setText("");
        select_all.setChecked(false);
        added_list.clear();
        String txt="Send "+"("+added_list.size()+")";
        join.setText(txt);
        filter_excel_data.clear();
        for(int i=0;i<excel_data.size();i++){
            if(excel_data.get(i).getH().trim().equals(data_case_number)){
                filter_excel_data.add(excel_data.get(i));
            }
        }
        excel_adapter=new Excel_Adapter(getContextNullSafety(),filter_excel_data,onClickInterface,onAgainClickInterface);
        excel_adapter.notifyDataSetChanged();
        if(mRecyclerView!=null)
            mRecyclerView.setAdapter(excel_adapter);
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
}