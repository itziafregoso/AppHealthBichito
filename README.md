# 🌿 HealthBichito+
Aplicación móvil de salud y bienestar desarrollada por Itzia Fregoso.  
Incluye monitoreo con sensores WearOS, Firebase Authentication, Firestore, dashboards de actividad y sincronización entre app móvil y smartwatch.

---

## 📱 Tecnologías principales
- Kotlin + Jetpack Compose
- Material Design 3
- Firebase Auth + Firestore
- Google Play Services
- WearOS Sensors
- MVVM + Clean Architecture
- Coroutines + Flow

---

## 🏗️ Arquitectura
El proyecto está organizado en capas limpias que separan UI, lógica y datos:
- **UI (Compose)** Pantallas, componentes, navegación.
- **Domain** Casos de uso y modelos del dominio.
- **Data** Firebase, repositorios, DTOs.
- **WearOS** Lógica independiente para el reloj.

---

## 🧭 Estructura del repositorio
- `/app` Código principal de la app Android.
- `/wearos` Código para el smartwatch.
- `/docs` Diagramas, vistas arquitectónicas, documentación.
- `/assets` Logos, colores, UI.

---

## 🧪 Funciones principales
- Registro y login con Firebase
- Dashboard con pasos, calorías y ritmo cardíaco
- Sincronización con Xiaomi Watch 2 (WearOS)
- Estadísticas diarias en tiempo real
- Perfil de usuario personalizado
- Notificaciones y recordatorios saludables

---

## 🧰 Cómo ejecutar el proyecto
1. Clona el repositorio  
   ```bash
   git clone https://github.com/itziafregoso/AppHealthBichito.git
