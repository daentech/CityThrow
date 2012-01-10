package uk.co.daentech.citythrow;

import android.app.Activity;
import android.os.Bundle;


/***
 * This class is used to read the motion of the user and detect the force with which the object is thrown
 * @author ddg
 *
 */
public class ForceMeterActivity extends Activity{
    
    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        
        //Get the character
        
        Character c = CityThrowActivity.sSelectedCharacter;
        
    }

}
