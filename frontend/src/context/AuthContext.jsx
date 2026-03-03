import { createContext, useContext, useEffect, useState } from 'react';
import LoadingSpinner from '../components/LoadingSpinner';
import {
  authAPI,
  clearAuthStorage,
  getStoredUser,
  saveAuthPayload,
} from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => getStoredUser());
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const isOAuthCallbackRoute = window.location.pathname.startsWith('/oauth2/callback');

    const bootstrapSession = async () => {
      const storedUser = getStoredUser();

      if (storedUser) {
        setUser(storedUser);
      }

      // OAuth callback performs its own refresh exchange exactly once.
      if (isOAuthCallbackRoute) {
        setLoading(false);
        return;
      }

      try {
        const response = await authAPI.refresh();
        saveAuthPayload(response.data);
        setUser(getStoredUser());
      } catch {
        clearAuthStorage();
        setUser(null);
      } finally {
        setLoading(false);
      }
    };

    bootstrapSession();
  }, []);

  const login = async (email, password) => {
    const response = await authAPI.login({ email, password });
    saveAuthPayload(response.data);
    const currentUser = getStoredUser();
    setUser(currentUser);
    return response.data;
  };

  const completeOAuthLogin = async () => {
    const response = await authAPI.refresh();
    saveAuthPayload(response.data);
    const currentUser = getStoredUser();
    setUser(currentUser);
    return currentUser;
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

  const logout = async () => {
    try {
      await authAPI.logout();
    } catch {
      // Ignore logout errors and clear local state.
    } finally {
      clearAuthStorage();
      setUser(null);
    }
  };

  const isAdmin = () => user?.roles?.includes('ROLE_ADMIN');

  const isAuthenticated = () => !!user;

  const value = {
    user,
    loading,
    login,
    completeOAuthLogin,
    register,
    verifyOtp,
    resetPassword,
    updatePassword,
    logout,
    isAdmin,
    isAuthenticated,
  };

  return <AuthContext.Provider value={value}>{loading ? <LoadingSpinner /> : children}</AuthContext.Provider>;
};

// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
