package in.aryomtech.cgalert.Fragments.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.aryomtech.cgalert.Fragments.Mcrc_Rm_Coll;
import in.aryomtech.cgalert.Fragments.admin.form;
import in.aryomtech.cgalert.Fragments.model.Excel_data;
import in.aryomtech.cgalert.Fragments.Interface.onAgainClickInterface;
import in.aryomtech.cgalert.Fragments.Interface.onClickInterface;
import in.aryomtech.cgalert.R;

public class Excel_Adapter extends RecyclerView.Adapter<Excel_Adapter.ViewHolder> {

    Context context;
    DatabaseReference reference;
    List<Excel_data> list;
    Dialog dialog;
    String gsID="";
    boolean is_selected=false;
    boolean isadmin=false;
    onClickInterface onClickInterface;
    onAgainClickInterface onAgainClickInterface;

    public Excel_Adapter(Context context, List<Excel_data> list,onClickInterface onClickInterface,onAgainClickInterface onAgainClickInterface,String gsID) {
        this.context = context;
        this.gsID=gsID;
        this.list = list;
        this.onClickInterface=onClickInterface;
        this.onAgainClickInterface=onAgainClickInterface;
        reference= FirebaseDatabase.getInstance().getReference().child("data");
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.card_design, parent, false);
        return new ViewHolder(view);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        isadmin=context.getSharedPreferences("isAdmin_or_not",Context.MODE_PRIVATE)
                .getBoolean("authorizing_admin",false);

