#ifndef SERVICIO_EMISORA_H_INCLUIDO
#define SERVICIO_EMISORA_H_INCLUIDO

#include <vector>
#include <cstring>
#include <bluefruit.h>

class ServicioEnEmisora {
public:
  // ----------------------------
  // Clase Caracteristica
  // ----------------------------
  class Caracteristica {
  private:
    uint8_t uuidCaracteristica[16];  // UUID de la característica
    BLECharacteristic laCaracteristica;

  public:
    // Constructor: recibe nombre de característica, props, permisos y tamaño de datos
    Caracteristica(const char* nombreCaracteristica_,
                   uint8_t props,
                   SecureMode_t permisoRead,
                   SecureMode_t permisoWrite,
                   uint8_t tam) {
      // Inicializa uuidCaracteristica copiando al revés desde string-C
      for (int i = 0; i < 16; i++) uuidCaracteristica[i] = 0;  // inicializa en 0
      int len = strlen(nombreCaracteristica_);
      for (int i = 0; i < len && i < 16; i++)
        uuidCaracteristica[15 - i] = nombreCaracteristica_[i];

      laCaracteristica = BLECharacteristic(uuidCaracteristica);
      laCaracteristica.setProperties(props);
      laCaracteristica.setPermission(permisoRead, permisoWrite);
      laCaracteristica.setMaxLen(tam);
    }

    // Escribir texto
    uint16_t escribirTexto(const char* str) {
      return laCaracteristica.write(str);
    }

    // Escribir datos binarios
    uint16_t escribirDatos(const void* datos, uint16_t longitud) {
      return laCaracteristica.write((const uint8_t*)datos, longitud);
    }


    // Notificar texto (string terminado en '\0')
    bool notificarTexto(const char* str) {
      return laCaracteristica.notify(str);
    }

    // Notificar datos binarios (ej: int16_t, float, arrays)
    bool notificarDatos(const void* datos, uint16_t longitud) {
      return laCaracteristica.notify((const uint8_t*)datos, longitud);
    }


    // Activar la característica
    void activar() {
      laCaracteristica.begin();
    }
  };

private:
  // ----------------------------
  // UUID fijo del servicio
  // ----------------------------
  static constexpr uint8_t uuidServicio[16] = {
    0x53, 0x45, 0x4E, 0x53, 0x4F, 0x52, 0x30, 0x31,
    0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39
  };

  BLEService elServicio;
  static constexpr int MAX_CARACTERISTICAS = 5;  // ajusta según tu necesidad
  Caracteristica* caracteristicas[MAX_CARACTERISTICAS];
  int numCaracteristicas = 0;

  void agregarCaracteristica(Caracteristica* c) {
    if (numCaracteristicas < MAX_CARACTERISTICAS) {
      caracteristicas[numCaracteristicas++] = c;
    }
  }

public:
  ServicioEnEmisora()
    : elServicio(uuidServicio) {}

  // Añadir característica al servicio
  void anyadirCaracteristica(Caracteristica& car) {
    lasCaracteristicas.push_back(&car);
  }

  // Activar servicio y todas sus características
  void activarServicio() {
    elServicio.begin();
    for (auto pCar : lasCaracteristicas) pCar->activar();
  }

  // Operador para acceder al BLEService directamente
  operator BLEService&() {
    return elServicio;
  }
};

#endif
