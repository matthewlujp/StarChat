package jp.ac.u_tokyo.constellationmatching;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by luning on 2017/07/09.
 */

public class MessageAdapter extends ArrayAdapter<Message> {
    /* xmlファイルからレイアウトを読み込むための変数 */
    private LayoutInflater inflater;

    public MessageAdapter(Context context, List<Message> list) {
        super(context, 0, list);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        /* ビューを受け取る */
        View view = convertView;
        if (view == null) {
            /* 受け取ったビューがnullなら新しくビューを生成（cell_message.xmlを読み込み） */
            view = inflater.inflate(R.layout.cell_message, null);
        }
        /* 表示すべきデータの取得 */
        final Message item = this.getItem(position);
        if (item != null) {
            /* Viewの取得 */
            TextView cellName = (TextView) view.findViewById(R.id.cellName);
            TextView cellMessage = (TextView) view.findViewById(R.id.cellMessage);
            TextView cellDate = (TextView) view.findViewById(R.id.cellDate);

            /* 名前とメッセージを表示 */
            cellName.setText(item.name);
            cellMessage.setText(item.message);

            /* 日付をフォーマットして表示 */
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date(item.date);
            cellDate.setText(sdf.format(date));
        }
        return view;
    }
}
