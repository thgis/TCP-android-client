package com.betterchat.www.utility;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class RetainedFragment extends Fragment {

    /**
     * Fragment initialization.  We way we want to be retained and
     * start our thread.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Tell the framework to try to keep this fragment around
        // during a configuration change.
        setRetainInstance(true);

        // Start up the worker
        
//        mThread.start();
    }

}
