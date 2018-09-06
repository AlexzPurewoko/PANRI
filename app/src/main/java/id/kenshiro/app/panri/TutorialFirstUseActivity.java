package id.kenshiro.app.panri;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.mylexz.utils.MylexzActivity;

import id.kenshiro.app.panri.page_fragment_first_usage.FragmentFirstUse1;
import id.kenshiro.app.panri.page_fragment_first_usage.FragmentFirstUse2;
import id.kenshiro.app.panri.page_fragment_first_usage.FragmentFirstUse3;
import id.kenshiro.app.panri.page_fragment_first_usage.FragmentFirstUse4;

public class TutorialFirstUseActivity extends MylexzActivity {
    FragmentTransaction fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acttutor_main);
        fragmentManager = getSupportFragmentManager().beginTransaction();
        fragmentManager.replace(R.id.acttutor_id_placeholder, new FragmentFirstUse4());
        fragmentManager.commit();
    }

}
