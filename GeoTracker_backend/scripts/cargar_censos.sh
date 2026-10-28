#!/usr/bin/env bash
# Inserta N censos masivos en la base de datos.
#
# Uso (desde la carpeta del backend, en el servidor):
#   bash scripts/cargar_censos.sh 1000
#
# Toma los datos de conexion del archivo .env del backend.
# Requiere el cliente de PostgreSQL (psql).

set -euo pipefail

CANTIDAD="${1:-}"

if ! [[ "$CANTIDAD" =~ ^[1-9][0-9]*$ ]]; then
    echo "Uso: $0 <cantidad>   (numero entero mayor a 0)" >&2
    exit 1
fi

DIR_BACKEND="$(cd "$(dirname "$0")/.." && pwd)"
ARCHIVO_ENV="$DIR_BACKEND/.env"

if [ ! -f "$ARCHIVO_ENV" ]; then
    echo "No se encontro $ARCHIVO_ENV" >&2
    exit 1
fi

# Carga DB_HOST, DB_PORT, DB_NAME, DB_USER y DB_PASSWORD del .env
set -a
# shellcheck disable=SC1090
. "$ARCHIVO_ENV"
set +a

export PGPASSWORD="${DB_PASSWORD:-}"
PSQL=(psql -h "${DB_HOST:-localhost}" -p "${DB_PORT:-5432}" -U "${DB_USER:-postgres}" -d "${DB_NAME:-bd_movil}" -v ON_ERROR_STOP=1 -q)

# Crea o actualiza la funcion PL/pgSQL
"${PSQL[@]}" -f "$DIR_BACKEND/sql/censos_masivos.sql"

INSERTADOS=$("${PSQL[@]}" -t -A -c "SELECT insertar_censos_masivos($CANTIDAD);")

echo "Censos masivos insertados: $INSERTADOS"
"${PSQL[@]}" -c "SELECT \"Origen\", COUNT(*) AS total FROM \"Censos\" GROUP BY \"Origen\" ORDER BY \"Origen\";"
