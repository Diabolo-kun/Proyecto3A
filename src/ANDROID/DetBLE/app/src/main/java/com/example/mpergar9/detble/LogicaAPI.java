package com.example.mpergar9.detble;

import android.util.Log;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class LogicaAPI {

    public String buildJsonBody(String tipomedicion, String contador, String medicion,
                                String user, String hora, String localizacion) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("tipomedicion", tipomedicion.isEmpty() ? 0 : Integer.parseInt(tipomedicion));
            if (!contador.isEmpty()) obj.put("contador", Integer.parseInt(contador));
            obj.put("medicion", medicion.isEmpty() ? 0 : Double.parseDouble(medicion));
            obj.put("user", user.isEmpty() ? 0 : Integer.parseInt(user));
            obj.put("hora", hora);
            obj.put("localizacion", localizacion);
            return obj.toString();
        } catch (Exception e) {
            return "{}";
        }
    }

    public String getMediciones(String ip) {
        String url = "http://" + ip + ":3000/mediciones";
        return sendRequest(url, "GET", null);
    }

    public String postMedicion(String ip, String jsonBody) {
        String url = "http://" + ip + ":3000/mediciones";
        return sendRequest(url, "POST", jsonBody);
    }

    private String sendRequest(String urlString, String method, String jsonBody) {
        HttpURLConnection conn = null;
        try {
            Log.d("API_DEBUG", "🌍 URL: " + urlString);
            Log.d("API_DEBUG", "🔨 Método: " + method);

            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");

            if ("POST".equalsIgnoreCase(method) && jsonBody != null) {
                conn.setDoOutput(true);
                Log.d("API_DEBUG", "📤 Enviando body...");
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonBody.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }

            int code = conn.getResponseCode();
            Log.d("API_DEBUG", "📡 Código de respuesta: " + code);

            InputStream is = code < HttpURLConnection.HTTP_BAD_REQUEST
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }

            Log.d("API_DEBUG", "✅ Respuesta cruda: " + response.toString());
            return "✅ Respuesta:\n" + response.toString();

        } catch (Exception e) {
            Log.e("API_DEBUG", "❌ Error en sendRequest", e);
            return "❌ Error: " + e.getMessage();
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
}
