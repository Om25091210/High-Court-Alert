package in.aryomtech.cgalert.Writ;

import static android.app.Activity.RESULT_OK;
import static in.aryomtech.cgalert.Home.REQUEST_CODE_STORAGE_PERMISSION;

import android.app.DatePickerDialog;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import in.aryomtech.cgalert.DistrictData;
import in.aryomtech.cgalert.R;
import in.aryomtech.cgalert.fcm.Specific;


public class AppellantFragment extends Fragment {


    View view;
    String  judge_summary, synopsis, decision;
    TextView respo, appella, judgement_summary, summary,submit;
    ImageView back;
    ArrayList<String> respondents, appellants;
    Context contextNullSafe;
    String  appellant_list = "";
    public static final int PICK_FILE = 1;
    Uri selected_uri_pdf=Uri.parse("");
    String respondent_list = "";
    DatePickerDialog.OnDateSetListener mDateSetListener;
    StorageReference storageReference1;
    DatabaseReference reference,user_ref;
    Dialog dialog,dialog1;
    TextView decisionDate;
    String pushkey;
    ConstraintLayout layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_appellant, container, false);
        assert getArguments() != null;
        respondents = getArguments().getStringArrayList("respondents");
        appellants = getArguments().getStringArrayList("appellants");
        pushkey = getArguments().getString("pushkey");

        Log.e("check_list_A", String.valueOf(appellants));

        judge_summary = getArguments().getString("judge_summary");
        synopsis = getArguments().getString("synopsis");
        decision = getArguments().getString("decision");
        back = view.findViewById(R.id.imageView4);
        respo = view.findViewById(R.id.respondents_list);
        appella = view.findViewById(R.id.appellants_list);
        judgement_summary = view.findViewById(R.id.summary_edt);
        summary = view.findViewById(R.id.synop_edt);
        submit = view.findViewById(R.id.submit);
       // add = view.findViewById(R.id.linearLayout12);
        String pdfpath = "WRIT_PDF/";
        layout = view.findViewById(R.id.cons_lay);
        reference = FirebaseDatabase.getInstance().getReference().child("writ").child(pushkey);
        storageReference1 = FirebaseStorage.getInstance().getReference().child(pdfpath);
        user_ref = FirebaseDatabase.getInstance().getReference().child("users");
        decisionDate = view.findViewById(R.id.decision_date);
        /*add.setOnClickListener(view1 ->{
            select_file();
        });*/

        decisionDate.setText(decision);

        back.setOnClickListener(v->{
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().remove(AppellantFragment.this).commit();
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
            dialog.show();
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

        if (appellants != null) {
            for (int i = 0; i < appellants.size(); i++) {
                appellant_list = appellant_list +  (i+1) + "\u2022 " + appellants.get(i) + "\n";
            }
            appella.setText(appellant_list);
        }
        if (respondents != null) {
            for (int i = 0; i < respondents.size(); i++) {
                respondent_list = respondent_list + (i+1) + "\u2022 " + respondents.get(i) + "\n";
            }
            respo.setText(respondent_list);
        }
        judgement_summary.setText(judge_summary.toLowerCase(Locale.ROOT));
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
            String date2 = d + "," + m + "," + year;
            decisionDate.setText(date);

        };

        submit.setOnClickListener(v->{
            if (!decisionDate.getText().toString().equals("")) {
                dialog1 = new Dialog(getContextNullSafety());
                dialog1.setCancelable(false);
                dialog1.setContentView(R.layout.loading_dialog);
                dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                LottieAnimationView lottieAnimationView = dialog1.findViewById(R.id.animate);
                lottieAnimationView.setAnimation("done.json");
                dialog1.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog1.dismiss();
                    }
                }, 1000);
                reference.child("decisionDate").setValue(decisionDate.getText().toString().trim());
            }
            else
                Toast.makeText(contextNullSafe, "Please Select Deciding Date", Toast.LENGTH_SHORT).show();

        });

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
        String pdfstamp = pushkey;
        final StorageReference filepath = storageReference1.child(pdfstamp + "." + "pdf");
        filepath.putFile(selected_uri_pdf)
                .addOnSuccessListener(taskSnapshot1 ->
                        taskSnapshot1.getStorage().getDownloadUrl().addOnCompleteListener(
                                task1 -> {
                                    String pdf_link = Objects.requireNonNull(task1.getResult()).toString();
                                    reference.child(pushkey).child("uploaded_file").setValue(pdf_link);
                                    Calendar cal = Calendar.getInstance();
                                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault());
                                    reference.child(pushkey).child("uploaded_date").setValue(simpleDateFormat.format(cal.getTime()));
                                    dialog1.dismiss();
                                    Snackbar.make(layout,"Pdf Uploaded Successfully.",Snackbar.LENGTH_LONG)
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
                                specific.noti("CG Sangyan","Attachment added. Tap to see", Objects.requireNonNull(token),pushkey,"writ");
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