package com.example.androidautoclicker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayout;
    private Point point;
    private MyAccessibilityService myAccessibilityService;
    private Button button;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // checkAccessibilityServicePermission();

        button = findViewById(R.id.startButton);
        button.setOnClickListener(v -> {
            Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(myIntent);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || Settings.canDrawOverlays(this)) {
                Log.d("button clicked", "service start");
                serviceIntent = new Intent(this, FloatingAutoClickService.class);
                startService(serviceIntent);
                onBackPressed();
        }
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || Settings.canDrawOverlays(this)) {
//                Log.d("button clicked", "service start");
//                serviceIntent = new Intent(MainActivity.this, FloatingAutoClickService.class);
//                startService(serviceIntent);
//                onBackPressed();
//            } else {
//                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//                startActivity(myIntent);
//
//                Toast.makeText(this, "You need System Alert Window Permission to do this", Toast.LENGTH_SHORT);
//            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceIntent == null) {
            stopService(serviceIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAccessibilityServicePermission();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
//           Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//           startActivity(myIntent);
//        }
    }

    public void checkAccessibilityServicePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int access = 0;
            try{
                access = Settings.Secure.getInt(this.getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            } catch (Settings.SettingNotFoundException e){
                e.printStackTrace();
            }
            if (access == 0) {
                Intent myIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(myIntent);
                Log.d("checkAccessibilityServicePermission", "ask for access");
            }
        }
    }
}