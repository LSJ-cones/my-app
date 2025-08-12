import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../services/api';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // 로그인 상태 확인
  const checkAuthStatus = async () => {
    try {
      // 현재는 간단하게 localStorage에서 사용자 정보를 가져옴
      const savedUser = localStorage.getItem('user');
      if (savedUser) {
        setUser(JSON.parse(savedUser));
      }
    } catch (error) {
      console.error('인증 상태 확인 실패:', error);
    } finally {
      setLoading(false);
    }
  };

  // 로그인
  const login = async (credentials) => {
    try {
      // 실제 로그인 API 호출 (현재는 임시)
      const response = await api.post('/auth/login', credentials);
      const userData = response.data;
      
      setUser(userData);
      localStorage.setItem('user', JSON.stringify(userData));
      return { success: true };
    } catch (error) {
      console.error('로그인 실패:', error);
      return { success: false, error: error.response?.data?.message || '로그인에 실패했습니다.' };
    }
  };

  // 로그아웃
  const logout = () => {
    setUser(null);
    localStorage.removeItem('user');
  };

  // 임시 로그인 (개발용)
  const tempLogin = (userData) => {
    setUser(userData);
    localStorage.setItem('user', JSON.stringify(userData));
  };

  useEffect(() => {
    checkAuthStatus();
  }, []);

  const value = {
    user,
    loading,
    login,
    logout,
    tempLogin,
    isAuthenticated: !!user
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};
