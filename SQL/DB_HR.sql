USE [master]
GO
/****** Object:  Database [SistemaRRHH]    Script Date: 10/07/2025 22:37:32 ******/
CREATE DATABASE [SistemaRRHH]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'SistemaRRHH', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\SistemaRRHH.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON 
( NAME = N'SistemaRRHH_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\SistemaRRHH_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
 WITH CATALOG_COLLATION = DATABASE_DEFAULT, LEDGER = OFF
GO
ALTER DATABASE [SistemaRRHH] SET COMPATIBILITY_LEVEL = 160
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [SistemaRRHH].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [SistemaRRHH] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [SistemaRRHH] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [SistemaRRHH] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [SistemaRRHH] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [SistemaRRHH] SET ARITHABORT OFF 
GO
ALTER DATABASE [SistemaRRHH] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [SistemaRRHH] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [SistemaRRHH] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [SistemaRRHH] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [SistemaRRHH] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [SistemaRRHH] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [SistemaRRHH] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [SistemaRRHH] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [SistemaRRHH] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [SistemaRRHH] SET  DISABLE_BROKER 
GO
ALTER DATABASE [SistemaRRHH] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [SistemaRRHH] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [SistemaRRHH] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [SistemaRRHH] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [SistemaRRHH] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [SistemaRRHH] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [SistemaRRHH] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [SistemaRRHH] SET RECOVERY FULL 
GO
ALTER DATABASE [SistemaRRHH] SET  MULTI_USER 
GO
ALTER DATABASE [SistemaRRHH] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [SistemaRRHH] SET DB_CHAINING OFF 
GO
ALTER DATABASE [SistemaRRHH] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [SistemaRRHH] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
ALTER DATABASE [SistemaRRHH] SET DELAYED_DURABILITY = DISABLED 
GO
ALTER DATABASE [SistemaRRHH] SET ACCELERATED_DATABASE_RECOVERY = OFF  
GO
EXEC sys.sp_db_vardecimal_storage_format N'SistemaRRHH', N'ON'
GO
ALTER DATABASE [SistemaRRHH] SET QUERY_STORE = ON
GO
ALTER DATABASE [SistemaRRHH] SET QUERY_STORE (OPERATION_MODE = READ_WRITE, CLEANUP_POLICY = (STALE_QUERY_THRESHOLD_DAYS = 30), DATA_FLUSH_INTERVAL_SECONDS = 900, INTERVAL_LENGTH_MINUTES = 60, MAX_STORAGE_SIZE_MB = 1000, QUERY_CAPTURE_MODE = AUTO, SIZE_BASED_CLEANUP_MODE = AUTO, MAX_PLANS_PER_QUERY = 200, WAIT_STATS_CAPTURE_MODE = ON)
GO
USE [SistemaRRHH]
GO
/****** Object:  User [admindb]    Script Date: 10/07/2025 22:37:32 ******/
CREATE USER [admindb] WITHOUT LOGIN WITH DEFAULT_SCHEMA=[RecursosHumanos]
GO
/****** Object:  Schema [RecursosHumanos]    Script Date: 10/07/2025 22:37:32 ******/
CREATE SCHEMA [RecursosHumanos]
GO
/****** Object:  Table [RecursosHumanos].[Accion_Personal]    Script Date: 10/07/2025 22:37:32 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [RecursosHumanos].[Accion_Personal](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[idEmpleado] [int] NOT NULL,
	[tipoAccion] [int] NOT NULL,
	[fecha] [date] NOT NULL,
	[detalle] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [RecursosHumanos].[Cat_Acciones]    Script Date: 10/07/2025 22:37:32 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [RecursosHumanos].[Cat_Acciones](
	[id] [int] NOT NULL,
	[tipo] [varchar](20) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [RecursosHumanos].[Cat_Contrato]    Script Date: 10/07/2025 22:37:32 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [RecursosHumanos].[Cat_Contrato](
	[id] [int] NOT NULL,
	[tipo] [varchar](20) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [RecursosHumanos].[Cat_Departamentos]    Script Date: 10/07/2025 22:37:32 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [RecursosHumanos].[Cat_Departamentos](
	[id] [int] NOT NULL,
	[nombre] [varchar](50) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [RecursosHumanos].[Cat_Movimientos]    Script Date: 10/07/2025 22:37:32 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [RecursosHumanos].[Cat_Movimientos](
	[id] [int] NOT NULL,
	[tipo] [varchar](20) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [RecursosHumanos].[Cat_Usuario]    Script Date: 10/07/2025 22:37:32 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [RecursosHumanos].[Cat_Usuario](
	[id] [int] NOT NULL,
	[tipo] [varchar](20) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [RecursosHumanos].[Contratacion]    Script Date: 10/07/2025 22:37:32 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [RecursosHumanos].[Contratacion](
	[id] [int] NOT NULL,
	[idEmpleado] [int] NOT NULL,
	[fechaInicio] [date] NOT NULL,
	[tipoContrato] [int] NOT NULL,
	[duracion] [int] NOT NULL,
	[salario] [money] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [RecursosHumanos].[Empleado]    Script Date: 10/07/2025 22:37:32 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [RecursosHumanos].[Empleado](
	[id] [int] NOT NULL,
	[nombre] [varchar](50) NOT NULL,
	[apellido] [varchar](50) NOT NULL,
	[fechaContratacion] [date] NOT NULL,
	[cargo] [varchar](50) NOT NULL,
	[salario] [money] NOT NULL,
	[idDepartamento] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [RecursosHumanos].[Movimiento_Personal]    Script Date: 10/07/2025 22:37:32 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [RecursosHumanos].[Movimiento_Personal](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[idEmpleado] [int] NOT NULL,
	[tipoMovimiento] [int] NOT NULL,
	[fecha] [date] NOT NULL,
	[detalle] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [RecursosHumanos].[Usuario]    Script Date: 10/07/2025 22:37:32 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [RecursosHumanos].[Usuario](
	[id] [int] NOT NULL,
	[nombre] [varchar](50) NOT NULL,
	[nombreCompleto] [varchar](100) NOT NULL,
	[correo] [varchar](100) NOT NULL,
	[telefono] [varchar](8) NULL,
	[contraseña] [varchar](64) NOT NULL,
	[tipoUsuario] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [RecursosHumanos].[Accion_Personal]  WITH CHECK ADD FOREIGN KEY([idEmpleado])
REFERENCES [RecursosHumanos].[Empleado] ([id])
GO
ALTER TABLE [RecursosHumanos].[Accion_Personal]  WITH CHECK ADD FOREIGN KEY([tipoAccion])
REFERENCES [RecursosHumanos].[Cat_Acciones] ([id])
GO
ALTER TABLE [RecursosHumanos].[Contratacion]  WITH CHECK ADD FOREIGN KEY([idEmpleado])
REFERENCES [RecursosHumanos].[Empleado] ([id])
GO
ALTER TABLE [RecursosHumanos].[Contratacion]  WITH CHECK ADD FOREIGN KEY([tipoContrato])
REFERENCES [RecursosHumanos].[Cat_Contrato] ([id])
GO
ALTER TABLE [RecursosHumanos].[Empleado]  WITH CHECK ADD  CONSTRAINT [FK_Departamento] FOREIGN KEY([idDepartamento])
REFERENCES [RecursosHumanos].[Cat_Departamentos] ([id])
GO
ALTER TABLE [RecursosHumanos].[Empleado] CHECK CONSTRAINT [FK_Departamento]
GO
ALTER TABLE [RecursosHumanos].[Movimiento_Personal]  WITH CHECK ADD FOREIGN KEY([idEmpleado])
REFERENCES [RecursosHumanos].[Empleado] ([id])
GO
ALTER TABLE [RecursosHumanos].[Movimiento_Personal]  WITH CHECK ADD FOREIGN KEY([tipoMovimiento])
REFERENCES [RecursosHumanos].[Cat_Movimientos] ([id])
GO
ALTER TABLE [RecursosHumanos].[Usuario]  WITH CHECK ADD FOREIGN KEY([tipoUsuario])
REFERENCES [RecursosHumanos].[Cat_Usuario] ([id])
GO
USE [master]
GO
ALTER DATABASE [SistemaRRHH] SET  READ_WRITE 
GO
