package com.taxialeairy.provider.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by Santosh Thorani
 * Android Developer
 * santosh.thorani@hotmail.com
 * on 9/12/2018.
 */
public class StaticMapFragment extends SupportMapFragment {
    public View mapView;
    public TouchableWrapper touchView;
    private StaticMapFragment.OnTouchListener listener;

    public static StaticMapFragment newInstance() {
        return new StaticMapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mapView = super.onCreateView(inflater, parent, savedInstanceState);
        // overlay a touch view on map view to intercept the event
        touchView = new TouchableWrapper(getActivity());
        touchView.addView(mapView);
        return touchView;
    }

    @Override
    public View getView() {
        return mapView;
    }

    public void setOnTouchListener(StaticMapFragment.OnTouchListener listener) {
        this.listener = listener;
    }

    public interface OnTouchListener {
        void onTouch();
    }

    public class TouchableWrapper extends FrameLayout {
        public TouchableWrapper(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (listener != null) {
                        listener.onTouch();
                    }
                    // consume event to prevent map from receiving the event
//                    return true;
            }
            return super.dispatchTouchEvent(event);
        }
    }
}
