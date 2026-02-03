import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';

const Register = () => {
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        password: '',
        confirmPassword: '',
    });
    const [loading, setLoading] = useState(false);
    const { register } = useAuth();
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (formData.password !== formData.confirmPassword) {
            toast.error('Passwords do not match!');
            return;
        }

        if (formData.password.length < 6) {
            toast.error('Password must be at least 6 characters!');
            return;
        }

        setLoading(true);

        try {
            const response = await register(formData.name, formData.email, formData.password);
            if (response.success) {
                toast.success('Registration successful! Please verify your email.');
                navigate('/verify-otp', { state: { email: formData.email } });
            } else {
                toast.error(response.message);
            }
        } catch (err) {
            toast.error(err.response?.data?.message || 'Registration failed. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-card">
                <div className="auth-header">
                    <div className="auth-icon">
                        <i className="bi bi-person-plus"></i>
                    </div>
                    <h2>Create Account</h2>
                    <p className="text-muted">Join us today</p>
                </div>

                <form onSubmit={handleSubmit}>
                    <div className="mb-3">
                        <label className="form-label">Full Name</label>
                        <div className="input-group">
                            <span className="input-group-text">
                                <i className="bi bi-person"></i>
                            </span>
                            <input
                                type="text"
                                className="form-control"
                                name="name"
                                placeholder="Enter your full name"
                                value={formData.name}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </div>

                    <div className="mb-3">
                        <label className="form-label">Email Address</label>
                        <div className="input-group">
                            <span className="input-group-text">
                                <i className="bi bi-envelope"></i>
                            </span>
                            <input
                                type="email"
                                className="form-control"
                                name="email"
                                placeholder="Enter your email"
                                value={formData.email}
                                onChange={handleChange}
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
                                name="password"
                                placeholder="Create a password"
                                value={formData.password}
                                onChange={handleChange}
                                required
                                minLength={6}
                            />
                        </div>
                        <small className="text-muted">Must be at least 6 characters</small>
                    </div>

                    <div className="mb-4">
                        <label className="form-label">Confirm Password</label>
                        <div className="input-group">
                            <span className="input-group-text">
                                <i className="bi bi-lock-fill"></i>
                            </span>
                            <input
                                type="password"
                                className="form-control"
                                name="confirmPassword"
                                placeholder="Confirm your password"
                                value={formData.confirmPassword}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </div>

                    <button
                        type="submit"
                        className="btn btn-primary w-100 btn-lg"
                        disabled={loading}
                    >
                        {loading ? (
                            <>
                                <span className="spinner-border spinner-border-sm me-2"></span>
                                Creating Account...
                            </>
                        ) : (
                            <>
                                <i className="bi bi-person-plus me-2"></i>
                                Create Account
                            </>
                        )}
                    </button>
                </form>

                <div className="text-center mt-4">
                    <p className="text-muted mb-0">
                        Already have an account?{' '}
                        <Link to="/login" className="text-decoration-none fw-semibold">
                            Sign In
                        </Link>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default Register;
