package uk.co.daentech.citythrow;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class AttackOverlay extends ItemizedOverlay<OverlayItem> {
    
    ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

    public AttackOverlay(Drawable marker, CityThrowActivity context) {
        super(marker);
        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
        boundCenterBottom(marker);
    }

    public void addItem(GeoPoint p){
        OverlayItem item = new OverlayItem(p, null, null);
        items.add(item);
        populate();
    }
    
    @Override
    protected OverlayItem createItem(int i) {
        return items.get(i);
    }

    @Override
    public int size() {
       return items.size();
    }
    
    public void updatePosition(GeoPoint p){
        items.clear();
        addItem(p);
    }

	public GeoPoint getProjectilePoint() {
		return items.get(0).getPoint();
	}

}
