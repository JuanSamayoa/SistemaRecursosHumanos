# Sistema de Recursos Humanos

## Descripción
El### Tecnologías y Herramientas

### Backend
- **Java 11+** - Lenguaje principal de desarrollo
- **JDBC** - Conectividad con base de datos SQL Server
- **Microsoft SQL Server** - Sistema de ### Características Técnicas

### Arquitectura de Software
- **Arquitectura en capas** - Separación clara de responsabilidades
- **Clean Code** - Código legible y mantenible siguiendo principios SOLID, DRY, KISS y YAGNI
- **Principios SOLID** - Diseño orientado a objetos robusto
- **Patrón Template Method** - Para sistema de reportes flexible
- **Interface segregation** - Para acciones de usuario especializadasde base de datos principal
- **MySQL** - Soporte opcional para base de datos alternativa
- **Arquitectura en capas** - Separación de responsabilidadesema de Recursos Humanos** es una aplicación empresarial robusta diseñada para la gestión integral del capital humano en organizaciones. Desarrollado con arquitectura limpia y siguiendo principios SOLID, DRY, KISS y YAGNI, el sistema proporciona una solución completa para la administración de empleados, contrataciones, movimientos de personal y generación de reportes estratégicos.

### Características de la Arquitectura
- **Código limpio y mantenible** con principios de Clean Code
- **Seguridad robusta** con cifrado AES-256 y hashing SHA-256
- **Gestión centralizada de base de datos** con conexiones optimizadas
- **Sistema de reportes flexible** con filtrado opcional de fechas
- **Interfaz de usuario intuitiva** con validaciones exhaustivas

## Módulos del Sistema

### 1. **Gestión de Personal**
- Registro, modificación y eliminación de empleados
- Asignación a 10 departamentos organizacionales específicos
- Validación de datos con retroalimentación inmediata
- Gestión de salarios y cargos

### 2. **Contratación de Personal**
- Proceso completo de contratación con múltiples tipos de contrato
- Gestión de fechas de contratación con validación automática
- Integración con sistema de empleados
- Seguimiento de estados de contratación

### 3. **Control de Acciones del Personal**
- Gestión de permisos, licencias y vacaciones
- Registro detallado con fechas flexibles
- Sistema de aprobaciones y seguimiento
- Historial completo de acciones

### 4. **Control de Movimientos del Personal**
- Registro de promociones, transferencias y cambios de puesto
- Movimientos entre departamentos
- Historial de carrera profesional
- Reportes de movilidad interna

### 5. **Gestión de Usuarios y Seguridad**
- Sistema de autenticación seguro con cifrado AES-256
- Tres roles diferenciados: Administrador, Reclutador, Operador
- Gestión de contraseñas con hashing SHA-256
- Control de acceso basado en roles

### 6. **Sistema de Reportes Avanzado**
- Reportes de contrataciones recientes con filtrado flexible
- Análisis de movimientos de personal
- Reportes de acciones del personal
- Reportes por departamento
- Exportación y visualización de datos

## Tecnologías y Herramientas

### Backend
- **Java 11+** - Lenguaje principal
- **JDBC** - Conectividad con base de datos
- **Microsoft SQL Server** - Sistema de gestión de base de datos
- **Arquitectura MVC** - Separación de responsabilidades

### Frontend
- **Java Swing** - Interfaz gráfica nativa
- **JFrame y JOptionPane** - Componentes de UI
- **Validación en tiempo real** - Experiencia de usuario mejorada

### Seguridad
- **AES-256** - Cifrado de contraseñas
- **SHA-256** - Hashing seguro
- **Gestión de configuración** - Variables de entorno (.env)

### Desarrollo
- **NetBeans IDE** - Entorno de desarrollo integrado
- **Git** - Control de versiones
- **Clean Architecture** - Principios de diseño

## Estructura del Proyecto

