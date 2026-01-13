import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Navbar from './components/Navbar';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';

// Protected route wrapper
const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
};

// Main App component
const App: React.FC<any> = (props) => {
  // Navbar Mode - Renders only the persistent navigation bar
  // The name '@pilotquiz/navbar' is defined in microfrontend-layout.html and mapped in root-config
  if (props.name === '@pilotquiz/navbar') {
    return (
      <AuthProvider>
        <div style={{ position: 'relative', zIndex: 100 }}>
          <Navbar />
        </div>
      </AuthProvider>
    );
  }

  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
};

const AppContent: React.FC = () => {
  const { isAuthenticated, isLoading } = useAuth();

  // Wait for auth state to load from localStorage before rendering routes
  if (isLoading) {
    return (
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        background: 'var(--color-background, #f8fafc)'
      }}>
        <div style={{ textAlign: 'center', color: 'var(--color-text-secondary, #64748b)' }}>
          <div style={{ fontSize: '1.5rem', marginBottom: '8px' }}>✈️</div>
          <div>Loading...</div>
        </div>
      </div>
    );
  }

  return (
    <BrowserRouter>
      {/* Navbar is separate now */}
      <Routes>
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* Protected Routes */}
        <Route path="/dashboard" element={
          <ProtectedRoute>
            <Dashboard />
          </ProtectedRoute>
        } />
      </Routes>
    </BrowserRouter>
  );
};

export default App;
