package com.taxialeairy.provider;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.annotation.TargetApi;
import android.os.Build;
import android.util.Property;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import static android.view.FrameMetrics.ANIMATION_DURATION;

public class MarkerAnimation {
    private static Animator animator;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void animateMarkerToICS(Marker marker, LatLng finalPosition, final LatLngInterpolator latLngInterpolator) {

        TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
            @Override
            public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                return latLngInterpolator.interpolate(fraction, startValue, endValue);
            }
        };
        Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");

        // ADD THIS TO STOP ANIMATION IF ALREADY ANIMATING TO AN OBSOLETE LOCATION
        if (animator != null && animator.isRunning()) {
            animator.cancel();
            animator = null;
        }
        animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, finalPosition);
        animator.setDuration((long) ANIMATION_DURATION);
        animator.start();
    }
}
