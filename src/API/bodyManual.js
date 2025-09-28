function bodyManual(req, res, next) {
  if (req.method !== 'POST' && req.method !== 'PUT') return next();

  let data = '';
  req.on('data', chunk => { data += chunk; });
  req.on('end', () => {
    console.log("📥 BODY CRUDO recibido:", data);
    try {
      req.bodyManual = JSON.parse(data);
      console.log("📌 Body parseado:", req.bodyManual);
      next();
    } catch (err) {
      console.log("❌ JSON inválido:", err.message);
      res.status(400).json({ error: "JSON inválido" });
    }
  });
}

module.exports = bodyManual;
