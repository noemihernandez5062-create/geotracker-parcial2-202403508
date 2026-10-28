import { Router } from 'express';
import { CensoController } from '../controllers/CensoController';
import { authMiddleware, requireRol } from '../middlewares/authMiddleware';

const router = Router();

// Capturar censo: solo censista
router.post('/', authMiddleware, requireRol('CENSISTA'), CensoController.create);

// Ver censos en el mapa (normal y masivo): solo administrador
router.get('/', authMiddleware, requireRol('ADMINISTRADOR'), CensoController.findAll);

export default router;
