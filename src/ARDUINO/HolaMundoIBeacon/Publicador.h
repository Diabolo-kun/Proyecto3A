// -*- mode: c++ -*-

// --------------------------------------------------------------
// Jordi Bataller i Mascarell
// --------------------------------------------------------------

#ifndef PUBLICADOR_H_INCLUIDO
#define PUBLICADOR_H_INCLUIDO

// --------------------------------------------------------------
// --------------------------------------------------------------
class Publicador {

  // ............................................................
  // ............................................................
private:

  uint8_t beaconUUID[16] = { 
	'E', 'P', 'S', 'G', '-', 'G', 'T', 'I', 
	'-', 'P', 'R', 'O', 'Y', '-', '3', 'A'
	};

  // ............................................................
  // ............................................................
public:
  EmisoraBLE laEmisora {
	"MANU", //  nombre emisora
	  0x004c, // fabricanteID (Apple)
	  4 // txPower
	  };
  
  const int RSSI = -53; // por poner algo, de momento no lo uso

  // ............................................................
  // ............................................................
public:

  // ............................................................
  // ............................................................
  enum MedicionesID  {
	CO2 = 01,
	TEMPERATURA = 02,
	RUIDO = 03
  };

  // ............................................................
  // ............................................................
  Publicador( ) {
	// ATENCION: no hacerlo aquí. (*this).laEmisora.encenderEmisora();
	// Pondremos un método para llamarlo desde el setup() más tarde
  } // ()

  // ............................................................
  // ............................................................
  void encenderEmisora() {
	(*this).laEmisora.encenderEmisora();
  } // ()

  // ............................................................
  // ............................................................
  void publicarCO2(int16_t valorCO2, uint8_t contador, long tiempoEspera) {
    uint16_t major = (MedicionesID::CO2 << 8) + contador;

    // Empezamos anuncio
    (*this).laEmisora.emitirAnuncioIBeacon((*this).beaconUUID, major, valorCO2, (*this).RSSI);

    Globales::elPuerto.escribir( "   publicarCO2(): valor=" );
	  Globales::elPuerto.escribir( valorCO2 );
	  Globales::elPuerto.escribir( "   contador=" );
	  Globales::elPuerto.escribir( contador );
	  Globales::elPuerto.escribir( "   todo="  );
	  Globales::elPuerto.escribir( major );
	  Globales::elPuerto.escribir( "\n" );

    // Si quieres mantener la medición visible durante un tiempo, espera el tiempo indicado
    esperar(tiempoEspera);

    // Finalmente, detenemos el anuncio
    (*this).laEmisora.detenerAnuncio();
}


  // ............................................................
  // ............................................................
  void publicarTemperatura( int16_t valorTemperatura,
							uint8_t contador, long tiempoEspera ) {

	uint16_t major = (MedicionesID::TEMPERATURA << 8) + contador;
	(*this).laEmisora.emitirAnuncioIBeacon( (*this).beaconUUID, 
											major,
											valorTemperatura, // minor
											(*this).RSSI // rssi
									);

  Globales::elPuerto.escribir( "   publicarTemperatura(): valor=" );
	Globales::elPuerto.escribir( valorTemperatura );
	Globales::elPuerto.escribir( "   contador=" );
	Globales::elPuerto.escribir( contador );
	Globales::elPuerto.escribir( "   todo="  );
	Globales::elPuerto.escribir( major );
	Globales::elPuerto.escribir( "\n" );                

	esperar( tiempoEspera );

	(*this).laEmisora.detenerAnuncio();
  } // ()
	
}; // class

// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------
#endif