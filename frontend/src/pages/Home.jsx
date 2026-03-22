import { useMemo } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { OAUTH_PROVIDERS, getOAuthAuthorizationUrl } from '../services/api';

const Home = () => {
  const { isAuthenticated, user } = useAuth();
  const isLoggedIn = isAuthenticated();
  const displayName = user?.name?.trim() || user?.email?.split('@')[0] || 'User';

  const oauthProviders = useMemo(
    () =>
      OAUTH_PROVIDERS.map((provider) => ({
        ...provider,
        href: getOAuthAuthorizationUrl(provider.id),
      })),
    []
  );

  return (
    <div className="home-container">
      <div className="hero-section simple-hero-section">
        <div className="container">
          <div className="simple-hero-content">
            <span className="badge bg-primary mb-3 px-4 py-2 rounded-pill simple-hero-badge">
              <i className="bi bi-stars me-2"></i>
              Version 2.0 Security Stack
            </span>

            <h1 className="simple-hero-title">
              Next-Generation
              <br />
              <span className="simple-hero-highlight">Authentication System</span>
            </h1>

            {isLoggedIn && <p className="simple-hero-greeting">Welcome back, {displayName}</p>}

            {!isLoggedIn && (
              <p className="simple-hero-copy">
                Access-token + refresh-token architecture with OAuth2 login providers and role-aware authorization.
              </p>
            )}

            <div className="simple-hero-actions d-flex gap-3 flex-wrap">
              {isLoggedIn ? (
                <Link to="/dashboard" className="btn btn-primary btn-lg px-5 py-3">
                  <i className="bi bi-speedometer2 me-2"></i>
                  Go to Dashboard
                </Link>
              ) : (
                <>
                  <Link to="/register" className="btn btn-primary btn-lg px-5 py-3">
                    <i className="bi bi-rocket-takeoff me-2"></i>
                    Get Started
                  </Link>
                  <Link to="/login" className="btn btn-outline-primary btn-lg px-5 py-3">
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
              <p className="mb-0 section-muted">Use your existing identity provider account to continue securely.</p>
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
            <h6 className="text-uppercase letter-spacing-2 mb-2 tech-kicker">Powered By</h6>
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
            <p className="section-muted">Everything you need for a secure application</p>
          </div>

          <div className="row g-4">
            <div className="col-md-4">
              <div className="feature-card h-100 jitter-card">
                <i className="bi bi-shield-check mb-3"></i>
                <h4>Access + Refresh Tokens</h4>
                <p className="feature-copy">
                  Short-lived access tokens with secure refresh-token rotation for safer long sessions.
                </p>
              </div>
            </div>

            <div className="col-md-4">
              <div className="feature-card h-100 jitter-card">
                <i className="bi bi-person-check mb-3"></i>
                <h4>OAuth2 Social Login</h4>
                <p className="feature-copy">
                  Seamless login support for Google, GitHub, Apple, and LinkedIn accounts.
                </p>
              </div>
            </div>

            <div className="col-md-4">
              <div className="feature-card h-100 jitter-card">
                <i className="bi bi-person-lock mb-3"></i>
                <h4>Role-Based Access</h4>
                <p className="feature-copy">
                  Granular permission control with USER and ADMIN role separation.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <footer className="py-4 text-center border-top mt-5 home-footer">
        <div className="container">
          <p className="mb-0 home-footer-note">© {new Date().getFullYear()} Authentication System.</p>
        </div>
      </footer>
    </div>
  );
};

export default Home;
