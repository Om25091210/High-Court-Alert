package in.aryomtech.cgalert;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import in.aryomtech.cgalert.Fragments.Mcrc_Rm_Coll;
import in.aryomtech.cgalert.Fragments.Mcrc_Rm_Return;
import in.aryomtech.cgalert.Fragments.Similar_Collection;
import in.aryomtech.cgalert.Fragments.Similar_Return;
import in.aryomtech.cgalert.Fragments.model.Excel_data;
import in.aryomtech.cgalert.Fragments.pending_coll;
import in.aryomtech.cgalert.Fragments.pending_return;
import in.aryomtech.cgalert.Fragments.today;
import in.aryomtech.cgalert.Fragments.urgent_data;
import in.aryomtech.myapplication.v4.FragmentPagerItemAdapter;
import in.aryomtech.myapplication.v4.FragmentPagerItems;
import soup.neumorphism.NeumorphCardView;


public class Frag_Home extends Fragment {

    List<Excel_data> pending_return=new ArrayList<>();
    long total_coll,total_return;
    Query query_coll,query_return;
    TextView coll_text,text_return;
    DatabaseReference reference;
    NeumorphCardView blue,back;
    private Context contextNullSafe;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_frag__home, container, false);
        if (contextNullSafe == null) getContextNullSafety();

        blue=view.findViewById(R.id.blue);
        back=view.findViewById(R.id.back);
        blue.setOnClickListener(v->{
            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.constraint,new pending_coll())
                    .addToBackStack(null)
                    .commit();
        });
        back.setOnClickListener(v->{
            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.constraint,new pending_return())
                    .addToBackStack(null)
                    .commit();
        });
        coll_text=view.findViewById(R.id.textView6);
        text_return=view.findViewById(R.id.textView8);

        query_coll = FirebaseDatabase.getInstance().getReference().child("data").orderByChild("type").equalTo("MCRC_RM_COLL");
        query_return = FirebaseDatabase.getInstance().getReference().child("data").orderByChild("type").equalTo("MCRC _RM_ RETURN");

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                ((FragmentActivity)getContextNullSafety()).getSupportFragmentManager(), FragmentPagerItems.with(getContextNullSafety())
                .add("Urgent diary", urgent_data.class)
                .add("Today", today.class)
                .add("MCRC.RM.COLLECT", Mcrc_Rm_Coll.class)
                .add("MCRC.RM.RETURN", Mcrc_Rm_Return.class)
                .add("Similar Collection", Similar_Collection.class)
                .add("Similar Return", Similar_Return.class)
                .create());

        ViewPager viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = view.findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);

        get_pending();

        return view;
    }

    private void get_pending() {
        query_coll.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(snapshot.child(ds.getKey()).child("J").getValue(String.class).equals("None")){
                        total_coll++;
                    }
                }
                String text=total_coll+"";
                coll_text.setText(text);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        query_return.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(snapshot.child(ds.getKey()).child("J").getValue(String.class).equals("None")){
                        total_return++;
                        pending_return.add(snapshot.child(ds.getKey()).getValue(Excel_data.class));
                    }
                }
                String text=total_return+"";
                text_return.setText(text);
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextNullSafe = context;
    }

}