```
SistemaRecursosHumanos/
├── src/ProyectoFinal/
│   ├── Person.java                        # Clase base para personas
│   ├── Employee.java                      # Gestión de empleados
│   ├── User.java                          # Sistema de usuarios
│   ├── Hiring.java                        # Gestión de contrataciones
│   ├── PersonalActions.java               # Acciones del personal
│   ├── PersonalMovement.java              # Movimientos de personal
│   ├── Report.java                        # Clase abstracta para reportes
│   ├── RecentHiringsReport.java           # Reportes de contrataciones
│   ├── PersonalMovementsReport.java       # Reportes de movimientos
│   ├── PersonalActionsReport.java         # Reportes de acciones
│   ├── DepartmentReport.java              # Reportes por departamento
│   ├── DatabaseManager.java               # Gestión de conexiones BD
│   ├── ConfigurationSystem.java           # Sistema de configuración
│   ├── PasswordSecurity.java              # Seguridad de contraseñas
│   ├── Utils.java                         # Utilidades generales
│   ├── UserActions.java                   # Interface de acciones de usuario
│   └── forms/                             # Interfaces de usuario (11 formularios)
│       ├── LoginForm.java                 # Formulario de inicio de sesión
│       ├── MainMenuForm.java              # Menú principal
│       ├── PersonalManagementForm.java    # Gestión de empleados
│       ├── HiringStaffForm.java           # Contratación de personal
│       ├── PersonalActionControlForm.java # Control de acciones
│       ├── PersonalMovementControlForm.java # Control de movimientos
│       ├── UserManagementForm.java        # Gestión de usuarios
│       └── [otros formularios]            # Formularios de reportes
├── SQL/
│   └── Recursos Humanos.sql               # Esquema de base de datos
├── lib/                                   # Librerías JDBC
│   ├── mssql-jdbc-12.8.1.jre11.jar      # Driver SQL Server
│   └── mysql-connector-j-9.1.0.jar       # Driver MySQL (opcional)
├── build/                                 # Archivos compilados
├── dist/                                  # Distribución JAR
├── .env                                   # Configuración de entorno
└── README.md
```

## Instalación y Configuración

### Requisitos Previos
- **Java JDK 11+** - Entorno de desarrollo Java
- **Microsoft SQL Server** - Base de datos (SQL Server 2019+ recomendado)
- **NetBeans IDE** - Entorno de desarrollo (opcional, pero recomendado)
- **Git** - Control de versiones

### Pasos de Instalación

#### 1. Clonar el Repositorio
```bash
git clone https://github.com/JuanSamayoa/SistemaRecursosHumanos.git
cd SistemaRecursosHumanos
```

#### 2. Configurar Base de Datos
1. **Instalar SQL Server** y crear una nueva base de datos llamada `RecursosHumanos`
2. **Ejecutar el script SQL** ubicado en `SQL/Recursos Humanos.sql`
3. **Verificar** que las tablas se hayan creado correctamente

#### 3. Configurar Variables de Entorno
1. **Copiar** el archivo `.env.example` como `.env`
2. **Actualizar** las credenciales de base de datos:
```env
DB_SERVER=localhost
DB_PORT=1433
DB_NAME=RecursosHumanos
DB_USER=tu_usuario
DB_PASSWORD=tu_contraseña_cifrada
DB_ENCRYPT_KEY=tu_clave_de_32_caracteres
```

#### 4. Generar Contraseña Cifrada
Usar la utilidad incluida para cifrar contraseñas:
```bash
# Compilar la utilidad de seguridad
javac -cp "lib/*" -d build/classes src/ProyectoFinal/PasswordSecurity.java

# Cifrar contraseña
java -cp "lib/*;build/classes" ProyectoFinal.PasswordSecurity encrypt tu_contraseña tu_clave_de_32_caracteres
```

#### 5. Compilar el Proyecto
```bash
# Desde el directorio raíz del proyecto
javac -cp "lib/*" -d build/classes src/ProyectoFinal/*.java src/ProyectoFinal/forms/*.java
```

#### 6. Ejecutar la Aplicación
```bash
# Opción 1: Desde clases compiladas
java -cp "lib/*;build/classes" ProyectoFinal.forms.LoginForm

# Opción 2: Desde JAR (si existe)
java -jar dist/SistemaRecursosHumanos.jar
```

### Configuración en NetBeans
1. **Abrir** NetBeans IDE
2. **Importar** el proyecto existente (File → Open Project)
3. **Configurar** las librerías JDBC en el classpath del proyecto
4. **Configurar** el archivo `.env` con las credenciales de base de datos
5. **Ejecutar** desde `LoginForm.java` (clic derecho → Run File)

## Guía de Uso

### Inicio de Sesión
1. **Ejecutar** la aplicación desde `LoginForm`
2. **Ingresar** credenciales de usuario válidas
3. **Seleccionar** el rol apropiado

### Roles y Permisos

#### 🔑 Administrador
- **Acceso completo** a todos los módulos
- **Gestión de usuarios** y configuración del sistema
- **Generación de todos los reportes**
- **Administración de empleados, contrataciones y movimientos**

#### 👥 Reclutador
- **Gestión de empleados** (agregar, modificar, consultar)
- **Proceso de contratación** completo
- **Reportes de contrataciones** y personal
- **Acceso limitado** a movimientos internos

#### 📊 Operador
- **Control de acciones del personal** (permisos, vacaciones)
- **Registro de movimientos** internos
- **Reportes operativos** específicos
- **Consulta de información** de empleados

### Funcionalidades Principales

#### Gestión de Personal
- **Agregar empleados** con validación completa
- **Actualizar información** existente
- **Consultar** empleados por diversos criterios
- **Gestión de departamentos** (10 departamentos disponibles)

