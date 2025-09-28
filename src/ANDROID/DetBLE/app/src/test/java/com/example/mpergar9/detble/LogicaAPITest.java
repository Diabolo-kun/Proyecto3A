package com.example.mpergar9.detble;

import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.*;

public class LogicaAPITest {

    @Test
    public void testBuildJsonBody() throws Exception {
        LogicaAPI api = new LogicaAPI();

        String json = api.buildJsonBody("1", "10", "23.5", "5", "12:30", "Madrid");
        JSONObject obj = new JSONObject(json);

        assertEquals(1, obj.getInt("tipomedicion"));
        assertEquals(10, obj.getInt("contador"));
        assertEquals(23.5, obj.getDouble("medicion"), 0.0001);
        assertEquals(5, obj.getInt("user"));
        assertEquals("12:30", obj.getString("hora"));
        assertEquals("Madrid", obj.getString("localizacion"));
    }

    @Test
    public void testBuildJsonBodyEmptyValues() throws Exception {
        LogicaAPI api = new LogicaAPI();

        String json = api.buildJsonBody("", "", "", "", "", "");
        JSONObject obj = new JSONObject(json);

        assertEquals(0, obj.getInt("tipomedicion"));
        assertEquals(0, obj.getDouble("medicion"), 0.0001);
        assertEquals(0, obj.getInt("user"));
        assertEquals("", obj.getString("hora"));
        assertEquals("", obj.getString("localizacion"));
        assertFalse(obj.has("contador")); // contador no debería aparecer
    }

    @Test
    public void testBuildJsonBodyPartialValues() throws Exception {
        LogicaAPI api = new LogicaAPI();

        String json = api.buildJsonBody("2", "", "12.7", "", "14:00", "");
        JSONObject obj = new JSONObject(json);

        assertEquals(2, obj.getInt("tipomedicion"));
        assertEquals(12.7, obj.getDouble("medicion"), 0.0001);
        assertEquals(0, obj.getInt("user"));
        assertEquals("14:00", obj.getString("hora"));
        assertEquals("", obj.getString("localizacion"));
        assertFalse(obj.has("contador"));
    }
}
