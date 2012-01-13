package uk.co.daentech.citythrow;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class InstructionsActivity extends Activity implements OnClickListener{
	
	@Override
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		
        setContentView(R.layout.instructions);
        
        Button btnDone = (Button)findViewById(R.id.btnDone);
        btnDone.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btnDone:
			this.finish();
			break;
		default:
			break;
		}
		
	}

}
