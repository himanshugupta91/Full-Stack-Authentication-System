import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';

const Navbar = () => {
    const { logout, isAuthenticated, isAdmin } = useAuth();
    const { isDarkMode, toggleTheme } = useTheme();
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const navigate = useNavigate();

    const handleLogout = async () => {
        await logout();
        setIsMenuOpen(false);
        navigate('/login');
    };

    const closeMenu = () => {
        setIsMenuOpen(false);
    };

    return (
        <nav className="navbar navbar-expand-lg jitter-navbar bento-navbar">
            <div className="container">
                <div className="bento-navbar-shell d-flex flex-wrap align-items-center justify-content-between">
                    <Link className="navbar-brand bento-brand" to="/" onClick={closeMenu}>
                        <i className="bi bi-shield-lock me-2"></i>
                        AuthSystem
                    </Link>

                    <div className="d-flex align-items-center order-lg-3 ms-lg-3 bento-navbar-controls">
                        <button
                            type="button"
                            className="btn btn-sm theme-toggle-btn bento-control"
                            onClick={toggleTheme}
                            title={isDarkMode ? 'Switch to light mode' : 'Switch to dark mode'}
                            aria-label={isDarkMode ? 'Switch to light mode' : 'Switch to dark mode'}
                        >
                            <i className={`bi ${isDarkMode ? 'bi-sun-fill' : 'bi-moon-stars-fill'} theme-toggle-icon`}></i>
                            <span className="theme-toggle-text d-none d-sm-inline">
                                {isDarkMode ? 'Light' : 'Dark'}
                            </span>
                        </button>
                        <button
                            className={`navbar-toggler ms-2 bento-control bento-menu-btn ${isMenuOpen ? '' : 'collapsed'}`}
                            type="button"
                            onClick={() => setIsMenuOpen((currentState) => !currentState)}
                            aria-controls="navbarNav"
                            aria-expanded={isMenuOpen}
                            aria-label="Toggle navigation"
                        >
                            <span className="navbar-toggler-icon"></span>
                        </button>
                    </div>

                    <div className={`collapse navbar-collapse bento-collapse ${isMenuOpen ? 'show' : ''}`} id="navbarNav">
                        <ul className="navbar-nav ms-auto mb-2 mb-lg-0 align-items-center nav-motion-group bento-nav-grid">
                            {isAuthenticated() ? (
                                <>
                                    <li className="nav-item bento-nav-cell">
                                        <Link className="nav-link nav-motion-link bento-nav-link" to="/dashboard" onClick={closeMenu}>
                                            <i className="bi bi-speedometer2 me-1"></i>
                                            Dashboard
                                        </Link>
                                    </li>
                                    {isAdmin() && (
                                        <li className="nav-item bento-nav-cell">
                                            <Link className="nav-link nav-motion-link bento-nav-link" to="/admin" onClick={closeMenu}>
                                                <i className="bi bi-gear me-1"></i>
                                                Admin Panel
                                            </Link>
                                        </li>
                                    )}
                                    <li className="nav-item ms-2 bento-nav-cell">
                                        <button
                                            type="button"
                                            className="btn btn-outline-primary btn-sm nav-logout-btn bento-logout"
                                            onClick={handleLogout}
                                        >
                                            <i className="bi bi-box-arrow-right me-1"></i>
                                            Logout
                                        </button>
                                    </li>
                                </>
                            ) : (
                                <>
                                    <li className="nav-item bento-nav-cell">
                                        <Link className="nav-link nav-motion-link bento-nav-link" to="/login" onClick={closeMenu}>
                                            <i className="bi bi-box-arrow-in-right me-1"></i>
                                            Login
                                        </Link>
                                    </li>
                                    <li className="nav-item bento-nav-cell">
                                        <Link className="nav-link nav-motion-link bento-nav-link" to="/register" onClick={closeMenu}>
                                            <i className="bi bi-person-plus me-1"></i>
                                            Register
                                        </Link>
                                    </li>
                                </>
                            )}
                        </ul>
                    </div>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;
