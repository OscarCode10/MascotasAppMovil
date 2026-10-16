# Guía 6 — MascotasApp: CRUD con Volley + PHP + MySQL

**Asignatura:** Computación Móvil — UMB · **Docente:** Robert Osorio Torres
**Tema elegido:** registro de mascotas de una veterinaria.

La app permite **Crear, Leer, Actualizar y Eliminar** mascotas guardadas en MySQL, consumiendo servicios PHP en Apache (XAMPP) mediante la biblioteca **Volley**.

---

## 1. Contenido de la entrega

```
MascotasCRUD_Entrega/
├── MascotasApp/                  ← Proyecto Android Studio (Java)
│   └── app/src/main/
│       ├── java/com/umb/mascotasapp/MainActivity.java
│       ├── res/layout/activity_main.xml   (formulario + lista)
│       ├── res/layout/item_mascota.xml    (tarjeta de cada mascota)
│       └── res/drawable/ic_perro|gato|ave|conejo|pez.xml
└── backend_php/
    ├── mascotas_db.sql            ← Base de datos, tabla, usuario y datos de prueba
    └── MascotasDB/                ← Carpeta que va dentro de htdocs
        ├── conexion.php           (equivale a DatabaseConfig.php de la guía)
        ├── listar.php             R
        ├── insertar.php           C
        ├── actualizar.php         U
        └── eliminar.php           D
```

## 2. Requisitos de la UI (lo que pide la guía)

| Control pedido | Dónde está en la app |
|---|---|
| 1. Lista desplegable | `Spinner` de **especie**: Perro, Gato, Ave, Conejo, Pez |
| 2. Checkbox | **Vacunado** y **Esterilizado** |
| 3. Radiobutton | **Sexo**: Macho / Hembra (dentro de un `RadioGroup`) |
| 4. ImageView | Imagen que **cambia según la especie** elegida (y miniatura en cada tarjeta de la lista) |

## 3. Cómo funciona el CRUD

| Operación | Botón en la app | Servicio PHP | Método |
|---|---|---|---|
| Crear | **Guardar** | `insertar.php` | POST |
| Leer | Se carga al abrir + **Refrescar** | `listar.php` | GET |
| Actualizar | Tocar una mascota → **Actualizar** | `actualizar.php` | POST |
| Eliminar | Tocar una mascota → **Eliminar** (pide confirmación) | `eliminar.php` | POST |

Todos los servicios responden el mismo JSON sencillo: `{"ok": true/false, "mensaje": "...", "data": [...]}`. En Android se usa **un solo método** (`enviarPost`) para insertar, actualizar y eliminar — esa es la "forma sencilla" de consumir servicios frente a la guía original, que solo hacía el registro.

En PHP se usan **consultas preparadas** (`prepare` + `bind_param`) para evitar inyección SQL.

## 4. Paso a paso para ejecutarlo

1. **Encender XAMPP:** abrir el panel de XAMPP y darle *Start* a **Apache** y **MySQL**.
2. **Copiar el backend:** copiar la carpeta `backend_php/MascotasDB` dentro de `C:\xampp\htdocs\` → debe quedar `C:\xampp\htdocs\MascotasDB\`.
3. **Crear la base de datos:** entrar a `http://localhost/phpmyadmin` → pestaña **SQL** → pegar todo el contenido de `mascotas_db.sql` → **Continuar**. Esto crea la BD `mascotas_db`, la tabla `mascotas`, el usuario `umb_movil` (clave `movil2026`) y 3 mascotas de prueba.
4. **Probar el servicio en el navegador:** abrir `http://localhost/MascotasDB/listar.php`. Debe salir un JSON con Firulais, Michi y Piolín. Si sale eso, el backend está listo.
5. **Abrir el proyecto:** en Android Studio → *File → Open* → seleccionar la carpeta **`MascotasApp`** (la que tiene `settings.gradle.kts` adentro; ojo con la doble carpeta al descomprimir). Esperar a que termine *Gradle Sync*.
6. **Revisar la URL** en `MainActivity.java` (línea `BASE_URL`):
   - **Emulador:** dejar `http://10.0.2.2/MascotasDB/` (10.0.2.2 es el "localhost" del PC visto desde el emulador).
   - **Celular físico:** el celular y el PC en el mismo WiFi; en el PC ejecutar `ipconfig` y usar la IPv4, ej. `http://192.168.1.10/MascotasDB/`.
7. **Ejecutar** (▶ Run) y probar: registrar una mascota, tocarla en la lista, cambiarle datos y actualizar, y eliminarla.
8. **Verificar en phpMyAdmin** que los cambios quedaron en la tabla `mascotas` (bueno para mostrarlo en el video).

## 5. Problemas comunes

| Síntoma | Solución |
|---|---|
| Toast "No se pudo conectar…" | Apache apagado, o `BASE_URL` incorrecta. Probar primero el paso 4. |
| Funciona en emulador pero no en el celular | Usar la IP del PC (paso 6) y permitir Apache en el **Firewall de Windows** (o desactivarlo un momento para la prueba). |
| `"Error de conexión: Access denied…"` | No se ejecutó la parte del usuario en el SQL. Volver a correr `mascotas_db.sql` o poner `root` y clave vacía en `conexion.php`. |
| `Cleartext HTTP traffic not permitted` | Ya está resuelto con `android:usesCleartextTraffic="true"` en el Manifest. |
| MySQL de XAMPP no arranca (puerto 3306 ocupado) | Cerrar otro MySQL instalado o cambiar el puerto en XAMPP. |

## 6. Detalles técnicos

- **Lenguaje:** Java · **minSdk** 21 · **targetSdk** 34 · Gradle 8.7 / AGP 8.5.2
- **Dependencia:** `implementation("com.android.volley:volley:1.2.1")`
- **Permiso:** `android.permission.INTERNET`
- Volley envía los datos con `getParams()` como formulario, por eso en PHP se leen con `$_POST`.
- `DefaultRetryPolicy` de 8 segundos con 1 reintento (ventaja de Volley: personalización de reintentos).

## 7. Conclusiones

- Volley simplifica las peticiones HTTP: maneja la cola, los hilos y devuelve la respuesta directamente en el hilo de la UI.
- Separar la app (cliente) del servidor (PHP + MySQL) permite cambiar la base de datos sin tocar la app, siempre que el JSON se mantenga igual.
- Estandarizar la respuesta JSON (`ok`, `mensaje`, `data`) hace que un solo método en Android sirva para todas las operaciones del CRUD.
