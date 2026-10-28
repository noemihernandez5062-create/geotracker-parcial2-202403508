import express from 'express';
import cors from 'cors';
import authRoutes from './interfaces/routes/authRoutes';
import profileRoutes from './interfaces/routes/profileRoutes';
import censoRoutes from './interfaces/routes/censoRoutes';
import livenessRoutes from './interfaces/routes/livenessRoutes';
import ubicacionRoutes from './interfaces/routes/ubicacionRoutes';

const app = express();

app.use(cors());
app.use(express.json());

app.get('/', (_req, res) => {
  res.json({
    success: true,
    message: 'API Desarrollo Movil funcionando',
    data: null
  });
});

app.use('/api/auth', authRoutes);
app.use('/api/profile', profileRoutes);
app.use('/api/censos', censoRoutes);
app.use('/api/liveness', livenessRoutes);
app.use('/api/ubicaciones', ubicacionRoutes);

app.use((_req, res) => {
  res.status(404).json({
    success: false,
    message: 'Ruta no encontrada',
    data: null
  });
});

export default app;
