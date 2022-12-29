package in.aryomtech.cgalert.duo_frags;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import in.aryomtech.cgalert.R;


public class about extends Fragment {

    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter ;
    TabLayout tabIndicator;
    Button btnNext;
    int position = 0 ;
    private Context contextNullSafe;
    FirebaseAuth auth;
    FirebaseUser user;
    Animation btnAnim ;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_about, container, false);
        if (contextNullSafe == null) getContextNullSafety();

        // ini views
        btnNext = view.findViewById(R.id.btn_next);
        tabIndicator = view.findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getContextNullSafety().getApplicationContext(),R.anim.button_animation);
        // fill list screen
        final List<ScreenItem> mList = new ArrayList<>();
       /* mList.add(new ScreenItem("","माननीय महाधिवक्ता सतिशचंद्र वर्मा तथा श्रीमान पुलिस महानिदेशक अशोक जुनेजा के सानिध्य में " +
                "स्थापित पुलिस लीगल सेक्शन,कैंप बिलासपुर के द्वारा कार्यालय को पेपरलेस बनाए जाने के प्रयास में इस एंड्रायड एप SANGYAN के " +
                "माध्यम से पुलिस थानों को तत्काल सूचना संप्रेषित करने  के उद्देश्य से बनाया गया है, ताकि केश- डायरियों को सही समय पर माननीय न्यायालय के समक्ष" +
                " प्रस्तुत किया जा सके।",R.drawable.adikari_photos));*/
        mList.add(new ScreenItem("Operations and Managed By:-\nPolice Legal Cell, Camp Bilaspur Advocate General Office, High Court Chhattisgarh","Incharge: DSP PANKAJ AWASTHI\nWorking Staff:-\n1) SI SHWETA MISHRA GOURAHA\n2) SI(T/C) ABHINAW VERMA\n3) CONS.496 RAKESH BHARDWAJ",R.drawable.cg_logo));
        mList.add(new ScreenItem("Man Behind The Idea\nSI(T/C) ABHINAW VERMA","Man Who guided the project from scratch to the final stage. His guidance overall made the project very efficient and easy to use.\n\nHelp & FAQ\nContact:- 8269737971\nEmail:-abhinawtheverma@gmail.com",R.drawable.cg_logo));
        mList.add(new ScreenItem("Submission Alert","The High Court needs case diaries to be submitted on time for case hearings and this app will help the court via alerting the Police Stations.",R.drawable.ic_onboarding_one));
        mList.add(new ScreenItem("How it works?","The color red indicates that the case diary is supposed to be submitted or withdrawn whereas the clock shows the number of days left for the same.",R.drawable.ic_onboarding_two));
        mList.add(new ScreenItem("Finally","Once the diary is submitted, it will be inspected by the respective judges of the court.",R.drawable.ic_onboarding_three));
        // setup viewpager
        screenPager =view.findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(getContextNullSafety(),mList);
        screenPager.setAdapter(introViewPagerAdapter);
        // setup tablayout with viewpager
        tabIndicator.setupWithViewPager(screenPager);
        // next button click Listner


        btnNext.setOnClickListener(v -> {
            position = screenPager.getCurrentItem();
            if (position < mList.size()) {
                position++;
                screenPager.setCurrentItem(position);
            }
            if (position == mList.size()-1) { // when we rech to the last screen
                // TODO : show the GETSTARTED Button and hide the indicator and the next button
                loaddLastScreen();
            }
        });
        // tablayout add change listener
        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == mList.size()-1) {
                    loaddLastScreen();
                }
                else{
                    btnNext.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // skip button click listener

        OnBackPressedCallback callback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                FragmentTransaction ft=fm.beginTransaction();
                if(fm.getBackStackEntryCount()>0) {
                    fm.popBackStack();
                }
                ft.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);


        return view;
    }

    // show the GETSTARTED Button and hide the indicator and the next button
    private void loaddLastScreen() {
        btnNext.setVisibility(View.INVISIBLE);
       // tabIndicator.setVisibility(View.INVISIBLE);
        // TODO : ADD an animation the getstarted button
        // setup animation
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
