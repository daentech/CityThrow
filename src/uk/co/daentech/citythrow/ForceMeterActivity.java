package uk.co.daentech.citythrow;

import java.util.Random;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    
    private TextView angleText;
    
    public static int sAttackForce;
	public static double sAttackAngle;
	
    private float[] mAccelV = null;
    private float[] mMagnetV = null;
    private float maxForce = 0;

	private double mAttackAngle;
	private double mWindStrength;
    
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
        mAngle = (Math.atan2(dlat, dlon)) % (Math.PI * 2); // in radians. //Subtract PI/2 to correct for north being 0 on compass
        
        TextView tv = (TextView)findViewById(R.id.nameTextView);
        tv.setText("" + mChar.getName());
        tv = (TextView)findViewById(R.id.hpTextView);
        tv.setText(mChar.getHP() + "/100");
        tv = (TextView)findViewById(R.id.distanceTextView);
        tv.setText(getDistance());
        ProgressBar pb = (ProgressBar)findViewById(R.id.windProgress);
        pb.setProgress(getWind());
        //angleText = (TextView)findViewById(R.id.angleText);
        
        Button btn = (Button)findViewById(R.id.btnAttackLaunch);
        btn.setOnClickListener(this);
        angleBar = (ProgressBar)findViewById(R.id.directionProgress);
        
        
    }

    private int getWind() {
		Random rnd = new Random();
		mWindStrength = (rnd.nextFloat() - 0.5) * 2;
		return  (int)(mWindStrength*1000) + 500;
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
        
        return (int)distance + "m";
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
        else if (progressDialog != null && !progressDialog.isShowing()) Log.d("Orientation:",val[0] + "");
        
        double diffAngle = (( mAngle - val[0] + Math.PI) % (Math.PI * 2)) - Math.PI ;

        if (!sensing){
        	mAttackAngle = val[0];
            angleBar.setProgress((int)(scaleAngle(diffAngle,1000)));
            //angleText.setText("Compass Angle: " + val[0] + "\nExpected Angle: " + mAngle + "\nDifference: " + diffAngle +"\nValue: " + angleBar.getProgress());
        } else {
        	// Check we have the dialog open
        	if (progressDialog.isShowing()){
        		float currForce = (mAccelV[0] * mAccelV[0] + mAccelV[1] * mAccelV[1] + mAccelV[2] * mAccelV[2])
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        		Log.d("CurrForce:", currForce + "");
        		maxForce = Math.max(maxForce, currForce);
        		if (maxForce > 5 && currForce < 2){
        			// Assume swing finished
        			progressDialog.setProgress((int)(1000/20 * maxForce));
        			ForceMeterActivity.sAttackAngle = mAttackAngle;
        			ForceMeterActivity.sAttackForce = (int) (1000/20 * maxForce);
        			Intent mIntent = new Intent();
        			mIntent.putExtra("AttackAngle", mAttackAngle);
        			mIntent.putExtra("AttackForce", progressDialog.getProgress());
        			mIntent.putExtra("WindStrength", mWindStrength);
        			progressDialog.dismiss();
        			setResult(RESULT_OK, mIntent);
        			this.finish();
        		}
        		
        	}
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
        	maxForce = 0;
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
        
        return 500 - a * scalar;
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.optionsmenu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.instructionsoption:
	        startActivity(new Intent(this,InstructionsActivity.class));
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}
