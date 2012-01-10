package uk.co.daentech.citythrow;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
        Character c = new Character(Character.type.ME, "This is my location", "I'm here!", p, R.drawable.mymarker, this);
        characters.add(c);
        myLocationOverlay.addCharacter(c);
        
        addDummyPeople(myLocationOverlay);
        List<Overlay> list = mv.getOverlays();

        list.add(myLocationOverlay);        
    }

	private MyLocationOverlay addDummyPeople(MyLocationOverlay overlay) {
        // This method creates dummy people to throw objects at
        for (int i = 0; i < 10 - overlay.size(); i++){
            
        }
        return overlay;
    }

    @Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

    public void onLocationChanged(Location location) {
        if (location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            p = new GeoPoint((int) (lat * 1000000), (int) (lng * 1000000));
            characters.get(0).updatePoint(p);
            myLocationOverlay.updateLocation(characters.get(0));
            mc.animateTo(p);
            
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
                
            }
            break;
        }
    }
    
}