        holder.last_date.setText(list.get(position).getL());
        holder.textViewTitle.setText(list.get(position).getB().toUpperCase() + "");
        holder.textViewBody.setText(list.get(position).getC().toUpperCase());
        holder.Rm.setText(list.get(position).getK());
        if(list.get(position).getNumber()!=null)
            holder.number.setText(list.get(position).getNumber());
        holder.mcrc.setText(list.get(position).getD().toUpperCase());
        holder.pr_case_no.setText(list.get(position).getD()+" No. -");
        holder.crime_no.setText(list.get(position).getH() +"/"+ list.get(position).getI());
        holder.case_no.setText(list.get(position).getE() +"/"+ list.get(position).getG());
        holder.name.setText(list.get(position).getF().toUpperCase());
        holder.receiving_date.setText(list.get(position).getJ());
        if(list.get(position).getL()!=null){
            if(!list.get(position).getL().equals("None")) {
                holder.day_left.setVisibility(View.VISIBLE);
                if (nDays_Between_Dates(list.get(position).getL()) == 0) {
                    holder.day_left.setText("0d");
                    holder.day_left.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_clock_time, 0, 0, 0);
                } else if (nDays_Between_Dates(list.get(position).getL()) <= 5) {
                    holder.day_left.setText(nDays_Between_Dates(list.get(position).getL()) + "d");
                    holder.day_left.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_clock_time, 0, 0, 0);
                }
                else if(nDays_Between_Dates(list.get(position).getL())==6){
                    holder.day_left.setText("--");
                    holder.day_left.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_red_clock, 0, 0, 0);
                }
                else {
                    holder.day_left.setText("--");
                    holder.day_left.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_time_exceed, 0, 0, 0);
                }
            }
            else
                holder.day_left.setVisibility(View.GONE);
        }
        else
            holder.day_left.setVisibility(View.GONE);

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

        if (list.get(position).getJ().equals("None") || list.get(position).getJ().equals("nan")) {
            holder.layout.setBackgroundResource(R.drawable.bg_card_red);
            if(list.get(position).getSeen()!=null && isadmin)
                holder.seen.setVisibility(View.VISIBLE);
            else
                holder.seen.setVisibility(View.GONE);
        }
        else {
            holder.layout.setBackgroundResource(R.drawable.bg_card_white);
            if(list.get(position).getSeen()!=null && isadmin)
                holder.seen.setVisibility(View.VISIBLE);
            else
                holder.seen.setVisibility(View.GONE);
        }

        if(list.get(position).getSent()!=null)
            holder.notified.setVisibility(View.VISIBLE);
        else
            holder.notified.setVisibility(View.GONE);

        if (list.get(position).getType().equals("RM CALL")) {
            holder.message.setText("उपरोक्त मूल केस डायरी तथा पूर्व अपराधिक रिकॉर्ड, दिनाँक "+list.get(position).getL()+" तक बेल शाखा, कार्यालय महाधिवक्ता,उच्च न्यायालय छतीसगढ़ में  अनिवार्यतः जमा करें।");
            holder.type.setVisibility(View.VISIBLE);
            holder.type.setImageResource(R.drawable.ic_submit_type);
        } else if (list.get(position).getType().equals("RM RETURN")) {
            holder.message.setText("उपरोक्त मूल केस डायरी "+list.get(position).getL()+" के भीतर बेल शाखा, कार्यालय महाधिवक्ता,उच्च न्यायालय से वापिस ले जावें।");
            holder.type.setVisibility(View.VISIBLE);
            holder.type.setImageResource(R.drawable.ic_return_type);
        } else
            holder.type.setVisibility(View.GONE);

        if (is_selected){
            String added="Added";
            holder.add_button.setText(added);
            holder.add_button.setBackgroundResource(R.drawable.add_bg_green);
        }
        else{
            String add="Add";
            holder.add_button.setText(add);
            holder.add_button.setBackgroundResource(R.drawable.add_card);
        }

        holder.add_button.setOnClickListener(v->{
            if(holder.add_button.getText().toString().equalsIgnoreCase("add")){
                onClickInterface.setClick(position);
                String added="Added";
                holder.add_button.setText(added);
                holder.add_button.setTextColor(Color.parseColor("#171746"));
                holder.add_button.setBackgroundResource(R.drawable.add_bg_green);
            }
            else if(holder.add_button.getText().toString().equalsIgnoreCase("added")){
                onAgainClickInterface.set_remove_click(position);
                String add="Add";
                holder.add_button.setText(add);
                holder.add_button.setTextColor(Color.parseColor("#FF000000"));
                holder.add_button.setBackgroundResource(R.drawable.add_card);
            }
        });

        String message = "हाईकोर्ट अलर्ट:-डायरी माँग"+"\nदिनाँक:- "+list.get(position).getDate()+"\n\n" + "Last Date - " + list.get(position).getL() + "\n"
                + "District - " + list.get(position).getC() + "\n" +
                "Police Station - " + list.get(position).getB() + "\n"+
                list.get(position).getD() + " No. - " + list.get(position).getE() +"/"+ list.get(position).getG()+"\n" +
                "RM Date - " + list.get(position).getK()+ "\n" +
                "Case Type - " + list.get(position).getD() +  "\n" +
                "Name - " + list.get(position).getF()+  "\n" +
                "Crime No. - " + list.get(position).getH() +"/"+ list.get(position).getI()+  "\n" +
                "Received - " + list.get(position).getJ() + "\n\n"
                + "उपरोक्त मूल केश डायरी दिनाँक "+list.get(position).getK()+" तक बेल शाखा, कार्यालय महाधिवक्ता,उच्च न्यायालय छतीसगढ़ में  अनिवार्यतः जमा करें।";

        holder.share.setOnClickListener(v->{
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, message );
            context.startActivity(Intent.createChooser(shareIntent, "Share link using"));
        });

        holder.layout.setOnClickListener(v->{
            if (holder.layout_details.getVisibility() == View.VISIBLE) {
                holder.layout_details.setVisibility(View.GONE);
                // Its visible
            } else {
                holder.layout_details.setVisibility(View.VISIBLE);
                // Either gone or invisible
            }
        });
        holder.imageRemovedata.setOnClickListener(v->{
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
                RecyclerView_delete(Collections.singletonList(list.get(position)));
            });
        });
        holder.view.setOnClickListener(v->{
            Excel_data excel_data=new Excel_data(
                    list.get(position).getA()
                    ,list.get(position).getB()
                    ,list.get(position).getC()
                    ,list.get(position).getD()
                    ,list.get(position).getE()
                    ,list.get(position).getF()
                    ,list.get(position).getG()
                    ,list.get(position).getH()
                    ,list.get(position).getI()
                    ,list.get(position).getJ()
                    ,list.get(position).getK()
                    ,list.get(position).getL()
                    ,list.get(position).getM()
                    ,list.get(position).getN()
                    ,list.get(position).getDate()
                    ,list.get(position).getType()
                    ,list.get(position).getPushkey()
                    ,list.get(position).getReminded()
                    ,list.get(position).getDate_of_alert()
                    ,list.get(position).getSeen()
                    ,list.get(position).getSent()
                    ,list.get(position).getNumber());

            Bundle bundle=new Bundle();
            bundle.putSerializable("excel_data_sending", excel_data);
            form form=new form();
            form.setArguments(bundle);
            ((FragmentActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,form)
                    .addToBackStack(null)
                    .commit();

        });

        if(isadmin) {
            holder.add_button.setVisibility(View.VISIBLE);
            holder.view.setVisibility(View.VISIBLE);
            holder.imageRemovedata.setVisibility(View.VISIBLE);
        }
        else {
            holder.add_button.setVisibility(View.GONE);
            holder.view.setVisibility(View.GONE);
            holder.imageRemovedata.setVisibility(View.GONE);
        }

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
            else if(startDate.before(endDate))
                return 6;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Math.abs(diffDays);
    }
    public void selectAll(){
        is_selected=true;
        notifyDataSetChanged();
    }
    public void unselectall(){
        is_selected=false;
        notifyDataSetChanged();
    }
    public void remove(Excel_data key){
        int actualPosition=list.indexOf(key);
        list.remove(actualPosition);
        notifyItemRemoved(actualPosition);
        notifyItemRangeChanged(actualPosition, list.size());
    }
    public void RecyclerView_delete(List<Excel_data> delete_list) {
        Toast.makeText(context, "Deleting selected data...", Toast.LENGTH_SHORT).show();
        dialog.dismiss();

        Dialog dialog1 = new Dialog(context);
        dialog1.setCancelable(false);
        dialog1.setContentView(R.layout.loading_dialog);
        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LottieAnimationView lottieAnimationView = dialog1.findViewById(R.id.animate);
        lottieAnimationView.setAnimation("done.json");
        dialog1.show();
        // create a new Gson instance
        Gson gson = new Gson();
        // convert your list to json
        String jsonExcelList = gson.toJson(delete_list);
        // print your generated json
        Log.e("jsonCartList: " , jsonExcelList);

        String prev_keygen=delete_list.get(0).getB()+"-"+delete_list.get(0).getE();

        String URL = "https://script.google.com/macros/s/"
                + gsID+"/exec?"
                +"data="+jsonExcelList
                +"&keygen="+hashGenerator(prev_keygen)
                +"&action=deleteData";

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String code="";
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            code=jsonObj.get("code")+"";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(code.equals("202")){
                            reference.child(delete_list.get(0).getPushkey()).removeValue();
                            int actualPosition=list.indexOf(delete_list.get(0));
                            list.remove(actualPosition);
                            notifyItemRemoved(actualPosition);
                            notifyItemRangeChanged(actualPosition, list.size());
                            dialog1.dismiss();
                            Toast.makeText(context, "Data deleted.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            dialog1.dismiss();
                            Toast.makeText(context, "Failed to delete. Try again!", Toast.LENGTH_SHORT).show();
                        }
                        Log.e("BULK code", response +"");
                        Log.e("BULK response",response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // enjoy your error status
                Log.e("Status of code = ","Wrong");
                dialog1.dismiss();
                Toast.makeText(context, "Failed to delete. Try again!", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }
    protected String hashGenerator(String str_hash) {
        // TODO Auto-generated method stub
        StringBuffer finalString=new StringBuffer();
        finalString.append(str_hash);
        //		logger.info("Parameters for SHA-512 : "+finalString);
        String hashGen=finalString.toString();
        StringBuffer sb = null;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
            md.update(hashGen.getBytes());
            byte byteData[] = md.digest();
            //convert the byte to hex format method 1
            sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sb.toString();
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewTitle,add_button,message;
        TextView textViewBody,view,last_date;
        TextView day_left,number;
        TextView Rm,mcrc,crime_no,case_no,pr_case_no,name,receiving_date;
        ConstraintLayout layout;
        LinearLayout layout_details;
        ImageView tick,type,imageRemovedata,notified,seen, share;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.station_name);//
            textViewBody = itemView.findViewById(R.id.dist_name);//
            add_button = itemView.findViewById(R.id.textView21);
            Rm = itemView.findViewById(R.id.name_rm);//
            day_left = itemView.findViewById(R.id.day_left);//
            mcrc = itemView.findViewById(R.id.case_type);//
            crime_no = itemView.findViewById(R.id.crime_no);//
            case_no = itemView.findViewById(R.id.case_no);//
            pr_case_no = itemView.findViewById(R.id.pr_case_no);//
            tick = itemView.findViewById(R.id.imageView2);
            layout = itemView.findViewById(R.id.layout);
            number=itemView.findViewById(R.id.num);
            layout_details = itemView.findViewById(R.id.linearLayout3);
            type = itemView.findViewById(R.id.type);
            name = itemView.findViewById(R.id.person_name);//
            receiving_date = itemView.findViewById(R.id.receiving_date);//
            view = itemView.findViewById(R.id.view);//
            last_date = itemView.findViewById(R.id.last_date);//
            imageRemovedata = itemView.findViewById(R.id.imageRemoveImage);//
            share = itemView.findViewById(R.id.share);
            seen = itemView.findViewById(R.id.seen);
            message = itemView.findViewById(R.id.message);
            notified = itemView.findViewById(R.id.notified);
        }
    }
}
