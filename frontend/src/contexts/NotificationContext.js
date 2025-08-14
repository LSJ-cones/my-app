import React, { createContext, useContext, useState, useEffect, useRef } from 'react';
import { useAuth } from './AuthContext';
import NotificationPopup from '../components/NotificationPopup';
import { toast } from 'react-hot-toast';

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
  const wsRef = useRef(null);
  const popupIdRef = useRef(0);

  // WebSocket 연결
  useEffect(() => {
    if (!user) {
      return;
    }

    // WebSocket 연결
    const connectWebSocket = () => {
      try {
        const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
        const wsUrl = `${protocol}//${window.location.host}/ws/notifications`;
        
        wsRef.current = new WebSocket(wsUrl);

        wsRef.current.onopen = () => {
          console.log('WebSocket 연결됨');
        };

        wsRef.current.onmessage = (event) => {
          try {
            const notification = JSON.parse(event.data);
            console.log('실시간 알림 수신:', notification);
            
            // 새 알림 추가
            setNotifications(prev => [notification, ...prev]);
            setUnreadCount(prev => prev + 1);
            
            // 팝업 표시
            showNotificationPopup(notification);
            
            // 토스트 메시지 표시
            toast.success(notification.content, {
              duration: 3000,
              position: 'top-right',
            });
          } catch (error) {
            console.error('알림 파싱 오류:', error);
          }
        };

        wsRef.current.onclose = () => {
          console.log('WebSocket 연결 종료');
          // 3초 후 재연결 시도
          setTimeout(connectWebSocket, 3000);
        };

        wsRef.current.onerror = (error) => {
          console.error('WebSocket 오류:', error);
        };
      } catch (error) {
        console.error('WebSocket 연결 실패:', error);
      }
    };

    connectWebSocket();

    // 컴포넌트 언마운트 시 연결 종료
    return () => {
      if (wsRef.current) {
        wsRef.current.close();
      }
    };
  }, [user]);

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
      // API 호출
      const response = await fetch(`/api/notifications/${notificationId}/read`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        // 로컬 상태 업데이트
        setNotifications(prev => 
          prev.map(notification => 
            notification.id === notificationId 
              ? { ...notification, status: 'READ' }
              : notification
          )
        );
        setUnreadCount(prev => Math.max(0, prev - 1));
      }
    } catch (error) {
      console.error('알림 읽음 처리 실패:', error);
    }
  };

  // 알림 목록 조회
  const fetchNotifications = async () => {
    try {
      const response = await fetch('/api/notifications?page=0&size=50&sortBy=createdAt&sortDirection=desc');
      if (response.ok) {
        const data = await response.json();
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
