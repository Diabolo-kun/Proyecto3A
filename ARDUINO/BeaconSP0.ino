#include <bluefruit.h>
#undef min 
#undef max

#include "LED.h"
#include "PuertoSerie.h"

#include "EmisoraBLE.h"
#include "Publicador.h"
#include "Medidor.h"

// ----------------------------
// Variables configurables
// ----------------------------
String dispositivoNombre = "GTI-SP0-MPG";
String beaconMensaje = "HolaBeacon";
int intervaloAnuncio = 1000;
int txPower = 4;  // dBm

// ----------------------------
namespace Globales {
  LED elLED(7);
  PuertoSerie elPuerto(115200);
  Publicador elPublicador(dispositivoNombre.c_str(), 0x004c, txPower);
  Medidor elMedidor;
}

// ----------------------------
void mostrarMenu() {
  Serial.println(F("\n===== MENU CONFIGURACION BEACON ====="));
  Serial.println(F("1. Cambiar nombre del dispositivo"));
  Serial.println(F("2. Cambiar mensaje del beacon"));
  Serial.println(F("3. Cambiar intervalo de anuncio (ms)"));
  Serial.println(F("4. Cambiar potencia Tx"));
  Serial.println(F("5. Mostrar configuracion actual"));
  Serial.println(F("6. Guardar y continuar"));
  Serial.println(F("====================================="));
  Serial.print(F("Selecciona opcion: "));
}

// ----------------------------
void configurarBeacon() {
  bool configurando = true;
  while (configurando) {
    mostrarMenu();
    while (!Serial.available());
    int opcion = Serial.parseInt();

    switch (opcion) {
      case 1:
        Serial.println(F("Introduce nuevo nombre: "));
        while (!Serial.available());
        dispositivoNombre = Serial.readStringUntil('\n');
        dispositivoNombre.trim();
        break;
      case 2:
        Serial.println(F("Introduce nuevo mensaje (máx 21 chars): "));
        while (!Serial.available());
        beaconMensaje = Serial.readStringUntil('\n');
        beaconMensaje.trim();
        if (beaconMensaje.length() > 21) beaconMensaje = beaconMensaje.substring(0, 21);
        break;
      case 3:
        Serial.println(F("Introduce nuevo intervalo en ms: "));
        while (!Serial.available());
        intervaloAnuncio = Serial.parseInt();
        break;
      case 4:
        Serial.println(F("Introduce nueva potencia Tx (-40 a 4 dBm): "));
        while (!Serial.available());
        txPower = Serial.parseInt();
        break;
      case 5:
        Serial.println(F("\n--- Configuracion actual ---"));
        Serial.print(F("Nombre: ")); Serial.println(dispositivoNombre);
        Serial.print(F("Mensaje: ")); Serial.println(beaconMensaje);
        Serial.print(F("Intervalo: ")); Serial.print(intervaloAnuncio); Serial.println(F(" ms"));
        Serial.print(F("TxPower: ")); Serial.print(txPower); Serial.println(F(" dBm"));
        Serial.println(F("-----------------------------\n"));
        break;
      case 6:
        configurando = false;
        break;
    }
  }
}

// ----------------------------
void setup() {
  Globales::elPuerto.esperarDisponible();
  Serial.println(F("Iniciando configuracion..."));
  configurarBeacon();

  // Usar configuración
  Globales::elPublicador.laEmisora = EmisoraBLE(dispositivoNombre.c_str(), 0x004c, txPower);
  Globales::elPublicador.encenderEmisora();
}

// ----------------------------
void loop() {
  // Ejemplo: enviar beacon con mensaje personalizado
  Globales::elPublicador.laEmisora.emitirAnuncioIBeaconLibre(beaconMensaje.c_str(), beaconMensaje.length());
  delay(intervaloAnuncio);
  Globales::elPublicador.laEmisora.detenerAnuncio();
}
