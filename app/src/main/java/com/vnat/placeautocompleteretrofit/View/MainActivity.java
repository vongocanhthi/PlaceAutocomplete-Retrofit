package com.vnat.placeautocompleteretrofit.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.vnat.placeautocompleteretrofit.R;
import com.vnat.placeautocompleteretrofit.ViewModel.PlaceViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    private boolean isPermisstion = false;
    private static final int RC_LOCATION = 533;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void Map(View view) {
//        if (isPermisstion){
//            startActivity(new Intent(this, MapsActivity.class));
//        }else{
//            permission();
//        }
        permission();
    }

    @AfterPermissionGranted(RC_LOCATION)
        private void permission() {
            String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            if (EasyPermissions.hasPermissions(this, perms)) {
                // Already have permission, do the thing
                // ...
                startActivity(new Intent(this, MapsActivity.class));
            } else {
                EasyPermissions.requestPermissions(this, "We need permissions to get the location",
                        RC_LOCATION, perms);
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        }

        @Override
        public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

        }

        @Override
        public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                new AppSettingsDialog.Builder(this).build().show();
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {

            }
        }

}