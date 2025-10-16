# Sistema de Recursos Humanos

Sistema de gestion integral del capital humano desarrollado en Java con interfaz grafica Swing y soporte para bases de datos SQL Server y MySQL.

## Caracteristicas

- Gestion completa de empleados, contrataciones y movimientos de personal
- Sistema de reportes con filtrado por fechas y departamentos
- Autenticacion de usuarios con control de acceso basado en roles
- Interfaz grafica intuitiva con validaciones de datos
- Arquitectura modular con separacion de responsabilidades

## Tecnologias

- Java con JDBC
- Microsoft SQL Server / MySQL
- Java Swing para interfaz grafica
- Apache Ant para automatizacion de builds

## Instalacion

### Prerrequisitos

- Java JDK
- Microsoft SQL Server o MySQL
- Apache Ant (opcional)

### Pasos

1. Clonar el repositorio:

   ```bash
   git clone https://github.com/JuanSamayoa/SistemaRecursosHumanos.git
   cd SistemaRecursosHumanos
   ```

2. Configurar la base de datos:

   - Crear base de datos llamada `SistemaRRHH`
   - Ejecutar el script `SQL/Recursos Humanos.sql`

3. Configurar las credenciales de base de datos:

   - Copiar `.env_example` a `.env`
   - Editar `.env` con las credenciales reales de la base de datos

4. Compilar y ejecutar:

   ```bash
   # Usando Ant
   ant jar
   java -jar dist/SistemaRecursosHumanos.jar

   # O manualmente
   javac -cp "lib/*" -d build/classes src/ProyectoFinal/*.java src/ProyectoFinal/forms/*.java
   java -cp "lib/*;build/classes" ProyectoFinal.forms.LoginForm
   ```

## Estructura del Proyecto

```
SistemaRecursosHumanos/
├── src/ProyectoFinal/
│   ├── ConfigurationSystem.java     # Configuracion del sistema
│   ├── DatabaseManager.java         # Gestion de conexiones a BD
│   ├── Person.java, Employee.java   # Modelos de datos
│   ├── User.java, UserActions.java  # Gestion de usuarios
│   ├── Hiring.java, PersonalMovement.java  # Logica de negocio
│   ├── Report.java, *Report.java    # Sistema de reportes
│   ├── Utils.java                   # Utilidades y validaciones
│   └── forms/                       # Interfaz grafica (12 formularios)
├── SQL/Recursos Humanos.sql         # Esquema de base de datos
├── lib/                             # Drivers JDBC
├── build/                           # Archivos compilados
├── dist/                            # JAR ejecutable
├── Documentacion/                   # Manuales y diagramas
└── test/                            # Pruebas unitarias
```

## Modulos

- **Gestion de Personal**: CRUD de empleados con informacion completa
- **Contrataciones**: Registro de nuevas contrataciones
- **Movimientos de Personal**: Transferencias y cambios de puesto
- **Acciones de Personal**: Permisos, licencias y vacaciones
- **Reportes**: Analisis por departamento y fechas
- **Administracion de Usuarios**: Gestion de usuarios y roles

## Seguridad

- Autenticacion de usuarios con validacion de credenciales
- Control de acceso basado en roles (Admin, Reclutador, Operador)
- Validacion de entradas y sanitizacion de datos
- Conexiones seguras a base de datos

## Desarrollador

- **Nombre**: Juan Samayoa
- **Carne**: 9390-23-2010
- **Curso**: Programacion II
- **Repositorio**: https://github.com/JuanSamayoa/SistemaRecursosHumanos

---

Version 1.0.0 - Octubre 2025
