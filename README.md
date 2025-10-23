# Convertify - App para convertir videos

Convertify es una aplicación Android (Kotlin) que convierte videos de múltiples plataformas (TikTok, Instagram, YouTube, Facebook). La conversión se realiza en la nube: la app envía el link del video a una API Gateway (AWS) que invoca una Lambda encargada de convertir el video y devolver un enlace de descarga final sin marcas de agua.

## 🛠️ Tecnologías utilizadas

- **Android Studio (Kotlin)**
- **AWS (API Gateway, Lambda)**
- **Firebase** (para autenticación y almacenamiento)

## 📱 Funcionalidades principales

- Inicio de sesión seguro mediante autenticación.
- Conversión remota usando arquitectura serverless (API Gateway → AWS Lambda).
- Soporte para múltiples plataformas: TikTok, Instagram, YouTube, Facebook.
- Descarga del video final sin marcas de agua (según la lógica de la Lambda).
- UI Android en Kotlin: envío de enlaces, recepción y presentación del enlace resultante, y gestión básica de archivos descargados.
- Integración con AdMob y Firebase Auth (presente en la app).

## 🧪 Mejoras futuras

- Inicio de sesión con Google, Facebook u otras plataformas.
- Funcionalidades para recortar, modificar videos.
