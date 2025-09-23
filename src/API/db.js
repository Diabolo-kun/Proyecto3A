const mysql = require('mysql2');

const db = mysql.createConnection({
  host: 'localhost',
  user: 'root',        // usuario por defecto en XAMPP
  password: '',        // contraseña (vacía si no pusiste una)
  database: 'medicionesaire',
  port: 3306  
});

db.connect((err) => {
  if (err) {
    console.error('❌ Error al conectar a MySQL:', err);
    return;
  }
  console.log('✅ Conexión establecida con la base de datos medicionesaire');
});

module.exports = db;
