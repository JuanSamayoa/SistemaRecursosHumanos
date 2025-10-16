# Sistema de Recursos Humanos

Aplicación empresarial para gestión integral del capital humano, desarrollada en Java con arquitectura limpia siguiendo principios SOLID, DRY, KISS y YAGNI.

## 🚀 Características Principales

- **Gestión Completa de Personal**: CRUD de empleados, contrataciones, movimientos y acciones
- **Sistema de Reportes**: Reportes flexibles con filtrado opcional por fechas
- **Seguridad Robusta**: Cifrado AES-256 y hashing SHA-256
- **Interfaz Intuitiva**: Validaciones exhaustivas y retroalimentación inmediata
- **Arquitectura Limpia**: Código mantenible con separación de responsabilidades

## 🛠️ Tecnologías

- **Java 11+** con JDBC
- **Microsoft SQL Server** (principal) / **MySQL** (opcional)
- **Java Swing** para interfaz gráfica
- **Clean Architecture** con principios SOLID

## 📦 Instalación Rápida

### Prerrequisitos

- Java JDK 11+
- Microsoft SQL Server o MySQL
- NetBeans IDE (opcional)

### Pasos

1. **Clonar repositorio**

   ```bash
   git clone https://github.com/JuanSamayoa/SistemaRecursosHumanos.git
   cd SistemaRecursosHumanos
   ```

2. **Configurar base de datos**

   - Crear BD llamada `RecursosHumanos`
   - Ejecutar `SQL/Recursos Humanos.sql`

3. **Configurar entorno**

   - Copiar y editar `.env` con credenciales de BD

4. **Compilar y ejecutar**
   ```bash
   javac -cp "lib/*" -d build/classes src/ProyectoFinal/*.java src/ProyectoFinal/forms/*.java
   java -cp "lib/*;build/classes" ProyectoFinal.forms.LoginForm
   ```

## 📁 Estructura del Proyecto

```
SistemaRecursosHumanos/
├── src/ProyectoFinal/
│   ├── Person.java, Employee.java, User.java          # Modelos de datos
│   ├── Hiring.java, PersonalActions.java, PersonalMovement.java  # Lógica de negocio
│   ├── Report.java, *Report.java                      # Sistema de reportes
│   ├── DatabaseManager.java, Utils.java, UserActions.java  # Servicios y utilidades
│   └── forms/                                         # Interfaz gráfica (11 formularios)
├── SQL/Recursos Humanos.sql                           # Esquema de BD
├── lib/                                               # Drivers JDBC
├── build/classes/                                     # Archivos compilados
├── dist/SistemaRecursosHumanos.jar                    # JAR ejecutable
└── Documentacion/                                     # Manuales y diagramas
```

## 🎯 Módulos del Sistema

- **👥 Gestión de Personal**: CRUD completo de empleados con 10 departamentos
- **📝 Contratación**: Proceso completo con múltiples tipos de contrato
- **📋 Acciones del Personal**: Permisos, licencias, vacaciones con historial
- **🔄 Movimientos**: Promociones, transferencias, cambios de puesto
- **👤 Usuarios**: Sistema de autenticación con 3 roles (Admin, Reclutador, Operador)
- **📊 Reportes**: Análisis por departamento, contrataciones, movimientos y acciones

## 🔐 Seguridad

- Autenticación segura con cifrado AES-256
- Hashing SHA-256 para contraseñas
- Control de acceso basado en roles
- Sanitización de inputs y validación robusta

## 📋 Información del Desarrollador

- **Desarrollador**: Juan Samayoa
- **Carné**: 9390-23-2010
- **Curso**: Programación II
- **GitHub**: [@JuanSamayoa](https://github.com/JuanSamayoa)

---

**Versión 2.0.0** - Julio 2025 | Sistema de Recursos Humanos Empresarial
