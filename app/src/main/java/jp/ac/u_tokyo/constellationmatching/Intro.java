package jp.ac.u_tokyo.constellationmatching;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class Intro extends AppCompatActivity {
    private Handler mHandler = new Handler();
    private Runnable updateText;
    private final static int requestCodeFromLogin = 1002;
    Intent login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

        login = new Intent(this, LogIn.class);


        updateText = new Runnable() {
            public void run() {
                mHandler.removeCallbacks(updateText);

                startActivityForResult(login, requestCodeFromLogin);

            }
        };
        mHandler.postDelayed(updateText, 2000);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == requestCodeFromLogin) {
            if (resultCode == Activity.RESULT_OK) {
                String name = intent.getStringExtra("name");
                Intent returnIntent = new Intent();
                returnIntent.putExtra("name", name);
                setResult( Activity.RESULT_OK, returnIntent );
                finish();


            }
        }
    }




}
