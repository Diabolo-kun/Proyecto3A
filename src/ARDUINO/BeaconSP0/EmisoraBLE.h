#ifndef EMISORA_H_INCLUIDO
#define EMISORA_H_INCLUIDO

#include <stdint.h>
#include <string.h>
#include "ServicioEnEmisora.h"
#include <bluefruit.h>

class EmisoraBLE {
private:
    const char* nombreEmisora;
    uint16_t fabricanteID;
    int8_t txPower;

public:
    EmisoraBLE(const char* nombre, uint16_t id, int8_t power)
        : nombreEmisora(nombre), fabricanteID(id), txPower(power) {}

    void setNombre(const char* nombre) { nombreEmisora = nombre; }
    void setTxPower(int8_t potencia) { txPower = potencia; }
    void setFabricanteID(uint16_t id) { fabricanteID = id; }

    void encenderEmisora() {
        Bluefruit.begin();
        detenerAnuncio();
    }

    void detenerAnuncio() {
        if (estaAnunciando()) Bluefruit.Advertising.stop();
    }

    bool estaAnunciando() { return Bluefruit.Advertising.isRunning(); }

    void emitirAnuncioIBeaconLibre(const char* carga, const uint8_t tamanyoCarga) {
        detenerAnuncio();
        Bluefruit.Advertising.clearData();
        Bluefruit.ScanResponse.clearData();
        Bluefruit.setName(nombreEmisora);
        Bluefruit.ScanResponse.addName();
        Bluefruit.Advertising.addFlags(BLE_GAP_ADV_FLAGS_LE_ONLY_GENERAL_DISC_MODE);

        uint8_t restoPrefijoYCarga[25] = { 0x4c, 0x00, 0x02, 21 };
        memset(&restoPrefijoYCarga[4], '-', 21);

        memcpy(&restoPrefijoYCarga[4], carga, tamanyoCarga > 21 ? 21 : tamanyoCarga);

        Bluefruit.Advertising.addData(BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA, restoPrefijoYCarga, 25);
        Bluefruit.Advertising.restartOnDisconnect(true);
        Bluefruit.Advertising.setInterval(100, 100);
        Bluefruit.Advertising.setFastTimeout(1);
        Bluefruit.Advertising.start(0);
    }
};

#endif
