#ifndef PUBLICADOR_H_INCLUIDO
#define PUBLICADOR_H_INCLUIDO

#include <stdint.h>
#include <string.h>
#include "EmisoraBLE.h"

class Publicador {
private:
  EmisoraBLE* laEmisora;
  int RSSI;
  uint8_t beaconUUID[16];

public:
  Publicador() : laEmisora(nullptr), RSSI(0) {
    memset(beaconUUID, 0, 16);
  }

  Publicador(EmisoraBLE* emisora, const uint8_t uuid[16], int rssi)
    : laEmisora(emisora), RSSI(rssi) {
    memcpy(beaconUUID, uuid, 16);
  }

  void setEmisora(EmisoraBLE* emisora) { laEmisora = emisora; }
  EmisoraBLE* getEmisora() { return laEmisora; }

  void encenderEmisora() {
    if (laEmisora) laEmisora->encenderEmisora();
  }

  void publicarCO2(int16_t valorCO2, uint8_t contador, long tiempoEspera);
  void publicarTemperatura(int16_t valorTemperatura, uint8_t contador, long tiempoEspera);
};


#endif
