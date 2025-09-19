/**
npm init -y
npm install express
npm install mysql2
npm install bcrypt
node index.js
*/const express = require('express');
const app = express();
const PORT = 3000;
const db = require('./db');
const bcrypt = require('bcrypt');

app.use(express.json());

console.log(`init`);

// ------------------- USERS -------------------

// Crear un usuario (POST /users)
app.post('/users', async (req, res) => {
  const { nombre, apellidos, telefono, gmail, password } = req.body;

  if (!nombre || !apellidos || !telefono || !gmail || !password) {
    return res.status(400).json({ error: 'Faltan datos' });
  }

  try {
    const hashedPassword = await bcrypt.hash(password, 10);
    const sql = 'INSERT INTO users (nombre, apellidos, telefono, gmail, password) VALUES (?, ?, ?, ?, ?)';
    
    db.query(sql, [nombre, apellidos, telefono, gmail, hashedPassword], (err, result) => {
      if (err) {
        console.error('❌ Error al insertar usuario:', err);
        return res.status(500).json({ error: 'Error en el servidor' });
      }
      res.status(201).json({ mensaje: 'Usuario creado', id: result.insertId });
    });
  } catch (err) {
    console.error('❌ Error al hashear la contraseña:', err);
    res.status(500).json({ error: 'Error en el servidor' });
  }
});

// Listar todos los usuarios (GET /users)
app.get('/users', (req, res) => {
  db.query('SELECT id, nombre, apellidos, telefono, gmail FROM users', (err, rows) => {
    if (err) {
      console.error('❌ Error al obtener usuarios:', err);
      return res.status(500).json({ error: 'Error en el servidor' });
    }
    res.json(rows);
  });
});

// Actualizar un usuario (PUT /users/:id)
app.put('/users/:id', async (req, res) => {
  const { id } = req.params;
  const { nombre, apellidos, telefono, gmail, password } = req.body;

  // Verificar que haya al menos un campo a actualizar
  if (!nombre && !apellidos && !telefono && !gmail && !password) {
    return res.status(400).json({ error: 'Debes enviar al menos un campo para actualizar' });
  }

  const fields = [];
  const values = [];

  if (nombre) {
    fields.push('nombre = ?');
    values.push(nombre);
  }
  if (apellidos) {
    fields.push('apellidos = ?');
    values.push(apellidos);
  }
  if (telefono) {
    fields.push('telefono = ?');
    values.push(telefono);
  }
  if (gmail) {
    fields.push('gmail = ?');
    values.push(gmail);
  }
  if (password) {
    try {
      const hashedPassword = await bcrypt.hash(password, 10);
      fields.push('password = ?');
      values.push(hashedPassword);
    } catch (err) {
      console.error('❌ Error al hashear la contraseña:', err);
      return res.status(500).json({ error: 'Error en el servidor' });
    }
  }

  values.push(id); // Para el WHERE

  const sql = `UPDATE users SET ${fields.join(', ')} WHERE id = ?`;

  db.query(sql, values, (err, result) => {
    if (err) {
      console.error('❌ Error al actualizar usuario:', err);
      return res.status(500).json({ error: 'Error en el servidor' });
    }

    if (result.affectedRows === 0) {
      return res.status(404).json({ error: 'Usuario no encontrado' });
    }

    res.json({ mensaje: 'Usuario actualizado' });
  });
});

// ------------------- MEDICIONES -------------------

// Crear una medición (POST /mediciones)
app.post('/mediciones', (req, res) => {
  const { medicion1, medicion2, medicion3, user, hora, localizacion } = req.body;

  if (!medicion1 || !user || !hora || !localizacion) {
    return res.status(400).json({ error: 'Faltan datos obligatorios' });
  }

  const sql = `
    INSERT INTO mediciones (medicion1, medicion2, medicion3, user, hora, localizacion)
    VALUES (?, ?, ?, ?, ?, ?)
  `;
  db.query(sql, [medicion1, medicion2, medicion3, user, hora, localizacion], (err, result) => {
    if (err) {
      console.error('❌ Error al insertar medición:', err);
      return res.status(500).json({ error: 'Error en el servidor' });
    }
    res.status(201).json({ mensaje: 'Medición registrada', id: result.insertId });
  });
});

// Listar todas las mediciones (GET /mediciones)
app.get('/mediciones', (req, res) => {
  db.query('SELECT * FROM mediciones', (err, rows) => {
    if (err) {
      console.error('❌ Error al obtener mediciones:', err);
      return res.status(500).json({ error: 'Error en el servidor' });
    }
    res.json(rows);
  });
});

// ------------------- SERVER -------------------
app.listen(PORT, () => {
  console.log(`🚀 Servidor corriendo en http://localhost:${PORT}`);
});
