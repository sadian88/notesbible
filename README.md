# NotesBible

Aplicación Android inspirada en [Pencil Bible](https://pencilbible.com/) que permite descargar y estudiar diferentes traducciones de la Biblia completamente offline.

## Características principales
- **Catálogo de traducciones**: lista curada de Biblias en español e inglés con metadatos y enlaces directos al contenido en formato JSON.
- **Descargas para uso sin conexión**: los textos se guardan localmente en una base de datos Room para que puedas leerlos aunque no tengas internet.
- **Lectura estructurada**: navegador por libro y capítulo con Jetpack Compose.
- **Notas a mano alzada**: abre un lienzo inspirado en Pencil Bible para dibujar ideas o bosquejos sobre cada capítulo y guárdalos localmente.
- **Arquitectura offline-first**: repositorio único que combina Retrofit, Room y flujos de Kotlin coroutines para mantener la UI actualizada.

## Cómo ejecutar
1. Abre el proyecto en Android Studio Giraffe o superior.
2. Sincroniza Gradle; el proyecto usa Kotlin 1.9 y Compose BOM 2023.10.01.
3. Conecta un dispositivo Android (API 24+) o crea un emulador.
4. Ejecuta la app con el botón **Run**.

> _Nota_: Los enlaces de descarga utilizan los JSON públicos del proyecto [`thiagobodruk/bible`](https://github.com/thiagobodruk/bible). Asegúrate de tener conexión a internet para descargar una traducción la primera vez.

## Estructura del proyecto
```
.
├── app
│   ├── build.gradle.kts
│   └── src/main
│       ├── java/com/notesbible/app
│       │   ├── data        # Room, Retrofit y repositorios
│       │   ├── ui          # Pantallas y ViewModels en Compose
│       │   └── NotesBibleApp.kt
│       └── res             # Recursos XML
├── build.gradle.kts
├── gradle.properties
└── settings.gradle.kts
```

## Próximos pasos sugeridos
- Añadir autenticación opcional para sincronizar notas entre dispositivos.
- Integrar un motor de búsqueda de versículos.
- Crear un sistema de notas/destacados inspirado en Pencil Bible.

## Preguntas frecuentes

### ¿Qué significa el error `./gradlew -q help (fails because the Gradle wrapper is not present in this repository)`?
Significa que en el repositorio no existe el script `gradlew` (el *Gradle Wrapper*) ni sus archivos asociados, por lo que al intentar ejecutar `./gradlew ...` la terminal no encuentra el ejecutable. Para solucionarlo tienes dos opciones:

1. Instalar el wrapper ejecutando `gradle wrapper --gradle-version 8.2 --distribution-type all`, lo cual generará los archivos `gradlew`, `gradlew.bat` y `gradle/wrapper/*` que deben versionarse en Git.
2. Mientras no haya wrapper, puedes usar una instalación local de Gradle (por ejemplo `gradle -q help`) recordando que todos los colaboradores deberán tener la misma versión instalada manualmente.

La recomendación es agregar el wrapper al repositorio para evitar este tipo de errores y garantizar que todos usen la misma versión de Gradle.
