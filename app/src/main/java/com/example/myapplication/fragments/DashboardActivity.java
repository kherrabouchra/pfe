package com.example.myapplication.fragments;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;

public class DashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        SeekBar seekBar = findViewById(R.id.seekBar);
        Drawable sadThumb = ContextCompat.getDrawable(this, R.drawable.cil_sad);
        Drawable neutralThumb = ContextCompat.getDrawable(this, R.drawable.cil_net);
        Drawable happyThumb = ContextCompat.getDrawable(this, R.drawable.cil_good);

// Set the initial thumb to Sad
        seekBar.setThumb(sadThumb);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Change the thumb based on progress
                if (progress == 0) {
                    seekBar.setThumb(sadThumb); // Sad
                } else if (progress == 1) {
                    seekBar.setThumb(neutralThumb); // Neutral
                } else if (progress == 2) {
                    seekBar.setThumb(happyThumb); // Happy
                }
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu for BottomAppBar
        getMenuInflater().inflate(R.menu.bottom_nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.nav_search) {
            Toast.makeText(this, "Search clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.nav_notifications) {
            Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.nav_messages) {
            Toast.makeText(this, "Messages clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.nav_profile) {
            Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
