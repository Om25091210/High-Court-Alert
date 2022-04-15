package in.aryomtech.cgalert.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import in.aryomtech.cgalert.Fragments.Adapter.installation_Adapter;
import in.aryomtech.cgalert.Fragments.model.user_data;
import in.aryomtech.cgalert.R;


public class Installation extends Fragment {

    View view;
    EditText search;
    RecyclerView recyclerView;
    private Context contextNullSafe;
    DatabaseReference reference;
    List<user_data> mylist=new ArrayList<>();
    List<user_data> list=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_installation, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        recyclerView=view.findViewById(R.id.recycler_view);
        reference= FirebaseDatabase.getInstance().getReference().child("users");
        search=view.findViewById(R.id.search);

        LinearLayoutManager mManager = new LinearLayoutManager(getContextNullSafety());
        recyclerView.setItemViewCacheSize(500);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(mManager);

        search.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(s+"");
            }
        });
        get_user();
        return view;
    }

    private void search(CharSequence s) {
        mylist.clear();
        for(user_data object:list){
            if (object.getName().contains(s.toString().toLowerCase().trim())) {
                mylist.add(object);
            }
            else if(object.getPhone().contains(s.toString().toLowerCase().trim())){
                mylist.add(object);
            }
        }
        installation_Adapter installation_adapter=new installation_Adapter(getContextNullSafety(),mylist);
        installation_adapter.notifyDataSetChanged();
        if(recyclerView!=null)
            recyclerView.setAdapter(installation_adapter);
    }

    private void get_user() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()) {
                    list.add(snapshot.child(ds.getKey()).getValue(user_data.class));
                }
                installation_Adapter installation_adapter=new installation_Adapter(getContextNullSafety(),list);
                installation_adapter.notifyDataSetChanged();
                if(recyclerView!=null)
                    recyclerView.setAdapter(installation_adapter);
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