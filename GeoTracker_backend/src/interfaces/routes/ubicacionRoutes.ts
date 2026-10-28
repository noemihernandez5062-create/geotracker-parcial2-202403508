import { Router } from 'express';
import { UbicacionController } from '../controllers/UbicacionController';
import { authMiddleware } from '../middlewares/authMiddleware';

const router = Router();

// Cualquier usuario autenticado guarda y ve solo sus propias ubicaciones
router.post('/', authMiddleware, UbicacionController.create);
router.get('/', authMiddleware, UbicacionController.findMine);

export default router;
