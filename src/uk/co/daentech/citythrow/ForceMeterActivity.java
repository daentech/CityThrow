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
    
    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        setContentView(R.layout.enemyinfo);
        //Get the character
        
        mChar = CityThrowActivity.sSelectedCharacter;
        
        TextView tv = (TextView)findViewById(R.id.nameTextView);
        tv.setText("" + mChar.getName());
        tv = (TextView)findViewById(R.id.hpTextView);
        tv.setText(mChar.getHP() + "/100");
        tv = (TextView)findViewById(R.id.distanceTextView);
        tv.setText(getDistance());
        
        Button btn = (Button)findViewById(R.id.btnAttackLaunch);
        btn.setOnClickListener(this);
        
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
        if(!sensing) return;
        switch(event.sensor.getType()){
        case Sensor.TYPE_ACCELEROMETER:
            float v[] = event.values;
            if(Math.abs(v[0]) > 1 || Math.abs(v[1]) > 1)
                System.out.println("Accelerometer Changed: " + v[0] + "," + v[1] + "," + v[2]);
            break;
        case Sensor.TYPE_MAGNETIC_FIELD:
            break;
        default:
            break;
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
            ProgressDialog progressDialog;
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("Force...");
            progressDialog.setCancelable(false);
            return progressDialog;
        default:
            return null;
        }
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
