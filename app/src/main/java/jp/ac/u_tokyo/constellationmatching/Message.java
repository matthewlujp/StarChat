package jp.ac.u_tokyo.constellationmatching;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by luning on 2017/07/09.
 */

public class Message {
    /* メンバ変数 */
    public String name;
    public String message;
    public long date;

    /* コンストラクタ */
    public Message(String name, String message, long date) {
        this.name = name;
        this.message = message;
        this.date = date;
    }

    /* JSONObjectを引数に取るコンストラクタ */
    public Message(JSONObject object) {
        this.name = object.optString("name");
        this.message = object.optString("message");
        this.date = object.optLong("date");
    }

    /* JSONArrayからMessageのArrayListを返すユーティリティ */
    public static ArrayList<Message> parse(JSONArray array) {
        ArrayList<Message> messageList = new ArrayList<Message>();
        for (int i=0; i<array.length(); i++) {
            Message message = new Message(array.optJSONObject(i));
            /* 今回は事情があってJSONとは逆順になるよう工夫 */
            messageList.add(0, message);
        }
        return messageList;
    }
}
