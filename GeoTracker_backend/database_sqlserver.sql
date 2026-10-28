IF DB_ID('BD_DesarrolloMovil') IS NULL
BEGIN
    CREATE DATABASE BD_DesarrolloMovil;
END
GO

USE BD_DesarrolloMovil;
GO

IF OBJECT_ID('dbo.Usuarios', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Usuarios (
        Id INT IDENTITY(1,1) PRIMARY KEY,
        Nombre VARCHAR(100) NOT NULL,
        Correo VARCHAR(150) NOT NULL UNIQUE,
        Telefono VARCHAR(20) NULL,
        PasswordHash VARCHAR(255) NOT NULL,
        FechaRegistro DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        Activo BIT NOT NULL DEFAULT 1
    );
END
GO
