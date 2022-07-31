package in.aryomtech.cgalert;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    DatabaseReference reference, reference1;
    TextView yes,no;


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
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(list.get(position));

        reference = FirebaseDatabase.getInstance().getReference().child("Phone numbers").child(district).child(list.get(position));

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.number.setText("+91 " + snapshot.getValue(String.class));

                holder.call_btn.setOnClickListener(v -> {
                    String phone = "+91" + snapshot.getValue(String.class);
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    context.startActivity(intent);
                });

                holder.wp_btn.setOnClickListener(v -> {
                    String url = "https://api.whatsapp.com/send?phone=" + "+91" + snapshot.getValue(String.class);
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

                holder.share_btn.setOnClickListener(v->{
                    try {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Download App");
                        String message = "District - " + district +"\n" + "Police Station - " + list.get(position) +"\n" +
                                "Phone Number - " + "+91 " + snapshot.getValue(String.class) + "\n\n⭐ CG HIGH COURT ALERT ⭐";
                        intent.putExtra(Intent.EXTRA_TEXT, message);
                        context.startActivity(Intent.createChooser(intent, "Share using"));
                    } catch (Exception e) {
                        Toast.makeText(context, "Error Occurred", Toast.LENGTH_SHORT).show();
                    }
                });

                holder.delete.setOnClickListener(v -> {
                    Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_delete);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.show();
                    dialog.setCancelable(false);
                    yes = dialog.findViewById(R.id.yes);
                    no = dialog.findViewById(R.id.no);

                    yes.setOnClickListener(v2->{
                        reference1 = FirebaseDatabase.getInstance().getReference().child("Phone numbers").child(district);
                        reference1.child(list.get(position)).removeValue();
                        dialog.dismiss();
                    });

                    no.setOnClickListener(v1->{
                        dialog.dismiss();
                    });
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView number;
        ImageView call_btn, wp_btn, delete, share_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.ps_name);
            number = itemView.findViewById(R.id.ps_number);
            call_btn = itemView.findViewById(R.id.call);
            wp_btn = itemView.findViewById(R.id.wp);
            delete = itemView.findViewById(R.id.delete);
            share_btn = itemView.findViewById(R.id.share);
        }
    }

}
