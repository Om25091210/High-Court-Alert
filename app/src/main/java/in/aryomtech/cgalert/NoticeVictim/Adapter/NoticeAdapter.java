package in.aryomtech.cgalert.NoticeVictim.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import in.aryomtech.cgalert.NoticeVictim.Interface.onUploadInterface;
import in.aryomtech.cgalert.NoticeVictim.model.Notice_model;
import in.aryomtech.cgalert.R;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder>{


    List<Notice_model> list;
    Context context;
    DatabaseReference reference;
    String date;
    Dialog dialog;
    boolean isadmin=false;
    onUploadInterface onUploadInterface;

    public NoticeAdapter(Context context, onUploadInterface onUploadInterface, List<Notice_model> list) {
        this.context = context;
        this.list = list;
        this.onUploadInterface=onUploadInterface;
        reference= FirebaseDatabase.getInstance().getReference().child("notice");
    }
    @NonNull
    @Override
    public NoticeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_notice_card,parent,false);
        return new NoticeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeAdapter.ViewHolder holder, int position) {

        isadmin=context.getSharedPreferences("isAdmin_or_not",Context.MODE_PRIVATE)
                .getBoolean("authorizing_admin",false);

        holder.district.setText(list.get(position).getDistrict());
        holder.station.setText(list.get(position).getStation());
        holder.crime_no.setText(list.get(position).getCrimeNo());
        holder.crime_yr.setText(" / " + list.get(position).getCrimeYear());
        holder.case_type.setText(list.get(position).getCaseType());
        holder.case_yr.setText(" / " + list.get(position).getCaseYear());
        holder.notice_dt.setText(list.get(position).getNoticeDate());
        holder.hearing_dt.setText(list.get(position).getHearingDate());
        holder.advocate_name.setText(list.get(position).getAdvocate());
        holder.appellant.setText(list.get(position).getAppellant());
        holder.case_no.setText(" / " +list.get(position).getCaseNo());

        if (list.get(position).getUploaded_file()==null) {
            holder.layout.setBackgroundResource(R.drawable.bg_card_red);
        }
        else {
            holder.layout.setBackgroundResource(R.drawable.bg_card_white);
        }

        holder.upload.setOnClickListener(v->{
            onUploadInterface.set_click_position(list.get(position).getPushkey());
        });
        if(list.get(position).getReminded()!=null) {
            if(list.get(position).getReminded().equals("once")){
                holder.tick.setVisibility(View.VISIBLE);
                holder.tick.setImageResource(R.drawable.ic_blue_tick);
            }
            else if(list.get(position).getReminded().equals("twice")){
                holder.tick.setVisibility(View.VISIBLE);
                holder.tick.setImageResource(R.drawable.ic_green_tick);
            }
        }
        else
            holder.tick.setVisibility(View.GONE);

        if(list.get(position).getSeen()!=null && isadmin)
            holder.seen.setVisibility(View.VISIBLE);
        else
            holder.seen.setVisibility(View.GONE);

        if(list.get(position).getSent()!=null)
            holder.notified.setVisibility(View.VISIBLE);
        else
            holder.notified.setVisibility(View.GONE);

        holder.imageRemoveImage.setOnClickListener(v->{
            dialog = new Dialog(context);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_for_sure);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            TextView cancel=dialog.findViewById(R.id.textView96);
            TextView yes=dialog.findViewById(R.id.textView95);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
            cancel.setOnClickListener(vi-> dialog.dismiss());
            yes.setOnClickListener(vi-> {
                reference.child(list.get(position).getPushkey()).removeValue();
                int actualPosition= holder.getAdapterPosition();
                list.remove(actualPosition);
                notifyItemRemoved(actualPosition);
                notifyItemRangeChanged(actualPosition, list.size());
            });
        });
        holder.download.setOnClickListener(v->{
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(position).getDoc_url()));
                context.startActivity(browserIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        date = list.get(position).getHearingDate();
        holder.message.setText("अतः उक्त अपराध क्रमांक के पीड़ित को स्वयं मय वैध पहचान पत्र के साथ या अपने अधिवक्ता के माध्यम से दिनांक - " + date + " को उच्च न्यायालय, बिलासपुर के हेल्प डेस्क या संबंधित जिले के (DLSA) " +
                "DISTRICT LEGAL SERVICES AUTHORITY में उपस्थित होकर अपना प्रतिनिधित्व सुनिश्चित करने हेतु सूचित करने का कष्ट करें।");

        String body="Noticed Date - " + list.get(position).getNoticeDate()+ "\n" +
                "Hearing Date - " + list.get(position).getHearingDate() +  "\n" +
                "Crime No. - " + list.get(position).getCrimeNo()+"/"+list.get(position).getCrimeYear()+  "\n" +
                "Case No. - " + list.get(position).getCaseType() +"/"+ list.get(position).getCaseNo()+"/"+list.get(position).getCaseYear()+  "\n" +
                "District - " + list.get(position).getDistrict() +  "\n" +
                "Police Station - " + list.get(position).getStation() +  "\n\n";

        holder.share.setOnClickListener(v->{
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, body+holder.message.getText().toString());
            context.startActivity(Intent.createChooser(shareIntent, "Share link using"));
        });

        holder.layout.setOnClickListener(v->{
            if (holder.message.getVisibility() == View.VISIBLE) {
                holder.message.setVisibility(View.GONE);
                // Its visible
            } else {
                holder.message.setVisibility(View.VISIBLE);
                // Either gone or invisible
            }
        });

        if(isadmin) {
            holder.imageRemoveImage.setVisibility(View.VISIBLE);
        }
        else {
            holder.imageRemoveImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView district,station,crime_no,crime_yr,case_type,case_yr,notice_dt,hearing_dt,advocate_name,message,appellant,case_no;
        ConstraintLayout layout;
        ImageView imageRemoveImage,tick,notified,seen,share;
        TextView download,upload;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            download = itemView.findViewById(R.id.download);
            notified = itemView.findViewById(R.id.notified);
            seen = itemView.findViewById(R.id.seen);
            imageRemoveImage = itemView.findViewById(R.id.imageRemoveImage);
            district = itemView.findViewById(R.id.district_ed);
            station = itemView.findViewById(R.id.station_name);
            crime_no = itemView.findViewById(R.id.crime_no_ed);
            crime_yr = itemView.findViewById(R.id.crime_yr_ed);
            case_type = itemView.findViewById(R.id.case_type_ed);
            case_yr = itemView.findViewById(R.id.case_yr_ed);
            notice_dt = itemView.findViewById(R.id.notice_dt_ed);
            hearing_dt = itemView.findViewById(R.id.hearing_dt_ed);
            advocate_name = itemView.findViewById(R.id.advocate_ed);
            message = itemView.findViewById(R.id.message);
            layout = itemView.findViewById(R.id.notice_layout);
            appellant = itemView.findViewById(R.id.appellant);
            case_no  = itemView.findViewById(R.id.case_no_ed);
            tick  = itemView.findViewById(R.id.imageView2);
            share  = itemView.findViewById(R.id.share);
            upload  = itemView.findViewById(R.id.upload);
        }
    }
}
