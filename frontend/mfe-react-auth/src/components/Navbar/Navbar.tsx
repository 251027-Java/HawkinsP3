import React from 'react';
import { navigateToUrl } from 'single-spa';
import { useAuth } from '../../context/AuthContext';
import './Navbar.css';

const Navbar: React.FC = () => {
    const { user, isAuthenticated, logout } = useAuth();

    const handleNavigation = (path: string) => (e: React.MouseEvent) => {
        e.preventDefault();
        navigateToUrl(path);
    };

    const handleLogout = () => {
        logout();
        navigateToUrl('/login');
    };

    return (
        <nav className="navbar">
            <div className="navbar-container">
                <a href="/" onClick={handleNavigation('/')} className="navbar-brand">
                    <span className="brand-icon">✈️</span>
                    <span className="brand-text">Pilot Quiz</span>
                </a>

                <div className="navbar-links">
                    {isAuthenticated ? (
                        <>
                            <a href="/dashboard" onClick={handleNavigation('/dashboard')} className="nav-link">Dashboard</a>
                            <a href="/quizzes" onClick={handleNavigation('/quizzes')} className="nav-link">Quizzes</a>
                            <a href="/progress" onClick={handleNavigation('/progress')} className="nav-link">Progress</a>
                        </>
                    ) : (
                        <>
                            <a href="/login" onClick={handleNavigation('/login')} className="nav-link">Login</a>
                            <a href="/register" onClick={handleNavigation('/register')} className="nav-link nav-link-primary">Get Started</a>
                        </>
                    )}
                </div>

                {isAuthenticated && user && (
                    <div className="navbar-user">
                        <span className="user-greeting">
                            Hello, {user.firstName}
                        </span>
                        <button onClick={handleLogout} className="logout-btn">
                            Logout
                        </button>
                    </div>
                )}
            </div>
        </nav>
    );
};

export default Navbar;

