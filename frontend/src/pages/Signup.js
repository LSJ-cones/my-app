import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { UserPlus, Eye, EyeOff, ArrowLeft } from 'lucide-react';
import { toast } from 'react-hot-toast';
import api from '../services/api';

const Signup = () => {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
  });
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const validateForm = () => {
    if (formData.password !== formData.confirmPassword) {
      toast.error('비밀번호가 일치하지 않습니다.');
      return false;
    }
    
    if (formData.password.length < 6) {
      toast.error('비밀번호는 최소 6자 이상이어야 합니다.');
      return false;
    }
    
    if (!formData.email.includes('@')) {
      toast.error('올바른 이메일 주소를 입력해주세요.');
      return false;
    }
    
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }
    
    setLoading(true);

    try {
      const response = await api.post('/auth/signup', {
        username: formData.username,
        email: formData.email,
        password: formData.password
      });
      
      if (response.data) {
        toast.success('회원가입이 완료되었습니다! 로그인해주세요.');
        navigate('/login');
      }
    } catch (error) {
      console.error('회원가입 오류:', error);
      if (error.response?.data?.message) {
        toast.error(error.response.data.message);
      } else {
        toast.error('회원가입에 실패했습니다.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <div className="mx-auto h-16 w-16 bg-gradient-to-r from-red-600 to-red-800 rounded-2xl flex items-center justify-center shadow-lg">
            <UserPlus className="h-8 w-8 text-white" />
          </div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-white">
            회원가입
          </h2>
          <p className="mt-2 text-center text-sm text-gray-400">
            새로운 계정을 만들어 블로그를 시작하세요
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
              <label htmlFor="email" className="block text-sm font-medium text-gray-300">
                이메일
              </label>
              <input
                id="email"
                name="email"
                type="email"
                required
                value={formData.email}
                onChange={handleChange}
                className="mt-1 appearance-none relative block w-full px-3 py-3 border border-gray-600 placeholder-gray-500 text-white bg-gray-800/50 rounded-xl focus:outline-none focus:ring-2 focus:ring-red-500/50 focus:border-transparent sm:text-sm"
                placeholder="이메일을 입력하세요"
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
                  placeholder="비밀번호를 입력하세요 (최소 6자)"
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
            
            <div>
              <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-300">
                비밀번호 확인
              </label>
              <div className="mt-1 relative">
                <input
                  id="confirmPassword"
                  name="confirmPassword"
                  type={showConfirmPassword ? 'text' : 'password'}
                  required
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  className="appearance-none relative block w-full px-3 py-3 pr-10 border border-gray-600 placeholder-gray-500 text-white bg-gray-800/50 rounded-xl focus:outline-none focus:ring-2 focus:ring-red-500/50 focus:border-transparent sm:text-sm"
                  placeholder="비밀번호를 다시 입력하세요"
                />
                <button
                  type="button"
                  onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                  className="absolute inset-y-0 right-0 pr-3 flex items-center"
                >
                  {showConfirmPassword ? (
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
              {loading ? '회원가입 중...' : '회원가입'}
            </button>
          </div>
        </form>

        {/* 로그인 링크 */}
        <div className="mt-6 text-center">
          <div className="text-sm text-gray-400 mb-4">
            이미 계정이 있으신가요?
          </div>
          <Link
            to="/login"
            className="inline-flex items-center px-4 py-2 text-sm font-medium text-gray-300 bg-gray-800/50 border border-gray-600 rounded-lg hover:bg-gray-700/50 hover:border-gray-500 transition-all duration-200"
          >
            <ArrowLeft className="w-4 h-4 mr-2" />
            로그인으로 돌아가기
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Signup;
