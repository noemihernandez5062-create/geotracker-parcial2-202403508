-- Carga masiva de censos (PL/pgSQL)
--
-- Inserta N censos con datos aleatorios y Origen = 'MASIVO', para distinguirlos
-- de los capturados desde la app (Origen = 'NORMAL').
--
-- Requisito: la tabla "Censos" ya existe (la crea el backend al iniciar).
--
-- Uso:
--   SELECT insertar_censos_masivos(1000);
--   -- Con otra zona (lat_min, lat_max, lng_min, lng_max):
--   SELECT insertar_censos_masivos(1000, 14.50, 14.70, -90.65, -90.40);

CREATE OR REPLACE FUNCTION insertar_censos_masivos(
    p_cantidad INTEGER,
    p_lat_min  DOUBLE PRECISION DEFAULT 14.50,
    p_lat_max  DOUBLE PRECISION DEFAULT 14.70,
    p_lng_min  DOUBLE PRECISION DEFAULT -90.65,
    p_lng_max  DOUBLE PRECISION DEFAULT -90.40
)
RETURNS INTEGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_nombres TEXT[] := ARRAY[
        'Ana', 'Luis', 'Maria', 'Jose', 'Carmen', 'Carlos', 'Rosa', 'Juan',
        'Sofia', 'Pedro', 'Lucia', 'Miguel', 'Elena', 'Jorge', 'Paula', 'Diego'
    ];
    v_apellidos TEXT[] := ARRAY[
        'Hernandez', 'Lopez', 'Garcia', 'Martinez', 'Perez', 'Gonzalez',
        'Rodriguez', 'Ramirez', 'Morales', 'Castillo', 'Reyes', 'Cruz'
    ];
    v_insertados INTEGER;
BEGIN
    IF p_cantidad IS NULL OR p_cantidad < 1 THEN
        RAISE EXCEPTION 'La cantidad debe ser mayor a 0 (recibido: %)', p_cantidad;
    END IF;

    IF p_lat_min >= p_lat_max OR p_lng_min >= p_lng_max THEN
        RAISE EXCEPTION 'El rango de coordenadas no es valido';
    END IF;

    INSERT INTO "Censos" (
        "Nombre", "Apellido", "TotalMiembros", "CantidadTelevisiones",
        "Latitud", "Longitud", "Origen", "UsuarioId", "FechaRegistro"
    )
    SELECT
        v_nombres[1 + floor(random() * array_length(v_nombres, 1))::INTEGER],
        v_apellidos[1 + floor(random() * array_length(v_apellidos, 1))::INTEGER],
        1 + floor(random() * 8)::INTEGER,   -- 1 a 8 miembros
        floor(random() * 5)::INTEGER,       -- 0 a 4 televisiones
        p_lat_min + random() * (p_lat_max - p_lat_min),
        p_lng_min + random() * (p_lng_max - p_lng_min),
        'MASIVO',
        NULL,
        NOW()
    FROM generate_series(1, p_cantidad);

    GET DIAGNOSTICS v_insertados = ROW_COUNT;
    RETURN v_insertados;
END;
$$;
