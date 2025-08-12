import React, { useState } from 'react';
import { User, Mail, Shield, Edit3, Save, X, Eye, EyeOff, Activity, Calendar, MessageSquare, Heart, Eye as EyeIcon, Settings, LogOut } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { toast } from 'react-hot-toast';

const Profile = () => {
  const { user, logout } = useAuth();
  const [isEditing, setIsEditing] = useState(false);
  const [isChangingPassword, setIsChangingPassword] = useState(false);
  const [showCurrentPassword, setShowCurrentPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  
  const [profileData, setProfileData] = useState({
    username: user?.username || '사용자',
    email: user?.email || 'user@example.com'
  });
  
  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  });

  // 모의 활동 통계 데이터
  const activityStats = {
    totalPosts: 15,
    totalComments: 42,
    totalLikes: 128,
    totalViews: 2048,
    joinDate: '2024-01-15',
    lastActive: '2024-08-13'
  };

  const handleProfileChange = (e) => {
    const { name, value } = e.target;
    setProfileData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handlePasswordChange = (e) => {
    const { name, value } = e.target;
    setPasswordData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleProfileSave = async () => {
    try {
      // 실제 API 호출 대신 모의 처리
      await new Promise(resolve => setTimeout(resolve, 1000));
      toast.success('프로필이 성공적으로 업데이트되었습니다.');
      setIsEditing(false);
    } catch (error) {
      toast.error('프로필 업데이트에 실패했습니다.');
    }
  };

  const handlePasswordSave = async () => {
    if (passwordData.newPassword !== passwordData.confirmPassword) {
      toast.error('새 비밀번호가 일치하지 않습니다.');
      return;
    }

    if (passwordData.newPassword.length < 6) {
      toast.error('새 비밀번호는 최소 6자 이상이어야 합니다.');
      return;
    }

    try {
      // 실제 API 호출 대신 모의 처리
      await new Promise(resolve => setTimeout(resolve, 1000));
      toast.success('비밀번호가 성공적으로 변경되었습니다.');
      setIsChangingPassword(false);
      setPasswordData({
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      });
    } catch (error) {
      toast.error('비밀번호 변경에 실패했습니다.');
    }
  };

  const handleLogout = () => {
    logout();
    toast.success('로그아웃되었습니다.');
  };

  if (!user) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 flex items-center justify-center">
        <div className="glass-dark p-8 rounded-2xl text-center">
          <User className="w-16 h-16 text-red-400 mx-auto mb-4" />
          <h2 className="text-2xl font-bold text-white mb-4">로그인이 필요합니다</h2>
          <p className="text-gray-300 mb-6">프로필을 확인하려면 로그인해주세요.</p>
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

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 py-8">
      <div className="max-w-6xl mx-auto px-4">
        {/* 프로필 헤더 */}
        <div className="glass-dark p-8 rounded-2xl mb-8">
          <div className="flex flex-col lg:flex-row items-start lg:items-center justify-between">
            <div className="flex items-center space-x-6 mb-6 lg:mb-0">
              <div className="relative">
                <div className="w-24 h-24 bg-gradient-to-br from-red-500 to-red-700 rounded-full flex items-center justify-center">
                  <User className="w-12 h-12 text-white" />
                </div>
                <div className="absolute -bottom-2 -right-2 w-8 h-8 bg-green-500/20 rounded-full border-4 border-gray-900"></div>
              </div>
              <div>
                <h1 className="text-3xl font-bold text-gradient mb-2">
                  {isEditing ? (
                    <input
                      type="text"
                      name="username"
                      value={profileData.username}
                      onChange={handleProfileChange}
                      className="bg-gray-800/50 border border-gray-700 rounded-lg px-3 py-2 text-white focus:outline-none focus:ring-2 focus:ring-red-500/50"
                    />
                  ) : (
                    profileData.username
                  )}
                </h1>
                <p className="text-gray-300 flex items-center">
                  <Mail className="w-4 h-4 mr-2" />
                  {isEditing ? (
                    <input
                      type="email"
                      name="email"
                      value={profileData.email}
                      onChange={handleProfileChange}
                      className="bg-gray-800/50 border border-gray-700 rounded-lg px-3 py-2 text-white focus:outline-none focus:ring-2 focus:ring-red-500/50"
                    />
                  ) : (
                    profileData.email
                  )}
                </p>
                <div className="flex items-center space-x-4 mt-2">
                  <span className="px-3 py-1 bg-red-500/20 text-red-400 rounded-full text-sm font-medium">
                    {user.role || 'USER'}
                  </span>
                  <span className="text-gray-400 text-sm">
                    가입일: {new Date(activityStats.joinDate).toLocaleDateString('ko-KR')}
                  </span>
                </div>
              </div>
            </div>
            
            <div className="flex space-x-3">
              {isEditing ? (
                <>
                  <button
                    onClick={handleProfileSave}
                    className="btn-primary flex items-center space-x-2"
                  >
                    <Save className="w-4 h-4" />
                    <span>저장</span>
                  </button>
                  <button
                    onClick={() => setIsEditing(false)}
                    className="btn-secondary flex items-center space-x-2"
                  >
                    <X className="w-4 h-4" />
                    <span>취소</span>
                  </button>
                </>
              ) : (
                <button
                  onClick={() => setIsEditing(true)}
                  className="btn-primary flex items-center space-x-2"
                >
                  <Edit3 className="w-4 h-4" />
                  <span>프로필 수정</span>
                </button>
              )}
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* 활동 통계 */}
          <div className="lg:col-span-2">
            <div className="glass-dark p-6 rounded-2xl mb-6">
              <h2 className="text-2xl font-bold text-white mb-6 flex items-center">
                <Activity className="w-6 h-6 mr-3 text-red-400" />
                활동 통계
              </h2>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                <div className="text-center p-4 bg-gray-800/50 rounded-xl">
                  <div className="w-12 h-12 bg-blue-500/20 rounded-full flex items-center justify-center mx-auto mb-2">
                    <MessageSquare className="w-6 h-6 text-blue-400" />
                  </div>
                  <div className="text-2xl font-bold text-white">{activityStats.totalPosts}</div>
                  <div className="text-gray-400 text-sm">게시글</div>
                </div>
                <div className="text-center p-4 bg-gray-800/50 rounded-xl">
                  <div className="w-12 h-12 bg-green-500/20 rounded-full flex items-center justify-center mx-auto mb-2">
                    <MessageSquare className="w-6 h-6 text-green-400" />
                  </div>
                  <div className="text-2xl font-bold text-white">{activityStats.totalComments}</div>
                  <div className="text-gray-400 text-sm">댓글</div>
                </div>
                <div className="text-center p-4 bg-gray-800/50 rounded-xl">
                  <div className="w-12 h-12 bg-red-500/20 rounded-full flex items-center justify-center mx-auto mb-2">
                    <Heart className="w-6 h-6 text-red-400" />
                  </div>
                  <div className="text-2xl font-bold text-white">{activityStats.totalLikes}</div>
                  <div className="text-gray-400 text-sm">좋아요</div>
                </div>
                <div className="text-center p-4 bg-gray-800/50 rounded-xl">
                  <div className="w-12 h-12 bg-purple-500/20 rounded-full flex items-center justify-center mx-auto mb-2">
                    <EyeIcon className="w-6 h-6 text-purple-400" />
                  </div>
                  <div className="text-2xl font-bold text-white">{activityStats.totalViews}</div>
                  <div className="text-gray-400 text-sm">조회수</div>
                </div>
              </div>
            </div>

            {/* 최근 활동 */}
            <div className="glass-dark p-6 rounded-2xl">
              <h2 className="text-2xl font-bold text-white mb-6 flex items-center">
                <Calendar className="w-6 h-6 mr-3 text-red-400" />
                최근 활동
              </h2>
              <div className="space-y-4">
                <div className="flex items-center space-x-4 p-4 bg-gray-800/50 rounded-xl">
                  <div className="w-10 h-10 bg-blue-500/20 rounded-full flex items-center justify-center">
                    <MessageSquare className="w-5 h-5 text-blue-400" />
                  </div>
                  <div className="flex-1">
                    <p className="text-white font-medium">새 게시글 작성</p>
                    <p className="text-gray-400 text-sm">"React Hooks 완벽 가이드" 게시글을 작성했습니다.</p>
                  </div>
                  <span className="text-gray-500 text-sm">2시간 전</span>
                </div>
                <div className="flex items-center space-x-4 p-4 bg-gray-800/50 rounded-xl">
                  <div className="w-10 h-10 bg-green-500/20 rounded-full flex items-center justify-center">
                    <MessageSquare className="w-5 h-5 text-green-400" />
                  </div>
                  <div className="flex-1">
                    <p className="text-white font-medium">댓글 작성</p>
                    <p className="text-gray-400 text-sm">"Spring Boot 시작하기" 게시글에 댓글을 달았습니다.</p>
                  </div>
                  <span className="text-gray-500 text-sm">1일 전</span>
                </div>
                <div className="flex items-center space-x-4 p-4 bg-gray-800/50 rounded-xl">
                  <div className="w-10 h-10 bg-red-500/20 rounded-full flex items-center justify-center">
                    <Heart className="w-5 h-5 text-red-400" />
                  </div>
                  <div className="flex-1">
                    <p className="text-white font-medium">좋아요</p>
                    <p className="text-gray-400 text-sm">"JPA 완벽 가이드" 게시글에 좋아요를 눌렀습니다.</p>
                  </div>
                  <span className="text-gray-500 text-sm">3일 전</span>
                </div>
              </div>
            </div>
          </div>

          {/* 설정 및 보안 */}
          <div className="space-y-6">
            {/* 비밀번호 변경 */}
            <div className="glass-dark p-6 rounded-2xl">
              <h3 className="text-xl font-bold text-white mb-4 flex items-center">
                <Shield className="w-5 h-5 mr-2 text-red-400" />
                보안 설정
              </h3>
              
              {isChangingPassword ? (
                <div className="space-y-4">
                  <div className="relative">
                    <input
                      type={showCurrentPassword ? "text" : "password"}
                      name="currentPassword"
                      value={passwordData.currentPassword}
                      onChange={handlePasswordChange}
                      placeholder="현재 비밀번호"
                      className="input-field w-full pr-10"
                    />
                    <button
                      type="button"
                      onClick={() => setShowCurrentPassword(!showCurrentPassword)}
                      className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-white"
                    >
                      {showCurrentPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                    </button>
                  </div>
                  
                  <div className="relative">
                    <input
                      type={showNewPassword ? "text" : "password"}
                      name="newPassword"
                      value={passwordData.newPassword}
                      onChange={handlePasswordChange}
                      placeholder="새 비밀번호"
                      className="input-field w-full pr-10"
                    />
                    <button
                      type="button"
                      onClick={() => setShowNewPassword(!showNewPassword)}
                      className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-white"
                    >
                      {showNewPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                    </button>
                  </div>
                  
                  <div className="relative">
                    <input
                      type={showConfirmPassword ? "text" : "password"}
                      name="confirmPassword"
                      value={passwordData.confirmPassword}
                      onChange={handlePasswordChange}
                      placeholder="새 비밀번호 확인"
                      className="input-field w-full pr-10"
                    />
                    <button
                      type="button"
                      onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                      className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-white"
                    >
                      {showConfirmPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                    </button>
                  </div>
                  
                  <div className="flex space-x-2">
                    <button
                      onClick={handlePasswordSave}
                      className="btn-primary flex-1"
                    >
                      변경
                    </button>
                    <button
                      onClick={() => setIsChangingPassword(false)}
                      className="btn-secondary"
                    >
                      취소
                    </button>
                  </div>
                </div>
              ) : (
                <button
                  onClick={() => setIsChangingPassword(true)}
                  className="btn-secondary w-full"
                >
                  비밀번호 변경
                </button>
              )}
            </div>

            {/* 계정 관리 */}
            <div className="glass-dark p-6 rounded-2xl">
              <h3 className="text-xl font-bold text-white mb-4 flex items-center">
                <Settings className="w-5 h-5 mr-2 text-red-400" />
                계정 관리
              </h3>
              <div className="space-y-3">
                <button className="w-full text-left p-3 bg-gray-800/50 hover:bg-gray-700/50 rounded-xl transition-colors duration-200 text-gray-300 hover:text-white">
                  알림 설정
                </button>
                <button className="w-full text-left p-3 bg-gray-800/50 hover:bg-gray-700/50 rounded-xl transition-colors duration-200 text-gray-300 hover:text-white">
                  개인정보 설정
                </button>
                <button className="w-full text-left p-3 bg-gray-800/50 hover:bg-gray-700/50 rounded-xl transition-colors duration-200 text-gray-300 hover:text-white">
                  테마 설정
                </button>
              </div>
            </div>

            {/* 로그아웃 */}
            <div className="glass-dark p-6 rounded-2xl">
              <button
                onClick={handleLogout}
                className="w-full btn-danger flex items-center justify-center space-x-2"
              >
                <LogOut className="w-4 h-4" />
                <span>로그아웃</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Profile;
