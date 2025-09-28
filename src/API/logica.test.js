// logica.test.js
// Pruebas unitarias de logica.js con un mock de db (no toca la DDBB real)

const logica = require('./logica');

// Utilidad para crear un mock de `db.query`
function createMockDb() {
  return {
    query: jest.fn(),
  };
}

describe('Tests de logica.js', () => {
  let db;

  beforeEach(() => {
    db = createMockDb();
  });

  /* ---------- USERS ---------- */

  test('createUser inserta usuario y devuelve insertId', async () => {
    db.query.mockImplementation((sql, params, cb) =>
      cb(null, { insertId: 99 })
    );

    const id = await logica.createUser(db, {
      nombre: 'Pepe',
      apellidos: 'Lopez',
      telefono: '123456789',
      gmail: 'pepe@test.com',
      password: 'hashpass',
    });

    expect(id).toBe(99);
    expect(db.query).toHaveBeenCalledWith(
      expect.stringContaining('INSERT INTO users'),
      expect.any(Array),
      expect.any(Function)
    );
  });

  test('listUsers devuelve lista de usuarios', async () => {
    const fakeUsers = [
      { id: 1, nombre: 'Pepe', apellidos: 'Lopez', telefono: '123', gmail: 'pepe@test.com' },
    ];
    db.query.mockImplementation((sql, params, cb) => cb(null, fakeUsers));

    const res = await logica.listUsers(db);
    expect(res).toEqual(fakeUsers);
  });

  test('updateUser actualiza campos y devuelve filas afectadas', async () => {
    db.query.mockImplementation((sql, params, cb) =>
      cb(null, { affectedRows: 1 })
    );

    const affected = await logica.updateUser(db, 1, { nombre: 'Nuevo' });
    expect(affected).toBe(1);
    expect(db.query).toHaveBeenCalled();
  });

  test('updateUser sin campos válidos lanza error', async () => {
    await expect(logica.updateUser(db, 1, { invalido: 'x' }))
      .rejects
      .toThrow('No hay campos válidos para actualizar');
  });

  /* ---------- MEDICIONES ---------- */

  test('createMedicion inserta medición y devuelve insertId', async () => {
    db.query.mockImplementation((sql, params, cb) =>
      cb(null, { insertId: 123 })
    );

    const id = await logica.createMedicion(db, {
      tipomedicion: 1,
      contador: null,
      medicion: 25.3,
      user: 1,
      hora: '2025-09-26 12:00:00',
      localizacion: 'Madrid',
    });

    expect(id).toBe(123);
    expect(db.query).toHaveBeenCalledWith(
      expect.stringContaining('INSERT INTO mediciones'),
      expect.any(Array),
      expect.any(Function)
    );
  });

  test('listMediciones devuelve lista con JOIN', async () => {
    const fakeData = [
      {
        id: 1,
        medicion: 22.5,
        contador: null,
        hora: '2025-09-26',
        localizacion: 'Madrid',
        user_id: 1,
        user_nombre: 'Manuel',
        user_apellidos: 'Gonzalez',
        tipo_id: 2,
        tipo_medida: 'Temperatura',
        tipo_unidad: '°C',
        tipo_descripcion: 'Medición ambiente',
      },
    ];
    db.query.mockImplementation((sql, params, cb) => cb(null, fakeData));

    const res = await logica.listMediciones(db);
    expect(res).toEqual(fakeData);
    expect(db.query).toHaveBeenCalledWith(
      expect.stringContaining('JOIN users'),
      expect.any(Array),
      expect.any(Function)
    );
  });

  /* ---------- TIPOMEDICION ---------- */

  test('createTipoMedicion inserta tipomedicion y devuelve insertId', async () => {
    db.query.mockImplementation((sql, params, cb) =>
      cb(null, { insertId: 55 })
    );

    const id = await logica.createTipoMedicion(db, {
      medida: 'Temperatura',
      unidad: '°C',
      txt: 'Medición ambiente',
    });

    expect(id).toBe(55);
    expect(db.query).toHaveBeenCalledWith(
      expect.stringContaining('INSERT INTO tipomedicion'),
      expect.any(Array),
      expect.any(Function)
    );
  });

  test('updateTipoMedicion actualiza campos y devuelve filas afectadas', async () => {
    db.query.mockImplementation((sql, params, cb) =>
      cb(null, { affectedRows: 1 })
    );

    const affected = await logica.updateTipoMedicion(db, 1, { medida: 'Humedad' });
    expect(affected).toBe(1);
    expect(db.query).toHaveBeenCalled();
  });

  test('listTipoMedicion devuelve lista de tipos', async () => {
    const fakeTipos = [
      { id: 1, medida: 'Temperatura', unidad: '°C', txt: 'Medición ambiente' },
    ];
    db.query.mockImplementation((sql, params, cb) => cb(null, fakeTipos));

    const res = await logica.listTipoMedicion(db);
    expect(res).toEqual(fakeTipos);
    expect(db.query).toHaveBeenCalledWith(
      expect.stringContaining('SELECT id, medida, unidad, txt FROM tipomedicion'),
      expect.any(Array),
      expect.any(Function)
    );
  });
});
