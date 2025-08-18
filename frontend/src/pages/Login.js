import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { LogIn, Eye, EyeOff, UserPlus } from 'lucide-react';
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
  
  const { login } = useAuth();
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

        {/* 회원가입 링크 */}
        <div className="mt-6 text-center">
          <div className="text-sm text-gray-400 mb-4">
            계정이 없으신가요?
          </div>
          <Link
            to="/signup"
            className="inline-flex items-center px-4 py-2 text-sm font-medium text-red-400 bg-red-400/10 border border-red-400/20 rounded-lg hover:bg-red-400/20 hover:border-red-400/30 transition-all duration-200"
          >
            <UserPlus className="w-4 h-4 mr-2" />
            회원가입
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Login;
