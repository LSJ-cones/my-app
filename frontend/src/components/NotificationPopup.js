import React, { useState, useEffect } from 'react';
import { Bell, X, MessageSquare, Heart, User } from 'lucide-react';
import { toast } from 'react-hot-toast';

const NotificationPopup = ({ notification, onClose, onMarkAsRead }) => {
  const [isVisible, setIsVisible] = useState(true);

  useEffect(() => {
    // 5초 후 자동으로 사라짐
    const timer = setTimeout(() => {
      setIsVisible(false);
      setTimeout(onClose, 300); // 애니메이션 완료 후 제거
    }, 5000);

    return () => clearTimeout(timer);
  }, [onClose]);

  const getNotificationIcon = (type) => {
    switch (type) {
      case 'COMMENT':
      case 'REPLY':
        return <MessageSquare className="w-4 h-4 text-blue-400" />;
      case 'LIKE':
      case 'POST_LIKE':
        return <Heart className="w-4 h-4 text-red-400" />;
      case 'POST_UPDATE':
      case 'SYSTEM':
        return <Bell className="w-4 h-4 text-yellow-400" />;
      default:
        return <Bell className="w-4 h-4 text-gray-400" />;
    }
  };

  const getNotificationColor = (type) => {
    switch (type) {
      case 'COMMENT':
      case 'REPLY':
        return 'border-blue-500 bg-blue-500/10';
      case 'LIKE':
      case 'POST_LIKE':
        return 'border-red-500 bg-red-500/10';
      case 'POST_UPDATE':
      case 'SYSTEM':
        return 'border-yellow-500 bg-yellow-500/10';
      default:
        return 'border-gray-500 bg-gray-500/10';
    }
  };

  const handleMarkAsRead = () => {
    if (onMarkAsRead) {
      onMarkAsRead(notification.id);
    }
    setIsVisible(false);
    setTimeout(onClose, 300);
  };

  const handleClose = () => {
    setIsVisible(false);
    setTimeout(onClose, 300);
  };

  if (!isVisible) {
    return null;
  }

  return (
    <div className={`fixed top-4 right-4 z-50 max-w-sm w-full transform transition-all duration-300 ${
      isVisible ? 'translate-x-0 opacity-100' : 'translate-x-full opacity-0'
    }`}>
      <div className={`glass-dark p-4 rounded-xl border-l-4 shadow-lg ${getNotificationColor(notification.type)}`}>
        <div className="flex items-start space-x-3">
          <div className="flex-shrink-0">
            <div className="p-2 bg-gray-700/50 rounded-lg">
              {getNotificationIcon(notification.type)}
            </div>
          </div>
          
          <div className="flex-1 min-w-0">
            <div className="flex items-start justify-between">
              <div className="flex-1">
                <p className="text-sm font-medium text-white mb-1">
                  {notification.title}
                </p>
                <p className="text-xs text-gray-300 line-clamp-2">
                  {notification.content}
                </p>
                <div className="flex items-center space-x-2 mt-2">
                  <span className="text-xs text-gray-400">
                    {new Date(notification.createdAt).toLocaleTimeString('ko-KR', {
                      hour: '2-digit',
                      minute: '2-digit'
                    })}
                  </span>
                  {notification.status === 'UNREAD' && (
                    <span className="px-2 py-1 bg-red-500/20 text-red-400 rounded-full text-xs">
                      새 알림
                    </span>
                  )}
                </div>
              </div>
              
              <div className="flex items-center space-x-1 ml-2">
                {notification.status === 'UNREAD' && (
                  <button
                    onClick={handleMarkAsRead}
                    className="p-1 text-gray-400 hover:text-blue-400 transition-colors duration-200"
                    title="읽음 처리"
                  >
                    <div className="w-3 h-3 border border-current rounded-full" />
                  </button>
                )}
                <button
                  onClick={handleClose}
                  className="p-1 text-gray-400 hover:text-red-400 transition-colors duration-200"
                  title="닫기"
                >
                  <X className="w-3 h-3" />
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default NotificationPopup;
