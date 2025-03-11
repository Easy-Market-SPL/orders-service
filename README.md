![Logo](https://github.com/Easy-Market-SPL/.github/blob/main/Banner.png?raw=true)

# EasyMarket Orders Service

Microservicio encargado del CRUD de ordenes de la SPL EasyMarket

---
## Stack Tecnológico 🛠️

**Servidor:** Springboot

![BD](https://skillicons.dev/icons?i=spring,maven)

**Base de datos:** PostgresQL

![BD](https://skillicons.dev/icons?i=postgresql)

---
## Variables de Entorno 🔒

Para ejecutar este proyecto, necesitarás agregar las siguientes variables de entorno a tu archivo .env

* `EUREKA_URL` : URL del servidor Eureka. Ejemplo: http://localhost:8761/eureka

* `SPRING_CONFIG_IMPORT` : URL del servidor de configuración. Ejemplo: configserver:http://localhost:8888


### Configuracion con doppler 🚀

Como requisito para realizar el manejo de variables de entorno con doppler se debe tener instalado el CLI de doppler, para ello se debe seguir la guía de instalación en el siguiente [enlace](https://docs.doppler.com/docs/cli)

Debe ser parte del equipo de doppler para poder acceder a las variables de entorno del proyecto, para ello se debe enviar el correo de la cuenta de doppler al correo de alguno de los miembros del equipo para ser agregado.

Para obtener las variables de entorno del proyecto se debe ejecutar el siguiente comando en la terminal:

**1. Iniciar sesión en doppler**

```bash
doppler login
```

**2. Seleccionar el proyecto y el ambiente de desarrollo (dev)**

```bash
doppler setup
```
**3. Ejecutar el archivo por lotes para generar un archivo .env**

```bash
# windows
./env-vars.bat
```

## Ejecutar Localmente 💻

Clona y sigue las instrucciones para ejecutar el proyecto de infraestructura en
https://github.com/Easy-Market-SPL/dev-infrastructure



Clona el proyecto

```bash
  git clone https://github.com/Easy-Market-SPL/orders-service
```

Ve al directorio del proyecto

```bash
  cd orders-service
```

Instala las dependencias

```bash
  mvn install
```

Inicia el servidor

```bash
  mvn spring-boot:run
```

---

## Autores 🧑🏻‍💻

- [@Estebans441](https://www.github.com/Estebans441)