# Sistema de Recursos Humanos

## Descripción
El **Sistema de Recursos Humanos** es una aplicación diseñada para la gestión eficiente del personal dentro de una organización. Su objetivo es organizar y coordinar todas las actividades relacionadas con la contratación, administración de empleados, permisos, licencias, vacaciones y movimientos dentro de la empresa. Además, ofrece un módulo de reportes para la toma de decisiones estratégicas en el departamento de Recursos Humanos.

## Características Principales
El sistema se compone de los siguientes módulos:

1. **Gestión de Personal:** Permite el registro, modificación y eliminación de empleados, asignándolos a departamentos específicos.
2. **Contratación de Personal:** Administra el proceso de contratación, incluyendo contratos, tipos de contrato y duración.
3. **Control de Acciones del Personal:** Maneja permisos, licencias y vacaciones, manteniendo un registro detallado de cada acción.
4. **Control de Movimientos del Personal:** Registra cambios de puesto, promociones y transferencias de empleados.
5. **Gestión de Usuarios:** Administra cuentas de usuario con diferentes roles (Administrador, Reclutador, Operador) y permisos de acceso.
6. **Generación de Reportes:** Permite obtener reportes de empleados, contrataciones, movimientos y ausencias.

## Tecnologías Utilizadas
- **Lenguaje:** Java
- **Entorno de desarrollo:** NetBeans
- **Interfaz gráfica:** Swing con `JFrame` y `JOptionPane`
- **Base de datos:** Microsoft SQL Server

## Instalación y Ejecución
### Requisitos previos
- Tener instalado Java (JDK 8 o superior).
- Configurar SQL Server si se requiere una base de datos.
- Tener un IDE como NetBeans, IntelliJ IDEA o Eclipse.

### Pasos para instalar y ejecutar
1. **Clonar el repositorio:**
   ```sh
   git clone https://github.com/JuanSamayoa/SistemaRecursosHumanos.git
   cd tu-repositorio
   ```
2. **Abrir el proyecto en un IDE.**
3. **Configurar la base de datos** (si aplica): Importar el esquema SQL y actualizar las credenciales en el código fuente.
4. **Compilar y ejecutar el sistema.**

## Uso del Sistema
1. **Iniciar sesión** con un usuario válido.
2. **Acceder a los módulos según el rol asignado:**
   - Administrador: Acceso a todos los módulos.
   - Reclutador: Gestión de empleados y contrataciones.
   - Operador: Control de acciones y movimientos del personal.
3. **Generar reportes** sobre contrataciones, ausencias y movimientos de personal.

## Autor
Desarrollado por **[Juan Samayoa]** como parte de un proyecto de gestión de recursos humanos.
