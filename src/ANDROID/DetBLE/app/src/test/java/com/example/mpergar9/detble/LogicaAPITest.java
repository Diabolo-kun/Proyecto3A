package com.example.mpergar9.detble;

import org.junit.Test;
import static org.junit.Assert.*;

public class LogicaAPITest {

    private final LogicaAPI api = new LogicaAPI();

    @Test
    public void testBuildJsonBody_allValues() {
        String json = api.buildJsonBody("1", "10", "23.5", "5", "2025-09-30T12:00:00", "Madrid");
        assertTrue(json.contains("\"tipomedicion\":1"));
        assertTrue(json.contains("\"contador\":10"));
        assertTrue(json.contains("\"medicion\":23.5"));
        assertTrue(json.contains("\"user\":5"));
        assertTrue(json.contains("\"hora\":\"2025-09-30T12:00:00\""));
        assertTrue(json.contains("\"localizacion\":\"Madrid\""));
    }

    @Test
    public void testBuildJsonBody_emptyValues() {
        String json = api.buildJsonBody("", "", "", "", "horaTest", "locTest");
        assertTrue(json.contains("\"tipomedicion\":0"));
        assertTrue(json.contains("\"medicion\":0"));
        assertTrue(json.contains("\"user\":0"));
        assertTrue(json.contains("\"hora\":\"horaTest\""));
        assertTrue(json.contains("\"localizacion\":\"locTest\""));
    }

    @Test
    public void testBuildJsonBody_invalidNumber() {
        String json = api.buildJsonBody("abc", "xyz", "1.2.3", "qwe", "horaX", "locX");
        assertEquals("{}", json); // porque entra en Exception
    }
}
