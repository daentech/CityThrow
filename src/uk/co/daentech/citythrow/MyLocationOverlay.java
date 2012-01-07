package uk.co.daentech.citythrow;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MyLocationOverlay extends ItemizedOverlay<OverlayItem> {
    private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
    private Context mContext;
    
    public MyLocationOverlay(Drawable defaultMarker) {
        super(boundCenterBottom(defaultMarker));
    }
    
    public MyLocationOverlay(Drawable defaultMarker, Context context) {
        super(boundCenterBottom(defaultMarker));
        mContext = context;
      }

    public void addOverlay(OverlayItem overlay) {
        mOverlays.add(overlay);
        populate();
    }
    
    /*@Override
    public boolean onTouchEvent(MotionEvent event, MapView mapView) 
    {   
        //---when user lifts his finger---
        if (event.getAction() == 1) {                
            GeoPoint p = mapView.getProjection().fromPixels(
                (int) event.getX(),
                (int) event.getY());
                Toast.makeText(mContext, 
                    p.getLatitudeE6() / 1E6 + "," + 
                    p.getLongitudeE6() /1E6 , 
                    Toast.LENGTH_SHORT).show();
        }                            
        return false;
    }*/

    @Override
    protected OverlayItem createItem(int i) {
        return mOverlays.get(i);
    }
    
    @Override
    protected boolean onTap(int index) {
      OverlayItem item = mOverlays.get(index);
      AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
      dialog.setTitle(item.getTitle());
      dialog.setMessage(item.getSnippet());
      dialog.show();
      return true;
    }

    @Override
    public int size() {
        return mOverlays.size();
    }
    
    @Override
    public void draw(Canvas canvas, MapView mapView,
                                        boolean shadow) {
        super.draw(canvas, mapView, shadow);

    }
    
    public void setMyLocation(GeoPoint p){
        OverlayItem item = new OverlayItem(p,"My Location", "I'm here!");
        
        item.setMarker(getMarker(R.drawable.mymarker));
        if (mOverlays.size() > 0){
            mOverlays.set(0, item);
        } else {
            mOverlays.add(item);
        }
        populate();
    }
    
    public Drawable getMarker(int resource) {
        Drawable marker=mContext.getResources().getDrawable(resource);

        marker.setBounds(0, 0, marker.getIntrinsicWidth(),
                                            marker.getIntrinsicHeight());
        boundCenterBottom(marker);

        return(marker);
    }
}
