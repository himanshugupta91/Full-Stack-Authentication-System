import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { OAUTH_PROVIDERS, getOAuthAuthorizationUrl } from '../services/api';

const Home = () => {
  const { isAuthenticated, user } = useAuth();
  const isLoggedIn = isAuthenticated();
  const displayName = user?.name?.trim() || user?.email?.split('@')[0] || 'User';

  const oauthProviders = OAUTH_PROVIDERS.map((provider) => ({
    ...provider,
    href: getOAuthAuthorizationUrl(provider.id),
  }));

  return (
    <div className="home-container">
      <div className="hero-section">
        <div className="hero-motion-layer" aria-hidden="true">
          <span className="motion-blob blob-one"></span>
          <span className="motion-blob blob-two"></span>
          <span className="motion-blob blob-three"></span>
          <span className="motion-sheen"></span>
        </div>
        <div className="container">
          <div className="hero-content jitter-hero-stage">
            {/* Decorative ornament */}
            <div className="hero-ornament motion-item motion-ornament">
              <svg viewBox="0 0 200 40" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M20 20 Q50 5 80 20 Q110 35 140 20 Q170 5 180 20" stroke="#c7afff" strokeWidth="1.5" fill="none" opacity="0.6" />
                <path d="M30 20 Q60 35 90 20 Q120 5 150 20 Q170 30 180 20" stroke="#ffc5a8" strokeWidth="1.5" fill="none" opacity="0.5" />
                <circle cx="100" cy="20" r="3" fill="#c7afff" opacity="0.4" />
                <circle cx="60" cy="15" r="2" fill="#a8d2ff" opacity="0.4" />
                <circle cx="140" cy="15" r="2" fill="#ffc5a8" opacity="0.4" />
              </svg>
            </div>

            <span className="badge bg-primary mb-3 px-4 py-2 rounded-pill motion-item hero-kickoff" style={{ fontSize: '0.85rem', fontWeight: 500, letterSpacing: '0.5px' }}>
              <i className="bi bi-stars me-2"></i>
              v2.0.0 Token + OAuth Upgrade
            </span>

            <h1 className="display-3 mb-3 hero-title motion-item" style={{ letterSpacing: '-1.5px' }}>
              Next-Generation <br />
              <span className="text-gradient">Authentication System</span>
            </h1>
            {isLoggedIn && (
              <p className="mb-2 fw-semibold hero-subtitle motion-item" style={{ color: '#4f46e5' }}>
                Welcome back, {displayName}
              </p>
            )}
            {!isLoggedIn && (
              <p className="lead mb-4 mx-auto hero-subtitle motion-item" style={{ maxWidth: '650px', color: '#78756f', fontSize: '1.15rem' }}>
                Secure access-token and refresh-token flow with OAuth2 login providers: Google, GitHub, Apple, and
                LinkedIn.
              </p>
            )}

            <div className="d-flex justify-content-center gap-3 flex-wrap hero-actions motion-item">
              {isLoggedIn ? (
                <Link to="/dashboard" className="btn btn-primary btn-lg px-5 py-3 jitter-cta">
                  <i className="bi bi-speedometer2 me-2"></i>
                  Go to Dashboard
                </Link>
              ) : (
                <>
                  <Link to="/register" className="btn btn-primary btn-lg px-5 py-3 jitter-cta">
                    <i className="bi bi-rocket-takeoff me-2"></i>
                    Get Started
                  </Link>
                  <Link to="/login" className="btn btn-outline-primary btn-lg px-5 py-3 jitter-ghost">
                    <i className="bi bi-box-arrow-in-right me-2"></i>
                    Sign In
                  </Link>
                </>
              )}
            </div>
          </div>
        </div>
      </div>

      {!isLoggedIn && (
        <div className="social-login-section jitter-section">
          <div className="container">
            <div className="text-center mb-4">
              <h3 className="mb-2">One-click OAuth2 Sign In</h3>
              <p style={{ color: '#78756f' }} className="mb-0">Use your existing identity provider account to continue securely.</p>
            </div>
            <div className="home-oauth-grid">
              {oauthProviders.map((provider) => (
                <a key={provider.id} href={provider.href} className={`home-oauth-btn provider-${provider.id} jitter-card`}>
                  <i className={`${provider.iconClass} me-2`}></i>
                  Continue with {provider.label}
                </a>
              ))}
            </div>
          </div>
        </div>
      )}

      <div className="tech-stack-section jitter-section">
        <div className="container">
          <div className="text-center mb-4">
            <h6 className="text-uppercase letter-spacing-2 mb-2" style={{ color: '#78756f', fontSize: '0.8rem' }}>Powered By</h6>
            <h3>Modern Technology Stack</h3>
          </div>

          <div className="tech-grid">
            <div className="tech-card jitter-card">
              <i className="bi bi-filetype-java tech-icon"></i>
              <span className="tech-name">Java Spring Boot</span>
            </div>
            <div className="tech-card jitter-card">
              <i className="bi bi-filetype-jsx tech-icon"></i>
              <span className="tech-name">React + Vite</span>
            </div>
            <div className="tech-card jitter-card">
              <i className="bi bi-shield-lock tech-icon"></i>
              <span className="tech-name">Spring Security</span>
            </div>
            <div className="tech-card jitter-card">
              <i className="bi bi-database tech-icon"></i>
              <span className="tech-name">JPA / Hibernate</span>
            </div>
            <div className="tech-card jitter-card">
              <i className="bi bi-envelope tech-icon"></i>
              <span className="tech-name">JavaMailSender</span>
            </div>
            <div className="tech-card jitter-card">
              <i className="bi bi-bootstrap tech-icon"></i>
              <span className="tech-name">Bootstrap 5</span>
            </div>
          </div>
        </div>
      </div>

      <div className="features-section py-5 jitter-section">
        <div className="container">
          <div className="text-center mb-5">
            <h2 className="mb-3">Core Features</h2>
            <p style={{ color: '#78756f' }}>Everything you need for a secure application</p>
          </div>

          <div className="row g-4">
            <div className="col-md-4">
              <div className="feature-card h-100 jitter-card">
                <i className="bi bi-shield-check mb-3"></i>
                <h4>Access + Refresh Tokens</h4>
                <p style={{ color: '#78756f', textAlign: 'center' }}>
                  Short-lived access tokens with secure refresh-token rotation for safer long sessions.
                </p>
              </div>
            </div>

            <div className="col-md-4">
              <div className="feature-card h-100 jitter-card">
                <i className="bi bi-person-check mb-3"></i>
                <h4>OAuth2 Social Login</h4>
                <p style={{ color: '#78756f', textAlign: 'center' }}>
                  Seamless login support for Google, GitHub, Apple, and LinkedIn accounts.
                </p>
              </div>
            </div>

            <div className="col-md-4">
              <div className="feature-card h-100 jitter-card">
                <i className="bi bi-person-lock mb-3"></i>
                <h4>Role-Based Access</h4>
                <p style={{ color: '#78756f', textAlign: 'center' }}>
                  Granular permission control with USER and ADMIN role separation.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <footer className="py-4 text-center border-top mt-5" style={{ borderColor: '#e4e2df !important' }}>
        <div className="container">
          <p className="mb-0" style={{ color: '#a3a09c' }}>© 2026 Authentication System.</p>
        </div>
      </footer>
    </div>
  );
};

export default Home;
