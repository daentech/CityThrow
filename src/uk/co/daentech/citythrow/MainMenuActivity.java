package uk.co.daentech.citythrow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MainMenuActivity extends Activity implements OnClickListener{
	
	
	@Override
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		setContentView(R.layout.mainmenu);
		
		ImageView iv = (ImageView)findViewById(R.id.start);
		iv.setOnClickListener(this);
		iv = (ImageView)findViewById(R.id.instructions);
		iv.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		
		return false;
	}
	
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.start:
			startActivity(new Intent(this,CityThrowActivity.class));
			break;
		case R.id.instructions:
			startActivity(new Intent(this,InstructionsActivity.class));
			break;
		default:
			break;
		}
		
	}
}
