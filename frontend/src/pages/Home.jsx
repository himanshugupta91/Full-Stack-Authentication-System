import { useMemo } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { OAUTH_PROVIDERS, getOAuthAuthorizationUrl } from '../services/api';
import { KeyRound, ShieldCheck, Globe, Lock, Zap, RefreshCcw, Leaf, Atom, Shield, Database, Send, Layers } from 'lucide-react';

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
              Production-ready auth platform
            </span>

            <h1 className="simple-hero-title" style={{ fontSize: "clamp(2.5rem, 6vw, 4rem)", fontWeight: 800, lineHeight: 1.1 }}>
              Authentication &amp;{" "}
              <span className="simple-hero-highlight" style={{
                backgroundImage: "linear-gradient(to right, #818cf8, #c084fc)",
                WebkitBackgroundClip: "text",
                WebkitTextFillColor: "transparent",
                backgroundClip: "text",
                color: "transparent"
              }}>
                Authorization
              </span>{" "}
              done right
            </h1>

            {isLoggedIn && <p className="simple-hero-greeting">Welcome back, {displayName}</p>}

            {!isLoggedIn && (
              <p className="simple-hero-copy" style={{ fontSize: "1.125rem", lineHeight: 1.7, maxWidth: "40rem", margin: "0 auto 2.5rem" }}>
                A full-stack auth system with JWT rotation, OAuth2 social login, OTP email verification, Redis rate limiting, and role-based access control. Built to ship.
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
              <Leaf className="tech-icon mb-3" size={32} color="#22c55e" />
              <span className="tech-name">Java Spring Boot</span>
            </div>
            <div className="tech-card jitter-card">
              <Atom className="tech-icon mb-3" size={32} color="#0ea5e9" />
              <span className="tech-name">React + Vite</span>
            </div>
            <div className="tech-card jitter-card">
              <Shield className="tech-icon mb-3" size={32} color="#3b82f6" />
              <span className="tech-name">Spring Security</span>
            </div>
            <div className="tech-card jitter-card">
              <Database className="tech-icon mb-3" size={32} color="#6366f1" />
              <span className="tech-name">PostgreSQL</span>
            </div>
            <div className="tech-card jitter-card">
              <Send className="tech-icon mb-3" size={32} color="#f59e0b" />
              <span className="tech-name">JavaMailSender</span>
            </div>
            <div className="tech-card jitter-card">
              <Layers className="tech-icon mb-3" size={32} color="#ef4444" />
              <span className="tech-name">Redis</span>
            </div>
          </div>
        </div>
      </div>

      <div className="features-section py-5 jitter-section">
        <div className="container">
          <div className="text-center mb-5">
            <h2 className="mb-3">Everything you need for auth</h2>
            <p className="section-muted">The complete account lifecycle, security hardened</p>
          </div>

          <div className="row g-4 mb-4">
            <div className="col-md-4">
              <div className="feature-card h-100 jitter-card">
                <KeyRound className="mb-3" size={32} color="#6366f1" />
                <h4>JWT + Refresh Rotation</h4>
                <p className="feature-copy">
                  Short-lived access tokens with opaque refresh tokens rotated on every use. Stored as bcrypt hashes in the database.
                </p>
              </div>
            </div>

            <div className="col-md-4">
              <div className="feature-card h-100 jitter-card">
                <ShieldCheck className="mb-3" size={32} color="#10b981" />
                <h4>OTP Email Verification</h4>
                <p className="feature-copy">
                  One-time passwords delivered via email on registration. Resend flow with rate-limit protection.
                </p>
              </div>
            </div>

            <div className="col-md-4">
              <div className="feature-card h-100 jitter-card">
                <Globe className="mb-3" size={32} color="#0ea5e9" />
                <h4>OAuth2 Social Login</h4>
                <p className="feature-copy">
                  Sign in with Google, GitHub, Apple, and LinkedIn via Spring Security's OAuth2 client integration.
                </p>
              </div>
            </div>

            <div className="col-md-4">
              <div className="feature-card h-100 jitter-card">
                <Lock className="mb-3" size={32} color="#a855f7" />
                <h4>Role-Based Access Control</h4>
                <p className="feature-copy">
                  ROLE_USER and ROLE_ADMIN roles with Spring Security annotations and centralized policy enforcement.
                </p>
              </div>
            </div>

            <div className="col-md-4">
              <div className="feature-card h-100 jitter-card">
                <Zap className="mb-3" size={32} color="#f59e0b" />
                <h4>Redis Rate Limiting</h4>
                <p className="feature-copy">
                  Request counters and temporary lockout protection backed by Redis. Prevents brute-force and abuse.
                </p>
              </div>
            </div>

            <div className="col-md-4">
              <div className="feature-card h-100 jitter-card">
                <RefreshCcw className="mb-3" size={32} color="#f43f5e" />
                <h4>Full Password Lifecycle</h4>
                <p className="feature-copy">
                  Reset via email, authenticated change, and BCrypt hashing. Reset tokens stored hashed in the database.
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
