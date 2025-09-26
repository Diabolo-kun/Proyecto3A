package com.example.mpergar9.detble;

import android.os.Bundle;
import android.os.StrictMode;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiFormActivity extends AppCompatActivity {

    private EditText ipInput, tipomedicionInput, contadorInput, medicionInput, userInput, horaInput, localizacionInput;
    private TextView txtResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_form);

        // Para permitir red en el hilo principal (mejor sería usar AsyncTask/Thread, pero para pruebas rápidas sirve)
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());

        ipInput = findViewById(R.id.ipInput);
        tipomedicionInput = findViewById(R.id.tipomedicionInput);
        contadorInput = findViewById(R.id.contadorInput);
        medicionInput = findViewById(R.id.medicionInput);
        userInput = findViewById(R.id.userInput);
        horaInput = findViewById(R.id.horaInput);
        localizacionInput = findViewById(R.id.localizacionInput);
        txtResponse = findViewById(R.id.txtResponse);

        Button btnGetMediciones = findViewById(R.id.btnGetMediciones);
        Button btnPostMedicion = findViewById(R.id.btnPostMedicion);

        btnGetMediciones.setOnClickListener(v -> {
            String baseUrl = buildBaseUrl();
            if (baseUrl != null) {
                String res = sendRequest(baseUrl + "/mediciones", "GET", null);
                txtResponse.setText(res);
            }
        });

        btnPostMedicion.setOnClickListener(v -> {
            String baseUrl = buildBaseUrl();
            if (baseUrl != null) {
                String json = buildJsonBody();
                String res = sendRequest(baseUrl + "/mediciones", "POST", json);
                txtResponse.setText(res);
            }
        });
    }

    private String buildBaseUrl() {
        String ip = ipInput.getText().toString().trim();
        if (ip.isEmpty()) {
            Toast.makeText(this, "Introduce la IP del servidor", Toast.LENGTH_SHORT).show();
            return null;
        }
        return "http://" + ip + ":3000"; // puerto de tu API
    }

    private String buildJsonBody() {
        String tipomedicion = tipomedicionInput.getText().toString().trim();
        String contador = contadorInput.getText().toString().trim();
        String medicion = medicionInput.getText().toString().trim();
        String user = userInput.getText().toString().trim();
        String hora = horaInput.getText().toString().trim();
        String localizacion = localizacionInput.getText().toString().trim();

        StringBuilder sb = new StringBuilder("{");
        sb.append("\"tipomedicion\":").append(tipomedicion.isEmpty() ? "0" : tipomedicion).append(",");
        if (!contador.isEmpty()) sb.append("\"contador\":").append(contador).append(",");
        sb.append("\"medicion\":").append(medicion.isEmpty() ? "0" : medicion).append(",");
        sb.append("\"user\":").append(user.isEmpty() ? "0" : user).append(",");
        sb.append("\"hora\":\"").append(hora).append("\",");
        sb.append("\"localizacion\":\"").append(localizacion).append("\"");
        sb.append("}");
        return sb.toString();
    }

    private String sendRequest(String urlString, String method, String jsonBody) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");

            if ("POST".equalsIgnoreCase(method) && jsonBody != null) {
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonBody.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), "utf-8"));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }

            return "✅ Respuesta:\n" + response.toString();

        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
}
