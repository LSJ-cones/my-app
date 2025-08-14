import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import {
  Home,
  Plus,
  Bell,
  User,
  Menu,
  X,
  LogIn,
  LogOut,
  BookOpen
} from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { useNotifications } from '../contexts/NotificationContext';

const Header = () => {
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const location = useLocation();
  const { user, isAuthenticated, logout } = useAuth();
  const { unreadCount } = useNotifications();

  const isActive = (path) => location.pathname === path;

  const handleLogout = () => {
    logout();
    setIsMobileMenuOpen(false);
  };

  const navItems = isAuthenticated ? [
    { path: '/', icon: Home, label: '홈' },
    { path: '/posts/create', icon: Plus, label: '글쓰기' },
    { path: '/notifications', icon: Bell, label: '알림' },
    { path: '/profile', icon: User, label: '프로필' },
  ] : [
    { path: '/', icon: Home, label: '홈' },
    { path: '/login', icon: LogIn, label: '로그인' },
  ];

  return (
    <header className="bg-gradient-to-r from-gray-900 via-gray-800 to-gray-900 border-b border-gray-700 sticky top-0 z-50 shadow-lg">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* 로고 */}
          <Link to="/" className="flex items-center space-x-3 group">
            <div className="relative">
              <div className="w-10 h-10 bg-gradient-to-br from-red-500 to-red-700 rounded-lg flex items-center justify-center shadow-lg group-hover:shadow-red-500/25 transition-all duration-300">
                <BookOpen className="w-6 h-6 text-white" />
              </div>
              <div className="absolute -top-1 -right-1 w-4 h-4 bg-red-400 rounded-full animate-pulse"></div>
            </div>
            <div>
              <h1 className="text-xl font-bold bg-gradient-to-r from-red-400 to-red-600 bg-clip-text text-transparent">
                Lcones Blog
              </h1>
              <p className="text-xs text-gray-400">기술 블로그</p>
            </div>
          </Link>

          {/* 데스크톱 네비게이션 */}
          <nav className="hidden md:flex items-center space-x-6">
            {navItems.map((item) => (
              <Link
                key={item.path}
                to={item.path}
                className={`flex items-center space-x-2 px-3 py-2 rounded-lg transition-all duration-200 relative ${
                  isActive(item.path)
                    ? 'text-red-400 bg-gray-800 shadow-lg'
                    : 'text-gray-300 hover:text-red-400 hover:bg-gray-800'
                }`}
              >
                <item.icon size={20} />
                <span className="font-medium">{item.label}</span>
                {item.path === '/notifications' && unreadCount > 0 && (
                  <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center animate-pulse">
                    {unreadCount > 99 ? '99+' : unreadCount}
                  </span>
                )}
              </Link>
            ))}
            
            {isAuthenticated && (
              <button 
                onClick={handleLogout} 
                className="flex items-center space-x-2 px-3 py-2 rounded-lg text-gray-300 hover:text-red-400 hover:bg-gray-800 transition-all duration-200"
              >
                <LogOut size={20} />
                <span className="font-medium">로그아웃</span>
              </button>
            )}
          </nav>

          {/* 모바일 메뉴 버튼 */}
          <button
            onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
            className="md:hidden p-2 rounded-lg text-gray-300 hover:text-red-400 hover:bg-gray-800 transition-colors duration-200"
          >
            {isMobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
          </button>
        </div>

        {/* 모바일 메뉴 */}
        {isMobileMenuOpen && (
          <div className="md:hidden border-t border-gray-700 py-4">
            <nav className="flex flex-col space-y-2">
              {navItems.map((item) => (
                <Link
                  key={item.path}
                  to={item.path}
                  onClick={() => setIsMobileMenuOpen(false)}
                  className={`flex items-center space-x-3 px-3 py-2 rounded-lg transition-all duration-200 relative ${
                    isActive(item.path)
                      ? 'text-red-400 bg-gray-800'
                      : 'text-gray-300 hover:text-red-400 hover:bg-gray-800'
                  }`}
                >
                  <item.icon size={20} />
                  <span className="font-medium">{item.label}</span>
                  {item.path === '/notifications' && unreadCount > 0 && (
                    <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center animate-pulse">
                      {unreadCount > 99 ? '99+' : unreadCount}
                    </span>
                  )}
                </Link>
              ))}
              
              {isAuthenticated && (
                <button 
                  onClick={handleLogout} 
                  className="flex items-center space-x-3 px-3 py-2 rounded-lg text-gray-300 hover:text-red-400 hover:bg-gray-800 transition-colors duration-200"
                >
                  <LogOut size={20} />
                  <span className="font-medium">로그아웃</span>
                </button>
              )}
            </nav>
          </div>
        )}
      </div>
    </header>
  );
};

export default Header;
