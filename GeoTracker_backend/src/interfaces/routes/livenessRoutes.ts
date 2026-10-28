import { Router } from 'express';
import { LivenessController } from '../controllers/LivenessController';
import { authMiddleware } from '../middlewares/authMiddleware';

const router = Router();

// Verificacion biometrica (Face Liveness): cualquier usuario con sesion iniciada
router.post('/session', authMiddleware, LivenessController.crearSesion);
router.post('/results', authMiddleware, LivenessController.obtenerResultados);

export default router;
