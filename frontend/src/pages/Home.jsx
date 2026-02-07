import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Home = () => {
    const { isAuthenticated } = useAuth();

    return (
        <div className="home-container">
            {/* Hero Section */}
            <div className="hero-section">
                <div className="container">
                    <div className="hero-content">
                        <span className="badge bg-primary mb-4 px-3 py-2 rounded-pill">
                            <i className="bi bi-stars me-2"></i>
                            v1.0.0 Now Available
                        </span>
                        <h1 className="display-3 fw-bold mb-4">
                            Next-Generation <br />
                            <span className="text-gradient">Authentication System</span>
                        </h1>
                        <p className="lead text-muted mb-5 mx-auto" style={{ maxWidth: '600px' }}>
                            A production-ready full-stack solution featuring JWT security, One-Time Password verification,
                            and comprehensive role-based access control.
                        </p>

                        <div className="d-flex justify-content-center gap-3">
                            {isAuthenticated() ? (
                                <Link to="/dashboard" className="btn btn-primary btn-lg px-5 py-3">
                                    <i className="bi bi-speedometer2 me-2"></i>
                                    Go to Dashboard
                                </Link>
                            ) : (
                                <>
                                    <Link to="/register" className="btn btn-primary btn-lg px-4 py-3">
                                        <i className="bi bi-rocket-takeoff me-2"></i>
                                        Get Started
                                    </Link>
                                    <Link to="/login" className="btn btn-outline-primary btn-lg px-4 py-3">
                                        <i className="bi bi-box-arrow-in-right me-2"></i>
                                        Sign In
                                    </Link>
                                </>
                            )}
                        </div>
                    </div>
                </div>
            </div>

            {/* Tech Stack Section */}
            <div className="tech-stack-section">
                <div className="container">
                    <div className="text-center mb-5">
                        <h6 className="text-primary text-uppercase letter-spacing-2 mb-2">Powered By</h6>
                        <h3 className="fw-bold">Modern Technology Stack</h3>
                    </div>

                    <div className="tech-grid">
                        <div className="tech-card">
                            <i className="bi bi-filetype-java tech-icon"></i>
                            <span className="tech-name">Java Spring Boot</span>
                        </div>
                        <div className="tech-card">
                            <i className="bi bi-filetype-jsx tech-icon"></i>
                            <span className="tech-name">React + Vite</span>
                        </div>
                        <div className="tech-card">
                            <i className="bi bi-shield-lock tech-icon"></i>
                            <span className="tech-name">Spring Security</span>
                        </div>
                        <div className="tech-card">
                            <i className="bi bi-database tech-icon"></i>
                            <span className="tech-name">JPA / Hibernate</span>
                        </div>
                        <div className="tech-card">
                            <i className="bi bi-envelope tech-icon"></i>
                            <span className="tech-name">JavaMailSender</span>
                        </div>
                        <div className="tech-card">
                            <i className="bi bi-bootstrap tech-icon"></i>
                            <span className="tech-name">Bootstrap 5</span>
                        </div>
                    </div>
                </div>
            </div>

            {/* Features Section */}
            <div className="features-section py-5">
                <div className="container">
                    <div className="text-center mb-5">
                        <h2 className="fw-bold mb-3">Core Features</h2>
                        <p className="text-muted">Everything you need for a secure application</p>
                    </div>

                    <div className="row g-4">
                        <div className="col-md-4">
                            <div className="feature-card h-100">
                                <i className="bi bi-shield-check mb-3"></i>
                                <h4 className="text-white">JWT Security</h4>
                                <p className="text-muted text-center">
                                    Stateless authentication using JSON Web Tokens for secure API communication.
                                </p>
                            </div>
                        </div>

                        <div className="col-md-4">
                            <div className="feature-card h-100">
                                <i className="bi bi-envelope-paper mb-3"></i>
                                <h4 className="text-white">Email Verification</h4>
                                <p className="text-muted text-center">
                                    Secure OTP-based email verification flow to validate user identity.
                                </p>
                            </div>
                        </div>

                        <div className="col-md-4">
                            <div className="feature-card h-100">
                                <i className="bi bi-person-lock mb-3"></i>
                                <h4 className="text-white">Role-Based Access</h4>
                                <p className="text-muted text-center">
                                    Granular permission control with USER and ADMIN role separation.
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Footer */}
            <footer className="py-4 text-center text-muted border-top border-secondary border-opacity-25 mt-5">
                <div className="container">
                    <p className="mb-0">© 2024 Authentication System. Built with ❤️.</p>
                </div>
            </footer>
        </div>
    );
};

export default Home;
