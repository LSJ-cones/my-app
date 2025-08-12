import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { 
  Calendar, 
  User, 
  MessageSquare, 
  Heart, 
  Eye, 
  Clock,
  TrendingUp,
  BookOpen,
  Code,
  Database,
  Cloud,
  Zap,
  Plus,
  Search
} from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { toast } from 'react-hot-toast';
import api from '../services/api';

const Home = () => {
  const { user } = useAuth();
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [searchKeyword, setSearchKeyword] = useState('');
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    fetchPosts();
  }, [currentPage, selectedCategory, searchQuery]);

  const fetchPosts = async () => {
    try {
      setLoading(true);
      let response;
      
      if (searchQuery.trim()) {
        // 검색 API 사용
        response = await api.get('/posts/search', {
          params: {
            keyword: searchQuery,
            page: currentPage,
            size: 10
          }
        });
      } else if (selectedCategory !== 'all') {
        // 카테고리별 조회 (카테고리명으로 필터링)
        response = await api.get('/posts', {
          params: {
            page: currentPage,
            size: 10,
            category: selectedCategory
          }
        });
      } else {
        // 전체 조회
        response = await api.get('/posts', {
          params: {
            page: currentPage,
            size: 10
          }
        });
      }
      
      setPosts(response.data.content || response.data);
      setTotalPages(response.data.totalPages || 0);
      setTotalElements(response.data.totalElements || 0);
    } catch (error) {
      console.error('게시글 목록 로드 실패:', error);
      toast.error('게시글 목록을 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    setSearchQuery(searchKeyword);
    setCurrentPage(0);
  };

  const handleCategoryChange = (categoryId) => {
    setSelectedCategory(categoryId);
    setCurrentPage(0);
    setSearchQuery('');
    setSearchKeyword('');
  };

  const handleClearSearch = () => {
    setSearchKeyword('');
    setSearchQuery('');
    setCurrentPage(0);
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  const getTimeAgo = (dateString) => {
    const now = new Date();
    const date = new Date(dateString);
    const diff = now - date;
    const minutes = Math.floor(diff / (1000 * 60));
    const hours = Math.floor(diff / (1000 * 60 * 60));
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));

    if (minutes < 60) return `${minutes}분 전`;
    if (hours < 24) return `${hours}시간 전`;
    if (days < 7) return `${days}일 전`;
    return formatDate(dateString);
  };

  const getCategoryIcon = (category) => {
    switch (category) {
      case 'JAVA':
        return <Code className="w-4 h-4" />;
      case 'SPRING':
        return <Database className="w-4 h-4" />;
      case 'JAVASCRIPT':
        return <Zap className="w-4 h-4" />;
      case 'REACT':
        return <Cloud className="w-4 h-4" />;
      default:
        return <BookOpen className="w-4 h-4" />;
    }
  };

  const getCategoryColor = (category) => {
    switch (category) {
      case 'JAVA':
        return 'bg-orange-500/20 text-orange-400 border-orange-500/30';
      case 'SPRING':
        return 'bg-green-500/20 text-green-400 border-green-500/30';
      case 'JAVASCRIPT':
        return 'bg-yellow-500/20 text-yellow-400 border-yellow-500/30';
      case 'REACT':
        return 'bg-blue-500/20 text-blue-400 border-blue-500/30';
      default:
        return 'bg-gray-500/20 text-gray-400 border-gray-500/30';
    }
  };

  const categories = [
    { id: 'all', name: '전체', icon: <BookOpen className="w-4 h-4" /> },
    { id: 'JAVA', name: 'Java', icon: <Code className="w-4 h-4" /> },
    { id: 'SPRING', name: 'Spring', icon: <Database className="w-4 h-4" /> },
    { id: 'JAVASCRIPT', name: 'JavaScript', icon: <Zap className="w-4 h-4" /> },
    { id: 'REACT', name: 'React', icon: <Cloud className="w-4 h-4" /> }
  ];

  // 페이지 번호 배열 생성
  const getPageNumbers = () => {
    const pages = [];
    const maxVisiblePages = 5;
    
    if (totalPages <= maxVisiblePages) {
      // 전체 페이지가 5개 이하면 모두 표시
      for (let i = 0; i < totalPages; i++) {
        pages.push(i);
      }
    } else {
      // 현재 페이지 주변의 페이지들 표시
      let start = Math.max(0, currentPage - 2);
      let end = Math.min(totalPages - 1, start + maxVisiblePages - 1);
      
      if (end - start < maxVisiblePages - 1) {
        start = Math.max(0, end - maxVisiblePages + 1);
      }
      
      for (let i = start; i <= end; i++) {
        pages.push(i);
      }
    }
    
    return pages;
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 flex items-center justify-center">
        <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-red-500"></div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900">
      {/* 히어로 섹션 */}
      <div className="relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-r from-red-900/20 to-red-800/20"></div>
        <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-24">
          <div className="text-center">
            <h1 className="text-4xl md:text-6xl font-bold text-white mb-6">
              <span className="bg-gradient-to-r from-red-400 to-red-600 bg-clip-text text-transparent">
                Lcones Blog
              </span>
            </h1>
            <p className="text-xl text-gray-300 mb-8 max-w-3xl mx-auto">
              기술의 세계를 탐험하는 개발자의 기록
            </p>
            <div className="flex justify-center space-x-4">
              <div className="flex items-center space-x-2 text-gray-400">
                <TrendingUp className="w-5 h-5" />
                <span>최신 기술 트렌드</span>
              </div>
              <div className="flex items-center space-x-2 text-gray-400">
                <Code className="w-5 h-5" />
                <span>실무 개발 경험</span>
              </div>
              <div className="flex items-center space-x-2 text-gray-400">
                <Database className="w-5 h-5" />
                <span>아키텍처 설계</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* 메인 콘텐츠 */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        {/* 검색 및 필터 */}
        <div className="mb-8 space-y-4">
          {/* 검색바 */}
          <form onSubmit={handleSearch} className="flex space-x-2">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
              <input
                type="text"
                value={searchKeyword}
                onChange={(e) => setSearchKeyword(e.target.value)}
                placeholder="게시글 검색..."
                className="w-full pl-10 pr-4 py-3 bg-gray-800/50 border border-gray-700 rounded-xl text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-red-500/50 focus:border-transparent"
              />
            </div>
            <button
              type="submit"
              className="px-6 py-3 bg-red-600 hover:bg-red-700 text-white rounded-xl transition-colors duration-200"
            >
              검색
            </button>
            {searchQuery && (
              <button
                type="button"
                onClick={handleClearSearch}
                className="px-6 py-3 bg-gray-600 hover:bg-gray-700 text-white rounded-xl transition-colors duration-200"
              >
                초기화
              </button>
            )}
          </form>

          {/* 검색 결과 표시 */}
          {searchQuery && (
            <div className="flex items-center space-x-2 text-gray-300">
              <Search className="w-4 h-4" />
              <span>검색 결과: "{searchQuery}"</span>
              <span className="text-gray-400">({totalElements}개)</span>
            </div>
          )}

          {/* 카테고리 필터 */}
          <div className="flex flex-wrap gap-2">
            {categories.map((category) => (
              <button
                key={category.id}
                onClick={() => handleCategoryChange(category.id)}
                className={`flex items-center space-x-2 px-4 py-2 rounded-lg border transition-all duration-200 ${
                  selectedCategory === category.id
                    ? 'bg-red-600 text-white border-red-600 shadow-lg'
                    : 'bg-gray-800 text-gray-300 border-gray-600 hover:bg-gray-700 hover:border-red-500'
                }`}
              >
                {category.icon}
                <span className="font-medium">{category.name}</span>
              </button>
            ))}
          </div>
        </div>

        {/* 게시글 목록 */}
        <div className="grid gap-6">
          {posts.length === 0 ? (
            <div className="text-center py-12">
              <BookOpen className="w-16 h-16 text-gray-500 mx-auto mb-4" />
              <h3 className="text-xl font-semibold text-gray-300 mb-2">
                {searchQuery ? '검색 결과가 없습니다' : '게시글이 없습니다'}
              </h3>
              <p className="text-gray-400">
                {searchQuery ? '다른 키워드로 검색해보세요.' : '첫 번째 게시글을 작성해보세요!'}
              </p>
            </div>
          ) : (
            posts.map((post) => (
              <article
                key={post.id}
                className="bg-gray-800/50 backdrop-blur-sm border border-gray-700 rounded-xl p-6 hover:bg-gray-800/70 transition-all duration-300 hover:shadow-xl hover:shadow-red-500/10"
              >
                <div className="flex items-start justify-between mb-4">
                  <div className="flex items-center space-x-3">
                    <span className={`inline-flex items-center space-x-1 px-3 py-1 rounded-full text-xs font-medium border ${getCategoryColor(post.category)}`}>
                      {getCategoryIcon(post.category)}
                      <span>{post.category}</span>
                    </span>
                    <span className="text-sm text-gray-400">
                      {getTimeAgo(post.createdAt)}
                    </span>
                  </div>
                  {user && post.authorId === user.id && (
                    <span className="text-xs text-red-400 bg-red-400/10 px-2 py-1 rounded-full">
                      내 글
                    </span>
                  )}
                </div>

                <Link to={`/posts/${post.id}`} className="block group">
                  <h2 className="text-xl font-bold text-white mb-3 group-hover:text-red-400 transition-colors duration-200">
                    {post.title}
                  </h2>
                  <p className="text-gray-300 mb-4 line-clamp-2">
                    {post.content.length > 150 
                      ? `${post.content.substring(0, 150)}...` 
                      : post.content
                    }
                  </p>
                </Link>

                <div className="flex items-center justify-between text-sm text-gray-400">
                  <div className="flex items-center space-x-4">
                    <div className="flex items-center space-x-1">
                      <User className="w-4 h-4" />
                      <span>{post.authorName || post.author || '익명'}</span>
                    </div>
                    <div className="flex items-center space-x-1">
                      <MessageSquare className="w-4 h-4" />
                      <span>{post.commentCount || 0}</span>
                    </div>
                    <div className="flex items-center space-x-1">
                      <Heart className="w-4 h-4" />
                      <span>{post.likeCount || 0}</span>
                    </div>
                    <div className="flex items-center space-x-1">
                      <Eye className="w-4 h-4" />
                      <span>{post.viewCount || 0}</span>
                    </div>
                  </div>
                  <div className="flex items-center space-x-1">
                    <Calendar className="w-4 h-4" />
                    <span>{formatDate(post.createdAt)}</span>
                  </div>
                </div>
              </article>
            ))
          )}
        </div>

        {/* 페이지네이션 */}
        {totalPages > 1 && (
          <div className="mt-8 flex justify-center">
            <div className="flex space-x-2">
              <button
                onClick={() => setCurrentPage(Math.max(0, currentPage - 1))}
                disabled={currentPage === 0}
                className="px-4 py-2 bg-gray-800 text-gray-300 rounded-lg hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
              >
                이전
              </button>
              
              {getPageNumbers().map((pageNum) => (
                <button
                  key={pageNum}
                  onClick={() => setCurrentPage(pageNum)}
                  className={`px-4 py-2 rounded-lg transition-colors duration-200 ${
                    currentPage === pageNum
                      ? 'bg-red-600 text-white'
                      : 'bg-gray-800 text-gray-300 hover:bg-gray-700'
                  }`}
                >
                  {pageNum + 1}
                </button>
              ))}
              
              <button
                onClick={() => setCurrentPage(Math.min(totalPages - 1, currentPage + 1))}
                disabled={currentPage === totalPages - 1}
                className="px-4 py-2 bg-gray-800 text-gray-300 rounded-lg hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
              >
                다음
              </button>
            </div>
          </div>
        )}

        {/* 글쓰기 버튼 */}
        {user && (
          <div className="fixed bottom-8 right-8">
            <Link
              to="/posts/create"
              className="bg-gradient-to-r from-red-600 to-red-700 text-white p-4 rounded-full shadow-lg hover:from-red-700 hover:to-red-800 transition-all duration-300 hover:scale-110"
            >
              <Plus className="w-6 h-6" />
            </Link>
          </div>
        )}
      </div>
    </div>
  );
};

export default Home;
