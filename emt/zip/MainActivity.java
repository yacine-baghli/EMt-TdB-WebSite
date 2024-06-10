package com.embesystems.geove;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.embesystems.geove.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
  private static final String TAG = "MainActivity";

  public static final int MESSAGE_STATE_CHANGE = 1;

    /*0	Automatic protocol detection
   1	SAE J1850 PWM (41.6 kbaud)
   2	SAE J1850 VPW (10.4 kbaud)
   3	ISO 9141-2 (5 baud init, 10.4 kbaud)
   4	ISO 14230-4 KWP (5 baud init, 10.4 kbaud)
   5	ISO 14230-4 KWP (fast init, 10.4 kbaud)
   6	ISO 15765-4 CAN (11 bit ID, 500 kbaud)
   7	ISO 15765-4 CAN (29 bit ID, 500 kbaud)
   8	ISO 15765-4 CAN (11 bit ID, 250 kbaud) - used mainly on utility vehicles and Volvo
   9	ISO 15765-4 CAN (29 bit ID, 250 kbaud) - used mainly on utility vehicles and Volvo


    01 04 - ENGINE_LOAD
    01 05 - ENGINE_COOLANT_TEMPERATURE
    01 0C - ENGINE_RPM
    01 0D - VEHICLE_SPEED
    01 0F - INTAKE_AIR_TEMPERATURE
    01 10 - MASS_AIR_FLOW
    01 11 - THROTTLE_POSITION_PERCENTAGE
    01 1F - ENGINE_RUN_TIME
    01 2F - FUEL_LEVEL
    01 46 - AMBIENT_AIR_TEMPERATURE
    01 51 - FUEL_TYPE
    01 5E - FUEL_CONSUMPTION_1
    01 5F - FUEL_CONSUMPTION_2

   */

  public static final int MESSAGE_READ = 2;
  public static final int MESSAGE_WRITE = 3;
  public static final int MESSAGE_DEVICE_NAME = 4;
  public static final int MESSAGE_TOAST = 5;
  // Key names received from the BluetoothChatService Handler
  public static final String DEVICE_NAME = "Reader_EmbeSystems";
  public static final String TOAST = "toast";

  protected final static char[] dtcLetters = {'P', 'C', 'B', 'U'};
  protected final static char[] hexArray = "0123456789ABCDEF".toCharArray();

  private static final String[] PIDS = {
          "01", "02", "03", "04", "05", "06", "07", "08",
          "09", "0A", "0B", "0C", "0D", "0E", "0F", "10",
          "11", "12", "13", "14", "15", "16", "17", "18",
          "19", "1A", "1B", "1C", "1D", "1E", "1F", "20"};

  // Intent request codes
  private static final int REQUEST_CONNECT_DEVICE = 2;
  private static final int REQUEST_ENABLE_BT = 3;
  final List<String> commandslist = new ArrayList<String>();
  final List<Double> avgconsumption = new ArrayList<Double>();

  BluetoothDevice currentdevice;
  boolean commandmode = false, initialized = false, m_getPids = false, tryconnect = false, defaultStart = false;
  String devicename = null, deviceprotocol = null;

  String[] initializeCommands;
  Intent serverIntent = null;
//  TroubleCodes troubleCodes;
  String VOLTAGE = "ATRV",
          PROTOCOL = "ATDP",
          RESET = "ATZ",
          PIDS_SUPPORTED20 = "0100",
          ENGINE_COOLANT_TEMP = "0105",  //A-40
          ENGINE_RPM = "010C",  //((A*256)+B)/4
          ENGINE_LOAD = "0104",  // A*100/255
          VEHICLE_SPEED = "010D",  //A
          INTAKE_AIR_TEMP = "010F",  //A-40
          MAF_AIR_FLOW = "0110", //MAF air flow rate 0 - 655.35	grams/sec ((256*A)+B) / 100  [g/s]
          ENGINE_OIL_TEMP = "015C",  //A-40
          FUEL_RAIL_PRESSURE = "0122", // ((A*256)+B)*0.079
          INTAKE_MAN_PRESSURE = "010B", //Intake manifold absolute pressure 0 - 255 kPa
          CONT_MODULE_VOLT = "0142",  //((A*256)+B)/1000
          AMBIENT_AIR_TEMP = "0146",  //A-40
          CATALYST_TEMP_B1S1 = "013C",  //(((A*256)+B)/10)-40
          STATUS_DTC = "0101", //Status since DTC Cleared
          THROTTLE_POSITION = "0111", //Throttle position 0 -100 % A*100/255
          OBD_STANDARDS = "011C", //OBD standards this vehicle
          PIDS_SUPPORTED = "0120"; //PIDs supported

  // Local Bluetooth adapter
  private BluetoothAdapter mBluetoothAdapter = null;
  // Member object for the chat services
  private BluetoothService mBtService = null;
  MenuItem itemtemp;
  private Menu menu;
  private ListView mConversationView;
  private TextView engineLoad, Fuel, voltage, coolantTemperature, Status, Loadtext, Volttext, Temptext, Info, Airtemp_text, airTemperature, Maf_text, Maf;
  private TextView speed, rpm, dth,
          latitude, longitude, altitude, nbrsatgps, vitessegps, directiongps, dthgps;
  private TextView releve;
  private Spinner sp_releve;
  private ArrayAdapter<String> releves_adapter;
  private String mConnectedDeviceName = "Ecu";
  private int rpmval = 0, intakeairtemp = 0, ambientairtemp = 0, coolantTemp = 0, mMaf = 0,
          engineoiltemp = 0, b1s1temp = 0, Enginetype = 0, FaceColor = 0,
          whichCommand = 0, m_dedectPids = 0, connectcount = 0, trycount = 0;
  private int mEnginedisplacement = 1500;
  // The Handler that gets information back from the BluetoothChatService
  // Array adapter for the conversation thread
  private ArrayAdapter<String> mConversationArrayAdapter;

  private ActivityMainBinding binding;
  String UserName, UserPW, VehicleID;
  private FusedLocationProviderClient fusedLocationClient;
  private static final int  locationRequestCode = 1000;
  private LocationRequest locationRequest;
  //    private SettingsClient settingsClient;
