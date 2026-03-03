import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';


const Navbar = () => {
    const { logout, isAuthenticated, isAdmin } = useAuth();
    const navigate = useNavigate();

    const handleLogout = async () => {
        await logout();
        navigate('/login');
    };

    return (
        <nav className="navbar navbar-expand-lg jitter-navbar">
            <div className="container">
                <Link className="navbar-brand" to="/">
                    <i className="bi bi-shield-lock me-2"></i>
                    AuthSystem
                </Link>

                <div className="d-flex align-items-center order-lg-3 ms-lg-3">
                    <button
                        className="navbar-toggler ms-2"
                        type="button"
                        data-bs-toggle="collapse"
                        data-bs-target="#navbarNav"
                    >
                        <span className="navbar-toggler-icon"></span>
                    </button>
                </div>

                <div className="collapse navbar-collapse" id="navbarNav">
                    <ul className="navbar-nav ms-auto mb-2 mb-lg-0 align-items-center nav-motion-group">
                        {isAuthenticated() ? (
                            <>
                                <li className="nav-item">
                                    <Link className="nav-link nav-motion-link" to="/dashboard">
                                        <i className="bi bi-speedometer2 me-1"></i>
                                        Dashboard
                                    </Link>
                                </li>
                                {isAdmin() && (
                                    <li className="nav-item">
                                        <Link className="nav-link nav-motion-link" to="/admin">
                                            <i className="bi bi-gear me-1"></i>
                                            Admin Panel
                                        </Link>
                                    </li>
                                )}
                                <li className="nav-item ms-2">
                                    <button
                                        className="btn btn-outline-primary btn-sm nav-logout-btn"
                                        onClick={handleLogout}
                                    >
                                        <i className="bi bi-box-arrow-right me-1"></i>
                                        Logout
                                    </button>
                                </li>
                            </>
                        ) : (
                            <>
                                <li className="nav-item">
                                    <Link className="nav-link nav-motion-link" to="/login">
                                        <i className="bi bi-box-arrow-in-right me-1"></i>
                                        Login
                                    </Link>
                                </li>
                                <li className="nav-item">
                                    <Link className="nav-link nav-motion-link" to="/register">
                                        <i className="bi bi-person-plus me-1"></i>
                                        Register
                                    </Link>
                                </li>
                            </>
                        )}
                    </ul>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;
