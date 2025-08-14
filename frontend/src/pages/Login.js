import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { LogIn, Eye, EyeOff } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { toast } from 'react-hot-toast';
import api from '../services/api';

const Login = () => {
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  
  const { login, tempLogin } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      // 실제 로그인 API 호출
      const result = await login(formData);
      if (result.success) {
        toast.success('로그인되었습니다!');
        navigate('/');
      } else {
        toast.error(result.error || '로그인에 실패했습니다.');
      }
    } catch (error) {
      console.error('로그인 오류:', error);
      toast.error('로그인에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  // 개발용 빠른 로그인 (실제 API 호출)
  const quickLogin = async (username) => {
    setLoading(true);
    try {
      const credentials = {
        username: username,
        password: username === 'admin' ? 'admin123' : 'user123'
      };
      
      const result = await login(credentials);
      if (result.success) {
        toast.success(`${username}으로 로그인되었습니다!`);
        navigate('/');
      } else {
        toast.error(result.error || '로그인에 실패했습니다.');
      }
    } catch (error) {
      console.error('빠른 로그인 오류:', error);
      toast.error('로그인에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <div className="mx-auto h-16 w-16 bg-gradient-to-r from-red-600 to-red-800 rounded-2xl flex items-center justify-center shadow-lg">
            <LogIn className="h-8 w-8 text-white" />
          </div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-white">
            로그인
          </h2>
          <p className="mt-2 text-center text-sm text-gray-400">
            블로그에 로그인하여 모든 기능을 이용하세요
          </p>
        </div>
        
        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          <div className="space-y-4">
            <div>
              <label htmlFor="username" className="block text-sm font-medium text-gray-300">
                사용자명
              </label>
              <input
                id="username"
                name="username"
                type="text"
                required
                value={formData.username}
                onChange={handleChange}
                className="mt-1 appearance-none relative block w-full px-3 py-3 border border-gray-600 placeholder-gray-500 text-white bg-gray-800/50 rounded-xl focus:outline-none focus:ring-2 focus:ring-red-500/50 focus:border-transparent sm:text-sm"
                placeholder="사용자명을 입력하세요"
              />
            </div>
            
            <div>
              <label htmlFor="password" className="block text-sm font-medium text-gray-300">
                비밀번호
              </label>
              <div className="mt-1 relative">
                <input
                  id="password"
                  name="password"
                  type={showPassword ? 'text' : 'password'}
                  required
                  value={formData.password}
                  onChange={handleChange}
                  className="appearance-none relative block w-full px-3 py-3 pr-10 border border-gray-600 placeholder-gray-500 text-white bg-gray-800/50 rounded-xl focus:outline-none focus:ring-2 focus:ring-red-500/50 focus:border-transparent sm:text-sm"
                  placeholder="비밀번호를 입력하세요"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute inset-y-0 right-0 pr-3 flex items-center"
                >
                  {showPassword ? (
                    <EyeOff className="h-5 w-5 text-gray-400" />
                  ) : (
                    <Eye className="h-5 w-5 text-gray-400" />
                  )}
                </button>
              </div>
            </div>
          </div>

          <div>
            <button
              type="submit"
              disabled={loading}
              className="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-medium rounded-xl text-white bg-gradient-to-r from-red-600 to-red-700 hover:from-red-700 hover:to-red-800 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 shadow-lg"
            >
              {loading ? '로그인 중...' : '로그인'}
            </button>
          </div>
        </form>

        {/* 개발용 빠른 로그인 버튼 */}
        <div className="mt-6">
          <div className="text-center text-sm text-gray-400 mb-3">
            개발용 빠른 로그인
          </div>
          <div className="flex space-x-2">
            <button
              onClick={() => quickLogin('admin')}
              disabled={loading}
              className="flex-1 py-2 px-3 text-sm font-medium text-white bg-gray-800/50 border border-gray-600 rounded-lg hover:bg-gray-700/50 transition-colors duration-200 disabled:opacity-50"
            >
              Admin
            </button>
            <button
              onClick={() => quickLogin('user')}
              disabled={loading}
              className="flex-1 py-2 px-3 text-sm font-medium text-white bg-gray-800/50 border border-gray-600 rounded-lg hover:bg-gray-700/50 transition-colors duration-200 disabled:opacity-50"
            >
              User
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;
