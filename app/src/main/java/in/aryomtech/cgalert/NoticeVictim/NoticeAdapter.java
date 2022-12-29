package in.aryomtech.cgalert.NoticeVictim;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.List;

import in.aryomtech.cgalert.Adapter_dataShow;
import in.aryomtech.cgalert.R;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder>{


    List<Notice_model> list;
    Context context;
    String date;
    int x = 0;

    public NoticeAdapter(Context context, List<Notice_model> list) {
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public NoticeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_notice_card,parent,false);
        return new NoticeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeAdapter.ViewHolder holder, int position) {


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
        holder.case_no.setText(list.get(position).getCaseNo());
        date = list.get(position).getHearingDate();
        holder.message.setText("अतः उक्त अपराध क्रमांक के पीड़ित को स्वयं मय वैध पहचान पत्र के साथ या अपने अधिवक्ता के माध्यम से दिनांक - " + date + " को उच्च न्यायालय, बिलासपुर के हेल्प डेस्क या संबंधित जिले के (DLSA) " +
                "DISTRICT LEGAL SERVICES AUTHORITY में उपस्थित होकर अपना प्रतिनिधित्व सुनिश्चित करने हेतु सूचित करने का कष्ट करें।");

        holder.layout.setOnClickListener(v->{
            if (x == 0) {
                holder.message.setVisibility(View.VISIBLE);
                x=1;
            }
            else {
                holder.message.setVisibility(View.GONE);
                x=0;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView district,station,crime_no,crime_yr,case_type,case_yr,notice_dt,hearing_dt,advocate_name,message,appellant,case_no;
        ConstraintLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

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
        }
    }
}
