package com.example.androidautoclicker;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        point = new Point(0, 0);
//        MyAccessibilityService myAccessibilityService = new MyAccessibilityService();
//        myAccessibilityService.onCreate();
        checkAccessibilityServicePermission();
    }

    public void checkAccessibilityServicePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int access = 0;
            try{
                access = Settings.Secure.getInt(this.getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            } catch (Settings.SettingNotFoundException e){
                e.printStackTrace();
                //put a Toast
            }
            if (access == 0) {
                Intent myIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(myIntent);
            }
        }
    }

}