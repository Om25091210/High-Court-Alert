package in.aryomtech.cgalert;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Adapter_PhoneNo extends RecyclerView.Adapter<Adapter_PhoneNo.ViewHolder> {

    List<String> list;
    Context context;
    String district;
    DatabaseReference reference;
    String num;


    public Adapter_PhoneNo(Context context, List<String> list, String district) {
        this.context = context;
        this.list = list;
        this.district = district;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_number_data, parent, false);
        return new ViewHolder(view);
    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(list.get(position));

        reference = FirebaseDatabase.getInstance().getReference().child("Phone numbers").child(district).child(list.get(position));

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.number.setText(snapshot.getValue(String.class));

                holder.call_btn.setOnClickListener(v->{
                    String phone = "+91" + snapshot.getValue(String.class);
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    context.startActivity(intent);
                });


                holder.wp_btn.setOnClickListener(v->{
                    String url = "https://api.whatsapp.com/send?phone=" +"+91" + snapshot.getValue(String.class);
                    try {
                        PackageManager pm = v.getContext().getPackageManager();
                        pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        v.getContext().startActivity(i);
                    } catch (PackageManager.NameNotFoundException e) {
                        v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        
        holder.delete.setOnClickListener(v->{
            reference.removeValue();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView number;
        ImageView call_btn, wp_btn, delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.ps_name);
            number = itemView.findViewById(R.id.ps_number);
            call_btn = itemView.findViewById(R.id.call);
            wp_btn = itemView.findViewById(R.id.wp);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
