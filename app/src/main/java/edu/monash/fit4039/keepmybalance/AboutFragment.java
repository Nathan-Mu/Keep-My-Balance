package edu.monash.fit4039.keepmybalance;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by nathan on 11/6/17.
 */

public class AboutFragment extends Fragment {
    private View vAbout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vAbout = inflater.inflate(R.layout.fragment_about, container, false);
        return vAbout;
    }
}
