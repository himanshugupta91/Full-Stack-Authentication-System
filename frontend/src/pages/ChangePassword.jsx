import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { userAPI } from '../services/api';
import toast from 'react-hot-toast';

/**
 * ChangePassword Component
 * Allows authenticated users to update their password.
 * Includes validation for matching passwords and minimum length.
 */
const ChangePassword = () => {
    // State for form inputs (current, new, confirm password)
    const [formData, setFormData] = useState({
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
    });
    const [loading, setLoading] = useState(false);

    const { logout } = useAuth();
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    /**
     * Handles password change form submission.
     * Validates inputs and interacts with the API.
     */
    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        // Validation: Check if new passwords match
        if (formData.newPassword !== formData.confirmPassword) {
            toast.error("New passwords don't match!");
            setLoading(false);
            return;
        }

        // Validation: Check password length
        if (formData.newPassword.length < 6) {
            toast.error("New password must be at least 6 characters long.");
            setLoading(false);
            return;
        }

        try {
            // Call API to change password
            const response = await userAPI.changePassword({
                currentPassword: formData.currentPassword,
                newPassword: formData.newPassword
            });

            if (response.data.success) {
                toast.success('Password changed successfully! You will be logged out in 3 seconds.');

                // Logout user after successful password change for security
                setTimeout(() => {
                    logout();
                    navigate('/login');
                }, 3000);
            } else {
                toast.error(response.data.message);
            }
        } catch (err) {
            toast.error(err.response?.data?.message || 'An error occurred. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-card">
                <div className="auth-header">
                    <div className="auth-icon">
                        <i className="bi bi-key-fill"></i>
                    </div>
                    <h2>Change Password</h2>
                    <p className="text-muted">Update your secure password</p>
                </div>

                <form onSubmit={handleSubmit}>
                    <div className="mb-3">
                        <label className="form-label">Current Password</label>
                        <div className="input-group">
                            <span className="input-group-text">
                                <i className="bi bi-lock"></i>
                            </span>
                            <input
                                type="password"
                                className="form-control"
                                placeholder="Enter current password"
                                name="currentPassword"
                                value={formData.currentPassword}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </div>

                    <div className="mb-3">
                        <label className="form-label">New Password</label>
                        <div className="input-group">
                            <span className="input-group-text">
                                <i className="bi bi-lock-fill"></i>
                            </span>
                            <input
                                type="password"
                                className="form-control"
                                placeholder="Enter new password"
                                name="newPassword"
                                value={formData.newPassword}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </div>

                    <div className="mb-4">
                        <label className="form-label">Confirm New Password</label>
                        <div className="input-group">
                            <span className="input-group-text">
                                <i className="bi bi-check-circle"></i>
                            </span>
                            <input
                                type="password"
                                className="form-control"
                                placeholder="Confirm new password"
                                name="confirmPassword"
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
                                Updating...
                            </>
                        ) : (
                            <>
                                <i className="bi bi-save me-2"></i>
                                Update Password
                            </>
                        )}
                    </button>

                    <div className="text-center mt-3">
                        <button
                            type="button"
                            className="btn btn-link text-muted text-decoration-none"
                            onClick={() => navigate('/dashboard')}
                        >
                            <i className="bi bi-arrow-left me-1"></i>
                            Back to Dashboard
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default ChangePassword;
