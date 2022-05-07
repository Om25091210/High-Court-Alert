package in.aryomtech.cgalert;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.simform.customcomponent.SSCustomEdittextOutlinedBorder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Form extends AppCompatActivity {

    AutoCompleteTextView ac_district, ac_caseType;
    EditText editText_RMdate, editText_receiveDate, editText_beforeDate,editText_policeStation,editText_caseNo,
            criminal_name,case_year,crime_no,crime_year;
    ImageView calendar,calendar_re, calendar_before;
    DatePickerDialog picker;
    CheckBox callCheck,returnCheck;
    SSCustomEdittextOutlinedBorder serialNo;
    LinearLayout submit;
    DatabaseReference reference;
    String rev_RMDate = "";
    String rmDate, receiveDate, beforeDate, policeStation, caseNo, criminalName, caseYear, crimeNo, crimeYear, District, CaseType;
    String currentDate;
    String check;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        ac_district = findViewById(R.id.ac_district);
        ac_caseType = findViewById(R.id.ac_case_type);
        editText_RMdate = findViewById(R.id.date_edt);
        calendar = findViewById(R.id.calendar_img);
        editText_receiveDate = findViewById(R.id.diary_re_date);
        editText_beforeDate = findViewById(R.id.before_date);
        calendar_before = findViewById(R.id.before_cal);
        calendar_re = findViewById(R.id.diary_cal);
        callCheck = findViewById(R.id.checkBox_RM_call);
        returnCheck = findViewById(R.id.checkBox_RM_return);
        serialNo = findViewById(R.id.edtSerial);
        editText_policeStation = findViewById(R.id.policeStation);
        editText_caseNo = findViewById(R.id.case_no_edt);
        criminal_name = findViewById(R.id.name_edt);
        case_year = findViewById(R.id.case_year_edt);
        crime_no = findViewById(R.id.crime_no_edt);
        crime_year = findViewById(R.id.crime_year_edt);
        submit = findViewById(R.id.submit_layout);
        reference = FirebaseDatabase.getInstance().getReference().child("data");



        rmDate = editText_RMdate.getText().toString();
        beforeDate = editText_beforeDate.getText().toString();
        receiveDate = editText_receiveDate.getText().toString();
        policeStation = editText_policeStation.getText().toString();
        caseNo = editText_caseNo.getText().toString();
        criminalName = criminal_name.getText().toString();
        caseYear = case_year.getText().toString();
        crimeYear = crime_year.getText().toString();
        crimeNo = crime_no.getText().toString();
        District = ac_district.getText().toString();
        CaseType = ac_caseType.getText().toString();


        String[] district = {"Bilaspur", "Raipur", "Kanker", "Baloda Bazar", "Balod", "Balrampur", "Bastar", "Bemetara"};
        //Creating the instance of ArrayAdapter containing list of language names
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, district);
        //Getting the instance of AutoCompleteTextView
        ac_district.setThreshold(1);//will start working from first character
        ac_district.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        ac_district.setTextColor(Color.RED);

        String[] caseType = {"MCRC", "MCRCA"};
        //Creating the instance of ArrayAdapter containing list of language names
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, caseType);
        //Getting the instance of AutoCompleteTextView
        ac_caseType.setThreshold(1);//will start working from first character
        ac_caseType.setAdapter(adapter1);//setting the adapter data into the AutoCompleteTextView
        ac_caseType.setTextColor(Color.RED);


        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        currentDate = sdf.format(new Date());
        editText_RMdate.setText(currentDate);
        editText_receiveDate.setText(currentDate);
        editText_beforeDate.setText(currentDate);

        calendar.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            // date picker dialog
            picker = new DatePickerDialog(Form.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            editText_RMdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            rev_RMDate = String.valueOf(year + (monthOfYear + 1) +dayOfMonth);
                        }
                    }, year, month, day);
            picker.show();
        });

        if (callCheck.isChecked()){
            check = "RM CALL";
        }
        if (returnCheck.isChecked()){
            check = "RM RETURN";
        }


        calendar_re.setOnClickListener(v->{
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            // date picker dialog
            picker = new DatePickerDialog(Form.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            editText_receiveDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        }
                    }, year, month, day);
            picker.show();
        });


        calendar_before.setOnClickListener(v->{
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            // date picker dialog
            picker = new DatePickerDialog(Form.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            editText_beforeDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        }
                    }, year, month, day);
            picker.show();
        });


       /* char x =' ';
        for (int i = 0 ; i < rmDate.length();i++){
            x = rmDate.charAt(i);
            rev_RMDate += x+rev_RMDate;
        }*/

        submit.setOnClickListener(V->{
            pushData();
        });
    }

    private void pushData() {
        String push = (rev_RMDate + policeStation + District + CaseType + caseNo ) ;
        // bro iske aage ka pushkey me samajh nai aara haiupar me reverse ka code daal diya hu uncomment krlena
        reference.child(push).child("A").setValue(serialNo);
        reference.child(push).child("B").setValue(policeStation);
        reference.child(push).child("C").setValue(District);
        reference.child(push).child("D").setValue(CaseType);
        reference.child(push).child("E").setValue(caseNo);
        reference.child(push).child("F").setValue(criminalName);
        reference.child(push).child("G").setValue(caseYear);
        reference.child(push).child("H").setValue(crimeNo);
        reference.child(push).child("I").setValue(crimeYear);
        reference.child(push).child("J").setValue(receiveDate);
        reference.child(push).child("K").setValue(rmDate);
        reference.child(push).child("L").setValue(beforeDate);
        reference.child(push).child("date").setValue(currentDate);
        reference.child(push).child("pushkey").setValue(push.toString());
        reference.child(push).child("type").setValue(check);
    }



   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        return true;
    }*/
}
