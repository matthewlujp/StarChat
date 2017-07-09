package jp.ac.u_tokyo.constellationmatching;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Chat extends AppCompatActivity {
    /* Viewを格納する変数 */
    private EditText editTextMessage;
    private ImageButton buttonSubmit;
    private ListView listView;

    private String userName = "";
    private String partnerName = "";

    private AQuery aQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        /* AQueryの初期化 */
        aQuery = new AQuery(this);

        /* それぞれの名前に対応するViewを取得する */
        editTextMessage = (EditText) findViewById(R.id.writeBox);
        buttonSubmit = (ImageButton) findViewById(R.id.messageSendButton);
        listView = (ListView) findViewById(R.id.listView);

        Intent receivedIntent = getIntent();
        userName = receivedIntent.getStringExtra("userName");
        partnerName = receivedIntent.getStringExtra("partnerName");

        /* クリックした時の動作を指定する */
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editTextMessage.getText().toString();
                if (message.isEmpty()) {
                    Toast.makeText(Chat.this, "メッセージを入力して下さい。", Toast.LENGTH_SHORT).show();
                } else {
                    /* メッセージを送信するメソッドを呼び出す */
                    submitMessage(message);
                }
            }
        });

        /*
        buttonLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMessage();
            }
        });
        */


        /* ListViewの項目をクリックした時の動作 */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // アイテムの取得
                MessageAdapter adapter = (MessageAdapter) parent.getAdapter();
                Message message = adapter.getItem(position);
                // 内容の表示
                //Toast.makeText(Chat.this, message.name + "：" + message.message, Toast.LENGTH_SHORT).show();
            }
        });
        // アプリ起動時に1回、メッセージを読み込む
        loadMessage();
    }

    private void loadMessage() {
        submitMessage("");
    }

    private void submitMessage(String message) {
        /* 読み込み完了時のコールバック（ここではJSONObjectの例を示したが、JSONArrayやStringも指定可能） */
        AjaxCallback<JSONObject> callback = new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject root, AjaxStatus status) {
                Toast.makeText(Chat.this, url, Toast.LENGTH_SHORT).show();
                /* HTTPステータスコードが200なら */
                if (status.getCode() == HttpStatus.SC_OK) {
                    JSONArray array = root.optJSONArray("chat");
                    ArrayList<Message> messageList = Message.parse(array);
                    MessageAdapter adapter = new MessageAdapter(Chat.this, messageList);
                    listView.setAdapter(adapter);
                }
            }
        };
        String url = String.format("%s?event=%s&src=%s&dst=%s&message=%s",
                MainActivity.serverUrl, "chat", userName, partnerName, message);
        callback.url(url);
        callback.method(AQuery.METHOD_GET);
        /*
        if (name != null && message != null) {
            callback.param("name", name);
            callback.param("message", message);
        }
        */
        callback.type(JSONObject.class);
        //callback.progress(new ProgressDialog(this));
        aQuery.ajax(callback);
    }
}
