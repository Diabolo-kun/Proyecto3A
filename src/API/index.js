/**
npm init -y
npm install express
npm install mysql2
npm install bcrypt
node index.js
npm install --save-dev jest //hacer los tests
*/
const express = require('express');
const bcrypt = require('bcrypt');
const db = require('./db');
const logica = require('./logica');
const bodyManual = require('./bodyManual');
const cors = require('cors');
const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());

console.log(`init`);

// Para otras rutas que sí reciban JSON estándar
app.use(express.json());

/* ---------- USERS ---------- */

// Crear usuario
app.post('/users', bodyManual, async (req, res) => {
  console.log("================== POST /users ==================");
  console.log("📥 Headers recibidos:", req.headers);
  console.log("📥 Body parseado manualmente:", req.bodyManual);

  const { nombre, apellidos, telefono, gmail, password } = req.bodyManual;

  if (!nombre || !apellidos || !telefono || !gmail || !password) {
    console.log("⚠️ Faltan datos obligatorios para crear usuario");
    return res.status(400).json({ error: 'Faltan datos' });
  }

  try {
    const hashed = await bcrypt.hash(password, 10);
    const id = await logica.createUser(db, { nombre, apellidos, telefono, gmail, password: hashed });
    console.log("✅ Usuario creado con ID:", id);
    res.status(201).json({ mensaje: 'Usuario creado', id });
  } catch (err) {
    console.error('❌ Error crear usuario:', err);
    res.status(500).json({ error: 'Error en el servidor' });
  }
});


// Listar usuarios
app.get('/users', async (req, res) => {
  try {
    const users = await logica.listUsers(db);
    res.json(users);
  } catch (err) {
    console.error('❌ Error listar usuarios:', err);
    res.status(500).json({ error: 'Error en el servidor' });
  }
});

// Actualizar usuario
app.put('/users/:id', bodyManual, async (req, res) => {
  console.log("================== PUT /users/:id ==================");
  console.log("📥 Headers recibidos:", req.headers);
  console.log("📥 Body parseado manualmente:", req.bodyManual);

  const { id } = req.params;
  const { nombre, apellidos, telefono, gmail, password } = req.bodyManual;

  if (!nombre && !apellidos && !telefono && !gmail && !password) {
    console.log("⚠️ Debes enviar al menos un campo");
    return res.status(400).json({ error: 'Debes enviar al menos un campo' });
  }

  try {
    const fields = { nombre, apellidos, telefono, gmail };
    if (password) fields.password = await bcrypt.hash(password, 10);

    console.log("📝 Campos a actualizar:", fields);

    const affected = await logica.updateUser(db, id, fields);
    if (affected === 0) {
      console.log("⚠️ Usuario no encontrado");
      return res.status(404).json({ error: 'Usuario no encontrado' });
    }

    console.log("✅ Usuario actualizado correctamente");
    res.json({ mensaje: 'Usuario actualizado' });
  } catch (err) {
    console.error('❌ Error actualizar usuario:', err);
    res.status(500).json({ error: 'Error en el servidor' });
  }
});

/* ---------- MEDICIONES ---------- */

// Crear medición
app.post('/mediciones', bodyManual, async (req, res) => {
  console.log("================== POST /mediciones ==================");
  console.log("📥 Headers recibidos:", req.headers);
  console.log("📥 Body parseado manualmente:", req.bodyManual);

  const { tipomedicion, contador, medicion, user, hora, localizacion } = req.bodyManual;

  if (!tipomedicion || medicion === undefined || !user || !hora || !localizacion) {
    console.log("⚠️ Faltan datos obligatorios en el body");
    return res.status(400).json({ error: 'Faltan datos obligatorios' });
  }

  try {
    const sqlParams = { tipomedicion, contador, medicion, user, hora, localizacion };
    console.log("📝 Preparando insert con los parámetros:", sqlParams);

    const id = await logica.createMedicion(db, sqlParams);
    console.log("✅ Medición creada correctamente con ID:", id);
    res.status(201).json({ mensaje: 'Medición registrada', id });
  } catch (err) {
    console.error('❌ Error durante la creación de medición:', err);
    res.status(500).json({ error: 'Error en el servidor' });
  }
});

// Listar mediciones (con JOIN)
app.get('/mediciones', async (req, res) => {
  console.log("================== GET /mediciones ==================");
  try {
    const mediciones = await logica.listMediciones(db);
    res.json(mediciones);
  } catch (err) {
    console.error('❌ Error listar mediciones:', err);
    res.status(500).json({ error: 'Error en el servidor' });
  }
});

/* ---------- TIPOMEDICION ---------- */

// Crear tipo de medición
app.post('/tipomedicion', bodyManual, async (req, res) => {
  console.log("================== POST /tipomedicion ==================");
  console.log("📥 Headers recibidos:", req.headers);
  console.log("📥 Body parseado manualmente:", req.bodyManual);

  const { medida, unidad, txt } = req.bodyManual;

  if (!medida || !unidad || !txt) {
    console.log("⚠️ Faltan datos obligatorios para crear tipomedicion");
    return res.status(400).json({ error: 'Faltan datos obligatorios' });
  }

  try {
    const sqlParams = { medida, unidad, txt };
    console.log("📝 Preparando insert con los parámetros:", sqlParams);

    const id = await logica.createTipoMedicion(db, sqlParams);
    console.log("✅ Tipomedición creada correctamente con ID:", id);
    res.status(201).json({ mensaje: 'Tipomedición registrada', id });
  } catch (err) {
    console.error('❌ Error durante la creación de tipomedicion:', err);
    res.status(500).json({ error: 'Error en el servidor' });
  }
});

// Actualizar tipo de medición
app.put('/tipomedicion/:id', bodyManual, async (req, res) => {
  console.log("================== PUT /tipomedicion/:id ==================");
  console.log("📥 Headers recibidos:", req.headers);
  console.log("📥 Body parseado manualmente:", req.bodyManual);

  const { id } = req.params;
  const { medida, unidad, txt } = req.bodyManual;

  if (!medida && !unidad && !txt) {
    console.log("⚠️ Debes enviar al menos un campo para actualizar");
    return res.status(400).json({ error: 'Debes enviar al menos un campo' });
  }

  try {
    const fields = { medida, unidad, txt };
    console.log("📝 Campos a actualizar:", fields);

    const affected = await logica.updateTipoMedicion(db, id, fields);
    if (affected === 0) {
      console.log("⚠️ Tipomedicion no encontrada");
      return res.status(404).json({ error: 'Tipomedicion no encontrada' });
    }

    console.log("✅ Tipomedicion actualizada correctamente");
    res.json({ mensaje: 'Tipomedicion actualizada' });
  } catch (err) {
    console.error('❌ Error actualizar tipomedicion:', err);
    res.status(500).json({ error: 'Error en el servidor' });
  }
});

// Listar tipos de medición
app.get('/tipomedicion', async (req, res) => {
  console.log("================== GET /tipomedicion ==================");
  try {
    const tipos = await logica.listTipoMedicion(db);
    res.json(tipos);
  } catch (err) {
    console.error('❌ Error listar tipomedicion:', err);
    res.status(500).json({ error: 'Error en el servidor' });
  }
});

/* ---------- START ---------- */
if (require.main === module) {
  app.listen(PORT, () => console.log(`🚀 Servidor en http://localhost:${PORT}`));
} else {
  module.exports = app;
}
