import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            const userData = await login(email, password);
            if (userData.roles?.includes('ROLE_ADMIN')) {
                navigate('/admin');
            } else {
                navigate('/dashboard');
            }
        } catch (err) {
            toast.error(err.response?.data?.message || 'Login failed. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-card">
                <div className="auth-header">
                    <div className="auth-icon">
                        <i className="bi bi-shield-lock"></i>
                    </div>
                    <h2>Welcome Back</h2>
                    <p className="text-muted">Sign in to your account</p>
                </div>

                <form onSubmit={handleSubmit}>
                    <div className="mb-3">
                        <label className="form-label">Email Address</label>
                        <div className="input-group">
                            <span className="input-group-text">
                                <i className="bi bi-envelope"></i>
                            </span>
                            <input
                                type="email"
                                className="form-control"
                                placeholder="Enter your email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                            />
                        </div>
                    </div>

                    <div className="mb-3">
                        <label className="form-label">Password</label>
                        <div className="input-group">
                            <span className="input-group-text">
                                <i className="bi bi-lock"></i>
                            </span>
                            <input
                                type="password"
                                className="form-control"
                                placeholder="Enter your password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                        </div>
                    </div>

                    <div className="d-flex justify-content-between align-items-center mb-4">
                        <Link to="/forgot-password" className="text-decoration-none">
                            Forgot Password?
                        </Link>
                    </div>

                    <button
                        type="submit"
                        className="btn btn-primary w-100 btn-lg"
                        disabled={loading}
                    >
                        {loading ? (
                            <>
                                <span className="spinner-border spinner-border-sm me-2"></span>
                                Signing in...
                            </>
                        ) : (
                            <>
                                <i className="bi bi-box-arrow-in-right me-2"></i>
                                Sign In
                            </>
                        )}
                    </button>
                </form>

                <div className="text-center mt-4">
                    <p className="text-muted mb-0">
                        Don't have an account?{' '}
                        <Link to="/register" className="text-decoration-none fw-semibold">
                            Create Account
                        </Link>
                    </p>
                </div>

                <div className="demo-credentials mt-4 p-3 bg-light rounded">
                    <small className="text-muted d-block mb-2">
                        <i className="bi bi-info-circle me-1"></i>
                        Demo Admin Credentials:
                    </small>
                    <code className="d-block">Email: admin@admin.com</code>
                    <code className="d-block">Password: admin123</code>
                </div>
            </div>
        </div>
    );
};

export default Login;
