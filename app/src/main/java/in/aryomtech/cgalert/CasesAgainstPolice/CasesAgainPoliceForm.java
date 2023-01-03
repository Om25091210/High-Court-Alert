package in.aryomtech.cgalert.CasesAgainstPolice;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
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

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import in.aryomtech.cgalert.R;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;


public class CasesAgainPoliceForm extends Fragment {

    View view;
    RecyclerView recyclerView2, recyclerView3;
    ArrayList<String> task_list;
    ArrayList<String> appellant_list;
    taskAdapter taskAdapter;
    AppellantAdapter appellantAdapter;
    String deleted_task;
    String type;
    AutoCompleteTextView nature, district;
    LinearLayout add_task, add_appellant;
    ImageView back;
    String pushkey;
    List<String> district_list;
    ConstraintLayout lay;
    DatabaseReference reference_phone;
    int x=1;
    private Context contextNullSafe;
    EditText dispose_summary, summary, due_date;
    TextView date_reply, judgement_date, time_limit, days, summ, submit, date_of_filing, due;
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
        nature = view.findViewById(R.id.nature);
        district = view.findViewById(R.id.district);
        summary = view.findViewById(R.id.summary_edt);
        submit = view.findViewById(R.id.submit_txt);
        date_of_filing = view.findViewById(R.id.date_of_filing);
        district_list =new ArrayList<>();
        reference_phone = FirebaseDatabase.getInstance().getReference().child("Phone numbers");
        due = view.findViewById(R.id.due);
        due_date = view.findViewById(R.id.due_date);
        add_task = view.findViewById(R.id.add_task);
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
        legal1 = view.findViewById(R.id.legal1);
        legal2 = view.findViewById(R.id.legal2);
        judgement_date = view.findViewById(R.id.judgement_date_edt);
        time_limit = view.findViewById(R.id.time_limit_edt);
        reference = FirebaseDatabase.getInstance().getReference().child("writ");
        pushkey = reference.push().getKey();
        appellantAdapter = new AppellantAdapter(getContext(), appellant_list,pushkey);
        taskAdapter = new taskAdapter(getContext(), task_list,pushkey);
        String[] array = {"WPHC","WPCR" , "WPS","WA", "WPPIL","WPC","CONT","CRR","CRA", "CRMP", "ACQT", "MAC"};
        //Creating the instance of ArrayAdapter containing list of language names
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>
                (getContextNullSafety(), android.R.layout.select_dialog_item, array);
        //Getting the instance of AutoCompleteTextView
        nature.setThreshold(1);//will start working from first character
        nature.setAdapter(adapter1);



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
                        deleted_task = task_list.get(position);
                        task_list.remove(position);
                        taskAdapter.notifyItemRemoved(position);
                        Snackbar.make(recyclerView2, deleted_task + " deleted.", Snackbar.LENGTH_LONG)
                                .setAction("Undo", v -> {
                                    task_list.add(position, deleted_task);
                                    taskAdapter.notifyItemInserted(position);
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

                        task_content.setText(task_list.get(position));
                        cancel.setOnClickListener(v -> {
                            task_list.remove(position);
                            task_list.add(position, task_content.getText().toString().trim());
                            taskAdapter.notifyItemChanged(position);
                            dialog.dismiss();
                        });
                        add.setOnClickListener(v -> {
                            if (!task_content.getText().toString().trim().equals("")) {
                                task_list.remove(position);
                                task_list.add(position, task_content.getText().toString().trim());
                                taskAdapter.notifyItemChanged(position);
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
            due.setVisibility(View.GONE);
            due_date.setVisibility(View.GONE);
            type = "Dismissed";
        });

        submit.setOnClickListener(v-> {
            if(!date_of_filing.getText().toString().trim().equals("")){
                if(!nature.getText().toString().trim().equals("")){
                    if(!district.getText().toString().trim().equals("")) {
                        if (!dispose_summary.getText().toString().trim().equals("") && x==0) {
                            if (!due_date.getText().toString().trim().equals("") && x==0) {
                                if (!summary.getText().toString().trim().equals("")) {
                                    datasend();
                                } else {
                                    summary.setError("Empty");
                                    Snackbar.make(lay, "Please Add Synopsis of the Case", Snackbar.LENGTH_LONG)
                                            .setActionTextColor(Color.parseColor("#171746"))
                                            .setTextColor(Color.parseColor("#FF7F5C"))
                                            .setBackgroundTint(Color.parseColor("#171746"))
                                            .show();
                                }
                            } else {
                                due_date.setError("Empty");
                                Snackbar.make(lay, "Please Add Due Date.", Snackbar.LENGTH_LONG)
                                        .setActionTextColor(Color.parseColor("#171746"))
                                        .setTextColor(Color.parseColor("#FF7F5C"))
                                        .setBackgroundTint(Color.parseColor("#171746"))
                                        .show();
                            }
                        } else {
                            dispose_summary.setError("Empty");
                            Snackbar.make(lay, "Please Add Judgement Summary.", Snackbar.LENGTH_LONG)
                                    .setActionTextColor(Color.parseColor("#171746"))
                                    .setTextColor(Color.parseColor("#FF7F5C"))
                                    .setBackgroundTint(Color.parseColor("#171746"))
                                    .show();
                        }
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

        due_date.setOnClickListener(v->{
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(
                    getActivity(),
                    mDateSetListener,
                    year, month, day);
            check_ = 4;
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
            if (check_ == 4){
                due_date.setText(date);
            }
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

    @SuppressLint("NotifyDataSetChanged")
    private void addTask() {

        Dialog dialog = new Dialog(getContext());
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.add_respondent);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView cancel = dialog.findViewById(R.id.textCancel);
        TextView add = dialog.findViewById(R.id.textAdd);
        EditText task_content = dialog.findViewById(R.id.inputURL);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        cancel.setOnClickListener(v -> dialog.dismiss());
        add.setOnClickListener(v -> {
            if (!task_content.getText().toString().trim().equals("")) {
                recyclerView2.setVisibility(View.VISIBLE);
                task_list.add(task_content.getText().toString().trim());
                Collections.reverse(task_list);
                taskAdapter = new taskAdapter(getContext(), task_list,pushkey);
                taskAdapter.notifyDataSetChanged();
                try {
                    recyclerView2.setAdapter(taskAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
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

    private void datasend(){

        reference.child(pushkey).child("district").setValue(district.getText().toString().toUpperCase(Locale.ROOT));
        reference.child(pushkey).child("dateOfFiling").setValue(date_of_filing.getText().toString().toUpperCase(Locale.ROOT));
        reference.child(pushkey).child("nature").setValue(nature.getText().toString().toUpperCase(Locale.ROOT));
        reference.child(pushkey).child("timeLimit").setValue(time_limit.getText().toString().toUpperCase(Locale.ROOT));
        reference.child(pushkey).child("summary").setValue(summary.getText().toString().toUpperCase(Locale.ROOT));
        reference.child(pushkey).child("dateReply").setValue(date_reply.getText().toString().toUpperCase(Locale.ROOT));
        reference.child(pushkey).child("judgementDate").setValue(judgement_date.getText().toString().toUpperCase(Locale.ROOT));
        reference.child(pushkey).child("Judgement").setValue(type.toUpperCase(Locale.ROOT));
        reference.child(pushkey).child("dSummary").setValue(dispose_summary.getText().toString().toUpperCase(Locale.ROOT));
        reference.child(pushkey).child("dueDate").setValue(due_date.getText().toString().toUpperCase(Locale.ROOT));
        reference.child(pushkey).child("pushkey").setValue(pushkey);
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
                appellantAdapter = new AppellantAdapter(getContext(), appellant_list,pushkey);
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