#### Sistema de Reportes
- **Filtrado flexible** por fechas (opcional)
- **Exportación** de datos
- **Visualización** en tablas interactivas
- **Reportes especializados** por módulo

#### Seguridad
- **Autenticación segura** con cifrado
- **Gestión de sesiones** por roles
- **Auditoría** de acciones del usuario

## Departamentos Disponibles
1. Dirección Ejecutiva
2. Secretaría
3. Unidad de Auditoría Interna
4. Recursos Humanos
5. Tecnología de la Información
6. Finanzas
7. Marketing
8. Operaciones
9. Ventas
10. Logística

## Características Técnicas

### Arquitectura de Software
- **Patrón MVC** - Separación clara de responsabilidades
- **Clean Code** - Código legible y mantenible
- **Principios SOLID** - Diseño orientado a objetos
- **Template Method** - Para sistema de reportes
- **Factory Pattern** - Para creación de objetos

### Seguridad Implementada
- **Cifrado AES-256** - Para contraseñas en base de datos
- **Hashing SHA-256** - Para verificación de contraseñas
- **Sanitización de inputs** - Prevención de inyección SQL
- **Validación robusta** - En todas las entradas de datos

### Optimizaciones
- **Gestión optimizada de conexiones** - Pool de conexiones para base de datos
- **Métodos CRUD estandarizados** - add(), update(), delete(), getAll(), getNextId()
- **Validación robusta** - En todas las entradas de datos con retroalimentación inmediata
- **Manejo de errores** - Try-catch exhaustivo con recuperación automática
- **Conversión de formatos** - Fechas entre formato guatemalteco (dd/MM/yyyy) e ISO (yyyy-MM-dd)

## Resolución de Problemas

### Problemas Comunes

#### Error de Conexión a Base de Datos
```
✅ Solución:
1. Verificar que SQL Server esté ejecutándose
2. Confirmar credenciales en archivo .env
3. Validar que la base de datos 'RecursosHumanos' exista
4. Comprobar conectividad de red (firewall, puertos)
```

#### Error de Compilación
```
✅ Solución:
1. Verificar que todas las librerías estén en classpath
2. Confirmar versión de Java (JDK 11+)
3. Limpiar y recompilar proyecto
4. Verificar estructura de directorios
```

#### Problemas de Autenticación
```
✅ Solución:
1. Verificar que las contraseñas estén correctamente cifradas
2. Comprobar configuración de clave de cifrado
3. Validar datos de usuario en base de datos
4. Reiniciar sesión
```

## Contribución y Desarrollo

### Estándares de Código
- **Java Code Conventions** - Estilo de código estándar
- **JavaDoc** - Documentación de métodos públicos
- **Unit Testing** - Pruebas para funciones críticas
- **Code Review** - Revisión antes de merge

### Estructura de Commits
```
feat: nueva funcionalidad
fix: corrección de bug
refactor: mejora de código sin cambio de funcionalidad
docs: actualización de documentación
test: agregar o modificar pruebas
```

## Licencia
Este proyecto está bajo la **Licencia MIT**. Ver el archivo `LICENSE` para más detalles.

## Contacto y Soporte
- **Desarrollador:** Juan Samayoa
- **Correo electrónico:** [juancho1705@gmail.com](mailto:juancho1705@gmail.com)
- **GitHub:** [@JuanSamayoa](https://github.com/JuanSamayoa)
- **LinkedIn:** [linkedin.com/in/juansamayoa](https://linkedin.com/in/juansamayoa)

### Reportar Issues
Para reportar bugs o solicitar nuevas funcionalidades, usar el sistema de **Issues** de GitHub con las siguientes etiquetas:
- `bug` - Para errores
- `enhancement` - Para mejoras
- `documentation` - Para documentación
- `question` - Para preguntas

---

## Historial de Versiones

### v2.0.0 (Actual) - Julio 2025
- ✅ Refactorización completa con Clean Code y principios SOLID, DRY, KISS, YAGNI
- ✅ Implementación de seguridad robusta (AES-256 + SHA-256)
- ✅ Sistema de reportes flexible con filtrado opcional de fechas
- ✅ Métodos CRUD estandarizados en inglés (add, update, delete, getAll, getNextId)
- ✅ Validaciones exhaustivas y manejo de errores mejorado
- ✅ Optimización de base de datos y consultas SQL
- ✅ Conversión automática de formatos de fecha (guatemalteco ↔ ISO)
- ✅ Interface UserActions para operaciones de usuario especializadas

### v1.0.0 - Versión Inicial
- Sistema básico de gestión de recursos humanos
- Funcionalidades CRUD básicas
- Interfaz de usuario con Swing
- Conexión a SQL Server

---

**Desarrollado por Juan Samayoa (Carné: 9390-23-2010)** | Sistema de Recursos Humanos Empresarial | Programación II
