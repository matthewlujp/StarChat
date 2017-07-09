package jp.ac.u_tokyo.constellationmatching;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {
    private final int requestCodeFromLogin = 1001;
    protected final static double RAD2DEG = 180/Math.PI;

    SensorManager sensorManager;
    ConstellationManager constellationManager;

    float[] rotationMatrix = new float[9];
    float[] gravity = new float[3];
    float[] geomagnetic = new float[3];
    float[] attitude = new float[3];

    private boolean isConstellationFound = false;

    /*
    TextView azimuthText;
    TextView pitchText;
    TextView rollText;
    */
    private ImageView dummyConstellation;
    private ImageView orionConstellation;
    private ImageView swanConstellation;
    private ImageView dracoConstellation;
    private TextView welcomeTextBox;

    TextView partnerNameBox;
    ImageButton chatStartButton;

    private AQuery aQuery;

    /* User info */
    private String userName = "";
    private int userSex = 0; /* male:0 female: 1 */
    private String partenerName = "";

    public static final String serverUrl = "https://script.google.com/macros/s/AKfycbx1A6vCyxNrtMTR2tt7wMB872ztTG6eUzbCefCZZFwXFZ-34jzt/exec";

    private Handler mHandler;
    private Runnable pollingRunner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        Intent intro = new Intent(this, Intro.class);
        startActivityForResult(intro, requestCodeFromLogin);

        aQuery = new AQuery(this);
        initSensor();
        mHandler = new Handler();

        dummyConstellation = (ImageView)findViewById(R.id.dummyImage);
        orionConstellation = (ImageView)findViewById(R.id.orionImage);
        swanConstellation = (ImageView)findViewById(R.id.swanImage);
        dracoConstellation = (ImageView)findViewById(R.id.dracoImage);
        partnerNameBox = (TextView) findViewById(R.id.chatPartnerName);
        chatStartButton = (ImageButton) findViewById(R.id.startChatButton);
        //welcomeTextBox = (TextView)findViewById(R.id.)

        chatStartButton.setOnClickListener(this);

        /* Add polling every second */
        pollingRunner = new Runnable() {
            public void run() {
                // Polling to the server
                polling(userName);
                //Toast.makeText(MainActivity.this, "asd", Toast.LENGTH_SHORT).show();

                mHandler.removeCallbacks(pollingRunner);
                mHandler.postDelayed(pollingRunner, 1000);
            }
        };
        mHandler.postDelayed(pollingRunner, 1000);

    }


    @Override
    public void onResume(){
        super.onResume();
        constellationManager = new ConstellationManager(this);

        //Calendar calender = Calendar.getInstance();
        /* ToDo: Get location from GPS */
        //constellationManager.obtainStarInfo(12, 22, calender);

        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);


    }


    @Override
    public void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, Chat.class);
        intent.putExtra("userName", userName);
        intent.putExtra("partnerName", partenerName);
        startActivity(intent);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == requestCodeFromLogin) {
            if (resultCode == Activity.RESULT_OK) {
                userName = intent.getStringExtra("name");
                //Toast.makeText(this, userName, Toast.LENGTH_LONG).show();
                String welcomeMessage = String.format("Welcome %s !\nLook up in the sky.", userName);

            }
        }
    }


    protected void initSensor(){
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onSensorChanged(SensorEvent event) {
        /* Ignore if a constellation has already been found. */
        if (isConstellationFound) return;

        switch(event.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                geomagnetic = event.values.clone();
                break;
            case Sensor.TYPE_ACCELEROMETER:
                gravity = event.values.clone();
                break;
        }

        if(geomagnetic != null && gravity != null){
            SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic);
            SensorManager.getOrientation(rotationMatrix, attitude);
            /*
            azimuthText.setText(Integer.toString( (int)(attitude[0] * RAD2DEG) +180));
            pitchText.setText(Integer.toString( (int)(attitude[1] * RAD2DEG) +90));
            rollText.setText(Integer.toString( (int)(attitude[2] * RAD2DEG)));
            */
        }

        ArrayList<Star> targetStars = ConstellationManager.measureStars;

        for (int i = 0; i < targetStars.size(); i++) {
            Star star = targetStars.get(i);
            double starAzimuth = star.getAzimuth();
            double starAltitude = star.getAltitude();

            if ((starAzimuth - 10) < (attitude[0] * RAD2DEG) + 180 && (attitude[0] * RAD2DEG) + 180 < (starAzimuth + 10) &&
                    (starAltitude - 5) < (attitude[1] * RAD2DEG) + 90 && (attitude[1] * RAD2DEG) + 90 < (starAltitude + 5)){
                Toast.makeText(this, star.getConstellation(), Toast.LENGTH_SHORT).show();
                submitConstellation(userName, star.getConstellation());
                isConstellationFound = true;

                /* Switch constellation */
                dummyConstellation.setVisibility(View.GONE);
                switch (star.getConstellation()) {
                    case "白鳥座":
                        swanConstellation.setVisibility(View.VISIBLE);
                        break;
                    case "竜座":
                        dracoConstellation.setVisibility(View.VISIBLE);
                        break;
                    default:
                        orionConstellation.setVisibility(View.VISIBLE);
                }

                break;
            }
        }
    }


    private void submitConstellation(String name, String constellation) {
        AjaxCallback<JSONObject> callback = new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject root, AjaxStatus status) {
                if (status.getCode() == HttpStatus.SC_OK) {
                    Log.d("constellation submit", url);
                    //Log.d("constellation submit", root.getString("content"));
                }
            }
        };
        /* URLとメソッドをセット */
        String url = String.format("%s?event=%s&name=%s&constellation=%s", serverUrl,"constellationFound", name, constellation);
        callback.url(url);
        callback.method(AQuery.METHOD_GET);
        // callback.param("name", name);
        // callback.param("constellation", constellation);
        callback.type(JSONObject.class);   /* コールバックの型 */
        //callback.progress(new ProgressDialog(this));   /* プログレスダイアログを指定 */
        aQuery.ajax(callback);
    }





    private void polling(String name) {
        /* 読み込み完了時のコールバック（ここではJSONObjectの例を示したが、JSONArrayやStringも指定可能） */
        AjaxCallback<JSONObject> callback = new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject root, AjaxStatus status) {
                /* HTTPステータスコードが200なら */
                //Toast.makeText(MainActivity.this, url, Toast.LENGTH_SHORT).show();
                if (isConstellationFound) {

                    if (status.getCode() == HttpStatus.SC_OK) {
                        JSONArray friends = root.optJSONArray("matching");
                        //Toast.makeText(MainActivity.this, friends.toString(), Toast.LENGTH_SHORT).show();
                        String strfriends = friends.toString();
                        strfriends = strfriends.substring(1, strfriends.length() - 1);
                        strfriends = strfriends.replace("\"", "");
                        String[] friendsArray = strfriends.toString().split(",");

                        /*
                        for (int i = 0; i < friendsArray.length; i++) {
                            String friendName = friendsArray[i];
                            Toast.makeText(MainActivity.this, friendName, Toast.LENGTH_SHORT).show();
                        }
                        */

                        // Chat with the first person
                        partenerName = friendsArray[0];

                        if (partenerName != "") {
                            chatStartButton.setVisibility(View.VISIBLE);
                            partnerNameBox.setVisibility(View.VISIBLE);
                            String chatPrompt = String.format("Let's connect with %s !", partenerName);
                            partnerNameBox.setText(chatPrompt);
                        }

                    }

                }

            }
        };

        String url = String.format("%s?event=%s&name=%s", serverUrl,"polling", name);
        callback.url(url);
        callback.method(AQuery.METHOD_GET);
        //callback.param("name", name);
        callback.type(JSONObject.class);
        // callback.progress(new ProgressDialog(this));
        aQuery.ajax(callback);
    }


