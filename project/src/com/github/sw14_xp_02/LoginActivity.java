package com.github.sw14_xp_02;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener{
	
	private Button login;
	private EditText email;
	private EditText password;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.login = (Button) this.findViewById(R.id.login_button);
        this.email = (EditText) this.findViewById(R.id.email_edit);
        this.password = (EditText) this.findViewById(R.id.password_edit);
        
        this.login.setOnClickListener(this);
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		Button clicked = (Button) v;
		if (clicked.getId() == this.login.getId())
		{
			login();
		}
		
		
		
	}
	
	 public void login(){
	      if(email.getText().toString().equals("admin") && 
	      password.getText().toString().equals("admin")){
	      Toast.makeText(getApplicationContext(), "Redirecting...", 
	      Toast.LENGTH_SHORT).show();
	      Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
	      startActivity(intent);
	      
	   }	
	   else{
	      Toast.makeText(getApplicationContext(), "Wrong Credentials",
	      Toast.LENGTH_SHORT).show();
	      
	      }
	 }
    
}
