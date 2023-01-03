package in.aryomtech.cgalert.NoticeVictim.Fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import static in.aryomtech.cgalert.Home.REQUEST_CODE_STORAGE_PERMISSION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import in.aryomtech.cgalert.DB.TinyDB;
import in.aryomtech.cgalert.NoticeVictim.Adapter.NoticeAdapter;
import in.aryomtech.cgalert.NoticeVictim.Interface.onUploadInterface;
import in.aryomtech.cgalert.NoticeVictim.model.Notice_model;
import in.aryomtech.cgalert.R;
import in.aryomtech.cgalert.fcm.Specific;

public class UrgentNTV extends Fragment {

    View view;
    RecyclerView recyclerView;
    Context contextNullSafe;
    List<Notice_model> list;
    DatabaseReference reference,user_ref;
    Uri selected_uri_pdf=Uri.parse("");
    Dialog dialog,dialog1;
    public static final int PICK_FILE = 1;
    String card_key;
    NoticeAdapter noticeAdapter;
    StorageReference storageReference1;
    String stat_name;
    FirebaseAuth auth;
    FirebaseUser user;
    String sp_of;
    TinyDB tinyDB;
    ImageView cg_logo;
    TextView no_data;
    SwipeRefreshLayout mSwipeRefreshLayout;
    in.aryomtech.cgalert.NoticeVictim.Interface.onUploadInterface onUploadInterface;
    String ps_or_admin="";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notice_main, container, false);

        auth= FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        stat_name= getContextNullSafety().getSharedPreferences("station_name_K",MODE_PRIVATE)
                .getString("the_station_name2003","");

        String pdfpath = "PDF/";
        storageReference1 = FirebaseStorage.getInstance().getReference().child(pdfpath);

        cg_logo=view.findViewById(R.id.imageView3);
        no_data=view.findViewById(R.id.no_data);
        recyclerView = view.findViewById(R.id.rv);
        list = new ArrayList<>();
        tinyDB=new TinyDB(getContextNullSafety());
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        noticeAdapter=new NoticeAdapter(getContextNullSafety(),onUploadInterface,list);
        LinearLayoutManager mManager = new LinearLayoutManager(getContextNullSafety());
        recyclerView.setItemViewCacheSize(500);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(mManager);
        user_ref = FirebaseDatabase.getInstance().getReference().child("users");
        reference = FirebaseDatabase.getInstance().getReference().child("notice");
        cg_logo.setVisibility(View.VISIBLE);
        no_data.setVisibility(View.VISIBLE);
        ps_or_admin=getContextNullSafety().getSharedPreferences("useris?",MODE_PRIVATE)
                .getString("the_user_is?","");
        sp_of=getContextNullSafety().getSharedPreferences("Is_SP",MODE_PRIVATE)
                .getString("Yes_of","none");
        if(sp_of.equals("none")) {
            if(tinyDB.getInt("num_station")==0){
                getDataForIG();
            }
            else if(tinyDB.getInt("num_station")==10){
                getDataForSDOP();
            }
            else{
                if(ps_or_admin.equals("home")) {
                    get_data();
                }
                else if(ps_or_admin.equals("p_home")){
                    get_ps_data();
                }
            }
        }
        else
            getdata_for_sp();
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if(sp_of.equals("none")) {
                if(tinyDB.getInt("num_station")==0){
                    getDataForIG();
                }
                else if(tinyDB.getInt("num_station")==10){
                    getDataForSDOP();
                }
                else{
                    if(ps_or_admin.equals("home")) {
                        get_data();
                    }
                    else if(ps_or_admin.equals("p_home")){
                        get_ps_data();
                    }
                }
            }
            else
                getdata_for_sp();
        });
        onUploadInterface = key -> {
            card_key=key;
            //Ask for permission
            if (ContextCompat.checkSelfPermission(requireContext().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) requireContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
            }
            else{
                select_file();
            }
        };
        OnBackPressedCallback callback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fm = ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
                ft.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);
        return view;
    }
    private void select_file() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE && resultCode == RESULT_OK){
            if (data != null){
                selected_uri_pdf= data.getData();
                String uriString = selected_uri_pdf.toString();

                Cursor cursor = getContextNullSafety().getContentResolver()
                        .query(selected_uri_pdf, null, null, null, null);
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                cursor.moveToFirst();

                String name = cursor.getString(nameIndex);
                String str_txt=name+" ("+readableFileSize(cursor.getLong(sizeIndex))+")";
                show_file_upload(str_txt);
            }
        }
    }

    private void show_file_upload(String str_txt) {
        dialog = new Dialog(getContextNullSafety());
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.upload_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView file_name=dialog.findViewById(R.id.file_name);
        file_name.setText(str_txt);
        TextView cancel=dialog.findViewById(R.id.textView96);
        TextView yes=dialog.findViewById(R.id.textView95);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        cancel.setOnClickListener(vi-> dialog.dismiss());
        yes.setOnClickListener(vi-> {
            upload_to_database();
        });
    }

    private void upload_to_database() {
        dialog.dismiss();
        dialog1 = new Dialog(getContextNullSafety());
        dialog1.setCancelable(false);
        dialog1.setContentView(R.layout.loading_dialog);
        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LottieAnimationView lottieAnimationView=dialog1.findViewById(R.id.animate);
        lottieAnimationView.setAnimation("loader.json");
        dialog1.show();
        String pdfstamp = card_key;
        final StorageReference filepath = storageReference1.child(pdfstamp + "." + "pdf");
        filepath.putFile(selected_uri_pdf)
                .addOnSuccessListener(taskSnapshot1 ->
                        taskSnapshot1.getStorage().getDownloadUrl().addOnCompleteListener(
                                task1 -> {
                                    String pdf_link = Objects.requireNonNull(task1.getResult()).toString();
                                    reference.child(card_key).child("uploaded_file").setValue(pdf_link);
                                    Calendar cal = Calendar.getInstance();
                                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault());
                                    reference.child(card_key).child("uploaded_date").setValue(simpleDateFormat.format(cal.getTime()));
                                    dialog1.dismiss();
                                    Snackbar.make(recyclerView,"Pdf Uploaded Successfully.",Snackbar.LENGTH_LONG)
                                            .setActionTextColor(Color.parseColor("#171746"))
                                            .setTextColor(Color.parseColor("#FF7F5C"))
                                            .setBackgroundTint(Color.parseColor("#171746"))
                                            .show();
                                    Notify_admins();
                                }));
    }

    private void Notify_admins() {
        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds:snapshot.getChildren()){
                        if(Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("name").getValue(String.class)).equals("admin")){
                            for(DataSnapshot ds_token:snapshot.child(ds.getKey()).child("token").getChildren()){
                                String token=snapshot.child(ds.getKey()).child("token").child(Objects.requireNonNull(ds_token.getKey())).getValue(String.class);
                                Specific specific=new Specific();
                                specific.noti("CG Sangyan","New document uploaded. Tap to see", Objects.requireNonNull(token),card_key,"notice");
                                //this will enable eye feature from admin side but it will have no effect as user has uploaded the needy document.
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length>0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                select_file();
            }
            else{
                Toast.makeText(getContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }

    }
    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
    private void get_data() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    try {
                        Date dNow = new Date();
                        SimpleDateFormat ft =
                                new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

                        Date list1 = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(snapshot.child(ds.getKey()).child("hearingDate").getValue(String.class) + "");
                        Date current = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(ft.format(dNow));
                        Log.e("date", list1.before(current) + "");
                        if (list1.before(current)) {
                            list.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(Notice_model.class));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(list.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                Collections.reverse(list);
                NoticeAdapter adapter = new NoticeAdapter(getContextNullSafety(), onUploadInterface, list);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getdata_for_sp() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                    try {
                        Date dNow = new Date();
                        SimpleDateFormat ft =
                                new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

                        Date list1 = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(snapshot.child(ds.getKey()).child("hearingDate").getValue(String.class) + "");
                        Date current = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(ft.format(dNow));
                        Log.e("date", list1.before(current) + "");
                        if (list1.before(current)) {
                            if (Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("district").getValue(String.class)).trim().toUpperCase().equals(sp_of)) {
                                list.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(Notice_model.class));
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(list.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                NoticeAdapter adapter = new NoticeAdapter(getContextNullSafety(), onUploadInterface, list);
                adapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDataForSDOP() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                    try {
                        Date dNow = new Date();
                        SimpleDateFormat ft =
                                new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

                        Date list1 = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(snapshot.child(ds.getKey()).child("hearingDate").getValue(String.class) + "");
                        Date current = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(ft.format(dNow));
                        Log.e("date", list1.before(current) + "");
                        if (list1.before(current)) {
                            if (tinyDB.getListString("districts_list").contains(Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("district").getValue(String.class)).trim().toUpperCase())
                                    && tinyDB.getListString("stations_list").contains("PS "+Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("station").getValue(String.class)).trim().toUpperCase())) {
                                list.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(Notice_model.class));
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(list.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                NoticeAdapter adapter = new NoticeAdapter(getContextNullSafety(), onUploadInterface, list);
                adapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDataForIG() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                    try {
                        Date dNow = new Date();
                        SimpleDateFormat ft =
                                new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

                        Date list1 = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(snapshot.child(ds.getKey()).child("hearingDate").getValue(String.class) + "");
                        Date current = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(ft.format(dNow));
                        Log.e("date", list1.before(current) + "");
                        if (list1.before(current)) {
                            if (tinyDB.getListString("districts_list").contains(Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("district").getValue(String.class)).trim().toUpperCase())) {
                                list.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(Notice_model.class));
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(list.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                NoticeAdapter adapter = new NoticeAdapter(getContextNullSafety(), onUploadInterface, list);
                adapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void get_ps_data() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                    try {
                        Date dNow = new Date();
                        SimpleDateFormat ft =
                                new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

                        Date list1 = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(snapshot.child(ds.getKey()).child("hearingDate").getValue(String.class) + "");
                        Date current = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(ft.format(dNow));
                        Log.e("date", list1.before(current) + "");
                        if (list1.before(current)) {
                            if ((Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("station").getValue(String.class)).trim()).toUpperCase().equals(stat_name.substring(3).trim())) {
                                list.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(Notice_model.class));
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(list.size()!=0){
                    cg_logo.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                NoticeAdapter adapter = new NoticeAdapter(getContextNullSafety(), onUploadInterface, list);
                adapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
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