package fr.arnaud.transfer_image_client;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import apifornetwork.tcp.client.ClientTCP;
import fr.arnaud.transfer_image_client.databinding.ActivityMainBinding;
import fr.arnaud.transfer_image_client.files.FileManager;
import fr.arnaud.transfer_image_client.files.Settings;
import fr.arnaud.transfer_image_client.popups.CreatedPopups;
import fr.arnaud.transfer_image_client.popups.PopupManager;

import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    public static Settings settings;
    public static MainActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        askPerms();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Gallery Sync");

        settings = FileManager.loadSettings(getFilesDir());

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ipManage_button)
            return PopupManager.showPopup(this, CreatedPopups.addressPopup.currentValue(settings.getIp()));
        else if (id == R.id.passwordManage_button)
            return PopupManager.showPopup(this, CreatedPopups.passwordPopup.currentValue(settings.getPassword()));
        else if (id == R.id.portManage_button)
            return PopupManager.showPopup(this, CreatedPopups.portPopup.currentValue("" + settings.getPort()));
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void askPerms() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                123);
    }

    public static void runUi(final Runnable runnable) {
        MainActivity.activity.runOnUiThread(runnable);
    }

}