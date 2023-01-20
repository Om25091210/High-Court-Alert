package in.aryomtech.cgalert.Writ;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import static android.app.Activity.RESULT_OK;
import static in.aryomtech.cgalert.Home.REQUEST_CODE_STORAGE_PERMISSION;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.OpenableColumns;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import in.aryomtech.cgalert.Login;
import in.aryomtech.cgalert.R;
import in.aryomtech.cgalert.Writ.Adapter.AppellantAdapter;
import in.aryomtech.cgalert.Writ.Adapter.RespondentAdapter;
import in.aryomtech.cgalert.fcm.Specific;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;


public class WritForm extends Fragment {

    View view;
    RecyclerView recyclerView2, recyclerView3;
    ArrayList<String> task_list;
    ArrayList<String> appellant_list;
    RespondentAdapter RespondentAdapter;
    AppellantAdapter appellantAdapter;
    Dialog dialog,dialog_resp;
    String deleted_task;
    StorageReference storageReference1;
    DatabaseReference user_ref;
    String type;
    public static final int PICK_FILE = 1;
    Uri selected_uri_pdf=Uri.parse("");
    AutoCompleteTextView nature, district,district_ac, officer;
    LinearLayout add_task, add_appellant;
    ImageView back,voice_text,add_attachment;
    Dialog dialog1;
    String pushkey;
    TextView send;
    List<String> district_list,ps_list,token_list;
    ConstraintLayout lay;
    DatabaseReference reference_phone;
    int x=1;
    private Context contextNullSafe;
    EditText dispose_summary, summary, due_date, case_no , case_year;
    TextView date_reply, judgement_date, time_limit, summ, submit, date_of_filing, due;
    CheckBox legal1, legal2, allowed, disposed, dismissed;
    int check_;

    DatePickerDialog.OnDateSetListener mDateSetListener;
    DatabaseReference reference;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_cases_again_form, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        task_list = new ArrayList<>();
        appellant_list = new ArrayList<>();
        lay = view.findViewById(R.id.lay);
        add_attachment = view.findViewById(R.id.add_attachment);
        nature = view.findViewById(R.id.nature);
        voice_text = view.findViewById(R.id.voice_text);
        district = view.findViewById(R.id.district);
        user_ref = FirebaseDatabase.getInstance().getReference().child("users");
        summary = view.findViewById(R.id.summary_edt);
        submit = view.findViewById(R.id.submit_txt);
        date_of_filing = view.findViewById(R.id.date_of_filing);
        district_list =new ArrayList<>();
        ps_list =new ArrayList<>();
        token_list =new ArrayList<>();
        reference_phone = FirebaseDatabase.getInstance().getReference().child("Phone numbers");
        due = view.findViewById(R.id.due);
        due_date = view.findViewById(R.id.due_date);
        add_task = view.findViewById(R.id.add_task);
        case_no = view.findViewById(R.id.case_no_edt);
        case_year = view.findViewById(R.id.case_year_edt);
        add_appellant = view.findViewById(R.id.add_appellant);
        recyclerView2 = view.findViewById(R.id.recycler_view);
        recyclerView3 = view.findViewById(R.id.recycler_view2);
        recyclerView2.setVisibility(View.GONE);
        recyclerView3.setVisibility(View.GONE);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        recyclerView2.setItemViewCacheSize(500);
        recyclerView2.setDrawingCacheEnabled(true);
        recyclerView2.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView2.setLayoutManager(layoutManager2);

