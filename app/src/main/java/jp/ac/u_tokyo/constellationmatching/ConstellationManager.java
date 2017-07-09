package jp.ac.u_tokyo.constellationmatching;

import android.app.Activity;
import android.util.Log;
import android.util.Xml;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.apache.http.HttpStatus;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by luning on 2017/07/09.
 */

public class ConstellationManager {
    public final static ArrayList<Star> measureStars = new ArrayList<Star>() {
        {
            //add(new Star("Rotanev", "いるか座", 261.47573817446346, 32.521375465151735));
            //add(new Star("Vega", "琴座", 301.36144795708157, 24.13099345142644));
            //add(new Star("Deneb", "白鳥座", 291.71982362129893, 42.77042586109796));
            //add(new Star("Sham", "矢座", 274.09168078078613, 23.689033052962305));
            add(new Star("Eltanin", "竜座", 317.31129284930375, 25.205979092688455));
            //add(new Star("Altair", "鷲座", 264.62593387384345, 20.05948531287531));                                                          \
        }
    };
    private final static String BASIC_API_URL = "http://www.walk-in-starrysky.com/star.do";
    private boolean didObtainStarInfo;
    private ArrayList<Star> starList;
    private AQuery aquery;
    private Activity activity;


    public ConstellationManager(Activity activity) {
        this.starList = new ArrayList<>();
        this.didObtainStarInfo = false;
        this.activity = activity;
        this.aquery = new AQuery(activity);
    }

    public void obtainStarInfo(double longitude, double latitude, Calendar Calendar) {
        asyncRequest(longitude, latitude, Calendar);
    }

    private String buildStarAPIURL(double longitude, double latitude, Calendar Calendar) {
        String url = BASIC_API_URL + "?";
        url += "cmd=display";
        url += String.format("&longitude=%s", longitude);
        url += String.format("&latitude=%s", latitude);
        url += String.format("&year=%d", Calendar.get(Calendar.YEAR));
        url += String.format("&month=%d", Calendar.get(Calendar.MONTH));
        url += String.format("&day=%d", Calendar.get(Calendar.DAY_OF_MONTH));
        url += String.format("&hour=%d", Calendar.get(Calendar.HOUR_OF_DAY));
        url += String.format("&minute=%d", Calendar.get(Calendar.MINUTE));
        url += String.format("&second=%d", Calendar.get(Calendar.SECOND));
        return url;
    }

    private void asyncRequest(double longitude, double latitude, Calendar Calendar) {
        didObtainStarInfo = false;
        AjaxCallback<String> callback = new AjaxCallback<String>() {
            @Override
            public void callback(String url, String res, AjaxStatus status) {
                if (status.getCode() == HttpStatus.SC_OK) {
                    // Log.d("api debug", res);

                    starList.clear();

                    // Not sure what's in the payload
                    try {
                        XmlPullParser xmlParser = Xml.newPullParser();
                        xmlParser.setInput(new StringReader(res));

                        int eventType = xmlParser.getEventType();
                        boolean inStarTag = false;
                        String currentTag = "";
                        String starName = null;
                        double direction = 1000;   /* Large number to represent */
                        double altitude = 1000;   /* Large number to represent */

                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            if (eventType == xmlParser.START_TAG) {
                                //Log.d("api start", xmlParser.getName() + "  out");
                                if (xmlParser.getName().equals("star")) {
                            /* Reset */
                                    inStarTag = true;
                                    starName = null;
                                    direction = 1000;
                                    altitude = 1000;
                                } else if (inStarTag) {
                            /* If in Star tag */
                                    currentTag = xmlParser.getName();
                                    //Log.d("api start", currentTag);
                                    // Log.d("api start", currentTag);
                                }

                            } else if (eventType == xmlParser.TEXT) {
                                if (currentTag.equals("enName")) {
                                    //Log.d("api text", currentTag);
                                    starName = xmlParser.getText();
                                    break;
                                } else if (currentTag.equals("direction")) {
                                    //Log.d("api text", currentTag);
                                    direction = Double.parseDouble(xmlParser.getText());
                                    break;
                                } else if (currentTag.equals("altitude")) {
                                    //Log.d("api text", currentTag);
                                    altitude = Double.parseDouble(xmlParser.getText());
                                    break;
                                }
                                currentTag = "";

                            } else if (eventType == xmlParser.END_TAG) {
                                //Log.d("api end", xmlParser.getName());
                                if (xmlParser.getName().equals("star")) {
                                    // Whether to add to star list
                                    if (starName != null && direction < 360 && altitude < 90) {
                                        starList.add(new Star(starName, direction, altitude));
                                        Log.d("api debug", starList.get(starList.size() - 1).toString());
                                    }
                                    inStarTag = false;
                                }
                            }

                            eventType = xmlParser.next();
                        }
                    } catch (XmlPullParserException e) {
                        Log.w("api debug", e);
                    } catch (IOException e) {
                        Log.w("api debug", e);
                    }

                    didObtainStarInfo = true;
                }
            }
        };

        callback.url(buildStarAPIURL(longitude, latitude, Calendar));
        callback.method(AQuery.METHOD_GET);
        callback.type(String.class);
        // callback.progress(new ProgressDialog(activity));
        aquery.ajax(callback);

    }
}
