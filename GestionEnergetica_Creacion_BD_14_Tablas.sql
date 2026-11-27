/*
  GestionEnergetica - reconstruccion documentada del DDL de desarrollo
  Fuentes: script inicial historico (13 tablas) y HU08_01_Ajustes_Esquema_SQLServer.sql.
  Incluye: 14 tablas, 15 claves foraneas, columnas HU-08, y sp_ConsultarEquipos.
  NO es un volcado literal de la instancia SQL Server ni incluye datos o los
  procedimientos HU-08 posteriores cuya definicion exacta no esta en las fuentes.
  ATENCION: usar SOLO en una instancia de prueba donde NO exista esta BD.
  No ejecutar sobre la base GestionEnergetica que ya esta en uso.
*/
USE master;
GO
IF DB_ID(N'GestionEnergetica') IS NOT NULL
    THROW 51000, 'GestionEnergetica ya existe. Se cancela para proteger datos y objetos actuales.', 1;
GO
CREATE DATABASE GestionEnergetica;
GO
USE GestionEnergetica;
GO

CREATE TABLE DigitalTwin (
    idTwin NVARCHAR(50) PRIMARY KEY,
    tipo NVARCHAR(100),
    estadoActual NVARCHAR(50),
    ultimaActualizacion DATETIME2
);
GO

-- =========================================
-- 2. EQUIPO
-- =========================================
CREATE TABLE Equipo (
    idEquipo NVARCHAR(50) PRIMARY KEY,
    tipo NVARCHAR(100),
    estado NVARCHAR(50),
    ubicacion NVARCHAR(150),
    idTwin NVARCHAR(50) NOT NULL,

    CONSTRAINT FK_Equipo_DigitalTwin
    FOREIGN KEY (idTwin) REFERENCES DigitalTwin(idTwin)
);
GO

-- =========================================
-- 3. ACTORES
-- =========================================
CREATE TABLE Ocupante (
    idOcupante NVARCHAR(50) PRIMARY KEY,
    ubicacion NVARCHAR(150)
);
GO

CREATE TABLE Administrador (
    idAdmin NVARCHAR(50) PRIMARY KEY,
    nombre NVARCHAR(150)
);
GO

CREATE TABLE PersonalMantenimiento (
    idTecnico NVARCHAR(50) PRIMARY KEY,
    nombre NVARCHAR(150)
);
GO

-- =========================================
-- 4. INDICADOR ENERGETICO
-- =========================================
CREATE TABLE IndicadorEnergetico (
    idIndicador NVARCHAR(50) PRIMARY KEY,
    nombre NVARCHAR(100),
    valor DECIMAL(18,4),
    fechaCalculo DATETIME2,
    idEquipo NVARCHAR(50) NOT NULL,
    idTwin NVARCHAR(50) NOT NULL,

    CONSTRAINT FK_Indicador_Equipo
    FOREIGN KEY (idEquipo) REFERENCES Equipo(idEquipo),

    CONSTRAINT FK_Indicador_DigitalTwin
    FOREIGN KEY (idTwin) REFERENCES DigitalTwin(idTwin)
);
GO

-- =========================================
-- 5. PREDICCION DE CONSUMO
-- =========================================
CREATE TABLE PrediccionConsumo (
    idPrediccion NVARCHAR(50) PRIMARY KEY,
    consumoEstimado DECIMAL(18,4),
    [precision] DECIMAL(8,4),
    fechaGeneracion DATETIME2,
    idTwin NVARCHAR(50) NOT NULL,
    idEquipo NVARCHAR(50) NOT NULL,

    CONSTRAINT FK_Prediccion_DigitalTwin
    FOREIGN KEY (idTwin) REFERENCES DigitalTwin(idTwin),

    CONSTRAINT FK_Prediccion_Equipo
    FOREIGN KEY (idEquipo) REFERENCES Equipo(idEquipo)
);
GO

-- =========================================
-- 6. DATO SENSOR
-- =========================================
CREATE TABLE DatoSensor (
    idDato NVARCHAR(50) PRIMARY KEY,
    idSensor NVARCHAR(50),
    valor DECIMAL(18,4),
    [timestamp] DATETIME2,
    idTwin NVARCHAR(50) NOT NULL,
    idEquipo NVARCHAR(50) NOT NULL,
    variable NVARCHAR(50) NULL,
    unidad NVARCHAR(20) NULL,

    CONSTRAINT FK_DatoSensor_DigitalTwin
    FOREIGN KEY (idTwin) REFERENCES DigitalTwin(idTwin),

    CONSTRAINT FK_DatoSensor_Equipo
    FOREIGN KEY (idEquipo) REFERENCES Equipo(idEquipo)
);
GO

