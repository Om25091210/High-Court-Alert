package in.aryomtech.cgalert.fcm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.aryomtech.cgalert.Fragments.model.Excel_data;
import in.aryomtech.cgalert.NoticeVictim.model.Notice_model;
import in.aryomtech.cgalert.R;
import in.aryomtech.cgalert.Splash;
import in.aryomtech.cgalert.Writ.Model.WritModel;
import in.aryomtech.cgalert.databinding.ActivityTempNotificationBinding;

public class temp_notification extends AppCompatActivity {

    ActivityTempNotificationBinding binding;
    DatabaseReference ref_data,ref_notice,ref_writ;
    String key,which_section;
    List<Excel_data> excel_data=new ArrayList<>();
    List<Notice_model> notice_data=new ArrayList<>();
    List<WritModel> writ_data=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTempNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        key = getIntent().getStringExtra("sending_msg_data");

        binding.caseDiary.linearLayout2.setVisibility(View.GONE);
        binding.caseDiary.linearLayout3.setVisibility(View.GONE);
        binding.caseDiary.type.setVisibility(View.GONE);
        binding.caseDiary.dayLeft.setVisibility(View.GONE);
        binding.caseDiary.share.setVisibility(View.GONE);
        binding.caseDiary.imageView2.setVisibility(View.GONE);

        ref_data = FirebaseDatabase.getInstance().getReference().child("data");
        ref_notice = FirebaseDatabase.getInstance().getReference().child("notice");
        ref_writ = FirebaseDatabase.getInstance().getReference().child("writ");