        send = view.findViewById(R.id.send);
        allowed = view.findViewById(R.id.allowed);
        disposed = view.findViewById(R.id.disposed);
        back = view.findViewById(R.id.imageView4);
        dismissed = view.findViewById(R.id.dismissed);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(getContext());
        recyclerView3.setItemViewCacheSize(500);
        recyclerView3.setDrawingCacheEnabled(true);
        recyclerView3.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView3.setLayoutManager(layoutManager3);
        summ = view.findViewById(R.id.disposed_summary);
        dispose_summary = view.findViewById(R.id.disposed_summary_edt);
        date_reply = view.findViewById(R.id.date_reply);
        String pdfpath = "WRIT_PDF/";
        legal1 = view.findViewById(R.id.legal1);
        legal2 = view.findViewById(R.id.legal2);
        judgement_date = view.findViewById(R.id.judgement_date_edt);
        time_limit = view.findViewById(R.id.time_limit_edt);
        reference = FirebaseDatabase.getInstance().getReference().child("writ");
        pushkey = reference.push().getKey();
        appellantAdapter = new AppellantAdapter(getContext(), appellant_list);
        RespondentAdapter = new RespondentAdapter(getContext(), task_list,0);
        String[] array = {"WPHC","WPCR" , "WPS","WA", "WPPIL","WPC","CONT","CRR","CRA", "CRMP", "ACQT", "MAC"};
        //Creating the instance of ArrayAdapter containing list of language names
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>
                (getContextNullSafety(), android.R.layout.select_dialog_item, array);
        //Getting the instance of AutoCompleteTextView
        nature.setThreshold(1);//will start working from first character
        nature.setAdapter(adapter1);
        storageReference1 = FirebaseStorage.getInstance().getReference().child(pdfpath);
        ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        ArrayList<String> result_voice = data
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        String voice_text_Str=summary.getText().toString().trim()+" "+result_voice.get(0)+"";
                        summary.setText(voice_text_Str);
                    }
                }
        );
        voice_text.setOnClickListener(v->{
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");

            try {
                startActivityForResult.launch(intent);
            } catch (ActivityNotFoundException a) {
                Toast.makeText(getContext(), "Sorry your device not supported", Toast.LENGTH_SHORT).show();
            }
        });

        send.setOnClickListener(v->{
            send_data();
        });

        get_districts_phone();

        add_task.setOnClickListener(v -> addTask());
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT
                | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition();

                switch (direction) {
                    case ItemTouchHelper.LEFT:
                    case ItemTouchHelper.RIGHT:
                        deleted_task = task_list.get(position);
                        task_list.remove(position);
                        RespondentAdapter.notifyItemRemoved(position);
                        Snackbar.make(recyclerView2, deleted_task + " deleted.", Snackbar.LENGTH_LONG)
                                .setAction("Undo", v -> {
                                    task_list.add(position, deleted_task);
                                    RespondentAdapter.notifyItemInserted(position);
                                })
                                .setActionTextColor(Color.parseColor("#ea4a1f"))
                                .setTextColor(Color.parseColor("#000000"))
                                .setBackgroundTint(Color.parseColor("#D9F5F8"))
                                .show();
                        break;
                }
            }
        };


        add_appellant.setOnClickListener(v -> addAppellant());
        ItemTouchHelper.SimpleCallback simpleCallback1 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT
                | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition();

                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        deleted_task = appellant_list.get(position);
                        appellant_list.remove(position);
                        appellantAdapter.notifyItemRemoved(position);
                        Snackbar.make(recyclerView3, deleted_task + " deleted.", Snackbar.LENGTH_LONG)
                                .setAction("Undo", v -> {
                                    appellant_list.add(position, deleted_task);
                                    appellantAdapter.notifyItemInserted(position);
                                })
                                .setActionTextColor(Color.parseColor("#ea4a1f"))
                                .setTextColor(Color.parseColor("#000000"))
                                .setBackgroundTint(Color.parseColor("#D9F5F8"))
                                .show();
                        break;
                    case ItemTouchHelper.RIGHT:
                        Dialog dialog = new Dialog(getContext());
                        dialog.setCancelable(true);
                        dialog.setContentView(R.layout.add_appellant);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        TextView cancel = dialog.findViewById(R.id.textCancel);
                        TextView add = dialog.findViewById(R.id.textAdd);
                        EditText task_content = dialog.findViewById(R.id.inputURL);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.show();

                        task_content.setText(appellant_list.get(position));
                        cancel.setOnClickListener(v -> {
                            appellant_list.remove(position);
                            appellant_list.add(position, task_content.getText().toString().trim());
                            appellantAdapter.notifyItemChanged(position);
                            dialog.dismiss();
                        });
                        add.setOnClickListener(v -> {
                            if (!task_content.getText().toString().trim().equals("")) {
                                appellant_list.remove(position);
                                appellant_list.add(position, task_content.getText().toString().trim());
                                appellantAdapter.notifyItemChanged(position);
                                dialog.dismiss();
                            } else
                                MotionToast.Companion.darkColorToast(getActivity(),
                                        "Info",
                                        "task cannot be empty.",
                                        MotionToastStyle.WARNING,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.SHORT_DURATION,
                                        ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
                        });
                        break;
                }
            }
        };

        add_attachment.setOnClickListener(v->{
            select_file();
        });

        legal1.setOnClickListener(v -> {
            legal2.setChecked(false);
        });

        allowed.setOnClickListener(v -> {
            dismissed.setChecked(false);
            disposed.setChecked(false);
            summ.setVisibility(View.VISIBLE);
            dispose_summary.setVisibility(View.VISIBLE);
            due.setVisibility(View.VISIBLE);
            due_date.setVisibility(View.VISIBLE);
            type = "Allowed";
            x=0;
        });

        disposed.setOnClickListener(v -> {
            dismissed.setChecked(false);
            allowed.setChecked(false);
            summ.setVisibility(View.VISIBLE);
            dispose_summary.setVisibility(View.VISIBLE);
            due.setVisibility(View.VISIBLE);
            due_date.setVisibility(View.VISIBLE);
            type = "Disposed";
            x=0;
        });

        dismissed.setOnClickListener(v -> {
            allowed.setChecked(false);
            disposed.setChecked(false);
            summ.setVisibility(View.GONE);
            dispose_summary.setVisibility(View.GONE);
            summ.setText("");
            dispose_summary.setText("");
            due.setVisibility(View.GONE);
            due_date.setVisibility(View.GONE);
            type = "Dismissed";
        });


        submit.setOnClickListener(v-> {
            if(!date_of_filing.getText().toString().trim().equals("")){
                if(!nature.getText().toString().trim().equals("")){
                    if(!district.getText().toString().trim().equals("")) {
                        datasend();
                    }
                    else{
                        district.setError("Empty");
                        Snackbar.make(lay,"Please Add District",Snackbar.LENGTH_LONG)
                                .setActionTextColor(Color.parseColor("#171746"))
                                .setTextColor(Color.parseColor("#FF7F5C"))
                                .setBackgroundTint(Color.parseColor("#171746"))
                                .show();
                    }
                }
                else{
                    nature.setError("Empty");
                    Snackbar.make(lay,"Please Add Nature of the Case.",Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.parseColor("#171746"))
                            .setTextColor(Color.parseColor("#FF7F5C"))
                            .setBackgroundTint(Color.parseColor("#171746"))
                            .show();
                }
            }
            else{
                date_of_filing.setError("Empty");
                Snackbar.make(lay,"Please Add Date of Filing",Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#171746"))
                        .setTextColor(Color.parseColor("#FF7F5C"))
                        .setBackgroundTint(Color.parseColor("#171746"))
                        .show();
            }
        });

        legal2.setOnClickListener(v -> {
            legal1.setChecked(false);
        });

        date_reply.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(
                    getActivity(),
                    mDateSetListener,
                    year, month, day);
            check_ = 0;
            dialog.show();
        });

        time_limit.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(
                    getActivity(),
                    mDateSetListener,
                    year, month, day);
            check_ = 2;
            dialog.show();
        });

        judgement_date.setOnClickListener(v -> {
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

        date_of_filing.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(
                    getActivity(),
                    mDateSetListener,
                    year, month, day);
            check_ = 3;
            dialog.show();
        });

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
            if (check_ == 0) {
                date_reply.setText(date);
            }
            if (check_ == 1) {
                judgement_date.setText(date);
            }
            if (check_ == 2) {
                time_limit.setText(date);
            }
            if (check_ == 3) {
                date_of_filing.setText(date);
            }
            /*if (check_ == 4){
                due_date.setText(date);
            }*/
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(simpleCallback1);
        itemTouchHelper.attachToRecyclerView(recyclerView2);
        itemTouchHelper2.attachToRecyclerView(recyclerView3);

        back.setOnClickListener(v -> back());
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
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
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return view;
    }

    private void back() {
        FragmentManager fm = ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        }
        ft.commit();
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextNullSafe = context;
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
            dialog.dismiss();
        });
    }

    private void upload_to_database() {
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
                                    NOtify_officers();
                                    dialog1.dismiss();
                                    Snackbar.make(lay,"Data Uploaded Successfully.",Snackbar.LENGTH_LONG)
                                            .setActionTextColor(Color.parseColor("#171746"))
                                            .setTextColor(Color.parseColor("#FF7F5C"))
                                            .setBackgroundTint(Color.parseColor("#171746"))
                                            .show();
                                }));
    }

    private void NOtify_officers() {
        Specific specific = new Specific();
        for (int i = 0; i < token_list.size(); i++) {
            specific.noti("CG Sangyan", "Writ Attachment added. Tap to see", Objects.requireNonNull(token_list.get(i)), pushkey, "writ");
        }
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

    @SuppressLint("NotifyDataSetChanged")
    private void addTask() {

        dialog_resp = new Dialog(getContext());
        dialog_resp.setCancelable(true);
        dialog_resp.setContentView(R.layout.layout_respondents_select);
        dialog_resp.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView cancel = dialog_resp.findViewById(R.id.textView96);
        TextView add = dialog_resp.findViewById(R.id.textView95);
        TextView msg = dialog_resp.findViewById(R.id.msg);
        district_ac = dialog_resp.findViewById(R.id.district);
        officer = dialog_resp.findViewById(R.id.officer);
        dialog_resp.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog_resp.show();

        district_ac.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                get_police_station(district_ac.getText().toString().trim());
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (getContextNullSafety(), android.R.layout.select_dialog_item, district_list);
        //Getting the instance of AutoCompleteTextView
        district_ac.setThreshold(1);//will start working from first character
        district_ac.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        district_ac.setTextColor(Color.RED);

        cancel.setOnClickListener(v -> dialog_resp.dismiss());
        add.setOnClickListener(v -> {
            if (!district_ac.getText().toString().trim().equals("") && !officer.getText().toString().trim().equals("")) {
                msg.setVisibility(View.VISIBLE);
                fetch_token_tick(officer.getText().toString().trim());
            } else
                MotionToast.Companion.darkColorToast(requireActivity(),
                        "Info",
                        "task cannot be empty.",
                        MotionToastStyle.WARNING,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(requireActivity(), R.font.helvetica_regular));
        });
    }

    private void fetch_token_tick(String name) {
        token_list.clear();
        final int[] c = {0};
        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (Objects.equals(snapshot.child(Objects.requireNonNull(ds.getKey())).child("name").getValue(String.class), name)) {
                        for (DataSnapshot ds_token : snapshot.child(ds.getKey()).child("token").getChildren()) {
                            String token = snapshot.child(ds.getKey()).child("token").child(Objects.requireNonNull(ds_token.getKey())).getValue(String.class);
                            if(!token_list.contains(token))
                                token_list.add(token);//saving tokens to notify later.
                            c[0] = 1;
                        }
                    }
                }
                if(!task_list.contains(district_ac.getText().toString().trim() + " - " + officer.getText().toString().trim())) {
                    recyclerView2.setVisibility(View.VISIBLE);
                    task_list.add(district_ac.getText().toString().trim() + " - " + officer.getText().toString().trim());
                    Collections.reverse(task_list);
                    if (c[0] == 1) {
                        RespondentAdapter = new RespondentAdapter(getContext(), task_list, 1);
                    } else {
                        RespondentAdapter = new RespondentAdapter(getContext(), task_list, 0);
                    }
                    RespondentAdapter.notifyDataSetChanged();
                    try {
                        recyclerView2.setAdapter(RespondentAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog_resp.dismiss();
                }
                else{
                    MotionToast.Companion.darkColorToast((Activity) getContextNullSafety(),
                            "Duplicates ☹️",
                            "Already added.",
                            MotionToastStyle.INFO,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(getContextNullSafety(), R.font.helvetica_regular));
                    dialog_resp.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void get_police_station(String district) {
        ps_list.clear();
        reference_phone.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.child(district).getChildren()) {
                    ps_list.add(dataSnapshot.getKey());
                    //Creating the instance of ArrayAdapter containing list of language names
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>
                            (getContextNullSafety(), android.R.layout.select_dialog_item, ps_list);
                    //Getting the instance of AutoCompleteTextView
                    officer.setThreshold(1);//will start working from first character
                    officer.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
                    officer.setTextColor(Color.RED);
                }
                Log.e("PS = ",ps_list+"");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private void send_data() {
        if(!date_of_filing.getText().toString().trim().equals("")
          && !nature.getText().toString().trim().equals("")
          && !case_no.getText().toString().trim().equals("")
          && !case_year.getText().toString().trim().equals("")
          && !district.getText().toString().trim().equals("")
          && appellant_list.size()!=0
          && task_list.size()!=0
          && !summary.getText().toString().trim().equals("")) {

            reference.child(pushkey).child("dateOfFiling").setValue(date_of_filing.getText().toString().toUpperCase());
            reference.child(pushkey).child("case_nature").setValue(nature.getText().toString().toUpperCase());
            reference.child(pushkey).child("caseNo").setValue(case_no.getText().toString().toUpperCase());
            reference.child(pushkey).child("caseYear").setValue(case_year.getText().toString().toUpperCase());
            reference.child(pushkey).child("district").setValue(district.getText().toString().toUpperCase());
            reference.child(pushkey).child("appellant").setValue(appellant_list);
            reference.child(pushkey).child("respondents").setValue(task_list);
            reference.child(pushkey).child("pushkey").setValue(pushkey);
            reference.child(pushkey).child("summary").setValue(summary.getText().toString().toUpperCase());
            upload_to_database();
        }
        else{
            Snackbar.make(lay,"Missing Details or PDF.",Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.parseColor("#171746"))
                    .setTextColor(Color.parseColor("#FF7F5C"))
                    .setBackgroundTint(Color.parseColor("#171746"))
                    .show();
        }
    }

    private void datasend() {
        if(!date_of_filing.getText().toString().trim().equals("")
                && !nature.getText().toString().trim().equals("")
                && !case_no.getText().toString().trim().equals("")
                && !case_year.getText().toString().trim().equals("")
                && !district.getText().toString().trim().equals("")
                && appellant_list.size()!=0
                && task_list.size()!=0
                && !summary.getText().toString().trim().equals("")
                && !judgement_date.getText().toString().trim().equals("")
                && type!=null) {

            reference.child(pushkey).child("district").setValue(district.getText().toString().toUpperCase());
            reference.child(pushkey).child("dateOfFiling").setValue(date_of_filing.getText().toString().toUpperCase());
            reference.child(pushkey).child("case_nature").setValue(nature.getText().toString().toUpperCase());
            reference.child(pushkey).child("summary").setValue(summary.getText().toString().toUpperCase());
            reference.child(pushkey).child("judgementDate").setValue(judgement_date.getText().toString().toUpperCase());
            reference.child(pushkey).child("Judgement").setValue(type.toUpperCase());
            reference.child(pushkey).child("dSummary").setValue(dispose_summary.getText().toString().toUpperCase());
            reference.child(pushkey).child("dueDate").setValue(due_date.getText().toString().toUpperCase());
            reference.child(pushkey).child("caseNo").setValue(case_no.getText().toString().toUpperCase());
            reference.child(pushkey).child("caseYear").setValue(case_year.getText().toString().toUpperCase());
            reference.child(pushkey).child("appellant").setValue(appellant_list);
            reference.child(pushkey).child("respondents").setValue(task_list);
            reference.child(pushkey).child("decisionDate").setValue("");
            reference.child(pushkey).child("pushkey").setValue(pushkey);
            upload_to_database();
        }
        else{
            Snackbar.make(lay,"Missing Details or PDF.",Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.parseColor("#171746"))
                    .setTextColor(Color.parseColor("#FF7F5C"))
                    .setBackgroundTint(Color.parseColor("#171746"))
                    .show();
        }
    }

    private void addAppellant() {
        Dialog dialog = new Dialog(getContext());
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.add_appellant);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView cancel = dialog.findViewById(R.id.textCancel);
        TextView add = dialog.findViewById(R.id.textAdd);
        EditText task_content = dialog.findViewById(R.id.inputURL);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        cancel.setOnClickListener(v -> dialog.dismiss());
        add.setOnClickListener(v -> {
            if (!task_content.getText().toString().trim().equals("")) {
                recyclerView3.setVisibility(View.VISIBLE);
                appellant_list.add(task_content.getText().toString().trim());
                Collections.reverse(appellant_list);
                appellantAdapter = new AppellantAdapter(getContext(), appellant_list);
                appellantAdapter.notifyDataSetChanged();
                try {
                    recyclerView3.setAdapter(appellantAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            } else
                MotionToast.Companion.darkColorToast(getActivity(),
                        "Info",
                        "task cannot be empty.",
                        MotionToastStyle.WARNING,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
        });

    }
    /**
     * CALL THIS IF YOU NEED CONTEXT
     */
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