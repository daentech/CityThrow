package uk.co.daentech.citythrow;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MyLocationOverlay extends ItemizedOverlay<OverlayItem> {
    private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
    private CityThrowActivity mContext;
    
    public MyLocationOverlay(Drawable defaultMarker) {
        super(boundCenterBottom(defaultMarker));
    }
    
    public MyLocationOverlay(Drawable defaultMarker, CityThrowActivity context) {
        super(boundCenterBottom(defaultMarker));
        mContext = context;
      }

    public void addOverlay(OverlayItem overlay) {
        mOverlays.add(overlay);
        populate();
    }
    
    public void addCharacter(Character c){
        OverlayItem item = new OverlayItem(c.getPoint(), c.getName(), c.getText());
        
        
        item.setMarker(getMarker(c));
        
        mOverlays.add(item);
        
        populate();
    }
    
    private Drawable getMarker(Character c) {
        Drawable marker=mContext.getResources().getDrawable(c.getMarker());
        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
        boundCenterBottom(marker);
        return marker;
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
      Character c = CityThrowActivity.characters.get(index);
      mContext.setSelectedCharacter(c);
      AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
      dialog.setTitle(item.getTitle());
      dialog.setMessage(item.getSnippet() + "\nHP: " + c.getHP() + "/100");
      if (c.getType() == Character.type.ME){
          // if me, do this
          dialog.setNegativeButton("Cancel", mContext);
      } else if (c.getType() == Character.type.FOE){
          // Show attack buttons
          dialog.setPositiveButton("Attack!", mContext);
          dialog.setNegativeButton("Cancel", mContext);
      } else if (c.getType() == Character.type.FRIEND){
          // friend
          dialog.setNegativeButton("Cancel", mContext);
      } else {
          dialog.setMessage("This is neither a friend nor foe... what did you click?");
      }
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
    
    public void updateLocation(Character c){
        OverlayItem item = new OverlayItem(c.getPoint(), c.getName(), c.getText());
        item.setMarker(getMarker(c));
        int i;
        for (i = 0; i < mOverlays.size(); i++){
            if (mOverlays.get(i).getTitle().equals(c.getName())){
                break;
            }
        }
        
        mOverlays.set(i, item);
        populate();
    }
}
