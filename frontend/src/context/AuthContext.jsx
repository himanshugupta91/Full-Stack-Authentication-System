import { createContext, useContext, useState, useEffect } from 'react';
import { authAPI } from '../services/api';
import LoadingSpinner from '../components/LoadingSpinner';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Check for stored user on mount
        const storedUser = localStorage.getItem('user');
        if (storedUser) {
            setUser(JSON.parse(storedUser));
        }
        setLoading(false);
    }, []);

    const login = async (email, password) => {
        const response = await authAPI.login({ email, password });
        const userData = response.data;

        localStorage.setItem('token', userData.token);
        localStorage.setItem('user', JSON.stringify(userData));
        setUser(userData);

        return userData;
    };

    const register = async (name, email, password) => {
        const response = await authAPI.register({ name, email, password });
        return response.data;
    };

    const verifyOtp = async (email, otp) => {
        const response = await authAPI.verifyOtp({ email, otp });
        return response.data;
    };

    const resetPassword = async (email) => {
        const response = await authAPI.resetPassword({ email });
        return response.data;
    };

    const updatePassword = async (token, newPassword) => {
        const response = await authAPI.updatePassword({ token, newPassword });
        return response.data;
    };

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setUser(null);
    };

    const isAdmin = () => {
        return user?.roles?.includes('ROLE_ADMIN');
    };

    const isAuthenticated = () => {
        return !!user && !!localStorage.getItem('token');
    };

    return (
        <AuthContext.Provider
            value={{
                user,
                loading,
                login,
                register,
                verifyOtp,
                resetPassword,
                updatePassword,
                logout,
                isAdmin,
                isAuthenticated,
            }}
        >
            {loading ? <LoadingSpinner /> : children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};
