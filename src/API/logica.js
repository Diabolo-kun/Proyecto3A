// logica.js
// Funciones puras que reciben un `db` (inyectado), para poder usar mocks en tests.

function execQuery(db, sql, params = []) {
  return new Promise((resolve, reject) => {
    db.query(sql, params, (err, rows) => {
      if (err) return reject(err);
      resolve(rows);
    });
  });
}

/* ---------- USERS ---------- */

async function createUser(db, { nombre, apellidos, telefono, gmail, password }) {
  const sql = 'INSERT INTO users (nombre, apellidos, telefono, gmail, password) VALUES (?, ?, ?, ?, ?)';
  const result = await execQuery(db, sql, [nombre, apellidos, telefono, gmail, password]);
  return result.insertId;
}

async function listUsers(db) {
  const sql = 'SELECT id, nombre, apellidos, telefono, gmail FROM users';
  return await execQuery(db, sql);
}

async function updateUser(db, id, fieldsObj) {
  const allowed = ['nombre', 'apellidos', 'telefono', 'gmail', 'password'];
  const sets = [];
  const values = [];
  for (const key of Object.keys(fieldsObj)) {
    if (allowed.includes(key) && fieldsObj[key] !== undefined) {
      sets.push(`${key} = ?`);
      values.push(fieldsObj[key]);
    }
  }
  if (sets.length === 0) throw new Error('No hay campos válidos para actualizar');
  values.push(id);

  const sql = `UPDATE users SET ${sets.join(', ')} WHERE id = ?`;
  const result = await execQuery(db, sql, values);
  return result.affectedRows;
}

/* ---------- MEDICIONES ---------- */

async function createMedicion(db, { tipomedicion, contador = null, medicion, user, hora, localizacion }) {
  const sql = `
    INSERT INTO mediciones (tipomedicion, contador, medicion, user, hora, localizacion)
    VALUES (?, ?, ?, ?, ?, ?)
  `;
  const result = await execQuery(db, sql, [tipomedicion, contador, medicion, user, hora, localizacion]);
  return result.insertId;
}

async function listMediciones(db) {
  const sql = `
    SELECT 
      m.id,
      m.medicion,
      m.contador,
      m.hora,
      m.localizacion,
      u.id AS user_id,
      u.nombre AS user_nombre,
      u.apellidos AS user_apellidos,
      t.id AS tipo_id,
      t.medida AS tipo_medida,
      t.unidad AS tipo_unidad,
      t.txt AS tipo_descripcion
    FROM mediciones m
    JOIN users u ON m.user = u.id
    JOIN tipomedicion t ON m.tipomedicion = t.id
    ORDER BY m.hora DESC, m.id DESC
  `;
  return await execQuery(db, sql);
}

module.exports = {
  createUser,
  listUsers,
  updateUser,
  createMedicion,
  listMediciones,
};
