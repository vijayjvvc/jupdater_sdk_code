package com.qtlws.android.jupdater;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UpdateBlocking extends AppCompatActivity {

    private long lastBackPressedTime = 0;
    private static final int EXIT_INTERVAL = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_blocking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button updateBtn = findViewById(R.id.updateApp);
        Button closeAppBtn = findViewById(R.id.closeApp);

        updateBtn.setOnClickListener(v -> {
            String apkUrl = getIntent().getStringExtra("update_url");;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(apkUrl));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        closeAppBtn.setOnClickListener(v -> {
            finishAffinity();
            System.exit(0);
        });

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBackPressedTime < EXIT_INTERVAL) {
            finishAffinity();
            System.exit(0);
        } else {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            lastBackPressedTime = currentTime;
        }
    }
}