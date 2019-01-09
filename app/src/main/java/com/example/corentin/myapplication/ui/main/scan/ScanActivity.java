package com.example.corentin.myapplication.ui.main.scan;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.corentin.myapplication.R;
import com.example.corentin.myapplication.data.model.local.LocalPreferences;
import com.example.corentin.myapplication.data.model.manager.BluetoothLEManager;
import com.example.corentin.myapplication.ui.main.action.ActionActivity;
import com.example.corentin.myapplication.ui.main.scan.adapter.DeviceAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ScanActivity extends AppCompatActivity {

    private final Runnable scanDevicesRunnable = () -> stopScan();

    private static final int REQUEST_ENABLE_BLE = 8888;
    private static final int REQUEST_LOCATION_CODE = 6666;
    private static final int REQUEST_ENABLED_LOCATION_CODE = 9999;
    private static final long SCAN_DURATION_MS = 10_000L;

    private BluetoothAdapter bluetoothAdapter;
    private boolean isScanning = false;
    private final Handler scanningHandler = new Handler();

    public static Intent getStartIntent(final Context ctx){
        return new Intent(ctx,ScanActivity.class);
    }

    private ArrayList<BluetoothDevice> deviceArrayList = new ArrayList<>();
    private DeviceAdapter adapter;

    TextView btdeco;
    TextView btScan;
    TextView btflou;
    ImageButton btToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new DeviceAdapter(this, deviceArrayList);

        setContentView(R.layout.activity_scan);
        btScan = findViewById(R.id.playScan);
        btdeco = findViewById(R.id.deconnexion);
        btToggle = findViewById(R.id.boutn);
        btScan.setOnClickListener(this::onClick);
        btflou = findViewById(R.id.btflou);
        //btToggle.setOnClickListener(this::onClickToggle);
        btdeco.setOnClickListener(this::onClickDeco);
        ListView listView = findViewById(R.id.listView);
        listView.setClickable(true);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listClick);
        //final ImageButton configuration = findViewById(R.id.boutn);
        btToggle.setOnClickListener(v->toggleLed());
        setUiMode(false);

    }
    private BluetoothDevice selectedDevice;
    private AdapterView.OnItemClickListener listClick = (parent, view, position, id) -> {
        final BluetoothDevice item = adapter.getItem(position);
        BluetoothLEManager.getInstance().setCurrentDevice(item);
        selectedDevice = item;
        LocalPreferences.getInstance(this).saveCurrentSelectedDevice(item.getName());
        connectToCurrentDevice();



    };
    private void onClick(View l) {
        checkPermissions();



        //ImageButton btToggle = findViewById(R.id.boutn);
       // btToggle.setVisibility(1);

        Toast.makeText(this, "scan", Toast.LENGTH_SHORT).show();
    }

    private void onClickToggle(View l)
    {
        Toast.makeText(this, "togglePin", Toast.LENGTH_SHORT).show();
    }

    private void onClickDeco(View l)
    {
        Toast.makeText(this, "deconnexion", Toast.LENGTH_SHORT).show();
        discconnectFromCurrentDevice();
    }

    private void setupBLE() {
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

        if (bluetoothManager == null || !bluetoothAdapter.isEnabled()) { // bluetooth is off
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BLE);
        } else {
            scanNearbyDevices(); // start scanning by default
        }
    }
    private void checkForLocationEnabled() {
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (lm != null) {
            final boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            final boolean network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!gps_enabled || !network_enabled) {
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_ENABLED_LOCATION_CODE);
            } else {
                setupBLE();
            }
        } else {
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_ENABLED_LOCATION_CODE);
        }
    }

    private void checkPermissions() {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            } else {
                checkForLocationEnabled();
            }
        }
    private void scanNearbyDevices() {
        if (isScanning) {
            return;
        }
        isScanning = true;
        scanningHandler.postDelayed(scanDevicesRunnable, SCAN_DURATION_MS);
        // for recent version of android
        final ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        final List<ScanFilter> scanFilters = new ArrayList<>();
        scanFilters.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(BluetoothLEManager.DEVICE_UUID)).build());
        bluetoothAdapter.getBluetoothLeScanner().startScan(scanFilters, settings, bleLollipopScanCallback);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,final String[] permissions,final int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkForLocationEnabled();
            } else {
                checkPermissions(); // force permission
            }
        }
    }
    private int i = 0;
    private final ScanCallback bleLollipopScanCallback = new ScanCallback() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override

        public void onScanResult(final int callbackType, final ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice bluetoothDevice = null;

            bluetoothDevice = result.getDevice();


            // C'est ici qu'il faut l'ajouter à l'adapter

            if(!deviceArrayList.contains(bluetoothDevice)){
                adapter.add(bluetoothDevice);
            }

        }

        @Override
        public void onScanFailed(final int errorCode) {
            super.onScanFailed(errorCode);
            //Toast.makeText(ScanActivity.this, getString(R.string.ble_scan_error, errorCode), Toast.LENGTH_SHORT).show();
        }
    };

    private void stopScan(){
        bluetoothAdapter.getBluetoothLeScanner().stopScan(bleLollipopScanCallback);
    }

