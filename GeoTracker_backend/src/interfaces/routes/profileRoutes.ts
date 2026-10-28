import express, { Router } from 'express';
import { ProfileController } from '../controllers/ProfileController';
import { authMiddleware } from '../middlewares/authMiddleware';

const router = Router();

// Lee el cuerpo como bytes solo para estos tipos de imagen
const leerImagen = express.raw({
  type: ['image/jpeg', 'image/png', 'image/webp'],
  limit: '5mb'
});

router.get('/', authMiddleware, ProfileController.getProfile);
router.put('/', authMiddleware, ProfileController.updateProfile);

router.get('/foto', authMiddleware, ProfileController.getFoto);
router.put('/foto', authMiddleware, leerImagen, ProfileController.updateFoto);

export default router;
