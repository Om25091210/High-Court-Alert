package in.aryomtech.cgalert.CasesAgainstPolice;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import in.aryomtech.cgalert.R;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;


public class CasesAgainPolice extends Fragment {

    View view;
    RecyclerView recyclerView2, recyclerView3;
    ArrayList<String> task_list;
    ArrayList<String> appellant_list;
    taskAdapter taskAdapter;
    AppellantAdapter appellantAdapter;
    String deleted_task;
    LinearLayout add_task, add_appellant;
    ImageView back;
    private Context contextNullSafe;
    TextView date_reply, judgement_date, time_limit, days;
    CheckBox legal1, legal2;
    int check_;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_cases_again_police, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        task_list = new ArrayList<>();
        appellant_list = new ArrayList<>();
        taskAdapter = new taskAdapter(getContext(), task_list);
        appellantAdapter = new AppellantAdapter(getContext(), appellant_list);
        add_task = view.findViewById(R.id.add_task);
        add_appellant = view.findViewById(R.id.add_appellant);
        recyclerView2 = view.findViewById(R.id.recycler_view);
        recyclerView3 = view.findViewById(R.id.recycler_view2);
        back = view.findViewById(R.id.imageView4);
        recyclerView2.setVisibility(View.GONE);
        recyclerView3.setVisibility(View.GONE);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        recyclerView2.setItemViewCacheSize(500);
        recyclerView2.setDrawingCacheEnabled(true);
        recyclerView2.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView2.setLayoutManager(layoutManager2);

        LinearLayoutManager layoutManager3 = new LinearLayoutManager(getContext());
        recyclerView3.setItemViewCacheSize(500);
        recyclerView3.setDrawingCacheEnabled(true);
        recyclerView3.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView3.setLayoutManager(layoutManager3);

        date_reply = view.findViewById(R.id.date_reply);
        legal1 = view.findViewById(R.id.legal1);
        legal2 = view.findViewById(R.id.legal2);
        judgement_date = view.findViewById(R.id.judgement_date_edt);
        time_limit = view.findViewById(R.id.time_limit_edt);
        days = view.findViewById(R.id.days);

        days.setOnClickListener(v -> {


        });
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextNullSafe = context;
    }

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
                taskAdapter = new taskAdapter(getContext(), task_list);
                taskAdapter.notifyDataSetChanged();
                try {
                    recyclerView2.setAdapter(taskAdapter);
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

}
