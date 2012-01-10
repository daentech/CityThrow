package uk.co.daentech.citythrow;

import java.util.Random;

import uk.co.daentech.citythrow.Character.type;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;

/***
 * The character class. Stores both friend and foe.
 * Stores current stats for this player, such as location, hp etc. 
 * @author ddg
 *
 */

public class Character {

    private int hp;
    private GeoPoint p;
    private String name, text;
    private static GeoPoint playerPosition;
    private int drawable;
    private Context mContext;
    private type mType;
    
    public enum type { ME, FRIEND, FOE }
    
    public Character (type t, String name, String text, GeoPoint p, int drawable, Context context){
        
        mType = t;
        if (t == type.ME){
            playerPosition = this.p;
        }
        
        if (p  == null){
            // Randomise the location of the new person within a certain radius of the player
            // If null, then we are generating a player rather than getting a real person from the web
            int lng, lat;
            Random rnd = new Random();
            
                lng = rnd.nextBoolean() ? playerPosition.getLongitudeE6() + (int)(rnd.nextFloat() * 400) : playerPosition.getLongitudeE6() - (int)(rnd.nextFloat() * 400);
                lat = rnd.nextBoolean() ? playerPosition.getLatitudeE6() + (int)(rnd.nextFloat() * 400) : playerPosition.getLatitudeE6() - (int)(rnd.nextFloat() * 400);
            this.p = new GeoPoint(lng,lat);
        } else {
            this.p = p;
        }
        
        this.hp = 100;
        this.name = name;
        this.text = text;
        this.drawable = drawable;
        this.mContext = context;
    }
    
    public GeoPoint getPoint(){
        return this.p;
    }
    
    public void updatePoint(GeoPoint p){
        this.p = p;
    }
    
    public int getHP(){
        return this.hp;
    }
    
    public void updateHP(int difference){
        this.hp -= difference;
    }
    
    public String getName(){
        return this.name;
    }
    
    public String getText(){
        return this.text;
    }

    public int getMarker() {
        return(drawable);
    }

    public type getType() {
        return this.mType;
    }
}
