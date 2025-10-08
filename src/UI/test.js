// test.js
console.log("🔍 Iniciando test de la página de mediciones...");

function testHTML() {
  const tabla = document.getElementById("tablaMediciones");
  const boton = document.getElementById("recargarBtn");

  if (!tabla) {
    console.error("❌ No se encontró la tabla con id 'tablaMediciones'.");
    return false;
  }
  if (!boton) {
    console.error("❌ No se encontró el botón con id 'recargarBtn'.");
    return false;
  }

  console.log("✅ Elementos principales encontrados correctamente.");

  // Simular click en recargar
  console.log("🔄 Ejecutando función de recarga...");
  boton.click();

  // Esperar unos segundos y revisar si DataTable está activo
  setTimeout(() => {
    if ($.fn.dataTable.isDataTable('#tablaMediciones')) {
      const filas = $('#tablaMediciones').DataTable().rows().count();
      console.log(`✅ DataTable activo con ${filas} filas cargadas.`);
    } else {
      console.warn("⚠️ DataTable aún no inicializado o sin datos.");
    }
  }, 3000);

  return true;
}

// Ejecutar test al cargar la página
window.addEventListener("load", testHTML);
