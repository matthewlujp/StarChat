package jp.ac.u_tokyo.constellationmatching;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class LogIn extends AppCompatActivity implements View.OnClickListener{
    private EditText nameBox;
    private ImageButton completeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        nameBox = (EditText)findViewById(R.id.Name);
        completeButton = (ImageButton)findViewById(R.id.NextButton);
        completeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String name = nameBox.getText().toString();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("name", name);
        setResult( Activity.RESULT_OK, returnIntent );
        finish();
    }
}