//    private void submitMessage(String name, String message) {
//        /* 読み込み完了時のコールバック（ここではJSONObjectの例を示したが、JSONArrayやStringも指定可能） */
//        AjaxCallback<JSONObject> callback = new AjaxCallback<JSONObject>() {
//            @Override
//            public void callback(String url, JSONObject root, AjaxStatus status) {
//                /* HTTPステータスコードが200なら */
//                if (status.getCode() == HttpStatus.SC_OK) {
//                    JSONArray array = root.optJSONArray("chat");
//                    ArrayList<Message> messageList = Message.parse(array);
//                    MessageAdapter adapter = new MessageAdapter(MainActivity.this, messageList);
//                    listView.setAdapter(adapter);
//                }
//            }
//        };
//        /* URLとメソッドをセット */
//        callback.url(serverUrl);
//        callback.method(AQuery.METHOD_GET);
//
//        /* パラメータをセット */
//        if (name != null && message != null) {
//            callback.param("name", name);
//            callback.param("message", message);
//        }
//
//        /* コールバックの型 */
//        callback.type(JSONObject.class);
//        /* プログレスダイアログを指定 */
//        callback.progress(new ProgressDialog(this));
//        /* HTTP通信の実行 */
//        aQuery.ajax(callback);
//    }




}
