package com.example.androidautoclicker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PointF;
import android.net.Uri;
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
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d("onActivityResult", "onActivityResult called");

                    startService(serviceIntent);
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d("onActivityResult", "Activity.RESULT_OK");
                        Intent intent = result.getData();
                        // Handle the Intent
                        serviceIntent = new Intent(MainActivity.this, FloatingAutoClickService.class);
                        startService(serviceIntent);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.startButton);
        button.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || Settings.canDrawOverlays(this)) {
                Log.d("button clicked", "service start");
                serviceIntent = new Intent(MainActivity.this, FloatingAutoClickService.class);
                startService(serviceIntent);
                onBackPressed();
            } else {
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"));
                mStartForResult.launch(myIntent);

                Toast.makeText(this, "You need System Alert Window Permission to do this", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (serviceIntent == null) {
            stopService(serviceIntent);
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAccessibilityServicePermission();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || Settings.canDrawOverlays(this)) {
//            serviceIntent = new Intent(MainActivity.this, FloatingAutoClickService.class);
//            startService(serviceIntent);
            Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"));
            mStartForResult.launch(myIntent);
        }
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
                Log.d("checkAccessibilityServicePermission", "ask for AccessibilityServicePermission");
            }
        }
    }
}