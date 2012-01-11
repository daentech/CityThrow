package uk.co.daentech.citythrow;

import java.util.Random;

import android.location.Location;

import com.google.android.maps.GeoPoint;

/***
 * The character class. Stores both friend and foe.
 * Stores current stats for this player, such as location, hp etc. 
 * @author ddg
 *
 */

public class Character {

    private static final double SPREAD_RADIUS = 1E5;
    private int hp;
    private GeoPoint p;
    private String name, text;
    private static GeoPoint playerPosition;
    private int drawable;
    private type mType;
    
    public enum type { ME, FRIEND, FOE }
    
    public Character (type t, String name, String text, GeoPoint p, int drawable){
        
        mType = t;
        if (t == type.ME){
            Character.playerPosition = p;
        }
        
        if (p  == null){
            // Randomise the location of the new person within a certain radius of the player
            // If null, then we are generating a player rather than getting a real person from the web
            int lng, lat;
            Random rnd = new Random();
            lng = rnd.nextBoolean() ? Character.playerPosition.getLongitudeE6() + (int)(rnd.nextFloat() * SPREAD_RADIUS) : Character.playerPosition.getLongitudeE6() - (int)(rnd.nextFloat() * SPREAD_RADIUS);
            lat = rnd.nextBoolean() ? Character.playerPosition.getLatitudeE6() + (int)(rnd.nextFloat() * SPREAD_RADIUS) : Character.playerPosition.getLatitudeE6() - (int)(rnd.nextFloat() * SPREAD_RADIUS);
            this.p = new GeoPoint(lat,lng);
        } else {
            this.p = p;
        }
        
        this.hp = 100;
        this.name = name;
        this.text = text;
        this.drawable = drawable;
    }
    
    public GeoPoint getPoint(){
        return this.p;
    }
    
    public void updatePoint(GeoPoint p){
        if (this.mType == type.ME)
            Character.playerPosition = p;
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
    
    public double getDistanceTo(Character c){
        GeoPoint enemyposition = this.getPoint();
        GeoPoint userposition = c.getPoint();
        Location userLoc = new Location("User");
        userLoc.setLatitude(userposition.getLatitudeE6() / 1E6);
        userLoc.setLongitude(userposition.getLongitudeE6() / 1E6);
        Location enemyLoc = new Location("Enemy");
        enemyLoc.setLatitude(enemyposition.getLatitudeE6() / 1E6);
        enemyLoc.setLongitude(enemyposition.getLongitudeE6() / 1E6);
        return userLoc.distanceTo(enemyLoc);
        

    }
}
