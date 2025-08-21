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
      // sessionStorage에서 사용자 정보와 토큰을 가져옴 (브라우저 종료시 자동 삭제)
      const savedUser = sessionStorage.getItem('user');
      const savedToken = sessionStorage.getItem('token');
      
      if (savedUser && savedToken) {
        const parsedUser = JSON.parse(savedUser);
        if (parsedUser && typeof parsedUser === 'object') {
          setUser(parsedUser);
        }
      }
    } catch (error) {
      console.error('인증 상태 확인 실패:', error);
      sessionStorage.removeItem('user');
      sessionStorage.removeItem('token');
    } finally {
      setLoading(false);
    }
  };

  // 로그인
  const login = async (credentials) => {
    try {
      // 실제 로그인 API 호출
      const response = await api.post('/auth/login', credentials);
      const { token, id, username, email, name, role } = response.data;
      
      const userData = {
        id: id,
        username: username,
        email: email,
        name: name,
        role: role
      };
      
      setUser(userData);
      sessionStorage.setItem('user', JSON.stringify(userData));
      sessionStorage.setItem('token', token);
      return { success: true };
    } catch (error) {
      console.error('로그인 실패:', error);
      return { success: false, error: error.response?.data?.message || '로그인에 실패했습니다.' };
    }
  };

  // 로그아웃
  const logout = () => {
    setUser(null);
    sessionStorage.removeItem('user');
    sessionStorage.removeItem('token');
  };

  useEffect(() => {
    checkAuthStatus();
  }, []);

  const value = {
    user,
    loading,
    login,
    logout,
    isAuthenticated: !!user
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};
