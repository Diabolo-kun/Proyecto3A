
package com.example.mpergar9.detble;
// ------------------------------------------------------------------
// ------------------------------------------------------------------

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// ------------------------------------------------------------------
// ------------------------------------------------------------------

public class MainActivity extends AppCompatActivity {

    // Actividad formulario API
    public void AbrirAPI(View view) {
        Intent intent = new Intent(this, ApiFormActivity.class);
        startActivity(intent);
    }

    private EditText editTextEntrada, dispbusqueda;
    private TextView textValorMajor, textValorMinor;
    private CheckBox checkBoxConfirmacion;
    private Button botonEnviar;

    private static final String ETIQUETA_LOG = ">>>>";
    private static final int CODIGO_PETICION_PERMISOS = 11223344;
    private BluetoothLeScanner elEscanner;
    private ScanCallback callbackDelEscaneo = null;

    private int majorGuardado = 0;
    private int minorGuardado = 0;
    private String majorParte1 = "00";
    private String majorParte2 = "00";

    private void actualizarValoresIBeacon(int major, int minor) {
        majorGuardado = major;
        minorGuardado = minor;

        // Convertimos a 4 dígitos con ceros a la izquierda
        String majorStr = String.format("%04d", major);
        majorParte1 = majorStr.substring(0, 2);
        majorParte2 = majorStr.substring(2, 4);

        if (textValorMajor != null && textValorMinor != null) {
            textValorMajor.setText("Valor major:( " + majorParte1 + " - " + majorParte2 + " )");
            textValorMinor.setText("Valor minor: " + minor);
        }

        // Si el checkbox está activado, enviamos automáticamente
        if (checkBoxConfirmacion.isChecked()) {
            enviarDatos();
        }
    }

    private void enviarDatos() {
        // Aquí defines qué quieres hacer con los valores
        Log.d(ETIQUETA_LOG, "Enviando → Major: "
                + majorParte1 + "-" + majorParte2
                + ", Minor: " + minorGuardado);
        String ip = editTextEntrada.getText().toString().trim();
    }

    public void botonEnviarPulsado(View v) {
        // Solo se ejecuta si el checkbox está desmarcado
        if (!checkBoxConfirmacion.isChecked()) {
            enviarDatos();
        } else {
            Toast.makeText(this, "El envío es automático", Toast.LENGTH_SHORT).show();
        }
    }

