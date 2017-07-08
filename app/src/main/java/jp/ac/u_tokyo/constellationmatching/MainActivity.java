package jp.ac.u_tokyo.constellationmatching;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final int requestCodeFromLogin = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        Intent intro = new Intent(this, Intro.class);
        startActivityForResult(intro, requestCodeFromLogin);


    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == requestCodeFromLogin) {
            if (resultCode == Activity.RESULT_OK) {
                String name = intent.getStringExtra("name");
                Toast.makeText(this, name, Toast.LENGTH_LONG).show();
            }
        }
    }


}
