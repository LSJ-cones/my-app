import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { 
  Heart, 
  MessageCircle, 
  Eye, 
  Clock,
  User,
  Calendar
} from 'lucide-react';
import { toast } from 'react-hot-toast';
import api from '../services/api';
import PostReaction from './PostReaction';

const PostList = () => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const pageSize = 10;

  const loadPosts = async (page = 0) => {
    try {
      setLoading(true);
      const response = await api.get(`/posts?page=${page}&size=${pageSize}`);
      setPosts(response.data.content);
      setTotalPages(response.data.totalPages);
      setTotalElements(response.data.totalElements);
      setCurrentPage(page);
      setError(null);
    } catch (error) {
      console.error('게시글 목록 로드 실패:', error);
      setError('게시글 목록을 불러오는데 실패했습니다.');
      toast.error('게시글 목록을 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPosts();
  }, []);

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  const formatTimeAgo = (dateString) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffInSeconds = Math.floor((now - date) / 1000);

    if (diffInSeconds < 60) return '방금 전';
    if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)}분 전`;
    if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)}시간 전`;
    if (diffInSeconds < 2592000) return `${Math.floor(diffInSeconds / 86400)}일 전`;
    return formatDate(dateString);
  };

  const truncateText = (text, maxLength = 150) => {
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
  };

  if (loading && posts.length === 0) {
    return (
      <div className="flex justify-center items-center min-h-[400px]">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  if (error && posts.length === 0) {
    return (
      <div className="text-center py-12">
        <div className="text-red-600 text-lg font-medium mb-4">{error}</div>
        <button
          onClick={() => loadPosts()}
          className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
        >
          다시 시도
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* 게시글 목록 */}
      <div className="grid gap-6">
        {posts.map((post) => (
          <div key={post.id} className="card hover:shadow-lg transition-shadow duration-200">
            <div className="p-6">
              <div className="flex items-start justify-between mb-4">
                <div className="flex-1">
                  <Link 
                    to={`/posts/${post.id}`}
                    className="block group"
                  >
                    <h2 className="text-xl font-bold text-gray-900 group-hover:text-primary-600 transition-colors mb-2 line-clamp-2">
                      {post.title}
                    </h2>
                    <p className="text-gray-600 mb-4 line-clamp-2">
                      {truncateText(post.content)}
                    </p>
                  </Link>
                  
                  <div className="flex items-center justify-between">
                    <div className="flex items-center space-x-4 text-sm text-gray-500">
                      <div className="flex items-center space-x-1">
                        <User size={16} />
                        <span>{post.author?.username || '익명'}</span>
                      </div>
                      <div className="flex items-center space-x-1">
                        <Calendar size={16} />
                        <span>{formatTimeAgo(post.createdAt)}</span>
                      </div>
                      <div className="flex items-center space-x-1">
                        <Eye size={16} />
                        <span>{post.viewCount || 0}</span>
                      </div>
                    </div>
                    
                    <div className="flex items-center space-x-4">
                      <PostReaction postId={post.id} />
                      <div className="flex items-center space-x-1 text-gray-500">
                        <MessageCircle size={16} />
                        <span>{post.commentCount || 0}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* 페이지네이션 */}
      {totalPages > 1 && (
        <div className="flex justify-center items-center space-x-2 mt-8">
          <button
            onClick={() => loadPosts(currentPage - 1)}
            disabled={currentPage === 0}
            className="px-3 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            이전
          </button>
          
          {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
            const pageNum = Math.max(0, Math.min(totalPages - 1, currentPage - 2 + i));
            return (
              <button
                key={pageNum}
                onClick={() => loadPosts(pageNum)}
                className={`px-3 py-2 text-sm font-medium rounded-lg transition-colors ${
                  currentPage === pageNum
                    ? 'bg-primary-600 text-white'
                    : 'text-gray-500 bg-white border border-gray-300 hover:bg-gray-50'
                }`}
              >
                {pageNum + 1}
              </button>
            );
          })}
          
          <button
            onClick={() => loadPosts(currentPage + 1)}
            disabled={currentPage === totalPages - 1}
            className="px-3 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            다음
          </button>
        </div>
      )}

      {/* 게시글이 없을 때 */}
      {posts.length === 0 && !loading && (
        <div className="text-center py-12">
          <div className="text-gray-500 text-lg mb-4">게시글이 없습니다.</div>
          <Link
            to="/posts/create"
            className="inline-flex items-center space-x-2 px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
          >
            <span>첫 번째 게시글 작성하기</span>
          </Link>
        </div>
      )}
    </div>
  );
};

export default PostList;