//    private LocationSettingsRequest locationSettingsRequest;
  private LocationCallback locationCallback;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    SharedPreferences pref = getSharedPreferences("priv_settings", Context.MODE_PRIVATE);
    UserName = pref.getString("UserName", "");
    UserPW = pref.getString("UserPW", "");
    VehicleID = pref.getString("VehicleID", "5");

    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    setSupportActionBar(binding.toolbar);
    
    Status = findViewById(R.id.Status);
    engineLoad = findViewById(R.id.Load);
    Fuel = findViewById(R.id.Fuel);
    coolantTemperature = findViewById(R.id.Temp);
    voltage = findViewById(R.id.Volt);
    Loadtext = findViewById(R.id.Load_text);
    Temptext = findViewById(R.id.Temp_text);
    Volttext = findViewById(R.id.Volt_text);
    Info = findViewById(R.id.info);
    Airtemp_text = findViewById(R.id.Airtemp_text);
    airTemperature = findViewById(R.id.Airtemp);
    Maf_text = findViewById(R.id.Maf_text);
    Maf = findViewById(R.id.Maf);
    speed = findViewById(R.id.GaugeSpeed);
    rpm = findViewById(R.id.GaugeRpm);
    mConversationView = findViewById(R.id.in);
    releve = findViewById(R.id.releve);
    sp_releve = findViewById(R.id.sp_releves);
    ArrayList<String> InfoID_list = new ArrayList<String>();
      releves_adapter =  new ArrayAdapter<String>( this,
            android.R.layout.simple_spinner_item, InfoID_list);
    releves_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    sp_releve.setAdapter(releves_adapter);
    UpdateRelevesAdapter();
    sp_releve.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String ss = parent.getItemAtPosition(pos).toString();
        releve.setText(ss.substring(0, ss.indexOf(' ')));
      }
      @Override
      public void onNothingSelected(AdapterView<?> parent) {

        // sometimes you need nothing here
      }
    });
    latitude = findViewById(R.id.tx_Latitude);
    longitude = findViewById(R.id.tx_Longitude);
    altitude = findViewById(R.id.tx_Altitude);
    nbrsatgps = findViewById(R.id.tx_GPSNr);
    vitessegps = findViewById(R.id.tx_Speed);
    directiongps = findViewById(R.id.tx_Dir);
    dthgps = findViewById(R.id.tx_DTHGPS);
    dth = findViewById(R.id.tx_DTH);

    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        settingsClient = LocationServices.getSettingsClient(this);
    // check permission
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      // request for permission
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
              locationRequestCode);
    } else {
      // already permission granted
      Toast.makeText( getApplicationContext(), "Permission garanted", Toast.LENGTH_LONG).show();
    }

    locationRequest = LocationRequest.create();
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    locationRequest.setInterval(2 * 1000); // 2 s
    locationRequest.setFastestInterval(1000);
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
//        builder.addLocationRequest(locationRequest);
//        locationSettingsRequest = builder.build();

    locationCallback = new LocationCallback() {
      @Override
      public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);
        if (locationResult == null) {
          return;
        }
        Location location = locationResult.getLastLocation();
        latitude.setText(String.valueOf(location.getLatitude()));
        longitude.setText(String.valueOf(location.getLongitude()));
        DecimalFormat df=new DecimalFormat("0.00");
        df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        altitude.setText( df.format(location.getAltitude()));
        vitessegps.setText(df.format(location.getSpeed()));
        nbrsatgps.setText(df.format(location.getAccuracy()));
        directiongps.setText(df.format(location.getBearing()));
        Date date = new Date(location.getTime());
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("CET"));
        dthgps.setText(formatter.format(date));
        dth.setText(formatter.format( System.currentTimeMillis()));
      }
    };
    initializeCommands = new String[]{"ATL0", "ATE1", "ATH1", "ATAT1", "ATSTFF", "ATI", "ATDP", "ATSP0", "0100"};

    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (mBluetoothAdapter == null) {
      Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
    }
    else
    {
      if (mBtService != null) {
        if (mBtService.getState() == BluetoothService.STATE_NONE) {
          mBtService.start();
        }
      }
    }

    // Initialize the array adapter for the conversation thread
    mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message) {
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        // Get the Item from ListView
        View view = super.getView(position, convertView, parent);

        // Initialize a TextView for ListView each Item
        TextView tv = (TextView) view.findViewById(R.id.listText);

        // Set the text color of TextView (ListView Item)
        tv.setTextColor(Color.parseColor("#3ADF00"));
        tv.setTextSize(10);

        // Generate ListView Item using TextView
        return view;
      }
    };

    mConversationView.setAdapter(mConversationArrayAdapter);

    Button mPidsButton = findViewById(R.id.button_pids);
    mPidsButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        String sPIDs = "0100";
        m_getPids = false;
        sendEcuMessage(sPIDs);
      }
    });

    Button mSendPointButton = findViewById(R.id.button_sendpoint);
    mSendPointButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        SendPoint();
      }
    });

    //GetReleves();

    //start location debug
    startLocationUpdates();

    binding.fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
      }
    });
  }

  public void addReleve() {
    // populate from server GetReleves
    try {
      String url = getString(R.string.url_server) + "addreleves.php";
      String data = "username="+ URLEncoder.encode(UserName, "UTF-8");
      data += "&password="+ URLEncoder.encode(UserPW, "UTF-8");
      data += "&v_id="+ URLEncoder.encode(VehicleID, "UTF-8");
      new httpPostRequestAddPoint(url, data).execute(""); //same call back
    } catch (Exception e) {
      e.printStackTrace();
      Log.d("Error", "error");
    }
  }
  public void UpdateRelevesAdapter() {
    // populate from server GetReleves
    try {
      String url = getString(R.string.url_server) + "getreleves.php";
      String data = ""; //"last=1";
      new httpPostRequestReleves(url, data).execute("");
    } catch (Exception e) {
      e.printStackTrace();
      Log.d("Error", "error");
    }
  }

  private void SendPoint() {
    String ref = releve.getText().toString();
    if (ref.substring(0,1).equals("1")) {
      Toast.makeText(getBaseContext(), "Old releves, do not modify them",
              Toast.LENGTH_SHORT).show();
      return;
    }
    String SQL_PHP = "";
    // todo addpoint via PHP
    try {
      String url = getString(R.string.url_server) + "pt_i.php";
//      String data = URLEncoder.encode("ref_releve", "UTF-8")+"="+ ref;
      String data = "ref_releve="+ ref;
      data += "&latitude="+ latitude.getText().toString();
      data += "&longitude="+ longitude.getText().toString();
      data += "&altitude="+ altitude.getText().toString();
      data += "&nbrsatgps="+ nbrsatgps.getText().toString();
      data += "&vitessegps="+ vitessegps.getText().toString();
      data += "&directiongps="+ directiongps.getText().toString();
      data += "&dthgps="+ dthgps.getText().toString();
      data += "&vitessemoy="+ speed.getText().toString();
      data += "&coolant_temperature="+ coolantTemperature.getText().toString();
      data += "&maf="+ Maf.getText().toString();
      data += "&engine_load="+ engineLoad.getText().toString();
      data += "&rpm="+ rpm.getText().toString();

      new httpPostRequestAddPoint(url, data).execute("");
//      Toast.makeText(getBaseContext(), response, Toast.LENGTH_SHORT).show();
    } catch (Exception e) {
      e.printStackTrace();
      Log.d("Error", "error");
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    this.menu = menu;
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
      Intent intent = new Intent(this, SettingsActivity.class);
      startActivity(intent);
      return true;
    }

    if (id == R.id.action_about) {
      Intent intent = new Intent(this, AboutActivity.class);
      startActivity(intent);
      return true;
    }

    if (id == R.id.action_bt_connect) {
      if (!mBluetoothAdapter.isEnabled()) {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        return false;
      }

      if (mBtService == null) setupChat();

      if (item.getTitle().equals(getString(R.string.action_bt_connect))) { // R.string.action_bt_connect
        // Launch the DeviceListActivity to see devices and do scan
        serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
      } else {
        if (mBtService != null)
        {
          mBtService.stop();
          item.setTitle(getString(R.string.action_bt_connect));
        }
      }
      return true;
    }

    if (id == R.id.action_bt_setting) {
      Intent intent = new Intent(this, DeviceListActivity.class);
      startActivity(intent);
      return true;
    }
    if (id == R.id.action_add_releve) {
      addReleve();
      return true;
    }
    if (id == R.id.action_update_releve) {
      UpdateRelevesAdapter();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void startLocationUpdates() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
      Toast.makeText( getApplicationContext(), "Start Localisation", Toast.LENGTH_SHORT).show();

    } else {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                locationRequestCode);
      }
    }
  }

  // BT and ECU routines
  private final Handler mBtHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {

      switch (msg.what) {
        case MESSAGE_STATE_CHANGE:

          switch (msg.arg1) {
            case BluetoothService.STATE_CONNECTED:

              Status.setText(getString(R.string.title_connected_to, mConnectedDeviceName));
              Info.setText(R.string.title_connected);
              try {
                itemtemp = menu.findItem(R.id.action_bt_connect);
                itemtemp.setTitle(R.string.action_bt_disconnect);
                Info.setText(R.string.title_connected);
              } catch (Exception e) {
              }

              tryconnect = false;
              resetvalues();
              sendEcuMessage(RESET);

              break;
            case BluetoothService.STATE_CONNECTING:
              Status.setText(R.string.title_connecting);
              Info.setText(R.string.tryconnectbt);
              break;
            case BluetoothService.STATE_LISTEN:

            case BluetoothService.STATE_NONE:

              Status.setText(R.string.title_not_connected);
              itemtemp = menu.findItem(R.id.action_bt_connect);
              itemtemp.setTitle(R.string.action_bt_connect);
              if (tryconnect) {
                mBtService.connect(currentdevice);
                connectcount++;
                if (connectcount >= 2) {
                  tryconnect = false;
                }
              }
              resetvalues();

              break;
          }
          break;
        case MESSAGE_WRITE:

          byte[] writeBuf = (byte[]) msg.obj;
          String writeMessage = new String(writeBuf);

          if (commandmode || !initialized) {
            mConversationArrayAdapter.add("Command:  " + writeMessage);
          }

          break;
        case MESSAGE_READ:

          String tmpmsg = clearMsg(msg);

          Info.setText(tmpmsg);

                    /*if (tmpmsg.contains(RSP_ID.NODATA.response) || tmpmsg.contains(RSP_ID.ERROR.response)) {

                        try{
                            String command = tmpmsg.substring(0,4);

                            if(isHexadecimal(command))
                            {
                                removePID(command);
                            }

                        }catch(Exception e)
                        {
                            Toast.makeText(getApplicationContext(), e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        }
                    }*/

          if (commandmode || !initialized) {
            mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + tmpmsg);
          }

          analysMsg(msg);

          break;
        case MESSAGE_DEVICE_NAME:
          // save the connected device's name
          mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
          break;
        case MESSAGE_TOAST:
          Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                  Toast.LENGTH_SHORT).show();
          break;
      }
    }
  };


  public void resetvalues() {

    engineLoad.setText("0 %");
    voltage.setText("0 V");
    coolantTemperature.setText("0 C°");
    Info.setText("");
    airTemperature.setText("0 C°");
    Maf.setText("0 g/s");
    Fuel.setText("0 - 0 l/h");

    m_getPids = false;
    whichCommand = 0;
    trycount = 0;
    initialized = false;
    defaultStart = false;
    avgconsumption.clear();
    mConversationArrayAdapter.clear();
  }

  private void connectDevice(Intent data) {
    tryconnect = true;
    // Get the device MAC address
    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
    // Get the BluetoothDevice object
    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
    try {
      // Attempt to connect to the device
      mBtService.connect(device);
      currentdevice = device;

    } catch (Exception e) {
    }
  }

  private void setupChat() {

    // Initialize the BluetoothChatService to perform bluetooth connections
    mBtService = new BluetoothService(this, mBtHandler);

  }

  private void sendEcuMessage(String message) {

    if (mBtService != null)
    {
      // Check that we're actually connected before trying anything
      if (mBtService.getState() != BluetoothService.STATE_CONNECTED) {
        //Toast.makeText(this, R.string.not_connected, Toast.LENGTH_LONG).show();
        return;
      }
      try {
        if (message.length() > 0) {

          message = message + "\r";
          // Get the message bytes and tell the BluetoothChatService to write
          byte[] send = message.getBytes();
          mBtService.write(send);
        }
      } catch (Exception e) {
      }
    }
  }

  private void sendInitCommands() {
    if (initializeCommands.length != 0) {

      if (whichCommand < 0) {
        whichCommand = 0;
      }

      String send = initializeCommands[whichCommand];
      sendEcuMessage(send);

      if (whichCommand == initializeCommands.length - 1) {
        initialized = true;
        whichCommand = 0;
        sendDefaultCommands();
      } else {
        whichCommand++;
      }
    }
  }

  private void sendDefaultCommands() {

    if (commandslist.size() != 0) {

      if (whichCommand < 0) {
        whichCommand = 0;
      }

      String send = commandslist.get(whichCommand);
      sendEcuMessage(send);

      if (whichCommand >= commandslist.size() - 1) {
        whichCommand = 0;
      } else {
        whichCommand++;
      }
    }
  }

  private String clearMsg(Message msg) {
    String tmpmsg = msg.obj.toString();

    tmpmsg = tmpmsg.replace("null", "");
    tmpmsg = tmpmsg.replaceAll("\\s", ""); //removes all [ \t\n\x0B\f\r]
    tmpmsg = tmpmsg.replaceAll(">", "");
    tmpmsg = tmpmsg.replaceAll("SEARCHING...", "");
    tmpmsg = tmpmsg.replaceAll("ATZ", "");
    tmpmsg = tmpmsg.replaceAll("ATI", "");
    tmpmsg = tmpmsg.replaceAll("atz", "");
    tmpmsg = tmpmsg.replaceAll("ati", "");
    tmpmsg = tmpmsg.replaceAll("ATDP", "");
    tmpmsg = tmpmsg.replaceAll("atdp", "");
    tmpmsg = tmpmsg.replaceAll("ATRV", "");
    tmpmsg = tmpmsg.replaceAll("atrv", "");

    return tmpmsg;
  }

  private void checkPids(String tmpmsg) {
    if (tmpmsg.indexOf("41") != -1) {
      int index = tmpmsg.indexOf("41");

      String pidmsg = tmpmsg.substring(index, tmpmsg.length());

      if (pidmsg.contains("4100")) {

        setPidsSupported(pidmsg);
        return;
      }
    }
  }

  private void analysMsg(Message msg) {

    String tmpmsg = clearMsg(msg);

    generateVolt(tmpmsg);

    getElmInfo(tmpmsg);

    if (!initialized) {

      sendInitCommands();

    } else {

      checkPids(tmpmsg);

      if (!m_getPids && m_dedectPids == 1) {
        String sPIDs = "0100";
        sendEcuMessage(sPIDs);
        return;
      }

      if (commandmode) {
  //      getFaultInfo(tmpmsg);
        return;
      }

      try {
        analysPIDS(tmpmsg);
      } catch (Exception e) {
        Info.setText("Error : " + e.getMessage());
      }

      sendDefaultCommands();
    }
  }

