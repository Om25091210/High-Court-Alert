package in.aryomtech.cgalert.fcm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import in.aryomtech.cgalert.Fragments.model.Excel_data;
import in.aryomtech.cgalert.Home;
import in.aryomtech.cgalert.R;
import in.aryomtech.cgalert.Splash;
import in.aryomtech.cgalert.databinding.ActivityTempNotificationBinding;

public class temp_notification extends AppCompatActivity {

    ActivityTempNotificationBinding binding;
    DatabaseReference reference;
    String key;
    List<Excel_data> excel_data=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTempNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        key = getIntent().getStringExtra("sending_msg_data");

        binding.linearLayout2.setVisibility(View.GONE);
        binding.linearLayout3.setVisibility(View.GONE);
        binding.type.setVisibility(View.GONE);
        binding.dayLeft.setVisibility(View.GONE);
        binding.share.setVisibility(View.GONE);
        binding.imageView2.setVisibility(View.GONE);

        reference.child(key).child("seen").setValue(1);

        reference = FirebaseDatabase.getInstance().getReference().child("data").child(key + "");
        fetch_data();
    }

    private void fetch_data() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
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
        binding.progressBar2.setVisibility(View.GONE);

        binding.linearLayout2.setVisibility(View.VISIBLE);
        binding.linearLayout3.setVisibility(View.VISIBLE);
        binding.type.setVisibility(View.VISIBLE);
        binding.dayLeft.setVisibility(View.VISIBLE);
        binding.imageView2.setVisibility(View.VISIBLE);
        binding.share.setVisibility(View.VISIBLE);

        binding.lastDate.setText(excel_data.get(0).getL());
        binding.stationName.setText(excel_data.get(0).getB());
        binding.distName.setText(excel_data.get(0).getC());
        binding.caseNo.setText(excel_data.get(0).getE() + "/" + excel_data.get(0).getG());
        binding.nameRm.setText(excel_data.get(0).getK());
        binding.caseType.setText(excel_data.get(0).getD());
        binding.personName.setText(excel_data.get(0).getF());
        binding.crimeNo.setText(excel_data.get(0).getH() + "/" + excel_data.get(0).getI());
        binding.receivingDate.setText(excel_data.get(0).getJ());

        //return call logic
        if (excel_data.get(0).getType().equals("RM CALL")) {
            binding.message.setText("उपरोक्त मूल केश डायरी दिनाँक " + excel_data.get(0).getK() + " तक बेल शाखा, कार्यालय महाधिवक्ता,उच्च न्यायालय छतीसगढ़ में  अनिवार्यतः जमा करें।");
            binding.type.setVisibility(View.VISIBLE);
            binding.type.setImageResource(R.drawable.ic_submit_type);
        } else if (excel_data.get(0).getType().equals("RM RETURN")) {
            binding.message.setText("उपरोक्त मूल केश डायरी " + excel_data.get(0).getK() + " से पांच दिवस के भीतर बेल शाखा, कार्यालय महाधिवक्ता,उच्च न्यायालय से वापिस ले जावें।");
            binding.type.setVisibility(View.VISIBLE);
            binding.type.setImageResource(R.drawable.ic_return_type);
        } else
            binding.type.setVisibility(View.GONE);

        //red white logic
        if (excel_data.get(0).getJ().equals("None") || excel_data.get(0).getJ().equals("nan"))
            binding.layout.setBackgroundResource(R.drawable.bg_card_red);
        else
            binding.layout.setBackgroundResource(R.drawable.bg_card_white);

        //tick logic
        if (excel_data.get(0).getReminded() != null) {
            if (excel_data.get(0).getReminded().equals("once")) {
                binding.imageView2.setVisibility(View.VISIBLE);
                binding.imageView2.setImageResource(R.drawable.ic_blue_tick);
            } else if (excel_data.get(0).getReminded().equals("twice")) {
                binding.imageView2.setVisibility(View.VISIBLE);
                binding.imageView2.setImageResource(R.drawable.ic_green_tick);
            }
        }

        if (excel_data.get(0).getL() != null) {
            if (!excel_data.get(0).getL().equals("None")) {
                binding.dayLeft.setVisibility(View.VISIBLE);
                if (nDays_Between_Dates(excel_data.get(0).getL()) == 0) {
                    binding.dayLeft.setText("0d");
                    binding.dayLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_clock_time, 0, 0, 0);
                } else if (nDays_Between_Dates(excel_data.get(0).getL()) <= 5) {
                    binding.dayLeft.setText(nDays_Between_Dates(excel_data.get(0).getL()) + "d");
                    binding.dayLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_clock_time, 0, 0, 0);
                } else {
                    binding.dayLeft.setText("--");
                    binding.dayLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_red_clock, 0, 0, 0);
                }
            } else
                binding.dayLeft.setVisibility(View.GONE);
        } else
            binding.dayLeft.setVisibility(View.GONE);

        String message = "हाईकोर्ट अलर्ट:-डायरी माँग" + "\nदिनाँक:- " + excel_data.get(0).getDate() + "\n\n" + "Last Date - " + excel_data.get(0).getL() + "\n"
                + "District - " + excel_data.get(0).getC() + "\n" +
                "Police Station - " + excel_data.get(0).getB() + "\n" +
                excel_data.get(0).getD() + " No. - " + excel_data.get(0).getE() + "/" + excel_data.get(0).getG() + "\n" +
                "RM Date - " + excel_data.get(0).getK() + "\n" +
                "Case Type - " + excel_data.get(0).getD() + "\n" +
                "Name - " + excel_data.get(0).getF() + "\n" +
                "Crime No. - " + excel_data.get(0).getH() + "/" + excel_data.get(0).getI() + "\n" +
                "Received - " + excel_data.get(0).getJ() + "\n\n"
                + "उपरोक्त मूल केश डायरी दिनाँक " + excel_data.get(0).getK() + " तक बेल शाखा, कार्यालय महाधिवक्ता,उच्च न्यायालय छतीसगढ़ में  अनिवार्यतः जमा करें।";

        binding.share.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(shareIntent, "Share link using"));
        });

        binding.imageView4.setOnClickListener(v -> {
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