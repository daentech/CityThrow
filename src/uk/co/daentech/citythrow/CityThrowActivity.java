package uk.co.daentech.citythrow;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import android.widget.TextView;

public class CityThrowActivity extends MapActivity implements LocationListener,
        OnClickListener {
    private static final int LAUNCH_ATTACK = 0;
    /** Called when the activity is first created. */
    private MapController mc;
    private float lng, lat;
    private GeoPoint p;
    private MyLocationOverlay myLocationOverlay;
    public static Character sSelectedCharacter;
    public static ArrayList<Character> characters;

    private TextView mAttackInfo;
    private static float sAttackAngle;
    private static int sAttackForce;
    private AttackOverlay mAttackOverlay;
    private List<Overlay> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mAttackInfo = (TextView) findViewById(R.id.txtShowAttackInfo);
        mAttackInfo.setVisibility(TextView.INVISIBLE);

        MapView mv = (MapView) findViewById(R.id.mapview);

        mv.setBuiltInZoomControls(true);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (loc == null) {
            loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (loc == null) {
            lng = 51.4557f;
            lat = -2.6028f;
        } else {
            lat = (float) loc.getLatitude();
            lng = (float) loc.getLongitude();
        }

        p = new GeoPoint((int) (lat * 1000000), (int) (lng * 1000000));
        mv.setSatellite(true);
        // get MapController that helps to set/get location, zoom etc.
        mc = mv.getController();
        mc.setCenter(p);
        mc.setZoom(14);

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 20.0f, this);

        characters = new ArrayList<Character>();

        myLocationOverlay = new MyLocationOverlay(getResources().getDrawable(
                R.drawable.marker), this);
        mAttackOverlay = new AttackOverlay(getResources().getDrawable(
                R.drawable.projectile), this);
        Character c = new Character(Character.type.ME, "This is my location",
                "I'm here!", p, R.drawable.mymarker);
        characters.add(c);
        myLocationOverlay.addCharacter(c);

        addDummyPeople();
        list = mv.getOverlays();

        list.add(myLocationOverlay);
    }

    private void addDummyPeople() {
        // If there are people, but the distance is too great then delete the
        // people and start again
        if (myLocationOverlay.size() > 1) {
            // We have numerous people
            myLocationOverlay.deleteEnemies();
        }
        // This method creates dummy people to throw objects at
        for (int i = 1; i < 11 - myLocationOverlay.size(); i++) {
            Character c = new Character(Character.type.FOE, "Enemy #" + i,
                    "Enemy!", null, R.drawable.marker);
            characters.add(c);
            myLocationOverlay.addCharacter(c);
        }
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    public void onLocationChanged(Location location) {
        if (location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            GeoPoint oldPosition = p;
            p = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
            characters.get(0).updatePoint(p);
            myLocationOverlay.updateLocation(characters.get(0));
            MapView mv = (MapView) findViewById(R.id.mapview);
            mv.invalidate();
            if (getDistance(oldPosition) > 500)
                addDummyPeople();
            mc.animateTo(p);
            mv.setSatellite(false);
            mv.preLoad();
        }
    }

    public void setSelectedCharacter(Character c) {
        sSelectedCharacter = c;
    }

    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
        case Dialog.BUTTON_NEGATIVE:
            break;
        case Dialog.BUTTON_POSITIVE:
            // This is the attack button
            if (sSelectedCharacter.getType() == Character.type.FOE) {
                // Prepare attack
                startActivityForResult(new Intent(this,
                        ForceMeterActivity.class), LAUNCH_ATTACK);
            }
            break;
        }
    }

    private double getDistance(GeoPoint oldPosition) {
        GeoPoint enemyposition = oldPosition;
        GeoPoint userposition = p;
        Location userLoc = new Location("User");
        userLoc.setLatitude(userposition.getLatitudeE6() / 1E6);
        userLoc.setLongitude(userposition.getLongitudeE6() / 1E6);
        Location enemyLoc = new Location("Enemy");
        enemyLoc.setLatitude(enemyposition.getLatitudeE6() / 1E6);
        enemyLoc.setLongitude(enemyposition.getLongitudeE6() / 1E6);
        double distance = userLoc.distanceTo(enemyLoc);

        return distance;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LAUNCH_ATTACK) {
            if (resultCode == RESULT_CANCELED)
                setTitle("OK");
            else if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    sAttackAngle = extras.getFloat("AttackAngle");
                    sAttackForce = extras.getInt("AttackForce");
                    mAttackInfo.setText("Attack Force: " + sAttackForce
                            + "\nAttack Angle: " + sAttackAngle);
                    mAttackInfo.setVisibility(TextView.VISIBLE);

                    new Thread(new Runnable() {
                        public void run() {
                            if (list.size() < 2)
                                list.add(mAttackOverlay);
                            mAttackOverlay
                                    .addItem(characters.get(0).getPoint());

                        }
                    }).start();

                }
            }
        }
    }
}