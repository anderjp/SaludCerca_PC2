# SaludCerca — Plataforma web de gestión de citas médicas

Proyecto del curso **Diseño de Patrones (100000SI47)** — Universidad Tecnológica del Perú (UTP).
**Práctica Calificada 2 (PC2):** implementación del 50% del software con **patrones creacionales y estructurales** (GoF).

SaludCerca conecta pacientes con médicos y centros de salud independientes para **agendar citas** (presenciales o por teleconsulta), gestionar un historial clínico básico y registrar el pago de la consulta. Las pantallas implementan los **prototipos de la PC1** (Paciente, Médico y Administrador) y cada una ejecuta por detrás los patrones de diseño.

---

## 👥 Integrantes (Grupo N.º 4)

| N° | Nombres y apellidos | Código UTP |
|----|---------------------|------------|
| 1 | Kanashiro Yaringaño Eduardo Alfonso | U23328413 |
| 2 | Criollo Arias Jean Pierre Sebastian | U23304637 |
| 3 | Santisteban Cisneros Johann Alexis | U23329723 |
| 4 | Romero Monsefu Anderson | U24236631 |
| 5 | Casana Solano Anthony José | U23305156 |
| 6 | Sauñe Rengifo Flavianna Georgette | U22211499 |

**Docente:** Ronald Angel Ayra Pinto

---

## 🧰 Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| Lenguaje | Java 21 |
| Framework | Spring Boot 4.1.1 (Spring Web MVC) |
| Vistas | Thymeleaf (diseño de los prototipos de la PC1) |
| Persistencia (ORM) | Spring Data JPA / Hibernate |
| Base de datos | MySQL / MariaDB 10.4 (XAMPP) |
| Validación | Jakarta Bean Validation |
| Construcción | Maven (wrapper `mvnw`) |

---

## ✅ Requisitos previos

- **JDK 21 o superior** instalado. El proyecto compila para Java 21.
- **MySQL/MariaDB** disponible. El entorno local verificado utiliza MariaDB 10.4 de **XAMPP**.
- No necesitas instalar Maven: el proyecto incluye el wrapper `mvnw`.

---

## 🚀 Puesta en marcha

### 1. Crear la base de datos

En una instalación nueva, ejecuta [`db/SaludCerca_Script_BD.sql`](db/SaludCerca_Script_BD.sql). Crea `saludcerca`, sus ocho tablas y datos de ejemplo. No lo vuelvas a importar sobre tablas existentes; los reinicios normales conservan los datos.

- **Con phpMyAdmin** (XAMPP): *Importar → elegir el archivo → Continuar*.
- **Por consola** (desde la carpeta del proyecto):

```powershell
& "C:\xampp\mysql\bin\mysql.exe" --default-character-set=utf8mb4 -u root --execute="source db/SaludCerca_Script_BD.sql"
```

Al primer arranque, la aplicación agrega automáticamente **datos de demostración** (más médicos, dos de ellos pendientes de verificación, y citas en la agenda) sin duplicarlos en los siguientes arranques.

### 2. Configurar la conexión (si hace falta)

