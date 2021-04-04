package com.coming.customer.ui.map;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.coming.customer.R;
import com.coming.customer.core.LocationManager;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.CameraPosition;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import io.reactivex.disposables.Disposable;


/**
 * Created by hlink21 on 24/3/17.
 */

public class MapNavigator /*implements Consumer<Location> */ {

    LocationManager locationManager;
    Handler handler = new Handler();
    Runnable runnable;
    private Disposable disposable;
    private TypeEvaluator<LatLng> latLngTypeEvaluator = new TypeEvaluator<LatLng>() {
        @Override
        public LatLng evaluate(float v, LatLng fromLng, LatLng toLatLng) {
            return SphericalUtil.interpolate(fromLng, toLatLng, v);
        }
    };
    private Location lastLocation;
    private float lastHead;
    private HuaweiMap googleMap;
    private Context context;
    private Marker currentMarker;
    private double length = 0;


    private PathUpdateListener pathUpdateListener;

    private int carMarker = 0;
    private int stylistMarker;


    public MapNavigator(LocationManager locationManager, HuaweiMap googleMap, Context context) {
        this.locationManager = locationManager;
        this.googleMap = googleMap;
        this.context = context;
    }


    public void setCarMarker(int carMarker) {
        this.carMarker = carMarker;
    }


    public void startNavigation() {
        length = 0;
     /*   if (disposable == null)
            disposable = locationManager.getSubject().subscribe(this);*/
        locationManager.triggerLocation(new LocationManager.LocationListener() {
            @Override
            public void onLocationAvailable(LatLng latLng) {

            }


            @Override
            public void onFail(Status status) {

            }
        });
    }


    public void stopNavigation() {
        if (disposable != null)
            disposable.dispose();

        disposable = null;
    }


    public void setCurrentMarker(final Marker currentMarker) {
        this.currentMarker = currentMarker;
        this.currentMarker.setFlat(true);
        this.currentMarker.setAnchor(0.5f, 0.5f);

        CameraPosition build = CameraPosition.builder()
                .bearing(currentMarker.getRotation())
                .target(currentMarker.getPosition())
                .zoom(16).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(build));
        lastHead = googleMap.getCameraPosition().bearing;


    }


    public void setCurrentMarker(@DrawableRes int currentMarker) {
        this.stylistMarker = currentMarker;
    }


    public void addMarker(@NonNull Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        currentMarker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.
                fromResource(R.drawable.ic_driver_marker)).position(latLng));
        currentMarker.setPosition(latLng);
        currentMarker.setFlat(true);
        currentMarker.setAnchor(0.5f, 0.5f);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(14f));

        lastHead = googleMap.getCameraPosition().bearing;
    }


    public void accept(@NonNull Location location) throws Exception {
        /*if (currentMarker != null){
            animate(location);
            lastLocation = location;
        }*/

        if (currentMarker == null)
            addMarker(location);

        if (lastLocation == null || lastLocation.getLatitude() != location.getLatitude()) {
            animate(location);
            lastLocation = location;
        }


    }


    private LatLng locationToLatLng(Location location) {
        if (location != null)
            return new LatLng(location.getLatitude(), location.getLongitude());
        else return new LatLng(0.0, 0.0);
    }


    private void animate(@NonNull Location location) {
        if (lastLocation == null) {
            return;
        }

        final LatLng from = locationToLatLng(lastLocation);
        final LatLng to = locationToLatLng(location);

        // for not drawing path when route get change

        // update the current path if required
        if (pathUpdateListener != null)
            pathUpdateListener.updatePath(to);

        // length += SphericalUtil.computeLength(Arrays.asList(from, to));

        ValueAnimator valueAnimator = ValueAnimator.ofObject(latLngTypeEvaluator, from, to);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(2000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                final LatLng newLatLng = (LatLng) valueAnimator.getAnimatedValue();

                currentMarker.setPosition(newLatLng);

                final CameraPosition lastCameraPosition = googleMap.getCameraPosition();

                CameraPosition cameraPosition = CameraPosition.builder(lastCameraPosition)
                        .target(newLatLng)
                        .build();


                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition)
                        , 1000, null);
                //googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            }

        });
        valueAnimator.start();

        final float bearingTo = (float) SphericalUtil.computeHeading(from, to);
        ValueAnimator bearingAnimator = ValueAnimator.ofFloat(lastHead, bearingTo);
        bearingAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        bearingAnimator.setDuration(400);
        bearingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                final float value = (float) valueAnimator.getAnimatedValue();
                currentMarker.setRotation(value);


              /*  handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {*/
                CameraPosition cameraPosition = CameraPosition.builder(googleMap.getCameraPosition())
                        .bearing(value)
                        .build();

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null);
                // googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                  /*  }
                },1000);*/


            }
        });
        bearingAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                lastHead = bearingTo;

                CameraPosition cameraPosition = CameraPosition.builder(googleMap.getCameraPosition())
                        .build();

                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
        bearingAnimator.start();


    }


    public double getLength() {
        return length;
    }


    public void setLength(double length) {
        this.length = length;
    }


    public void setPathUpdateListener(PathUpdateListener pathUpdateListener) {
        this.pathUpdateListener = pathUpdateListener;
    }
}