-- =========================================
-- 7. REPORTE DE CONFORT
-- =========================================
CREATE TABLE ReporteConfort (
    idReporte NVARCHAR(50) PRIMARY KEY,
    tipoConfort NVARCHAR(100),
    valoracion INT,
    fecha DATETIME2,
    idOcupante NVARCHAR(50) NOT NULL,

    CONSTRAINT FK_ReporteConfort_Ocupante
    FOREIGN KEY (idOcupante) REFERENCES Ocupante(idOcupante)
);
GO

-- =========================================
-- 8. REPORTE DE SOSTENIBILIDAD
-- =========================================
CREATE TABLE ReporteSostenibilidad (
    idReporte NVARCHAR(50) PRIMARY KEY,
    periodo NVARCHAR(50),
    emisionesCO2 DECIMAL(18,4),
    consumoTotal DECIMAL(18,4),
    fechaGeneracion DATETIME2,
    idAdmin NVARCHAR(50) NOT NULL,

    CONSTRAINT FK_ReporteSostenibilidad_Administrador
    FOREIGN KEY (idAdmin) REFERENCES Administrador(idAdmin)
);
GO

-- =========================================
-- 9. ALERTA
-- =========================================
CREATE TABLE Alerta (
    idAlerta NVARCHAR(50) PRIMARY KEY,
    tipo NVARCHAR(100),
    severidad NVARCHAR(50),
    fecha DATETIME2,
    idTwin NVARCHAR(50) NOT NULL,
    idEquipo NVARCHAR(50) NOT NULL,

    CONSTRAINT FK_Alerta_DigitalTwin
    FOREIGN KEY (idTwin) REFERENCES DigitalTwin(idTwin),

    CONSTRAINT FK_Alerta_Equipo
    FOREIGN KEY (idEquipo) REFERENCES Equipo(idEquipo)
);
GO

-- =========================================
-- 10. RECOMENDACION
-- =========================================
CREATE TABLE Recomendacion (
    idRecomendacion NVARCHAR(50) PRIMARY KEY,
    descripcion NVARCHAR(MAX),
    prioridad INT,
    estado NVARCHAR(50),
    idAlerta NVARCHAR(50) NOT NULL,

    CONSTRAINT FK_Recomendacion_Alerta
    FOREIGN KEY (idAlerta) REFERENCES Alerta(idAlerta)
);
GO

-- =========================================
-- 11. COMANDO DE CONTROL
-- =========================================
CREATE TABLE ComandoControl (
    idComando NVARCHAR(50) PRIMARY KEY,
    accion NVARCHAR(150),
    fechaEnvio DATETIME2,
    estado NVARCHAR(50),
    idAlerta NVARCHAR(50) NOT NULL,
    idRecomendacion NVARCHAR(50) NOT NULL,
    idReporte NVARCHAR(50) NULL,

    CONSTRAINT FK_ComandoControl_Alerta
    FOREIGN KEY (idAlerta) REFERENCES Alerta(idAlerta),

    CONSTRAINT FK_ComandoControl_Recomendacion
    FOREIGN KEY (idRecomendacion)
    REFERENCES Recomendacion(idRecomendacion),

    CONSTRAINT FK_ComandoControl_ReporteConfort
    FOREIGN KEY (idReporte)
    REFERENCES ReporteConfort(idReporte)
);
GO

-- =========================================
-- 13. REGISTRO DE ERRORES DE INGESTA (HU-08)
-- =========================================
CREATE TABLE dbo.RegistroIngestaError (
    idError BIGINT IDENTITY(1,1) NOT NULL
        CONSTRAINT PK_RegistroIngestaError PRIMARY KEY,
    idDato NVARCHAR(50) NULL,
    idSensor NVARCHAR(50) NULL,
    idEquipo NVARCHAR(50) NULL,
    variable NVARCHAR(50) NULL,
    unidad NVARCHAR(20) NULL,
    motivo NVARCHAR(400) NOT NULL,
    fechaRegistroUtc DATETIME2(3) NOT NULL
        CONSTRAINT DF_RegistroIngestaError_FechaUtc DEFAULT SYSUTCDATETIME()
);
GO

-- Procedimiento original documentado para consulta de equipos.
CREATE OR ALTER PROCEDURE dbo.sp_ConsultarEquipos
AS
BEGIN
    SET NOCOUNT ON;
    SELECT idEquipo, tipo, estado, ubicacion, idTwin
    FROM dbo.Equipo
    ORDER BY idEquipo;
END;
GO

-- Verificacion de tablas y claves foraneas
SELECT COUNT(*) AS TotalTablas
FROM sys.tables WHERE is_ms_shipped = 0;
SELECT COUNT(*) AS TotalClavesForaneas
FROM sys.foreign_keys;
GO
