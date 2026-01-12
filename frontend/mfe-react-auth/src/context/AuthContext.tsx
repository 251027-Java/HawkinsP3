import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { authApi, userApi } from '../api/client';

// User type
interface User {
    id: number;
    email: string;
    firstName: string;
    lastName: string;
    role: string;
}

// Auth context type
interface AuthContextType {
    user: User | null;
    token: string | null;
    isAuthenticated: boolean;
    isLoading: boolean;
    login: (email: string, password: string) => Promise<void>;
    register: (data: RegisterData) => Promise<void>;
    logout: () => void;
    updateUser: (data: Partial<User>) => void;
}

interface RegisterData {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [user, setUser] = useState<User | null>(null);
    const [token, setToken] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    // Initialize from localStorage on mount
    useEffect(() => {
        const storedToken = localStorage.getItem('pilotquiz_token');
        const storedUser = localStorage.getItem('pilotquiz_user');

        if (storedToken && storedUser && storedUser !== 'undefined') {
            try {
                setToken(storedToken);
                setUser(JSON.parse(storedUser));
            } catch (e) {
                // Invalid JSON in localStorage, clear it
                localStorage.removeItem('pilotquiz_token');
                localStorage.removeItem('pilotquiz_user');
            }
        }
        setIsLoading(false);
    }, []);

    // Listen for logout events from other MFEs
    useEffect(() => {
        const handleLogout = () => logout();
        window.addEventListener('pilotquiz:logout', handleLogout);
        return () => window.removeEventListener('pilotquiz:logout', handleLogout);
    }, []);

    const login = async (email: string, password: string) => {
        const response = await authApi.login(email, password);
        const { token: newToken, user: userData } = response;

        localStorage.setItem('pilotquiz_token', newToken);
        localStorage.setItem('pilotquiz_user', JSON.stringify(userData));

        setToken(newToken);
        setUser(userData);

        // Notify other MFEs
        window.dispatchEvent(new CustomEvent('pilotquiz:login', { detail: userData }));
    };

    const register = async (data: RegisterData) => {
        const response = await authApi.register(data);
        const { token: newToken, user: userData } = response;

        localStorage.setItem('pilotquiz_token', newToken);
        localStorage.setItem('pilotquiz_user', JSON.stringify(userData));

        setToken(newToken);
        setUser(userData);

        window.dispatchEvent(new CustomEvent('pilotquiz:login', { detail: userData }));
    };

    const logout = () => {
        localStorage.removeItem('pilotquiz_token');
        localStorage.removeItem('pilotquiz_user');
        setToken(null);
        setUser(null);

        window.dispatchEvent(new CustomEvent('pilotquiz:logout'));
    };

    const updateUser = (data: Partial<User>) => {
        if (user) {
            const updatedUser = { ...user, ...data };
            setUser(updatedUser);
            localStorage.setItem('pilotquiz_user', JSON.stringify(updatedUser));
        }
    };

    return (
        <AuthContext.Provider
            value={{
                user,
                token,
                isAuthenticated: !!token,
                isLoading,
                login,
                register,
                logout,
                updateUser,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = (): AuthContextType => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};

export default AuthContext;