//  private void getFaultInfo(String tmpmsg) {
//
//    String substr = "43";
//
//    int index = tmpmsg.indexOf(substr);
//
//    if (index == -1)
//    {
//      substr = "47";
//      index = tmpmsg.indexOf(substr);
//    }
//
//    if (index != -1) {
//
//      tmpmsg = tmpmsg.substring(index, tmpmsg.length());
//
//      if (tmpmsg.substring(0, 2).equals(substr)) {
//
//        performCalculations(tmpmsg);
//
//        String faultCode = null;
//        String faultDesc = null;
//
//        if (troubleCodesArray.size() > 0) {
//
//          for (int i = 0; i < troubleCodesArray.size(); i++) {
//            faultCode = troubleCodesArray.get(i);
//            faultDesc = troubleCodes.getFaultCode(faultCode);
//
//            Log.e(TAG, "Fault Code: " + substr + " : " + faultCode + " desc: " + faultDesc);
//
//            if (faultCode != null && faultDesc != null) {
//              mConversationArrayAdapter.add(mConnectedDeviceName + ":  TroubleCode -> " + faultCode + "\n" + faultDesc);
//            } else if (faultCode != null && faultDesc == null) {
//              mConversationArrayAdapter.add(mConnectedDeviceName + ":  TroubleCode -> " + faultCode +
//                      "\n" + "Definition not found for code: " + faultCode);
//            }
//          }
//        } else {
//          faultCode = "No error found...";
//          mConversationArrayAdapter.add(mConnectedDeviceName + ":  TroubleCode -> " + faultCode);
//        }
//      }
//    }
//  }

  protected void performCalculations(String fault) {

    final String result = fault;
    String workingData = "";
    int startIndex = 0;
//    troubleCodesArray.clear();

    try{

      if(result.indexOf("43") != -1)
      {
        workingData = result.replaceAll("^43|[\r\n]43|[\r\n]", "");
      }else if(result.indexOf("47") != -1)
      {
        workingData = result.replaceAll("^47|[\r\n]47|[\r\n]", "");
      }

      for (int begin = startIndex; begin < workingData.length(); begin += 4) {
        String dtc = "";
        byte b1 = hexStringToByteArray(workingData.charAt(begin));
        int ch1 = ((b1 & 0xC0) >> 6);
        int ch2 = ((b1 & 0x30) >> 4);
        dtc += dtcLetters[ch1];
        dtc += hexArray[ch2];
        dtc += workingData.substring(begin + 1, begin + 4);

        if (dtc.equals("P0000")) {
          continue;
        }

//        troubleCodesArray.add(dtc);
      }
    }catch(Exception e)
    {
      Log.e(TAG, "Error: " + e.getMessage());
    }
  }

  private byte hexStringToByteArray(char s) {
    return (byte) ((Character.digit(s, 16) << 4));
  }

  private void getElmInfo(String tmpmsg) {

    if (tmpmsg.contains("ELM") || tmpmsg.contains("elm")) {
      devicename = tmpmsg;
    }

    if (tmpmsg.contains("SAE") || tmpmsg.contains("ISO")
            || tmpmsg.contains("sae") || tmpmsg.contains("iso") || tmpmsg.contains("AUTO")) {
      deviceprotocol = tmpmsg;
    }

    if (deviceprotocol != null && devicename != null) {
      devicename = devicename.replaceAll("STOPPED", "");
      deviceprotocol = deviceprotocol.replaceAll("STOPPED", "");
      Status.setText(devicename + " " + deviceprotocol);
    }
  }


  private void setPidsSupported(String buffer) {
    Info.setText("Trying to get available pids : " + String.valueOf(trycount));
    trycount++;
    StringBuilder flags = new StringBuilder();
    String buf = buffer.toString();
    buf = buf.trim();
    buf = buf.replace("\t", "");
    buf = buf.replace(" ", "");
    buf = buf.replace(">", "");
    if (buf.indexOf("4100") == 0 || buf.indexOf("4120") == 0) {
      for (int i = 0; i < 8; i++) {
        String tmp = buf.substring(i + 4, i + 5);
        int data = Integer.valueOf(tmp, 16).intValue();
//                String retStr = Integer.toBinaryString(data);
        if ((data & 0x08) == 0x08) {
          flags.append("1");
        } else {
          flags.append("0");
        }

        if ((data & 0x04) == 0x04) {
          flags.append("1");
        } else {
          flags.append("0");
        }

        if ((data & 0x02) == 0x02) {
          flags.append("1");
        } else {
          flags.append("0");
        }

        if ((data & 0x01) == 0x01) {
          flags.append("1");
        } else {
          flags.append("0");
        }
      }

      commandslist.clear();
      commandslist.add(0, VOLTAGE);
      int pid = 1;

      StringBuilder supportedPID = new StringBuilder();
      supportedPID.append("Supported PIDS:\n");
      for (int j = 0; j < flags.length(); j++) {
        if (flags.charAt(j) == '1') {
          supportedPID.append(" " + PIDS[j] + " ");
          if (!PIDS[j].contains("11") && !PIDS[j].contains("01") && !PIDS[j].contains("20")) {
            commandslist.add(pid, "01" + PIDS[j]);
            pid++;
          }
        }
      }
      m_getPids = true;
      mConversationArrayAdapter.add(mConnectedDeviceName + ": " + supportedPID.toString());
      whichCommand = 0;
      sendEcuMessage("ATRV");
    }
  }

  private double calculateAverage(List<Double> listavg) {
    Double sum = 0.0;
    for (Double val : listavg) {
      sum += val;
    }
    return sum.doubleValue() / listavg.size();
  }

  private void analysPIDS(String dataRecieved) {
    int A = 0;
    int B = 0;
    int PID = 0;
    if ((dataRecieved != null) && (dataRecieved.matches("^[0-9A-F]+$"))) {
      dataRecieved = dataRecieved.trim();
      int index = dataRecieved.indexOf("41");
      String tmpmsg = null;
      if (index != -1) {
        tmpmsg = dataRecieved.substring(index, dataRecieved.length());
        if (tmpmsg.substring(0, 2).equals("41")) {
          PID = Integer.parseInt(tmpmsg.substring(2, 4), 16);
          A = Integer.parseInt(tmpmsg.substring(4, 6), 16);
          B = Integer.parseInt(tmpmsg.substring(6, 8), 16);
          calculateEcuValues(PID, A, B);
        }
      }
    }
  }

  private void generateVolt(String msg) {
    String VoltText = null;
    if ((msg != null) && (msg.matches("\\s*[0-9]{1,2}([.][0-9]{1,2})\\s*"))) {
      VoltText = msg + "V";
      mConversationArrayAdapter.add(mConnectedDeviceName + ": " + msg + "V");
    } else if ((msg != null) && (msg.matches("\\s*[0-9]{1,2}([.][0-9]{1,2})?V\\s*"))) {
      VoltText = msg;
      mConversationArrayAdapter.add(mConnectedDeviceName + ": " + msg);
    }
    if (VoltText != null) {
      voltage.setText(VoltText);
    }
  }

  private void calculateEcuValues(int PID, int A, int B) {
    double val = 0;
    int intval = 0;
    int tempC = 0;
    switch (PID) {
      case 4://PID(04): Engine Load
        // A*100/255
        val = A * 100 / 255;
        int calcLoad = (int) val;
        engineLoad.setText(Integer.toString(calcLoad) + " %");
        mConversationArrayAdapter.add("Engine Load: " + Integer.toString(calcLoad) + " %");
        double FuelFlowLH = (mMaf * calcLoad * mEnginedisplacement / 1000.0 / 714.0) + 0.8;
        if(calcLoad == 0)  FuelFlowLH = 0;
        avgconsumption.add(FuelFlowLH);
        Fuel.setText(String.format("%10.1f", calculateAverage(avgconsumption)).trim() + " l/h");
        mConversationArrayAdapter.add("Fuel Consumption: " + String.format("%10.1f", calculateAverage(avgconsumption)).trim() + " l/h");
        break;

      case 5://PID(05): Coolant Temperature
        // A-40
        tempC = A - 40;
        coolantTemp = tempC;
        coolantTemperature.setText(Integer.toString(coolantTemp) + " C°");
        mConversationArrayAdapter.add("Enginetemp: " + Integer.toString(tempC) + " C°");
        break;

      case 11://PID(0B)
        // A
        mConversationArrayAdapter.add("Intake Man Pressure: " + Integer.toString(A) + " kPa");
        break;

      case 12: //PID(0C): RPM
        //((A*256)+B)/4
        val = ((A * 256) + B) / 4;
        rpm.setText(String.format("%0.2f", val) + " rpm");
        break;

      case 13://PID(0D): KM
        // A
        speed.setText(String.format("%0.2f", A) + " km/h");
        break;

      case 15://PID(0F): Intake Temperature
        // A - 40
        tempC = A - 40;
        intakeairtemp = tempC;
        airTemperature.setText(Integer.toString(intakeairtemp) + " C°");
        mConversationArrayAdapter.add("Intakeairtemp: " + Integer.toString(intakeairtemp) + " C°");
        break;

      case 16://PID(10): Maf
        // ((256*A)+B) / 100  [g/s]
        val = ((256 * A) + B) / 100;
        mMaf = (int) val;
        Maf.setText(Integer.toString(intval) + " g/s");
        mConversationArrayAdapter.add("Maf Air Flow: " + Integer.toString(mMaf) + " g/s");
        break;

      case 17://PID(11)
        //A*100/255
        val = A * 100 / 255;
        intval = (int) val;
        mConversationArrayAdapter.add(" Throttle position: " + Integer.toString(intval) + " %");
        break;

      case 35://PID(23)

        // ((A*256)+B)*0.079
        val = ((A * 256) + B) * 0.079;
        intval = (int) val;
        mConversationArrayAdapter.add("Fuel Rail Pressure: " + Integer.toString(intval) + " kPa");
        break;

      case 49://PID(31)
        //(256*A)+B km
        val = (A * 256) + B;
        intval = (int) val;
        mConversationArrayAdapter.add("Distance traveled: " + Integer.toString(intval) + " km");
        break;

      case 70://PID(46)
        // A-40 [DegC]
        tempC = A - 40;
        ambientairtemp = tempC;
        mConversationArrayAdapter.add("Ambientairtemp: " + Integer.toString(ambientairtemp) + " C°");
        break;

      case 92://PID(5C)
        //A-40
        tempC = A - 40;
        engineoiltemp = tempC;
        mConversationArrayAdapter.add("Engineoiltemp: " + Integer.toString(engineoiltemp) + " C°");
        break;
      default:
    }
  }

  enum RSP_ID {
    PROMPT(">"),
    OK("OK"),
    MODEL("ELM"),
    NODATA("NODATA"),
    SEARCH("SEARCHING"),
    ERROR("ERROR"),
    NOCONN("UNABLE"),
    NOCONN_MSG("UNABLE TO CONNECT"),
    NOCONN2("NABLETO"),
    CANERROR("CANERROR"),
    CONNECTED("ECU CONNECTED"),
    BUSBUSY("BUSBUSY"),
    BUSY("BUSY"),
    BUSERROR("BUSERROR"),
    BUSINIERR("BUSINIT:ERR"),
    BUSINIERR2("BUSINIT:BUS"),
    BUSINIERR3("BUSINIT:...ERR"),
    BUS("BUS"),
    FBERROR("FBERROR"),
    DATAERROR("DATAERROR"),
    BUFFERFULL("BUFFERFULL"),
    STOPPED("STOPPED"),
    RXERROR("<"),
    QMARK("?"),
    UNKNOWN("");
    private String response;

    RSP_ID(String response) {
      this.response = response;
    }

    @Override
    public String toString() {
      return response;
    }
  }


  class httpPostRequestReleves extends AsyncTask<String, String, String> {
    ProgressDialog pd;
    String url, data;

    public httpPostRequestReleves(String url, String data) {
      this.url = url;
      this.data = data;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      pd = new ProgressDialog(MainActivity.this);
      pd.setMessage("Wait");
      pd.setMax(100);
      pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
      pd.setCancelable(true);
      pd.show();
    }

    @Override
    protected String doInBackground(String... name) {
      String response = "";
      BufferedReader reader = null;
      HttpURLConnection conn = null;
      try {
        Log.d("RequestManager", url + " ");
        Log.d("data ", data);
        URL urlObj = new URL(url);

        conn = (HttpURLConnection) urlObj.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(data);
        wr.flush();
        Log.d("post response code", conn.getResponseCode() + " ");
        int responseCode = conn.getResponseCode();
        reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line = null;

        while ((line = reader.readLine()) != null) {
          sb.append(line );
        }

        response = sb.toString();
      } catch (Exception e) {
        Log.e("Error: ", e.getMessage());
      }
      return response;
    }

    protected void onProgressUpdate(String... progress) {
      if (pd != null) pd.setProgress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(String response) {
      if (pd != null) {
        pd.dismiss();
      }
      Log.d("Recorded : ",  response);
//      Toast.makeText(getBaseContext(), "Recorded"
//                      + " : " + response
//              , Toast.LENGTH_SHORT).show();
      if (response.isEmpty()) return;
// decode
//[{"ID":10,"REF":21001,"DTH_DEBUT":"2021-11-13 21:05:00","DTH_FIN":"0000-00-00 00:00:00","ID_VEHICULE":5,"ID_USER":2,"REF_DTH_DEBUT":"21001 2021-11-13 21:05:00"}]
// [{"REF":12002,"REF_DTH_DEBUT":"12002 2012-03-08 06:51:20"},{"REF":12006,"REF_DTH_DEBUT":"12006 2012-03-08 20:22:29"},{"REF":12007,"REF_DTH_DEBUT":"12007 2012-03-08 18:30:22"},{"REF":12009,"REF_DTH_DEBUT":"12009 2012-03-10 10:45:30"},{"REF":12011,"REF_DTH_DEBUT":"12011 2012-03-11 09:38:43"},{"REF":12013,"REF_DTH_DEBUT":"12013 2012-03-11 15:27:30"},{"REF":12023,"REF_DTH_DEBUT":"12023 2012-06-21 09:36:41"},{"REF":12020,"REF_DTH_DEBUT":"12020 2012-06-20 18:59:13"},{"REF":12024,"REF_DTH_DEBUT":"12024 2012-06-22 11:46:31"}]
      JSONObject obj = null;
      JSONObject jsonRootObject = null;
      JSONArray jsonArray = null;
      String ref_dth_debut;
      ArrayList<String> list = new ArrayList<String>();
      try {
        jsonArray = new JSONArray(response);
        for(int i= 0; i<jsonArray.length(); i++)
        {
          obj = jsonArray.getJSONObject(i);
          ref_dth_debut =  obj.optString("REF_DTH_DEBUT");
          list.add(ref_dth_debut);
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
      releves_adapter.clear();
      releves_adapter.addAll(list);
      releves_adapter.notifyDataSetChanged();
      int count = sp_releve.getCount();
      if (count!=0) sp_releve.setSelection( count-1);
    }
  }

  class httpPostRequestAddPoint extends AsyncTask<String, String, String> {
    ProgressDialog pd;
    String url, data;

    public httpPostRequestAddPoint(String url, String data) {
      this.url = url;
      this.data = data;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      pd = new ProgressDialog(MainActivity.this);
      pd.setMessage("Wait");
      pd.setMax(100);
      pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
      pd.setCancelable(true);
      pd.show();
    }

    @Override
    protected String doInBackground(String... name) {
      String response = "";
      BufferedReader reader = null;
      HttpURLConnection conn = null;
      try {
        Log.d("RequestManager", url + " ");
        Log.e("data ", data);
        URL urlObj = new URL(url);

        conn = (HttpURLConnection) urlObj.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(data);
        wr.flush();
        Log.d("post response code", conn.getResponseCode() + " ");
        int responseCode = conn.getResponseCode();
        reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
          sb.append(line );
        }

        response = sb.toString();
      } catch (Exception e) {
        Log.e("Error: ", e.getMessage());
      }
      return response;
    }

    protected void onProgressUpdate(String... progress) {
      if (pd != null) pd.setProgress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(String name) {
      if (pd != null) {
        pd.dismiss();
      }
      Toast.makeText(getBaseContext(), "Recorded"
                      + " : " + name
              , Toast.LENGTH_SHORT).show();
    }
  }

}