    private void buscarTodosLosDispositivosBTLE() {
        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): empieza ");

        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): instalamos scan callback ");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            Log.d(ETIQUETA_LOG, " Permiso para escanear: NO ");
            return;
        }
        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult( int callbackType, ScanResult resultado ) {
                super.onScanResult(callbackType, resultado);
                //Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onScanResult() ");

                //if (resultado.getScanRecord().getDeviceName()!=null){
                    mostrarInformacionDispositivoBTLE(resultado);
                //}
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                //Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onBatchScanResults() ");

            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                //Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onScanFailed() ");

            }
        };

        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): empezamos a escanear ");


        this.elEscanner.startScan( this.callbackDelEscaneo);

    } // ()

    private void mostrarInformacionDispositivoBTLE( ScanResult resultado ) {

        BluetoothDevice bluetoothDevice = resultado.getDevice();
        byte[] bytes = resultado.getScanRecord().getBytes();
        int rssi = resultado.getRssi();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            Log.d(ETIQUETA_LOG, " Permiso Bluetooth: NO ");
            return;
        }

        //BluetoothDevice bluetoothDevice = resultado.getDevice();
        //byte[] bytes = resultado.getScanRecord() != null ? resultado.getScanRecord().getBytes() : new byte[0];
        //int rssi = resultado.getRssi();

        // Intentamos obtener el nombre del dispositivo
        //String nombre = bluetoothDevice.getName(); // puede ser null
        //if (nombre == null && resultado.getScanRecord() != null) {
        //    nombre = resultado.getScanRecord().getDeviceName(); // nombre publicado en el advertising
        //}
        //if (nombre == null) nombre = "DESCONOCIDO";

        //if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
        //    return;
        //}
        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " ****** DISPOSITIVO DETECTADO BTLE ****************** ");
        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " nombre = " + bluetoothDevice.getName() + " / " + resultado.getScanRecord().getDeviceName());
        Log.d(ETIQUETA_LOG, " toString = " + bluetoothDevice.toString());

        /*
        ParcelUuid[] puuids = bluetoothDevice.getUuids();
        if ( puuids.length >= 1 ) {
            //Log.d(ETIQUETA_LOG, " uuid = " + puuids[0].getUuid());
           // Log.d(ETIQUETA_LOG, " uuid = " + puuids[0].toString());
        }*/

        Log.d(ETIQUETA_LOG, " dirección = " + bluetoothDevice.getAddress());
        Log.d(ETIQUETA_LOG, " rssi = " + rssi );

        Log.d(ETIQUETA_LOG, " bytes = " + new String(bytes));
        Log.d(ETIQUETA_LOG, " bytes (" + bytes.length + ") = " + Utilidades.bytesToHexString(bytes));

        TramaIBeacon tib = new TramaIBeacon(bytes);

        Log.d(ETIQUETA_LOG, " ----------------------------------------------------");
        Log.d(ETIQUETA_LOG, " prefijo  = " + Utilidades.bytesToHexString(tib.getPrefijo()));
        Log.d(ETIQUETA_LOG, "          advFlags = " + Utilidades.bytesToHexString(tib.getAdvFlags()));
        Log.d(ETIQUETA_LOG, "          advHeader = " + Utilidades.bytesToHexString(tib.getAdvHeader()));
        Log.d(ETIQUETA_LOG, "          companyID = " + Utilidades.bytesToHexString(tib.getCompanyID()));
        Log.d(ETIQUETA_LOG, "          iBeacon type = " + Integer.toHexString(tib.getiBeaconType()));
        Log.d(ETIQUETA_LOG, "          iBeacon length 0x = " + Integer.toHexString(tib.getiBeaconLength()) + " ( "
                + tib.getiBeaconLength() + " ) ");
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToHexString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " major  = " + Utilidades.bytesToHexString(tib.getMajor()) + "( "
                + Utilidades.bytesToInt(tib.getMajor()) + " ) ");
        Log.d(ETIQUETA_LOG, " minor  = " + Utilidades.bytesToHexString(tib.getMinor()) + "( "
                + Utilidades.bytesToInt(tib.getMinor()) + " ) ");
        Log.d(ETIQUETA_LOG, " txPower  = " + Integer.toHexString(tib.getTxPower()) + " ( " + tib.getTxPower() + " )");
        Log.d(ETIQUETA_LOG, " ****************************************************");

    } // ()

    private void buscarEsteDispositivoBTLE(final String dispositivoBuscado) {
        Log.d(ETIQUETA_LOG, "buscarEsteDispositivoBTLE(): Start search: " + dispositivoBuscado);

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);

                Log.d(ETIQUETA_LOG, "Dispositivo encontrado: " + dispositivoBuscado);
                mostrarInformacionDispositivoBTLE(resultado);

                //Actualizamos los TextView con major/minor
                TramaIBeacon tib = new TramaIBeacon(resultado.getScanRecord().getBytes());
                int major = Utilidades.bytesToInt(tib.getMajor());
                int minor = Utilidades.bytesToInt(tib.getMinor());
                actualizarValoresIBeacon(major, minor);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.e(ETIQUETA_LOG, "onScanFailed(): " + errorCode);
            }
        };


        List<ScanFilter> filtros = new ArrayList<>();
        filtros.add(new ScanFilter.Builder()
                .setDeviceName(dispositivoBuscado)
                .build());

        //ScanSettings settings = new ScanSettings.Builder()
          //      .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            //    .build();

        android.bluetooth.le.ScanSettings settings =
                new android.bluetooth.le.ScanSettings.Builder()
                        .setScanMode(android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado );
            this.elEscanner.startScan(filtros, settings, this.callbackDelEscaneo);
        } else {
            Log.e(ETIQUETA_LOG, "No hay permiso para hacer startScan()");
        }
    }

    private void detenerBusquedaDispositivosBTLE() {

        if ( this.callbackDelEscaneo == null ) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            Log.d(ETIQUETA_LOG, " Permiso para escanear: NO ");
            return;
        }
        this.elEscanner.stopScan( this.callbackDelEscaneo );
        this.callbackDelEscaneo = null;

    } // ()

    public void botonBuscarDispositivosBTLEPulsado( View v ) {
        Log.d(ETIQUETA_LOG, " boton buscar dispositivos BTLE Pulsado" );
        this.buscarTodosLosDispositivosBTLE();
    } // ()

    public void botonBuscarNuestroDispositivoBTLEPulsado( View v ) {
        Log.d(ETIQUETA_LOG, "Botón nuestro dispositivo BTLE Pulsado");

        String nombreBuscado = dispbusqueda.getText().toString().trim();
        if (nombreBuscado.isEmpty()) {
            Toast.makeText(this, "Introduce un nombre de dispositivo", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(ETIQUETA_LOG, "Buscando dispositivo: " + nombreBuscado);
        this.buscarEsteDispositivoBTLE(nombreBuscado);

    } // ()

    public void botonDetenerBusquedaDispositivosBTLEPulsado( View v ) {
        Log.d(ETIQUETA_LOG, " boton detener busqueda dispositivos BTLE Pulsado" );
        this.detenerBusquedaDispositivosBTLE();
    } // ()

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void inicializarBlueTooth() {
        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): obtenemos adaptador BT");

        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();

        if (bta == null) {
            Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): Socorro: NO hay adaptador BT");
            return;
        }

        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): habilitamos adaptador BT");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    CODIGO_PETICION_PERMISOS
            );

            Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): permisos solicitados");
            return; // importante: no seguimos hasta que el usuario acepte
        } else {
            Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): ya tengo todos los permisos necesarios");
        }

        if (!bta.isEnabled()) {
            bta.enable();
        }

        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): habilitado = " + bta.isEnabled());
        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): estado = " + bta.getState());

        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): obtenemos escaner BTLE");

        this.elEscanner = bta.getBluetoothLeScanner();
        if (this.elEscanner == null) {
            Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): Socorro: NO hemos obtenido escaner BTLE!!!!");
        }

        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): comprobando permisos de escaneo y ubicación");

        // Comprobamos permisos críticos para escanear BLE
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    CODIGO_PETICION_PERMISOS
            );
            Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): permisos solicitados");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(ETIQUETA_LOG, " onCreate(): empieza ");


        editTextEntrada = findViewById(R.id.editTextEntrada);
        textValorMajor = findViewById(R.id.valor1);
        textValorMinor = findViewById(R.id.valor2);
        checkBoxConfirmacion = findViewById(R.id.checkBoxConfirmacion);
        botonEnviar = findViewById(R.id.botonEnviar);
        dispbusqueda = findViewById(R.id.TextBusqueda);

        botonEnviar.setOnClickListener(this::botonEnviarPulsado);

        dispbusqueda.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                detenerBusquedaDispositivosBTLE();
                Log.d(ETIQUETA_LOG, "Texto búsqueda cambiado → detener búsqueda");
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        inicializarBlueTooth();

        Log.d(ETIQUETA_LOG, " onCreate(): termina ");
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults);

        switch (requestCode) {
            case CODIGO_PETICION_PERMISOS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(ETIQUETA_LOG, " onRequestPermissionResult(): permisos concedidos  !!!!");
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                }  else {

                    Log.d(ETIQUETA_LOG, " onRequestPermissionResult(): Socorro: permisos NO concedidos  !!!!");

                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    } // ()


} // class
// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------


