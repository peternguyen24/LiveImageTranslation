package com.example.liveimagetranslation;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.liveimagetranslation.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ActivityResultLauncher<String> chooseImageIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpIntents();

        LoadImageFragment loadImageFragment = new LoadImageFragment(chooseImageIntent);
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container, loadImageFragment, LoadImageFragment.TAG).commitNow();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpIntents() {
        chooseImageIntent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> {
                    Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_SHORT)
                            .show();
                    showImageView(result);
                });
    }

    private void showImageView(Uri uri) {
        Fragment loadImageFragment = getSupportFragmentManager().findFragmentByTag(LoadImageFragment.TAG);
        if (loadImageFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(loadImageFragment).commitNow();
        }
        ImageViewerFragment imageViewerFragment = new ImageViewerFragment(uri);
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container, imageViewerFragment, ImageViewerFragment.TAG).commitNow();
    }

}