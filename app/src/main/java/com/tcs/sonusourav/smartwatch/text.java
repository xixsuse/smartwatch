package com.tcs.sonusourav.smartwatch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.shinelw.library.ColorArcProgressBar;

import javax.annotation.Nullable;

public class text extends AppCompatActivity {
    private Button cancelButton;
    private ColorArcProgressBar colorArcProgressBar;

 @Override
    protected void onCreate(@Nullable Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.history_listview);

        /*cancelButton=findViewById(R.id.heartbeat_cancel_btn);
        colorArcProgressBar=findViewById(R.id.bar);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorArcProgressBar.setCurrentValues(100);

            }
        });*/
    }
}
