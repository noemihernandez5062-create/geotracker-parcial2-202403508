import { InvokeCommand, LambdaClient } from '@aws-sdk/client-lambda';

// Las credenciales las toma el SDK automaticamente: en el servidor EC2, de AWS_ACCESS_KEY_ID y
// AWS_SECRET_ACCESS_KEY del .env (usuario IAM backend-movil-lambda); en una computadora, de la
// configuracion de la AWS CLI (~/.aws).
const lambdaClient = new LambdaClient({
  region: process.env.LIVENESS_REGION || 'us-east-1'
});

const FUNCIONES = {
  sesion: process.env.LAMBDA_GET_SESSION_ID || 'livenessGetSessionId',
  resultados: process.env.LAMBDA_GET_SESSION_RESULTS || 'livenessGetSessionResults',
  comparar: process.env.LAMBDA_COMPARE_FACES || 'livenessCompareFaces'
};

// Error devuelto por una Lambda; statusCode sirve para responder a la app
export class LambdaError extends Error {
  constructor(
    public readonly statusCode: number,
    message: string,
    public readonly tipo?: string
  ) {
    super(message);
  }
}

// Invoca la Lambda y devuelve su body ya convertido de JSON.
// Las Lambdas responden { statusCode, headers, body } como si las llamara API Gateway.
async function invocar(nombre: string, evento: object): Promise<any> {
  const respuesta = await lambdaClient.send(new InvokeCommand({
    FunctionName: nombre,
    Payload: Buffer.from(JSON.stringify(evento))
  }));

  const texto = Buffer.from(respuesta.Payload ?? new Uint8Array()).toString('utf8');
  const payload = texto ? JSON.parse(texto) : {};

  // Error no controlado dentro de la Lambda (p. ej. SessionNotFoundException)
  if (respuesta.FunctionError) {
    const tipo = payload.errorType as string | undefined;
    const estado = tipo === 'SessionNotFoundException' ? 404 : 502;
    throw new LambdaError(estado, payload.errorMessage || 'Error en la Lambda', tipo);
  }

  const body = typeof payload.body === 'string' ? JSON.parse(payload.body) : payload.body;

  if (payload.statusCode >= 400) {
    const mensaje = typeof body === 'string' ? body : body?.message;
    throw new LambdaError(payload.statusCode, mensaje || 'Error en la Lambda');
  }

  return body;
}

export async function crearSesion(): Promise<string> {
  return invocar(FUNCIONES.sesion, {});
}


export async function obtenerResultados(sessionId: string): Promise<{
  confidence: number | string;
  status: string;
  referenceImageBase64: string;
}> {
  return invocar(FUNCIONES.resultados, { body: JSON.stringify({ sessionId }) });
}

export async function compararRostros(
  sourceImage: string,
  targetImage: string
): Promise<{ match: boolean; similarity: number; threshold: number }> {
  return invocar(FUNCIONES.comparar, { body: JSON.stringify({ sourceImage, targetImage }) });
}
