package com.example.mpergar9.detble;

import android.os.Bundle;
import android.os.StrictMode;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

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
                android.util.Log.d("API_DEBUG", "➡ JSON a enviar: " + json);
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
        try {
            JSONObject obj = new JSONObject();
            obj.put("tipomedicion", tipomedicionInput.getText().toString().trim().isEmpty() ? 0 :
                    Integer.parseInt(tipomedicionInput.getText().toString().trim()));
            if (!contadorInput.getText().toString().trim().isEmpty()) {
                obj.put("contador", Integer.parseInt(contadorInput.getText().toString().trim()));
            }
            obj.put("medicion", medicionInput.getText().toString().trim().isEmpty() ? 0 :
                    Double.parseDouble(medicionInput.getText().toString().trim()));
            obj.put("user", userInput.getText().toString().trim().isEmpty() ? 0 :
                    Integer.parseInt(userInput.getText().toString().trim()));
            obj.put("hora", horaInput.getText().toString().trim());
            obj.put("localizacion", localizacionInput.getText().toString().trim());
            return obj.toString();
        } catch (Exception e) {
            return "{}"; // fallback seguro

        }
    }

    private String sendRequest(String urlString, String method, String jsonBody) {
        HttpURLConnection conn = null;
        try {
            android.util.Log.d("API_DEBUG", "🌍 URL: " + urlString);
            android.util.Log.d("API_DEBUG", "🔨 Método: " + method);

            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");

            if ("POST".equalsIgnoreCase(method) && jsonBody != null) {
                conn.setDoOutput(true);
                android.util.Log.d("API_DEBUG", "📤 Enviando body...");
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonBody.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }

            int code = conn.getResponseCode();
            android.util.Log.d("API_DEBUG", "📡 Código de respuesta: " + code);

            InputStream is = code < HttpURLConnection.HTTP_BAD_REQUEST
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }

            android.util.Log.d("API_DEBUG", "✅ Respuesta cruda: " + response.toString());

            return "✅ Respuesta:\n" + response.toString();

        } catch (Exception e) {
            android.util.Log.e("API_DEBUG", "❌ Error en sendRequest", e);
            return "❌ Error: " + e.getMessage();
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
}
