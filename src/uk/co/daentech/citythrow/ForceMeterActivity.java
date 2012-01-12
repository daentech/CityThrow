package uk.co.daentech.citythrow;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;


/***
 * This class is used to read the motion of the user and detect the force with which the object is thrown
 * @author ddg
 *
 */
public class ForceMeterActivity extends Activity implements OnClickListener, SensorEventListener{
    
    private static final int DIALOG_SENSING_BEGUN = 0;
    private Character mChar;
    private boolean sensing = false;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnet;
    private double mAngle;
    private ProgressBar angleBar;
    private ProgressDialog progressDialog;
    private float lastAngle;
    private TextView angleText;
    private float[] angleHistory = new float[10];
    private int angleIndex;
    
    private float[] mAccelV = null;
    private float[] mMagnetV = null;
    
    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnet = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        
        setContentView(R.layout.enemyinfo);
        //Get the character
        
        mChar = CityThrowActivity.sSelectedCharacter;
        
        // Get the angle between the player and the enemy
        Character player = CityThrowActivity.characters.get(0);
        
        double dlat = mChar.getPoint().getLatitudeE6() - player.getPoint().getLatitudeE6();
        double dlon = mChar.getPoint().getLongitudeE6() - player.getPoint().getLongitudeE6();
        mAngle = Math.atan2(dlat, dlon); // in radians
        
        TextView tv = (TextView)findViewById(R.id.nameTextView);
        tv.setText("" + mChar.getName());
        tv = (TextView)findViewById(R.id.hpTextView);
        tv.setText(mChar.getHP() + "/100");
        tv = (TextView)findViewById(R.id.distanceTextView);
        tv.setText(getDistance());
        angleText = (TextView)findViewById(R.id.angleText);
        
        Button btn = (Button)findViewById(R.id.btnAttackLaunch);
        btn.setOnClickListener(this);
        angleBar = (ProgressBar)findViewById(R.id.directionProgress);
        
    }

    private String getDistance() {
        GeoPoint enemyposition = mChar.getPoint();
        GeoPoint userposition = CityThrowActivity.characters.get(0).getPoint();
        Location userLoc = new Location("User");
        userLoc.setLatitude(userposition.getLatitudeE6() / 1E6);
        userLoc.setLongitude(userposition.getLongitudeE6() / 1E6);
        Location enemyLoc = new Location("Enemy");
        enemyLoc.setLatitude(enemyposition.getLatitudeE6() / 1E6);
        enemyLoc.setLongitude(enemyposition.getLongitudeE6() / 1E6);
        double distance = userLoc.distanceTo(enemyLoc);
        
        return distance + "m";
    }

    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub
        
    }

    public void onSensorChanged(SensorEvent event) {
        switch(event.sensor.getType()){
        case Sensor.TYPE_ACCELEROMETER:
            mAccelV = event.values;
            //if(Math.abs(mAccelV[0]) > 1 || Math.abs(mAccelV[1]) > 1)
            //    System.out.println("Accelerometer Changed: " + mAccelV[0] + "," + mAccelV[1] + "," + mAccelV[2]);
            break;
        case Sensor.TYPE_MAGNETIC_FIELD:
        	mMagnetV = event.values;
            break;
        default:
            break;
        }
        if(mAccelV == null || mMagnetV == null) return;
        float R[] = new float[9];
        float val[] = new float[3];
        SensorManager.getRotationMatrix(R, null, mAccelV, mMagnetV);
        val = SensorManager.getOrientation(R,val);
        if (val[0] == 0.0) return;
        //angleHistory[angleIndex++ % 10] = val[0];
        
        double diffAngle = (( mAngle - val[0] + Math.PI ) %  2*Math.PI ) - Math.PI ;
        if (!sensing){
            angleBar.setProgress((int)(scaleAngle(diffAngle,1000)));
            angleText.setText("Angle Difference: " + diffAngle + " Value: " + angleBar.getProgress());
            }
    }

    public void onClick(View v) {
        switch(v.getId()){
        case R.id.btnAttackLaunch:
            //Launch dialog
            sensing = true;
            showDialog(DIALOG_SENSING_BEGUN);
            break;
        default:
            break;
        }
    }
    
    @Override
    public Dialog onCreateDialog(int dialog){
        
        switch(dialog){
        case DIALOG_SENSING_BEGUN:
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(1000);
            progressDialog.setMessage("Force...");
            progressDialog.setCancelable(true);
            return progressDialog;
        default:
            return null;
        }
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnet, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
    
    private double scaleAngle(double a, int max){
        double scalar = 1000/(2*Math.PI);
        
        return 500 + a * scalar;
    }
    
}
