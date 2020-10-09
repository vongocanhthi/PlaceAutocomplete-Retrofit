package com.vnat.placeautocompleteretrofit.View;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.vnat.placeautocompleteretrofit.Adapter.ResultAdapter;
import com.vnat.placeautocompleteretrofit.Helper.RecyclerViewItemClickListener;
import com.vnat.placeautocompleteretrofit.Model.Place;
import com.vnat.placeautocompleteretrofit.Model.Result;
import com.vnat.placeautocompleteretrofit.R;
import com.vnat.placeautocompleteretrofit.ViewModel.PlaceViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import im.delight.android.location.SimpleLocation;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    @BindView(R.id.edtSearch)
    EditText edtSearch;

    @BindView(R.id.txtAddress)
    TextView txtAddress;

    @BindView(R.id.rcvAddress)
    RecyclerView rcvAddress;

    @BindView(R.id.imgClear)
    ImageView imgClear;

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;

    private PlaceViewModel placeViewModel;
    private SimpleLocation location;
    private ResultAdapter resultAdapter;

    private ArrayList<Result> resultArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        ButterKnife.bind(this);
        placeViewModel = ViewModelProviders.of(this).get(PlaceViewModel.class);

        init();
        funGetResultToList();
        addOnItemTouchListener();

    }

    private void addOnItemTouchListener() {
        rcvAddress.addOnItemTouchListener(new RecyclerViewItemClickListener(this, rcvAddress, new RecyclerViewItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Result result = resultArrayList.get(position);

                Double lat = result.getGeometry().getLocation().getLat();
                Double lng = result.getGeometry().getLocation().getLng();

                edtSearch.setText(result.getName() + ", " + result.getFormattedAddress());

                rcvAddress.setVisibility(View.GONE);

                moveToNow(lat, lng);

                hideKeyboard(MapsActivity.this);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    private void moveToNow(Double lat, Double lng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15));
    }


    private void funGetResultToList() {
        placeViewModel.getPlaceMutableLiveData().observe(this, new Observer<Place>() {
            @Override
            public void onChanged(Place place) {

                resultArrayList.clear();
                resultArrayList.addAll(place.getResults());
                resultAdapter.notifyDataSetChanged();
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                imgClear.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    imgClear.setVisibility(View.GONE);
                    rcvAddress.setVisibility(View.GONE);
                } else if (s.length() > 0) {
                    imgClear.setVisibility(View.VISIBLE);
                }

                if (s.length() >= 2) {
                    rcvAddress.setVisibility(View.VISIBLE);
                    placeViewModel.getPlace(s.toString(), getString(R.string.google_maps_key));

                    resultAdapter = new ResultAdapter(resultArrayList);
                    rcvAddress.setAdapter(resultAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void init() {
        location = new SimpleLocation(this);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvAddress.addItemDecoration(dividerItemDecoration);

        rcvAddress.setHasFixedSize(true);
        rcvAddress.setLayoutManager(new LinearLayoutManager(this));

        resultArrayList = new ArrayList<>();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setMyLocationEnabled();

        if (location.hasLocationEnabled()) {
            myLocation();
        }

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (location.hasLocationEnabled()) {
                    myLocation();
                } else {
                    SimpleLocation.openSettings(getApplicationContext());
                }
                return false;
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                CameraPosition cameraPosition = mMap.getCameraPosition();
                LatLng currentCenter = cameraPosition.target;

                try {
                    Geocoder geocoder;

                    List<Address> addresses;
                    geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                    addresses = geocoder.getFromLocation(currentCenter.latitude, currentCenter.longitude, 1);

                    if (addresses.size() > 0) {
                        String address = addresses.get(0).getAddressLine(0);
                        txtAddress.setText(address);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setMyLocationEnabled() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();

        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 200, 300);
    }

    private void myLocation() {
        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
//        mMap.addMarker(new MarkerOptions().position(myLocation).title("Your Here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
    }

    private void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        location.beginUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        location.endUpdates();

    }

    public void clearSearchText(View view) {
        edtSearch.setText("");
    }

}