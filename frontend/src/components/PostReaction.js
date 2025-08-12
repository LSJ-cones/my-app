import React, { useState, useEffect } from 'react';
import { ThumbsUp, ThumbsDown } from 'lucide-react';
import { toast } from 'react-hot-toast';
import api from '../services/api';

const PostReaction = ({ postId }) => {
  const [reaction, setReaction] = useState(null);
  const [likeCount, setLikeCount] = useState(0);
  const [dislikeCount, setDislikeCount] = useState(0);
  const [loading, setLoading] = useState(false);

  const loadReaction = async () => {
    try {
      const response = await api.get(`/posts/${postId}/reactions`);
      const data = response.data;
      
      setLikeCount(data.likeCount || 0);
      setDislikeCount(data.dislikeCount || 0);
      setReaction(data.userReaction);
    } catch (error) {
      console.error('반응 로드 실패:', error);
    }
  };

  const handleReaction = async (type) => {
    if (loading) return;
    
    setLoading(true);
    try {
      const response = await api.post(`/posts/${postId}/reactions`, {
        type: type
      });
      
      const data = response.data;
      setLikeCount(data.likeCount || 0);
      setDislikeCount(data.dislikeCount || 0);
      setReaction(data.userReaction);
      
      toast.success(type === 'LIKE' ? '좋아요를 눌렀습니다!' : '싫어요를 눌렀습니다!');
    } catch (error) {
      console.error('반응 처리 실패:', error);
      toast.error('반응 처리에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadReaction();
  }, [postId]);

  return (
    <div className="flex items-center space-x-2">
      {/* 좋아요 버튼 */}
      <button
        onClick={() => handleReaction('LIKE')}
        disabled={loading}
        className={`flex items-center space-x-1 px-3 py-1.5 rounded-lg transition-colors duration-200 ${
          reaction === 'LIKE'
            ? 'bg-green-100 text-green-700 border border-green-200'
            : 'text-gray-500 hover:text-green-600 hover:bg-green-50'
        } ${loading ? 'opacity-50 cursor-not-allowed' : ''}`}
      >
        <ThumbsUp size={16} />
        <span className="text-sm font-medium">{likeCount}</span>
      </button>

      {/* 싫어요 버튼 */}
      <button
        onClick={() => handleReaction('DISLIKE')}
        disabled={loading}
        className={`flex items-center space-x-1 px-3 py-1.5 rounded-lg transition-colors duration-200 ${
          reaction === 'DISLIKE'
            ? 'bg-red-100 text-red-700 border border-red-200'
            : 'text-gray-500 hover:text-red-600 hover:bg-red-50'
        } ${loading ? 'opacity-50 cursor-not-allowed' : ''}`}
      >
        <ThumbsDown size={16} />
        <span className="text-sm font-medium">{dislikeCount}</span>
      </button>
    </div>
  );
};

export default PostReaction;
