package in.aryomtech.cgalert;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

import in.aryomtech.cgalert.Fragments.Mcrc_Rm_Coll;
import in.aryomtech.cgalert.Fragments.total_data;

public class ViewPagerAdaptertask extends FragmentStateAdapter {

    public ViewPagerAdaptertask(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }
    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {

        if (position == 1) {
            return new total_data();
        }

        return new Mcrc_Rm_Coll();
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
