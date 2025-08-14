import React, { useState, useEffect } from 'react';
import { Bell, MessageSquare, Heart, User, Clock, Trash2, Eye, EyeOff, Filter, Search } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { toast } from 'react-hot-toast';
import api from '../services/api';

const Notifications = () => {
  const { user } = useAuth();
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('all'); // all, unread, read
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchNotifications();
  }, []);

  const fetchNotifications = async () => {
    try {
      setLoading(true);
      const response = await api.get('/notifications', {
        params: {
          page: 0,
          size: 50,
          sortBy: 'createdAt',
          sortDirection: 'desc'
        }
      });
      
      if (response.data && response.data.content) {
        setNotifications(response.data.content);
      } else {
        setNotifications([]);
      }
    } catch (error) {
      console.error('알림 조회 실패:', error);
      toast.error('알림을 불러오는데 실패했습니다.');
      setNotifications([]);
    } finally {
      setLoading(false);
    }
  };

  const getNotificationIcon = (type) => {
    switch (type) {
      case 'COMMENT':
        return <MessageSquare className="w-5 h-5 text-blue-400" />;
      case 'REPLY':
        return <MessageSquare className="w-5 h-5 text-blue-400" />;
      case 'LIKE':
        return <Heart className="w-5 h-5 text-red-400" />;
      case 'POST_LIKE':
        return <Heart className="w-5 h-5 text-red-400" />;
      case 'POST_UPDATE':
        return <Bell className="w-5 h-5 text-yellow-400" />;
      case 'SYSTEM':
        return <Bell className="w-5 h-5 text-yellow-400" />;
      default:
        return <Bell className="w-5 h-5 text-gray-400" />;
    }
  };

  const getTimeAgo = (dateString) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffInMs = now - date;
    const diffInMinutes = Math.floor(diffInMs / (1000 * 60));
    const diffInHours = Math.floor(diffInMs / (1000 * 60 * 60));
    const diffInDays = Math.floor(diffInMs / (1000 * 60 * 60 * 24));

    if (diffInMinutes < 1) return '방금 전';
    if (diffInMinutes < 60) return `${diffInMinutes}분 전`;
    if (diffInHours < 24) return `${diffInHours}시간 전`;
    if (diffInDays < 7) return `${diffInDays}일 전`;
    return date.toLocaleDateString('ko-KR');
  };

  const markAsRead = async (id) => {
    try {
      await api.put(`/notifications/${id}/read`);
      setNotifications(prev => 
        prev.map(notification => 
          notification.id === id 
            ? { ...notification, status: 'READ' }
            : notification
        )
      );
      toast.success('알림을 읽음 처리했습니다.');
    } catch (error) {
      console.error('알림 읽음 처리 실패:', error);
      toast.error('알림 읽음 처리에 실패했습니다.');
    }
  };

  const deleteNotification = async (id) => {
    try {
      await api.delete(`/notifications/${id}`);
      setNotifications(prev => prev.filter(notification => notification.id !== id));
      toast.success('알림을 삭제했습니다.');
    } catch (error) {
      console.error('알림 삭제 실패:', error);
      toast.error('알림 삭제에 실패했습니다.');
    }
  };

  const markAllAsRead = async () => {
    try {
      await api.put('/notifications/read-all');
      setNotifications(prev => 
        prev.map(notification => ({ ...notification, status: 'READ' }))
      );
      toast.success('모든 알림을 읽음 처리했습니다.');
    } catch (error) {
      console.error('모든 알림 읽음 처리 실패:', error);
      toast.error('모든 알림 읽음 처리에 실패했습니다.');
    }
  };

  const filteredNotifications = notifications.filter(notification => {
    const matchesFilter = filter === 'all' || 
      (filter === 'unread' && notification.status === 'UNREAD') ||
      (filter === 'read' && notification.status === 'READ');
    
    const matchesSearch = notification.content.toLowerCase().includes(searchTerm.toLowerCase());
    
    return matchesFilter && matchesSearch;
  });

  const unreadCount = notifications.filter(n => n.status === 'UNREAD').length;

  if (!user) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 flex items-center justify-center">
        <div className="glass-dark p-8 rounded-2xl text-center">
          <Bell className="w-16 h-16 text-red-400 mx-auto mb-4" />
          <h2 className="text-2xl font-bold text-white mb-4">로그인이 필요합니다</h2>
          <p className="text-gray-300 mb-6">알림을 확인하려면 로그인해주세요.</p>
          <button 
            onClick={() => window.location.href = '/login'}
            className="btn-primary"
          >
            로그인하기
          </button>
        </div>
      </div>
    );
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-red-400"></div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 py-8">
      <div className="max-w-4xl mx-auto px-4">
        {/* 헤더 */}
        <div className="glass-dark p-6 rounded-2xl mb-6">
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center space-x-3">
              <div className="p-3 bg-red-500/20 rounded-xl">
                <Bell className="w-8 h-8 text-red-400" />
              </div>
              <div>
                <h1 className="text-3xl font-bold text-gradient">알림 센터</h1>
                <p className="text-gray-300">새로운 소식을 확인하세요</p>
              </div>
            </div>
            <div className="flex items-center space-x-2">
              <span className="px-3 py-1 bg-red-500/20 text-red-400 rounded-full text-sm font-medium">
                {unreadCount}개 읽지 않음
              </span>
            </div>
          </div>

          {/* 검색 및 필터 */}
          <div className="flex flex-col sm:flex-row gap-4 mb-6">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
              <input
                type="text"
                placeholder="알림 검색..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-10 pr-4 py-3 bg-gray-800/50 border border-gray-700 rounded-xl text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-red-500/50 focus:border-transparent"
              />
            </div>
            <div className="flex space-x-2">
              <select
                value={filter}
                onChange={(e) => setFilter(e.target.value)}
                className="px-4 py-3 bg-gray-800/50 border border-gray-700 rounded-xl text-white focus:outline-none focus:ring-2 focus:ring-red-500/50"
              >
                <option value="all">전체</option>
                <option value="unread">읽지 않음</option>
                <option value="read">읽음</option>
              </select>
              <button
                onClick={markAllAsRead}
                className="px-4 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-xl transition-colors duration-200"
              >
                모두 읽음
              </button>
            </div>
          </div>
        </div>

        {/* 알림 목록 */}
        <div className="space-y-4">
          {filteredNotifications.length === 0 ? (
            <div className="glass-dark p-8 rounded-2xl text-center">
              <Bell className="w-16 h-16 text-gray-500 mx-auto mb-4" />
              <h3 className="text-xl font-semibold text-gray-300 mb-2">알림이 없습니다</h3>
              <p className="text-gray-400">새로운 알림이 오면 여기에 표시됩니다.</p>
            </div>
          ) : (
            filteredNotifications.map((notification) => (
              <div
                key={notification.id}
                className={`glass-dark p-6 rounded-2xl transition-all duration-200 hover:shadow-demon-lg ${
                  notification.status === 'UNREAD' ? 'border-l-4 border-red-400 bg-red-500/5' : ''
                }`}
              >
                <div className="flex items-start space-x-4">
                  <div className="flex-shrink-0">
                    <div className={`p-3 rounded-xl ${
                      notification.status === 'UNREAD' ? 'bg-red-500/20' : 'bg-gray-700/50'
                    }`}>
                      {getNotificationIcon(notification.type)}
                    </div>
                  </div>
                  
                  <div className="flex-1 min-w-0">
                    <div className="flex items-start justify-between">
                      <div className="flex-1">
                        <p className={`text-sm font-medium ${
                          notification.status === 'UNREAD' ? 'text-white' : 'text-gray-300'
                        }`}>
                          {notification.content}
                        </p>
                        <div className="flex items-center space-x-4 mt-2">
                          <div className="flex items-center space-x-1 text-gray-400">
                            <Clock className="w-4 h-4" />
                            <span className="text-sm">{getTimeAgo(notification.createdAt)}</span>
                          </div>
                          {notification.status === 'UNREAD' && (
                            <span className="px-2 py-1 bg-red-500/20 text-red-400 rounded-full text-xs">
                              새 알림
                            </span>
                          )}
                        </div>
                      </div>
                      
                      <div className="flex items-center space-x-2 ml-4">
                        {notification.status === 'UNREAD' && (
                          <button
                            onClick={() => markAsRead(notification.id)}
                            className="p-2 text-gray-400 hover:text-blue-400 transition-colors duration-200"
                            title="읽음 처리"
                          >
                            <Eye className="w-4 h-4" />
                          </button>
                        )}
                        <button
                          onClick={() => deleteNotification(notification.id)}
                          className="p-2 text-gray-400 hover:text-red-400 transition-colors duration-200"
                          title="삭제"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default Notifications;
