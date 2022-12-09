package com.apollo.pharmacy.ocr.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.adapters.PickFromStoreListAdapter;
import com.apollo.pharmacy.ocr.controller.DeliverySelectionController;
import com.apollo.pharmacy.ocr.fragments.KeyboardFragment;
import com.apollo.pharmacy.ocr.interfaces.DeliverySelectionListener;
import com.apollo.pharmacy.ocr.interfaces.OnRecyclerItemClickListener;
import com.apollo.pharmacy.ocr.model.PharmacyLocatorCustomList;
import com.apollo.pharmacy.ocr.model.StoreLocatorResponse;
import com.apollo.pharmacy.ocr.model.UserAddress;
import com.apollo.pharmacy.ocr.model.pharmacylocatorFilterlist;
import com.apollo.pharmacy.ocr.receiver.ConnectivityReceiver;
import com.apollo.pharmacy.ocr.utility.Constants;
import com.apollo.pharmacy.ocr.utility.NetworkUtils;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.apollo.pharmacy.ocr.utility.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class StorePickupActivity extends BaseActivity implements DeliverySelectionListener, OnMapReadyCallback,
        ConnectivityReceiver.ConnectivityReceiverListener, KeyboardFragment.OnClickKeyboard, OnRecyclerItemClickListener {

    private RecyclerView pharmacy_location_listview;
    private ArrayList<PharmacyLocatorCustomList> contacts;
    private String stringLatitude, stringLongitude;
    public String City_name;
    public ArrayList<Integer> Quantyty_arraylist;
    private SupportMapFragment mapFragment;
    private TextView search_edit_text;
    private PickFromStoreListAdapter adapter;
    private KeyboardFragment keyboardFrag;
    private final Handler handler = new Handler();
    private FrameLayout medic_keyboard;

    public StorePickupActivity() {
        super();
    }

    public void onSuccessGetAddressDetails(ArrayList<UserAddress> response) {

    }

    public void onFailure(String error) {
        Utils.showCustomAlertDialog(StorePickupActivity.this, error, false, getApplicationContext().getResources().getString(R.string.label_ok), "");
    }

    @Override
    public void onSuccessGetStoreLocators(List<StoreLocatorResponse> response) {
        ArrayList<pharmacylocatorFilterlist> near_locations_array = new ArrayList<pharmacylocatorFilterlist>();
        ArrayList<Double> distance_array1 = new ArrayList<Double>();
        ArrayList<Double> temp_distance_array1 = new ArrayList<Double>();

        for (StoreLocatorResponse data : response) {
            pharmacylocatorFilterlist load_filterist = new pharmacylocatorFilterlist();
            City_name = "HYDERABAD";
            String compare_str = data.getPcity();
            City_name = City_name.trim();
            if (compare_str.equals(City_name)) {
                load_filterist.setStoreid(data.getStoreid());
                load_filterist.setStorename(data.getStorename());
                load_filterist.setStoretype(data.getBranchtype());
                load_filterist.setStoreaddress(data.getBaddress());
                load_filterist.setStorenumber(data.getPhno());
                load_filterist.setStorelatitude(data.getPlat());
                load_filterist.setStorelongtude(data.getPlon());
                load_filterist.setStorecity(data.getPcity());
                load_filterist.setStorestate(data.getStaten());
                load_filterist.setStorepincode(data.getPincode());
                String storelatitude2 = data.getPlat();
                String storelongtude2 = data.getPlon();
                if (storelatitude2.length() == 0) {
                    storelatitude2 = "0.0";
                }
                if (storelongtude2.length() == 0) {
                    storelongtude2 = "0.0";
                }
                storelatitude2 = storelatitude2.replaceAll("\\s+", "");
                storelongtude2 = storelongtude2.replaceAll("\\s+", "");
                double theta = Double.valueOf(stringLongitude).doubleValue() - Double.valueOf(storelongtude2).doubleValue();
                double dist = Math.sin(deg2rad(Double.valueOf(stringLatitude).doubleValue()))
                        * Math.sin(deg2rad(Double.valueOf(storelatitude2).doubleValue()))
                        + Math.cos(deg2rad(Double.valueOf(stringLatitude).doubleValue()))
                        * Math.cos(deg2rad(Double.valueOf(storelatitude2).doubleValue()))
                        * Math.cos(deg2rad(theta));
                dist = Math.acos(dist);
                dist = rad2deg(dist);
                dist = dist * 60 * 1.1515;
                load_filterist.setStoredistance(String.valueOf(dist));
                near_locations_array.add(load_filterist);
                distance_array1.add(dist);
                temp_distance_array1.add(dist);
            }
        }
        Collections.sort(distance_array1);
        for (int i = 0; i < distance_array1.size(); i++) {
            PharmacyLocatorCustomList load_obj = new PharmacyLocatorCustomList();
            pharmacylocatorFilterlist load_filterist = new pharmacylocatorFilterlist();
            Double fetch_value = distance_array1.get(i);
            int index = temp_distance_array1.indexOf(fetch_value);
            load_filterist = near_locations_array.get(index);
            load_obj.setStoreid(load_filterist.getStoreid());
            load_obj.setStorename(load_filterist.getStorename());
            load_obj.setStoretype(load_filterist.getStoretype());
            load_obj.setStoreaddress(load_filterist.getStoreaddress());
            load_obj.setStorenumber(load_filterist.getStorenumber());
            load_obj.setStorelatitude(load_filterist.getStorelatitude());
            load_obj.setStorelongtude(load_filterist.getStorelongtude());
            load_obj.setStorecity(load_filterist.getStorecity());
            load_obj.setStorestate(load_filterist.getStorestate());
            load_obj.setStorepincode(load_filterist.getStorepincode());
            String storedistance2 = load_filterist.getStoredistance();
            double dist = Double.valueOf(storedistance2).doubleValue();
            storedistance2 = String.format("%.2f", dist);
            storedistance2 = storedistance2 + " km";
            load_obj.setStoredistance(storedistance2);
            contacts.add(load_obj);
        }
        adapter = new PickFromStoreListAdapter(StorePickupActivity.this, contacts, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(StorePickupActivity.this);
        pharmacy_location_listview.setLayoutManager(mLayoutManager);
        pharmacy_location_listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            findViewById(R.id.networkErrorLayout).setVisibility(View.GONE);
        } else {
            findViewById(R.id.networkErrorLayout).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        Constants.getInstance().setConnectivityListener(this);
        if (!ConnectivityReceiver.isConnected()) {
            findViewById(R.id.networkErrorLayout).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_pickup);

        ConstraintLayout constraint_layout = findViewById(R.id.constraint_layout);
        DeliverySelectionController controller = new DeliverySelectionController(this);
        stringLongitude = Double.toString(78.409420);
        stringLatitude = Double.toString(17.416500);
        contacts = new ArrayList<PharmacyLocatorCustomList>();
        Quantyty_arraylist = new ArrayList<>();

        Button removetext_button = (Button) findViewById(R.id.removetext_button);
        TextView confirm_button = (TextView) findViewById(R.id.confirm_button);
        search_edit_text = (TextView) findViewById(R.id.search_edit_text);
        ImageView closeImage = findViewById(R.id.close_image);
        medic_keyboard = findViewById(R.id.medic_keyboard);

        closeImage.setOnClickListener(view -> {
                    finish();
                    overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
                }
        );
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);
        mapFragment.getMapAsync(googleMap -> {
            googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(17.387140, 78.491684))
                    .title("LinkedIn")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(17.387140, 78.491684), 17));
        });

        removetext_button.setOnClickListener(view -> search_edit_text.setText(""));

        confirm_button.setOnClickListener(view -> {
            if (contacts != null && contacts.size() > 0) {
                boolean isSelected = false;
                for (PharmacyLocatorCustomList list : contacts) {
                    if (list.isSelected()) {
                        isSelected = true;
                        break;
                    }
                }
                if (isSelected) {
                    Intent intent = new Intent(StorePickupActivity.this, OrderSummaryActivity.class);
                    intent.putExtra("activityname", "PickFromStoreActivity");
                    startActivity(intent);
                    overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
                } else {
                    Toast.makeText(this, "Please select address", Toast.LENGTH_SHORT).show();
                }
            }
        });

        contacts = new ArrayList<PharmacyLocatorCustomList>();
        pharmacy_location_listview = findViewById(R.id.pharmacy_location_listview);
        adapter = new PickFromStoreListAdapter(StorePickupActivity.this, contacts, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(StorePickupActivity.this);
        pharmacy_location_listview.setLayoutManager(mLayoutManager);
        pharmacy_location_listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (NetworkUtils.isNetworkConnected(StorePickupActivity.this)) {
            controller.getStoreLocators(StorePickupActivity.this, StorePickupActivity.this);
        } else {
            Utils.showSnackbar(StorePickupActivity.this, constraint_layout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
        }

        search_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                String check = search_edit_text.getText().toString();
                if (!check.trim().isEmpty()) {
                    String text = search_edit_text.getText().toString().toLowerCase(Locale.getDefault());
                    adapter.filter(text);
                }
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
        keyboardFrag = KeyboardFragment.newInstance(StorePickupActivity.this, this);
        search_edit_text.setText("");
        search_edit_text.setInputType(InputType.TYPE_CLASS_TEXT);
        search_edit_text.setRawInputType(InputType.TYPE_CLASS_TEXT);
        hideSystemKeyBoard();
    }

    private void hideSystemKeyBoard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        search_edit_text.setOnTouchListener(touchListenerEdt);
    }

    View.OnTouchListener touchListenerEdt = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            medic_keyboard.setVisibility(View.VISIBLE);
            initKeyBoard();
            showKeyBoard((EditText) v);
            v.onTouchEvent(event);
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            return true;
        }
    };

    private void initKeyBoard() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.medic_keyboard, keyboardFrag, "KEYBOARD");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showKeyBoard(final EditText et) {
        if (et.getInputType() == InputType.TYPE_CLASS_NUMBER) {
            keyboardFrag.inputFilter = keyboardFrag.filterArray[1];
        } else {
            keyboardFrag.inputFilter = keyboardFrag.filterArray[2];
        }
        keyboardFrag.editTextFocus = et;
        handler.postDelayed(() -> {
            keyboardFrag.cursorPos = et.getSelectionStart();
            keyboardFrag.str = et.getText().toString().trim();
        }, 100);
    }

    @Override
    public void getKeyboardText(EditText editText, String str, int curPostion) {
        editText.setText("" + str);
        if (str.length() > 0 && str.length() >= curPostion)
            editText.setSelection(curPostion);
    }

    @Override
    public void onEnter(EditText editText, String str) {
        editText.setText(str);
        hideKeyBoard();
    }

    private void hideKeyBoard() {
        medic_keyboard.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("KEYBOARD")).commit();
    }

    @Override
    public void onHideKeyboard() {
        hideKeyBoard();
    }

    public void updatedmaps(double latitude, double longtude) {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longtude))
                        .title("LinkedIn")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longtude), 17));
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(17.387140, 78.491684))
                .title("Current Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(17.387140, 78.491684), 17));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
    }

    @Override
    public void onViewClick(int position) {
        PharmacyLocatorCustomList item = contacts.get(position);
        for (int i = 0; i < contacts.size(); i++) {
            contacts.get(i).setSelected(false);
        }
        for (int i = 0; i < contacts.size(); i++) {
            if (item.getStoreid() == contacts.get(i).getStoreid()) {
                contacts.get(i).setSelected(true);
            }
        }
        adapter.notifyDataSetChanged();
        if (position != 0) {
            UserAddress userAddress = new UserAddress();
            userAddress.setName("Apollo Pharmacy");
            userAddress.setAddress1(contacts.get(position).getStoreaddress());
            userAddress.setCity(contacts.get(position).getStorecity());
            userAddress.setState(contacts.get(position).getStorestate());
            userAddress.setPincode(contacts.get(position).getStorepincode());
            SessionManager.INSTANCE.setUseraddress(userAddress);
        }
    }
}
