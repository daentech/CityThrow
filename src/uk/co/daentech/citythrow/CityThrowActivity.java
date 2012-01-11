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

public class CityThrowActivity extends MapActivity implements LocationListener, OnClickListener{
    /** Called when the activity is first created. */
    private MapController mc;
    private float lng, lat;
    private GeoPoint p;
    private MyLocationOverlay myLocationOverlay;
    public static Character sSelectedCharacter;
    public static ArrayList<Character> characters;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        MapView mv = (MapView)findViewById(R.id.mapview);
        
        mv.setBuiltInZoomControls(true);
        
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 20.0f, this);
        
        p = new GeoPoint((int) (lat * 1000000), (int) (lng * 1000000));
        mv.setSatellite(true);
        //get MapController that helps to set/get location, zoom etc.
        mc = mv.getController();
        mc.setCenter(p);
        mc.setZoom(14);
        
        characters = new ArrayList<Character>();
        
        myLocationOverlay = new MyLocationOverlay(getResources().getDrawable(R.drawable.marker),this);
        Character c = new Character(Character.type.ME, "This is my location", "I'm here!", p, R.drawable.mymarker);
        characters.add(c);
        myLocationOverlay.addCharacter(c);
        
        addDummyPeople();
        List<Overlay> list = mv.getOverlays();

        list.add(myLocationOverlay);        
    }

	private void addDummyPeople() {
	    // If there are people, but the distance is too great then delete the people and start again
	    if(myLocationOverlay.size() > 1){
	        // We have numerous people
            myLocationOverlay.deleteEnemies();
	    }
        // This method creates dummy people to throw objects at
        for (int i = 1; i < 11 - myLocationOverlay.size(); i++){
            Character c = new Character(Character.type.FOE, "Enemy #" + i, "Enemy!", null, R.drawable.marker);
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
            if (getDistance(oldPosition) > 500)
                addDummyPeople();
            mc.animateTo(p);
            MapView mv = (MapView)findViewById(R.id.mapview);
            mv.invalidate();
        }
        
    }

    public void setSelectedCharacter(Character c){
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
        switch (which){
        case Dialog.BUTTON_NEGATIVE:
            break;
        case Dialog.BUTTON_POSITIVE:
            // This is the attack button
            if (sSelectedCharacter.getType() == Character.type.FOE){
                // Prepare attack
                startActivity(new Intent(this, ForceMeterActivity.class));
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
}