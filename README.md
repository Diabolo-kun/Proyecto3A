# Proyecto3A
Sprint 0 individual del proyecto de biometría y geolocalización del 1er cuatrimestre de 3º de GTI
Instalacion:
0-Encender BBDD:    
    Abrir Xampp, e iniciar apache y mysql.
    Teniendo la base de datros importada en phpmyadmin, disponible en este repositorio.
1-Conectar API:
    En terminal, pip, dotnet, node, npm tienen que estar instalados, para comprobar, usar en la terminal:
        pip --version,
        dotnet --version,
        node -v,
        npm -v
    Tambien seran necesarias los siguientes modulos de npm, para instalarlos, desde la terminal:
        npm init -y
        npm install express
        npm install mysql2
        npm install bcrypt
    Y finalmente para ejecutar la API, ejecutar el comando:
        node index.js