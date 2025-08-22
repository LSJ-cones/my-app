import React, { createContext, useContext, useState, useEffect, useRef } from 'react';
import { useAuth } from './AuthContext';
import NotificationPopup from '../components/NotificationPopup';
import { toast } from 'react-hot-toast';
import api from '../services/api';

const NotificationContext = createContext();

export const useNotifications = () => {
  const context = useContext(NotificationContext);
  if (!context) {
    throw new Error('useNotifications must be used within a NotificationProvider');
  }
  return context;
};

export const NotificationProvider = ({ children }) => {
  const { user } = useAuth();
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [activePopups, setActivePopups] = useState([]);
  const popupIdRef = useRef(0);

  // 알림 팝업 표시
  const showNotificationPopup = (notification) => {
    const popupId = popupIdRef.current++;
    const newPopup = {
      id: popupId,
      notification,
      timestamp: Date.now()
    };

    setActivePopups(prev => [...prev, newPopup]);

    // 최대 3개까지만 표시
    if (activePopups.length >= 3) {
      setActivePopups(prev => prev.slice(1));
    }
  };

  // 팝업 닫기
  const closePopup = (popupId) => {
    setActivePopups(prev => prev.filter(popup => popup.id !== popupId));
  };

  // 알림 읽음 처리
  const markAsRead = async (notificationId) => {
    try {
      // API 호출 (JWT 토큰 포함)
      const response = await api.put(`/notifications/${notificationId}/read`);

      if (response.status === 200) {
        // 로컬 상태 업데이트
        setNotifications(prev => 
          prev.map(notification => 
            notification.id === notificationId 
              ? { ...notification, status: 'READ' }
              : notification
          )
        );
        // unreadCount 재계산
        const newUnreadCount = notifications.filter(n => n.id !== notificationId && n.status === 'UNREAD').length;
        setUnreadCount(newUnreadCount);
      }
    } catch (error) {
      console.error('알림 읽음 처리 실패:', error);
    }
  };

  // 모든 알림 읽음 처리
  const markAllAsRead = async () => {
    try {
      const response = await api.put('/notifications/read-all');

      if (response.status === 200) {
        // 모든 알림을 읽음 상태로 변경
        setNotifications(prev => 
          prev.map(notification => ({ ...notification, status: 'READ' }))
        );
        setUnreadCount(0);
      }
    } catch (error) {
      console.error('전체 알림 읽음 처리 실패:', error);
    }
  };

  // 알림 목록 조회
  const fetchNotifications = async () => {
    try {
      const response = await api.get('/notifications?page=0&size=50&sortBy=createdAt&sortDirection=desc');
      if (response.status === 200) {
        const data = response.data;
        setNotifications(data.content || []);
        setUnreadCount(data.content?.filter(n => n.status === 'UNREAD').length || 0);
      }
    } catch (error) {
      console.error('알림 조회 실패:', error);
    }
  };

  // 초기 알림 로드
  useEffect(() => {
    if (user) {
      fetchNotifications();
    }
  }, [user]);

  const value = {
    notifications,
    unreadCount,
    fetchNotifications,
    markAsRead,
    markAllAsRead,
  };

  return (
    <NotificationContext.Provider value={value}>
      {children}
      
      {/* 실시간 알림 팝업들 */}
      {activePopups.map((popup, index) => (
        <div
          key={popup.id}
          style={{
            top: `${4 + index * 80}px`,
            right: '16px',
            zIndex: 1000 + index,
          }}
          className="fixed"
        >
          <NotificationPopup
            notification={popup.notification}
            onClose={() => closePopup(popup.id)}
            onMarkAsRead={markAsRead}
          />
        </div>
      ))}
    </NotificationContext.Provider>
  );
};
