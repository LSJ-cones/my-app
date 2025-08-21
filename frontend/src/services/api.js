import axios from 'axios';

// API 기본 설정
const API_BASE_URL = '/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터 - 토큰 추가
api.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터 - 에러 처리
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      sessionStorage.removeItem('token');
      sessionStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// 게시글 관련 API
export const postAPI = {
  // 게시글 목록 조회
  getPosts: (page = 0, size = 10) => 
    api.get(`/posts?page=${page}&size=${size}`),
  
  // 게시글 상세 조회
  getPost: (id) => 
    api.get(`/posts/${id}`),
  
  // 게시글 생성
  createPost: (data) => 
    api.post('/posts', data),
  
  // 게시글 수정
  updatePost: (id, data) => 
    api.put(`/posts/${id}`, data),
  
  // 게시글 삭제
  deletePost: (id) => 
    api.delete(`/posts/${id}`),
};

// 게시글 반응 관련 API
export const postReactionAPI = {
  // 게시글 반응 조회
  getReaction: (postId) => 
    api.get(`/posts/${postId}/reactions`),
  
  // 게시글 반응 추가/수정
  addReaction: (postId, type) => 
    api.post(`/posts/${postId}/reactions`, { type }),
  
  // 게시글 반응 삭제
  removeReaction: (postId) => 
    api.delete(`/posts/${postId}/reactions`),
  
  // 게시글 반응 통계
  getReactionStats: (postId) => 
    api.get(`/posts/${postId}/reactions/stats`),
  
  // 내 반응 목록
  getMyReactions: () => 
    api.get('/reactions/my'),
};

// 댓글 관련 API
export const commentAPI = {
  // 댓글 목록 조회
  getComments: (postId) => 
    api.get(`/posts/${postId}/comments`),
  
  // 댓글 생성
  createComment: (postId, data) => 
    api.post(`/posts/${postId}/comments`, data),
  
  // 댓글 수정
  updateComment: (postId, commentId, data) => 
    api.put(`/posts/${postId}/comments/${commentId}`, data),
  
  // 댓글 삭제
  deleteComment: (postId, commentId) => 
    api.delete(`/posts/${postId}/comments/${commentId}`),
};

// 사용자 관련 API
export const userAPI = {
  // 로그인
  login: (credentials) => 
    api.post('/auth/login', credentials),
  
  // 회원가입
  register: (userData) => 
    api.post('/auth/register', userData),
  
  // 내 정보 조회
  getMyInfo: () => 
    api.get('/users/me'),
  
  // 사용자 정보 수정
  updateProfile: (data) => 
    api.put('/users/me', data),
};

// 알림 관련 API
export const notificationAPI = {
  // 알림 목록 조회
  getNotifications: () => 
    api.get('/notifications'),
  
  // 알림 읽음 처리
  markAsRead: (notificationId) => 
    api.put(`/notifications/${notificationId}/read`),
  
  // 알림 삭제
  deleteNotification: (notificationId) => 
    api.delete(`/notifications/${notificationId}`),
  
  // 읽지 않은 알림 개수
  getUnreadCount: () => 
    api.get('/notifications/unread/count'),
};

export default api;