        check_key();
    }

    private void check_key() {

        ref_data.child(key).child("B").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Log.e("oooo","herer");
                    binding.caseDiary.getRoot().setVisibility(View.VISIBLE);
                    ref_data.child(key+"").child("seen").setValue("1");
                    populate_data();
                }
            }@Override public void onCancelled(@NonNull DatabaseError error) {}
        });

        ref_notice.child(key).child("advocate").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    binding.notice.getRoot().setVisibility(View.VISIBLE);
                    Log.e("HERH",key+"");
                    ref_notice.child(key+"").child("seen").setValue("1");
                    populate_notice();
                }
            }@Override public void onCancelled(@NonNull DatabaseError error) {}
        });

        ref_writ.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    binding.writ.getRoot().setVisibility(View.VISIBLE);
                    Log.e("writ",key+"");
                    ref_writ.child(key+"").child("seen").setValue("1");
                    populate_writ();
                }
            }@Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void populate_writ() {
        ref_writ.child(key+"").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                writ_data.add(snapshot.getValue(WritModel.class));
                show_writ();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void show_writ() {
        binding.writ.districtEd.setText(writ_data.get(0).getDistrict());
        binding.writ.dateOfFiling.setText(writ_data.get(0).getDateOfFiling());
        binding.writ.nature.setText(writ_data.get(0).getCase_nature());
        binding.writ.caseNo.setText(writ_data.get(0).getCaseNo());
        binding.writ.caseYear.setText(writ_data.get(0).getCaseYear());
        binding.writ.judgementDate.setText(writ_data.get(0).getJudgementDate());
        binding.writ.judgementType.setText(writ_data.get(0).getJudgement());

        if(writ_data.get(0).getDecisionDate()!=null) {
            if (writ_data.get(0).getDecisionDate().equals("")) {
                binding.layout.setBackgroundResource(R.drawable.bg_card_red);
            } else {
                binding.layout.setBackgroundResource(R.drawable.bg_card_white);
            }
        }
        if(writ_data.get(0).getJudgement()!=null) {
            if (writ_data.get(0).getJudgement().equals("DISMISSED")) {
                binding.layout.setBackgroundResource(R.drawable.bg_card_white);
            } else {
                binding.layout.setBackgroundResource(R.drawable.bg_card_white);
            }
        }

        //tick logic
        if (writ_data.get(0).getReminded() != null) {
            if (writ_data.get(0).getReminded().equals("once")) {
                binding.writ.imageView2.setVisibility(View.VISIBLE);
                binding.writ.imageView2.setImageResource(R.drawable.ic_blue_tick);
            } else if (writ_data.get(0).getReminded().equals("twice")) {
                binding.writ.imageView2.setVisibility(View.VISIBLE);
                binding.writ.imageView2.setImageResource(R.drawable.ic_green_tick);
            }
        }

        binding.notice.imageView4.setOnClickListener(v -> {
            Intent intent = new Intent(temp_notification.this, Splash.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        binding.notice.progressBar2.setVisibility(View.GONE);

    }

    private void populate_notice() {
        ref_notice.child(key+"").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notice_data.add(snapshot.getValue(Notice_model.class));
                show_notice();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void show_notice() {
        binding.notice.noticeDtEd.setText(notice_data.get(0).getNoticeDate());
        binding.notice.districtEd.setText(notice_data.get(0).getDistrict());
        binding.notice.stationName.setText(notice_data.get(0).getStation());
        binding.notice.crimeNoEd.setText(notice_data.get(0).getCrimeNo()+"/");
        binding.notice.crimeYrEd.setText(notice_data.get(0).getCrimeYear());
        binding.notice.caseTypeEd.setText(notice_data.get(0).getCaseType()+"/");
        binding.notice.caseNoEd.setText(notice_data.get(0).getCaseNo()+"/");
        binding.notice.caseYrEd.setText(notice_data.get(0).getCaseYear());
        binding.notice.appellant.setText(notice_data.get(0).getAppellant());
        binding.notice.hearingDtEd.setText(notice_data.get(0).getHearingDate());
        binding.notice.advocateEd.setText(notice_data.get(0).getAdvocate());
        binding.notice.message.setText("अतः उक्त अपराध क्रमांक के पीड़ित को स्वयं मय वैध पहचान पत्र के साथ या अपने अधिवक्ता के माध्यम से दिनांक - " + notice_data.get(0).getNoticeDate() + " को उच्च न्यायालय, बिलासपुर के हेल्प डेस्क या संबंधित जिले के (DLSA) " +
                "DISTRICT LEGAL SERVICES AUTHORITY में उपस्थित होकर अपना प्रतिनिधित्व सुनिश्चित करने हेतु सूचित करने का कष्ट करें।");

        //red white logic
        if (notice_data.get(0).getUploaded_file()==null)
            binding.layout.setBackgroundColor(Color.parseColor("#FAD8D9"));
        else
            binding.layout.setBackgroundColor(Color.parseColor("#FFFFFF"));

        //tick logic
        if (notice_data.get(0).getReminded() != null) {
            if (notice_data.get(0).getReminded().equals("once")) {
                binding.notice.imageView2.setVisibility(View.VISIBLE);
                binding.notice.imageView2.setImageResource(R.drawable.ic_blue_tick);
            } else if (notice_data.get(0).getReminded().equals("twice")) {
                binding.notice.imageView2.setVisibility(View.VISIBLE);
                binding.notice.imageView2.setImageResource(R.drawable.ic_green_tick);
            }
        }

        binding.notice.imageView4.setOnClickListener(v -> {
            Intent intent = new Intent(temp_notification.this, Splash.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        binding.notice.progressBar2.setVisibility(View.GONE);

        //showing time
        if (notice_data.get(0).getHearingDate() != null) {
            binding.notice.dayLeft.setVisibility(View.VISIBLE);
            if (nDays_Between_Dates(notice_data.get(0).getNoticeDate()) < 0) {
                binding.notice.dayLeft.setText("0d");
                binding.notice.dayLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_clock_time, 0, 0, 0);
            }
            else if (nDays_Between_Dates(notice_data.get(0).getNoticeDate()) >= 0) {
                binding.notice.dayLeft.setText(nDays_Between_Dates(notice_data.get(0).getNoticeDate())+"d");
                binding.notice.dayLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_clock_time, 0, 0, 0);
            }
            else {
                binding.notice.dayLeft.setText("--");
                binding.notice.dayLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_clock_time, 0, 0, 0);
            }
        } else
            binding.notice.dayLeft.setVisibility(View.GONE);
    }

    private void populate_data() {
        ref_data.child(key+"").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                excel_data.add(snapshot.getValue(Excel_data.class));
                show_data();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void show_data() {
        binding.caseDiary.progressBar2.setVisibility(View.GONE);

        binding.caseDiary.linearLayout2.setVisibility(View.VISIBLE);
        binding.caseDiary.linearLayout3.setVisibility(View.VISIBLE);
        binding.caseDiary.type.setVisibility(View.VISIBLE);
        binding.caseDiary.dayLeft.setVisibility(View.VISIBLE);
        binding.caseDiary.imageView2.setVisibility(View.VISIBLE);
        binding.caseDiary.share.setVisibility(View.VISIBLE);

        binding.caseDiary.lastDate.setText(excel_data.get(0).getL());
        binding.caseDiary.stationName.setText(excel_data.get(0).getB());
        binding.caseDiary.distName.setText(excel_data.get(0).getC());
        binding.caseDiary.caseNo.setText(excel_data.get(0).getE() + "/" + excel_data.get(0).getG());
        binding.caseDiary.nameRm.setText(excel_data.get(0).getK());
        binding.caseDiary.caseType.setText(excel_data.get(0).getD());
        binding.caseDiary.personName.setText(excel_data.get(0).getF());
        binding.caseDiary.crimeNo.setText(excel_data.get(0).getH() + "/" + excel_data.get(0).getI());
        binding.caseDiary.receivingDate.setText(excel_data.get(0).getJ());

        //return call logic
        if (excel_data.get(0).getType().equals("RM CALL")) {
            binding.caseDiary.message.setText("उपरोक्त मूल केश डायरी दिनाँक " + excel_data.get(0).getK() + " तक बेल शाखा, कार्यालय महाधिवक्ता,उच्च न्यायालय छतीसगढ़ में  अनिवार्यतः जमा करें।");
            binding.caseDiary.type.setVisibility(View.VISIBLE);
            binding.caseDiary.type.setImageResource(R.drawable.ic_submit_type);
        } else if (excel_data.get(0).getType().equals("RM RETURN")) {
            binding.caseDiary.message.setText("उपरोक्त मूल केश डायरी " + excel_data.get(0).getK() + " से पांच दिवस के भीतर बेल शाखा, कार्यालय महाधिवक्ता,उच्च न्यायालय से वापिस ले जावें।");
            binding.caseDiary.type.setVisibility(View.VISIBLE);
            binding.caseDiary.type.setImageResource(R.drawable.ic_return_type);
        } else
            binding.caseDiary.type.setVisibility(View.GONE);

        //red white logic
        if (excel_data.get(0).getJ().equals("None") || excel_data.get(0).getJ().equals("nan"))
            binding.layout.setBackgroundColor(Color.parseColor("#FAD8D9"));
        else
            binding.layout.setBackgroundColor(Color.parseColor("#FFFFFF"));

        //tick logic
        if (excel_data.get(0).getReminded() != null) {
            if (excel_data.get(0).getReminded().equals("once")) {
                binding.caseDiary.imageView2.setVisibility(View.VISIBLE);
                binding.caseDiary.imageView2.setImageResource(R.drawable.ic_blue_tick);
            } else if (excel_data.get(0).getReminded().equals("twice")) {
                binding.caseDiary.imageView2.setVisibility(View.VISIBLE);
                binding.caseDiary.imageView2.setImageResource(R.drawable.ic_green_tick);
            }
        }

        if (excel_data.get(0).getL() != null) {
            if (!excel_data.get(0).getL().equals("None")) {
                binding.caseDiary.dayLeft.setVisibility(View.VISIBLE);
                if (nDays_Between_Dates(excel_data.get(0).getL()) == 0) {
                    binding.caseDiary.dayLeft.setText("0d");
                    binding.caseDiary.dayLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_clock_time, 0, 0, 0);
                } else if (nDays_Between_Dates(excel_data.get(0).getL()) <= 5) {
                    binding.caseDiary.dayLeft.setText(nDays_Between_Dates(excel_data.get(0).getL()) + "d");
                    binding.caseDiary.dayLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_clock_time, 0, 0, 0);
                } else {
                    binding.caseDiary.dayLeft.setText("--");
                    binding.caseDiary.dayLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_red_clock, 0, 0, 0);
                }
            } else
                binding.caseDiary.dayLeft.setVisibility(View.GONE);
        } else
            binding.caseDiary.dayLeft.setVisibility(View.GONE);
        String message;
        if(excel_data.get(0).getType().equals("RM CALL")) {
             message= "हाईकोर्ट अलर्ट:-डायरी माँग" + "\nदिनाँक:- " + excel_data.get(0).getDate() + "\n\n" + "Last Date - " + excel_data.get(0).getL() + "\n"
                    + "District - " + excel_data.get(0).getC() + "\n" +
                    "Police Station - " + excel_data.get(0).getB() + "\n" +
                    excel_data.get(0).getD() + " No. - " + excel_data.get(0).getE() + "/" + excel_data.get(0).getG() + "\n" +
                    "RM Date - " + excel_data.get(0).getK() + "\n" +
                    "Case Type - " + excel_data.get(0).getD() + "\n" +
                    "Name - " + excel_data.get(0).getF() + "\n" +
                    "Crime No. - " + excel_data.get(0).getH() + "/" + excel_data.get(0).getI() + "\n" +
                    "Received - " + excel_data.get(0).getJ() + "\n\n"
                    + "उपरोक्त मूल केश डायरी दिनाँक " + excel_data.get(0).getL() + " तक बेल शाखा, कार्यालय महाधिवक्ता,उच्च न्यायालय छतीसगढ़ में  अनिवार्यतः जमा करें।";
        }
        else{
            message = "हाईकोर्ट अलर्ट:-डायरी वापसी"+"\nदिनाँक:- "+ excel_data.get(0).getDate()  +" \n\n" + "Last Date - " + excel_data.get(0).getL() + "\n"
                    + "District - " + excel_data.get(0).getC() + "\n" +
                    "Police Station - " + excel_data.get(0).getB() + "\n"+
                    excel_data.get(0).getD() + " No. - " + excel_data.get(0).getE() +"/"+ excel_data.get(0).getG()+"\n" +
                    "RM Date - " + excel_data.get(0).getK()+ "\n" +
                    "Case Type - " + excel_data.get(0).getD() +  "\n" +
                    "Name - " + excel_data.get(0).getF()+  "\n" +
                    "Crime No. - " + excel_data.get(0).getH() +"/"+ excel_data.get(0).getI()+  "\n" +
                    "Received - " + excel_data.get(0).getJ() + "\n\n" + "1)उपरोक्त मूल केश डायरी  महाधिवक्ता कार्यालय द्वारा दी गयी मूल पावती लाने पर ही दी जाएगी।\n"
                    +"2) उपरोक्त मूल केश डायरी "+ excel_data.get(0).getK() +" से पांच दिवस के भीतर बेल शाखा, कार्यालय महाधिवक्ता,उच्च न्यायालय से वापिस ले जावें।";

        }
        binding.caseDiary.share.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(shareIntent, "Share link using"));
        });

        binding.caseDiary.imageView4.setOnClickListener(v -> {
            Intent intent = new Intent(temp_notification.this, Splash.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    public static int nDays_Between_Dates(String date1) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String str = formatter.format(date);
        int diffDays = 0;
        try {
            SimpleDateFormat dates = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            Date startDate = dates.parse(date1);
            Date endDate = dates.parse(str);
            if(startDate.after(endDate)) {
                long diff = endDate.getTime() - startDate.getTime();
                diffDays = (int) (diff / (24 * 60 * 60 * 1000));
            }
            else if(startDate.equals(endDate))
                return 0;
            else{
                return 6;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Math.abs(diffDays);
    }
}