/////////////////////////////////////////////////////////////////////////////////////////////

    private void setUiMode(boolean isConnected) {
        if (isConnected) {
            // Connecté à un périphérique, passage en node action BLE
            adapter.clear();
            btdeco.setVisibility(View.GONE);
            //btScan.setVisibility(View.GONE);
            btScan.setVisibility(View.GONE);
            btflou.setVisibility(View.VISIBLE);
            btflou.setText(String.format("Connecté à : %s", selectedDevice.getName()));
            btdeco.setVisibility(View.VISIBLE);
            btToggle.setVisibility(View.VISIBLE);
        } else {
            // Non connecté, reset de la vue.
            btdeco.setVisibility(View.VISIBLE);
            btScan.setVisibility(View.VISIBLE);
            btflou.setVisibility(View.GONE);
            btdeco.setVisibility(View.GONE);
            btToggle.setVisibility(View.GONE);
        }
    }

    private BluetoothGatt currentBluetoothGatt = null; // current connection to BLE device
    private final Stack<BluetoothGattCharacteristic> charsStack = new Stack<>();

    private void connectToCurrentDevice() {
        final BluetoothDevice device = BluetoothLEManager.getInstance().getCurrentDevice();
        if (device != null) {
            currentBluetoothGatt = device.connectGatt(this, false, gattCallback);

        } else {
            Toast.makeText(this, "Pas de selection", Toast.LENGTH_SHORT).show();
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            super.onServicesDiscovered(gatt, status);
            runOnUiThread(() -> {Toast.makeText(ScanActivity.this, "Services discovered with success", Toast.LENGTH_SHORT).show();

            });
            startActivity(ActionActivity.getStartIntent(ScanActivity.this));

        }

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            runOnUiThread(()->{
                switch (newState) {
                    case BluetoothGatt.STATE_CONNECTED:
                        currentBluetoothGatt.discoverServices(); // start services
                        break;
                    case BluetoothProfile.STATE_DISCONNECTED:
                        gatt.close();
                        setUiMode(false);
                        break;
                }
            });
        }

        @Override
        public void onCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (!charsStack.isEmpty()) {
                currentBluetoothGatt.writeCharacteristic(charsStack.pop());
            }
        }
    };



    /**
     * Send the current configuration to pins
     */
    private void sendConfiguration() {
        if (currentBluetoothGatt == null) {
            Toast.makeText(this, "Non Connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText ledGPIOPin = findViewById(R.id.text);
        EditText buttonGPIOPin = findViewById(R.id.text2);
        final String pinLed = ledGPIOPin.getText().toString();
        final String pinButton = buttonGPIOPin.getText().toString();

        final BluetoothGattService service = currentBluetoothGatt.getService(BluetoothLEManager.DEVICE_UUID);
        if (service == null) {
            Toast.makeText(this, "UUID Introuvable", Toast.LENGTH_SHORT).show();
            return;
        }

        final BluetoothGattCharacteristic buttonCharact = service.getCharacteristic(BluetoothLEManager.CHARACTERISTIC_BUTTON_PIN_UUID);
        final BluetoothGattCharacteristic ledCharact = service.getCharacteristic(BluetoothLEManager.CHARACTERISTIC_LED_PIN_UUID);

        buttonCharact.setValue(pinButton);
        ledCharact.setValue(pinLed);

        currentBluetoothGatt.writeCharacteristic(buttonCharact); // async code, you cannot send 2 characteristics at the same time!
        charsStack.add(ledCharact); // stack the next write
    }
    private void discconnectFromCurrentDevice() {
        if(currentBluetoothGatt != null) {
            currentBluetoothGatt.disconnect();
            startActivity(ScanActivity.getStartIntent(this));

        }
    }






    private void toggleLed(){
        if (currentBluetoothGatt == null) {
            Toast.makeText(this, "Non Connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        final BluetoothGattService service = currentBluetoothGatt.getService(BluetoothLEManager.DEVICE_UUID);
        if (service == null) {
            Toast.makeText(this, "UUID Introuvable", Toast.LENGTH_SHORT).show();
            return;
        }

        final BluetoothGattCharacteristic toggleLed = service.getCharacteristic(BluetoothLEManager.CHARACTERISTIC_TOGGLE_LED_UUID);
        toggleLed.setValue("1");
        currentBluetoothGatt.writeCharacteristic(toggleLed);


    }



}
