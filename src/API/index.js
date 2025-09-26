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

const app = express();
const PORT = process.env.PORT || 3000;

app.use(express.json());

console.log(`init`);

/* ---------- USERS ---------- */

// Crear usuario
app.post('/users', async (req, res) => {
  const { nombre, apellidos, telefono, gmail, password } = req.body;
  if (!nombre || !apellidos || !telefono || !gmail || !password) {
    return res.status(400).json({ error: 'Faltan datos' });
  }
  try {
    const hashed = await bcrypt.hash(password, 10);
    const id = await logica.createUser(db, { nombre, apellidos, telefono, gmail, password: hashed });
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
app.put('/users/:id', async (req, res) => {
  const { id } = req.params;
  const { nombre, apellidos, telefono, gmail, password } = req.body;
  if (!nombre && !apellidos && !telefono && !gmail && !password) {
    return res.status(400).json({ error: 'Debes enviar al menos un campo' });
  }
  try {
    const fields = { nombre, apellidos, telefono, gmail };
    if (password) fields.password = await bcrypt.hash(password, 10);

    const affected = await logica.updateUser(db, id, fields);
    if (affected === 0) return res.status(404).json({ error: 'Usuario no encontrado' });

    res.json({ mensaje: 'Usuario actualizado' });
  } catch (err) {
    console.error('❌ Error actualizar usuario:', err);
    res.status(500).json({ error: 'Error en el servidor' });
  }
});

/* ---------- MEDICIONES ---------- */

// Crear medición
app.post('/mediciones', async (req, res) => {
  const { tipomedicion, contador, medicion, user, hora, localizacion } = req.body;
  if (!tipomedicion || medicion === undefined || !user || !hora || !localizacion) {
    return res.status(400).json({ error: 'Faltan datos obligatorios' });
  }
  try {
    const id = await logica.createMedicion(db, { tipomedicion, contador, medicion, user, hora, localizacion });
    res.status(201).json({ mensaje: 'Medición registrada', id });
  } catch (err) {
    console.error('❌ Error crear medición:', err);
    res.status(500).json({ error: 'Error en el servidor' });
  }
});

// Listar mediciones (con JOIN)
app.get('/mediciones', async (req, res) => {
  try {
    const mediciones = await logica.listMediciones(db);
    res.json(mediciones);
  } catch (err) {
    console.error('❌ Error listar mediciones:', err);
    res.status(500).json({ error: 'Error en el servidor' });
  }
});

/* ---------- START ---------- */
if (require.main === module) {
  app.listen(PORT, () => console.log(`🚀 Servidor en http://localhost:${PORT}`));
} else {
  module.exports = app;
}
