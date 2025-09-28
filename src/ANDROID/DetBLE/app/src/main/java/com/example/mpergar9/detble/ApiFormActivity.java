package com.example.mpergar9.detble;

import android.os.Bundle;
import android.os.StrictMode;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class ApiFormActivity extends AppCompatActivity {

    private EditText ipInput, tipomedicionInput, contadorInput, medicionInput, userInput, horaInput, localizacionInput;
    private TextView txtResponse;

    private LogicaAPI logicaApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_form);

        // Permitir red en el hilo principal (solo para pruebas)
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());

        ipInput = findViewById(R.id.ipInput);
        tipomedicionInput = findViewById(R.id.tipomedicionInput);
        contadorInput = findViewById(R.id.contadorInput);
        medicionInput = findViewById(R.id.medicionInput);
        userInput = findViewById(R.id.userInput);
        horaInput = findViewById(R.id.horaInput);
        localizacionInput = findViewById(R.id.localizacionInput);
        txtResponse = findViewById(R.id.txtResponse);

        logicaApi = new LogicaAPI();

        Button btnGetMediciones = findViewById(R.id.btnGetMediciones);
        Button btnPostMedicion = findViewById(R.id.btnPostMedicion);

        btnGetMediciones.setOnClickListener(v -> {
            String baseUrl = ipInput.getText().toString().trim();
            if (!baseUrl.isEmpty()) {
                txtResponse.setText(logicaApi.getMediciones(baseUrl));
            } else {
                Toast.makeText(this, "Introduce la IP del servidor", Toast.LENGTH_SHORT).show();
            }
        });

        btnPostMedicion.setOnClickListener(v -> {
            String baseUrl = ipInput.getText().toString().trim();
            if (!baseUrl.isEmpty()) {
                String jsonBody = logicaApi.buildJsonBody(
                        tipomedicionInput.getText().toString(),
                        contadorInput.getText().toString(),
                        medicionInput.getText().toString(),
                        userInput.getText().toString(),
                        horaInput.getText().toString(),
                        localizacionInput.getText().toString()
                );
                txtResponse.setText(logicaApi.postMedicion(baseUrl, jsonBody));
            } else {
                Toast.makeText(this, "Introduce la IP del servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
