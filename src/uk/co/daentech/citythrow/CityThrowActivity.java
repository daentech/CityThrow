package uk.co.daentech.citythrow;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class CityThrowActivity extends MapActivity implements LocationListener{
    /** Called when the activity is first created. */
    MapController mc;
    float lng, lat;
    GeoPoint p;
    
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
        
        MyLocationOverlay myLocationOverlay = new MyLocationOverlay();

        List<Overlay> list = mv.getOverlays();

        list.add(myLocationOverlay);
        
        list = addDummyPeople(list);
        
    }

	private List<Overlay> addDummyPeople(List<Overlay> list) {
        // This method creates dummy people to throw objects at
	    
        return list;
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
            mc.animateTo(p);
        }
        
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
    
    class MyLocationOverlay extends com.google.android.maps.Overlay {
        @Override

        public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
        
            super.draw(canvas, mapView, shadow);
            Paint paint = new Paint();
            // Converts lat/lng-Point to OUR coordinates on the screen.
            Point myScreenCoords = new Point();
            mapView.getProjection().toPixels(p, myScreenCoords);
            paint.setStrokeWidth(1);
            paint.setARGB(255, 255, 255, 255);
            paint.setStyle(Paint.Style.STROKE);
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
            canvas.drawBitmap(bmp, myScreenCoords.x, myScreenCoords.y, paint);
            canvas.drawText("Here I am...", myScreenCoords.x, myScreenCoords.y, paint);
            return true;
        }
        
        @Override
        public boolean onTouchEvent(MotionEvent event, MapView mapView) 
        {   
            //---when user lifts his finger---
            if (event.getAction() == 1) {                
                GeoPoint p = mapView.getProjection().fromPixels(
                    (int) event.getX(),
                    (int) event.getY());
                    Toast.makeText(getBaseContext(), 
                        p.getLatitudeE6() / 1E6 + "," + 
                        p.getLongitudeE6() /1E6 , 
                        Toast.LENGTH_SHORT).show();
            }                            
            return false;
        }        

    }
    
}