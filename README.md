# SIPINNA Atizapán — App ciudadana

App Android para reportar casos de trabajo infantil en Atizapán de Zaragoza,
Estado de México. Forma parte del Sistema de Protección Integral de Niñas, Niños
y Adolescentes (SIPINNA) del municipio.

El panel con el que el personal de SIPINNA da seguimiento a estos reportes vive
en [sipinna-admin](https://github.com/Ferrusca08/sipinna-admin).

## Qué hace

Cualquier persona puede levantar un reporte en cuatro pasos —categoría,
descripción, ubicación y contacto— y recibe un folio para consultar el avance.

**El reporte anónimo es el camino principal.** No se pide cuenta ni datos de
contacto: quien reporta decide cuánto quiere exponerse, y el caso se atiende
igual. Se pueden adjuntar hasta cinco fotos, y la app pide explícitamente evitar
el rostro de la niña, niño o adolescente.

## Arquitectura

Kotlin con Jetpack Compose, en capas `data` / `domain` / `presentation`.

| | |
|---|---|
| UI | Jetpack Compose, Material 3 |
| Inyección | Hilt |
| Red | Retrofit + OkHttp + Moshi |
| Mapas | Google Maps Compose |
| Ubicación | Play Services Location |
| Persistencia local | DataStore |

El backend es serverless en AWS: API Gateway HTTP, Lambda en Node 20, DynamoDB,
y S3 con cifrado KMS para la evidencia fotográfica.

## Compilar

Requiere **JDK 17** y el SDK de Android 35.

```bash
cp local.properties.example local.properties
# Llena API_BASE_URL y MAPS_API_KEY
./gradlew :app:assembleDebug
```

La APK queda en `app/build/outputs/apk/debug/`.

`local.properties` no se versiona: lleva la llave de Google Maps y la URL del
API. Restringe esa llave por nombre de paquete y huella SHA-1 en la consola de
Google Cloud.

## Privacidad

Es una app que maneja datos de menores de edad y de quien denuncia, así que
algunas decisiones no son negociables:

- En compilaciones de release no se registra el cuerpo de las peticiones.
- La evidencia viaja a S3 con URLs prefirmadas de vigencia corta y se cifra con
  KMS; la app nunca toca credenciales de AWS.
- Sin cuenta, el reporte no guarda ningún identificador de quien lo envió.

## Licencia

Sistema desarrollado para el H. Ayuntamiento de Atizapán de Zaragoza.