Los valores por defecto están en [`src/main/resources/application.properties`](src/main/resources/application.properties):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/saludcerca
spring.datasource.username=root
spring.datasource.password=
server.port=8082
```

Si MySQL requiere una contraseña, proporciónala localmente mediante `SPRING_DATASOURCE_PASSWORD`. La configuración incluida corresponde a una demostración local; no guardes credenciales reales en el repositorio.

### 3. Compilar y ejecutar la aplicación

Configura `JAVA_HOME` con tu instalación de JDK 21 y comprueba `java -version` y `javac -version`. Desde la raíz del proyecto:

```powershell
.\mvnw.cmd -DskipTests package
.\mvnw.cmd "-Dspring-boot.run.jvmArguments=-Dspring.devtools.restart.enabled=false" spring-boot:run
```

La compilación anterior omite las pruebas. Para ejecutarlas con la base local preparada: `.\mvnw.cmd test`.

En Linux/Mac: `./mvnw spring-boot:run`. También puedes ejecutar la clase `SaludCercaApplication` desde tu IDE (en VS Code, con el *Extension Pack for Java*).

Abre **http://localhost:8082** y elige un rol. Inicia **una sola instancia**: si ya está corriendo, una segunda falla con *"Port 8082 was already in use"*.

---

## 🖥️ Pantallas del sistema y patrón que ejecuta cada una

| Rol | Pantalla | Ruta | Patrón(es) que se ejecutan |
|-----|----------|------|----------------------------|
| Paciente | Buscar especialista | `/paciente` | **Singleton** (tarifario central) |
| Paciente | Agendar día y horario | `/paciente/medico/{id}` | regla de teleconsulta + disponibilidad real |
| Paciente | Confirmar y pagar | `/paciente/confirmar` | **Decorator** (costo), **Abstract Factory** (boleta/recibo), **Adapter** (DNI con proveedor simulado), **Factory Method** (canal) |
| Paciente | Cita confirmada | `/paciente/cita/{id}` | **Facade** (agenda cita, enlace, pago y notificación) |
| Médico | Agenda | `/medico` | citas reales del médico |
| Médico | Atender y diagnosticar | `/medico/cita/{id}` | **Prototype** (clon de la plantilla de historial) |
| Médico | Consulta finalizada | `/medico/cita/{id}/finalizada` | **Factory Method** (aviso al paciente) |
| Administrador | Panel | `/admin` | indicadores de la plataforma |
| Administrador | Verificar médicos | `/admin/medicos` | aprobación habilita al médico en la búsqueda |
| Administrador | Catálogo | `/admin/catalogo` | **Composite** (árbol de servicios y paquetes) |
| Administrador | Reportes | `/admin/reportes` | **Builder** (reporte paso a paso) |
| Administrador | Configuración | `/admin/configuracion` | **Singleton** (prueba de instancia única) |

Cada pantalla muestra una etiqueta morada **"◆ Patrón"** junto a la sección donde actúa el patrón.

---

## 🧩 Patrones de diseño implementados

### Creacionales

| Patrón | Caso en SaludCerca | Paquete | Laboratorio |
|--------|--------------------|---------|-------------|
| **Singleton** | Configuración central (IGV, moneda, recargos, tarifario) | `pattern.creational.singleton` | `/demo/singleton` |
| **Factory Method** | Canal de notificación (Email / SMS) | `pattern.creational.factorymethod` | `/demo/factory-method?tipo=sms` |
| **Prototype** | Clonación de plantillas de historial/receta | `pattern.creational.prototype` | `/demo/prototype?clave=nutricion` |
| **Abstract Factory** | Familia de comprobantes (Boleta / Recibo) | `pattern.creational.abstractfactory` | `/demo/abstract-factory?tipo=boleta&monto=120` |
| **Builder** | Construcción paso a paso de un reporte | `pattern.creational.builder` | `/demo/builder` |

### Estructurales

| Patrón | Caso en SaludCerca | Paquete | Laboratorio |
|--------|--------------------|---------|-------------|
| **Adapter** | Adaptar el proveedor de DNI simulado (RENIEC) | `pattern.structural.adapter` | `/demo/adapter?dni=45231890` |
| **Facade** | Agendar cita (orquesta todo el flujo) | `pattern.structural.facade` | `/demo/facade?idPaciente=1&idMedico=1&tipo=Teleconsulta&monto=80` |
| **Decorator** | Costo de consulta con añadidos (IGV, seguro) | `pattern.structural.decorator` | `/demo/decorator?base=80&teleconsulta=true&seguro=true` |
| **Composite** | Catálogo de servicios y paquetes/combos | `pattern.structural.composite` | `/demo/composite` |

El **laboratorio de patrones** (`/demo/creacionales` y `/demo/estructurales`) ejecuta cada patrón por separado; es útil para las preguntas técnicas.

---

## 📁 Estructura del proyecto

```
src/main/java/pe/utp/saludcerca/
├── SaludCercaApplication.java      # Clase principal
├── config/                         # DatosDemo, InicioRunner, PatronesConfig (bean del Adapter)
├── model/                          # Entidades JPA (8 tablas) y enums
├── repository/                     # Repositorios Spring Data JPA (8)
├── service/                        # CotizacionService, CatalogoService, ReporteService, AtencionService
├── pattern/
│   ├── creational/                 # Singleton, Factory Method, Prototype, Abstract Factory, Builder
│   └── structural/                 # Adapter, Facade, Decorator, Composite
└── web/                            # Controladores de Paciente, Médico, Administrador y laboratorio
src/main/resources/
├── templates/                      # Vistas Thymeleaf (paciente/, medico/, admin/, fragments/)
├── static/css/saludcerca.css       # Estilos (sistema de diseño de la PC1)
└── application.properties
db/
└── SaludCerca_Script_BD.sql        # Script de la base de datos
```

---

## 🧱 Principios SOLID aplicados

- **S** — Cada clase tiene una única responsabilidad (p. ej. `DisponibilidadService` solo verifica disponibilidad; `CotizacionService` solo calcula el costo).
- **O** — Nuevos canales, comprobantes y decoradores amplían las abstracciones y preservan la lógica común. Su registro o selección en la interfaz puede requerir ajustes.
- **L** — Cualquier implementación concreta (canal, decorador, fábrica, nodo del catálogo) sustituye a su abstracción.
- **I** — Interfaces pequeñas y enfocadas (`CanalNotificacion`, `Prototipo`, `ComprobanteFactory`, `ValidadorIdentidad`).
- **D** — Se depende de abstracciones con **inyección por constructor** (los controladores reciben `ValidadorIdentidad`, no el adaptador concreto).

---

## ▶️ Guion de la demostración (sustentación)

1. **Paciente** (`/paciente`): busca a la Dra. Castañeda → elige **Teleconsulta** y un horario → en *Confirmar y pagar* activa el **seguro** (el Decorator recalcula) y cambia **Boleta/Recibo** (Abstract Factory) → valida el DNI **45231890** con el padrón simulado (Adapter: paciente existente) → elige **SMS** (Factory Method) → confirma (**Facade**).
2. **Médico** (`/medico`): la cita recién agendada aparece en la agenda → *Atender* → el diagnóstico se llena con una **plantilla clonada** (Prototype) → finalizar.
3. **Administrador** (`/admin`): aprueba a un médico pendiente (aparece en la búsqueda) → *Catálogo* (Composite) → *Reportes* (Builder) → *Configuración* (Singleton).
4. Para comprobar la persistencia: en phpMyAdmin, `SELECT * FROM cita;`, `pago;` y `notificacion;`.

DNIs disponibles en el padrón simulado de RENIEC: `45231890` (Karla, registrada), `70123456` (José Luis, nuevo), `48765432` (Lucía, nueva).

---

_Proyecto académico — UTP, Diseño de Patrones, ciclo 2026-II._

## Inicio verificado en esta PC

Con MySQL de XAMPP encendido, ejecuta `powershell -NoProfile -ExecutionPolicy Bypass -File .\iniciar-local.ps1` desde esta carpeta. El script utiliza el JDK 21 portátil y la caché Maven disponibles en esta PC; no cambia Java globalmente. La aplicación abre en http://127.0.0.1:8082/. Para otro equipo, configura su JDK 21 y usa el wrapper Maven como se indica arriba.

La documentación de entrega usa Java 21 y la fecha 16/10/2026. Repositorio público: [https://github.com/anderjp/SaludCerca_PC2](https://github.com/anderjp/SaludCerca_PC2).

Para obtener una copia con Git:

```powershell
git clone https://github.com/anderjp/SaludCerca_PC2.git
cd SaludCerca_PC2
```

También se puede descargar desde **Code → Download ZIP**. Luego seguir los requisitos y pasos de ejecución de este README.

## Alcance y simulaciones de PC2

El proveedor de identidad utiliza un padrón local; los canales generan avisos de demostración y el enlace con formato Meet no crea una reunión. El pago se registra como Pendiente, sin cobro real. Los roles permiten recorrer las pantallas y todavía no implementan autenticación y autorización. Quedan pendientes cancelación de citas, verificación de centros y pago completado; el reporte usa el distrito como agrupación llamada región.

Para la demo de 15 minutos, consultar `GUIA_VIDEO_Y_ENTREGA_PC2.txt` y las notas del presentador de la PPT. La consigna contempla además cinco minutos de preguntas técnicas.
