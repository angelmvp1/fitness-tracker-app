# Apex Cycle Tracker

Aplicación Android moderna desarrollada en Kotlin con Jetpack Compose para llevar un seguimiento integral de entrenamientos, métricas corporales y ciclos hormonales blast & cruise.

## Características principales

- **Registro de entrenamientos** con ejercicios personalizados, series, repeticiones y pesos.
- **Bitácora corporal** para peso, % de grasa y mediciones (pecho, cintura, brazo, pierna).
- **Gestión de ciclos blast / cruise / off** con compuestos, dosis y frecuencia semanal.
- **Notas diarias** para documentar sensaciones, energía y efectos secundarios.
- **Recordatorios locales** (sin conexión) para suplementos o inyecciones, con repetición semanal opcional.
- **Dashboard premium** con gráficos de volumen semanal usando Compose Canvas y resumen de progreso.
- **Soporte de tema claro/oscuro** con paleta personalizada basada en negro, blanco, cyan y azul rey.
- **Almacenamiento 100% local** con Room + Kotlin Flows.

## Requisitos previos

- Android Studio Giraffe (o superior) con Android Gradle Plugin 8.2+.
- JDK 17 (incluido en Android Studio actual).
- Dispositivo o emulador con Android 8.0 (API 26) o superior.

## Cómo compilar e instalar

1. Clona este repositorio y ábrelo desde Android Studio (`File > Open` seleccionando la carpeta raíz).
2. Android Studio descargará automáticamente las dependencias de Gradle. Si es la primera vez, acepta la sincronización.
3. Conecta tu dispositivo (activa la depuración USB) o inicia un emulador.
4. Selecciona la configuración `app` y ejecuta **Run ▶** para compilar e instalar.
5. Acepta los permisos de notificaciones y alarmas exactas cuando la app los solicite para habilitar recordatorios.

Para compilar desde la terminal:

```bash
./gradlew assembleDebug
```

El APK resultante se ubicará en `app/build/outputs/apk/debug/app-debug.apk`.

## Estructura del proyecto

- `app/src/main/java/com/example/fitnesstracker/data` contiene entidades Room, DAOs y repositorio.
- `app/src/main/java/com/example/fitnesstracker/ui` aloja la navegación y pantallas Compose.
- `app/src/main/java/com/example/fitnesstracker/notifications` implementa notificaciones locales y recordatorios.
- `app/src/main/res` define recursos, temas y colores con soporte light/dark.

## Personalización

- Ajusta la paleta en `ui/theme/Color.kt` para adaptar la identidad visual.
- Modifica la lógica de recordatorios en `ReminderScheduler` si necesitas reglas recurrentes más avanzadas.
- Amplía las métricas corporales añadiendo nuevos campos en `BodyMetric` y su UI en `MetricsScreen`.

## Licencia

Uso personal. Puedes adaptar el código libremente para tus necesidades.
