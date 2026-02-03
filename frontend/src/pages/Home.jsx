import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Home = () => {
    const { isAuthenticated } = useAuth();

    return (
        <div className="home-container">
            <div className="hero-section">
                <div className="container">
                    <div className="row align-items-center min-vh-75">
                        <div className="col-lg-6">
                            <div className="hero-content">
                                <span className="badge bg-primary mb-3 px-3 py-2">
                                    <i className="bi bi-shield-check me-1"></i>
                                    Secure Authentication
                                </span>
                                <h1 className="display-4 fw-bold mb-4">
                                    Complete Authentication <br />
                                    <span className="text-gradient">Solution</span>
                                </h1>
                                <p className="lead text-muted mb-4">
                                    Full-stack authentication system with JWT tokens, OTP email verification,
                                    password reset, and role-based access control.
                                </p>
                                <div className="d-flex gap-3 flex-wrap">
                                    {isAuthenticated() ? (
                                        <Link to="/dashboard" className="btn btn-primary btn-lg">
                                            <i className="bi bi-speedometer2 me-2"></i>
                                            Go to Dashboard
                                        </Link>
                                    ) : (
                                        <>
                                            <Link to="/register" className="btn btn-primary btn-lg">
                                                <i className="bi bi-person-plus me-2"></i>
                                                Get Started
                                            </Link>
                                            <Link to="/login" className="btn btn-outline-primary btn-lg">
                                                <i className="bi bi-box-arrow-in-right me-2"></i>
                                                Sign In
                                            </Link>
                                        </>
                                    )}
                                </div>
                            </div>
                        </div>
                        <div className="col-lg-6">
                            <div className="hero-image">
                                <div className="feature-cards">
                                    <div className="feature-card">
                                        <i className="bi bi-key"></i>
                                        <span>JWT Tokens</span>
                                    </div>
                                    <div className="feature-card">
                                        <i className="bi bi-envelope-check"></i>
                                        <span>OTP Verification</span>
                                    </div>
                                    <div className="feature-card">
                                        <i className="bi bi-shield-lock"></i>
                                        <span>Password Reset</span>
                                    </div>
                                    <div className="feature-card">
                                        <i className="bi bi-people"></i>
                                        <span>Role-Based Access</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div className="features-section py-5">
                <div className="container">
                    <div className="text-center mb-5">
                        <h2 className="fw-bold">Features</h2>
                        <p className="text-muted">Built with modern technologies</p>
                    </div>

                    <div className="row g-4">
                        <div className="col-md-4">
                            <div className="feature-box">
                                <div className="feature-icon bg-primary">
                                    <i className="bi bi-database"></i>
                                </div>
                                <h5>Spring Boot Backend</h5>
                                <p className="text-muted">
                                    Robust REST API built with Spring Boot, Spring Security, and JPA.
                                </p>
                            </div>
                        </div>

                        <div className="col-md-4">
                            <div className="feature-box">
                                <div className="feature-icon bg-success">
                                    <i className="bi bi-code-slash"></i>
                                </div>
                                <h5>React Frontend</h5>
                                <p className="text-muted">
                                    Modern SPA with React, Vite, and Bootstrap 5 for beautiful UI.
                                </p>
                            </div>
                        </div>

                        <div className="col-md-4">
                            <div className="feature-box">
                                <div className="feature-icon bg-warning">
                                    <i className="bi bi-envelope"></i>
                                </div>
                                <h5>Email Integration</h5>
                                <p className="text-muted">
                                    OTP verification and password reset via JavaMailSender.
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Home;
