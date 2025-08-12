import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { ArrowLeft, Heart, MessageSquare, Eye, ThumbsUp, ThumbsDown, Edit, Trash2, Reply, Send, User, Clock, MoreVertical, File, Download, ExternalLink } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { toast } from 'react-hot-toast';
import api from '../services/api';

const PostDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [post, setPost] = useState(null);
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [commentContent, setCommentContent] = useState('');
  const [replyContent, setReplyContent] = useState('');
  const [replyingTo, setReplyingTo] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    fetchPost();
    fetchComments();
  }, [id]);

  const fetchPost = async () => {
    try {
      const response = await api.get(`/posts/${id}`);
      setPost(response.data);
    } catch (error) {
      console.error('게시글 로드 실패:', error);
      toast.error('게시글을 불러오는데 실패했습니다.');
      setLoading(false);
    }
  };

  const fetchComments = async () => {
    try {
      const response = await api.get(`/comments/post/${id}/all`);
      setComments(response.data);
    } catch (error) {
      console.error('댓글 로드 실패:', error);
      toast.error('댓글을 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleReaction = async (type) => {
    if (!user) {
      toast.error('로그인이 필요합니다.');
      return;
    }

    try {
      const response = await api.post(`/posts/${id}/reaction`, { type });
      setPost(prev => ({
        ...prev,
        likeCount: response.data.likeCount,
        dislikeCount: response.data.dislikeCount
      }));
      toast.success(type === 'LIKE' ? '좋아요를 눌렀습니다!' : '싫어요를 눌렀습니다!');
    } catch (error) {
      console.error('반응 처리 실패:', error);
      toast.error('반응 처리에 실패했습니다.');
    }
  };

  const handleCommentSubmit = async (e) => {
    e.preventDefault();
    if (!user) {
      toast.error('로그인이 필요합니다.');
      return;
    }

    if (!commentContent.trim()) {
      toast.error('댓글 내용을 입력해주세요.');
      return;
    }

    setSubmitting(true);
    try {
      const commentData = {
        content: commentContent,
        postId: parseInt(id)
      };

      await api.post('/comments', commentData);
      setCommentContent('');
      await fetchComments();
      toast.success('댓글이 작성되었습니다!');
    } catch (error) {
      console.error('댓글 작성 실패:', error);
      toast.error('댓글 작성에 실패했습니다.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleReplySubmit = async (e, parentCommentId) => {
    e.preventDefault();
    if (!user) {
      toast.error('로그인이 필요합니다.');
      return;
    }

    if (!replyContent.trim()) {
      toast.error('답글 내용을 입력해주세요.');
      return;
    }

    setSubmitting(true);
    try {
      const replyData = {
        content: replyContent,
        postId: parseInt(id),
        parentId: parentCommentId
      };

      await api.post('/comments', replyData);
      setReplyContent('');
      setReplyingTo(null);
      await fetchComments();
      toast.success('답글이 작성되었습니다!');
    } catch (error) {
      console.error('답글 작성 실패:', error);
      toast.error('답글 작성에 실패했습니다.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteComment = async (commentId) => {
    if (!window.confirm('정말로 이 댓글을 삭제하시겠습니까?')) {
      return;
    }

    try {
      await api.delete(`/comments/${commentId}`);
      await fetchComments();
      toast.success('댓글이 삭제되었습니다.');
    } catch (error) {
      console.error('댓글 삭제 실패:', error);
      toast.error('댓글 삭제에 실패했습니다.');
    }
  };

  const handleDeletePost = async () => {
    if (!window.confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
      return;
    }

    try {
      await api.delete(`/posts/${id}`);
      toast.success('게시글이 삭제되었습니다.');
      navigate('/');
    } catch (error) {
      console.error('게시글 삭제 실패:', error);
      toast.error('게시글 삭제에 실패했습니다.');
    }
  };

  const formatDate = (dateString) => {
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

  const formatFileSize = (bytes) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  const handleFileDownload = (file) => {
    const link = document.createElement('a');
    link.href = file.url;
    link.download = file.name;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const renderComment = (comment, isReply = false) => (
    <div key={comment.id} className={`${isReply ? 'ml-8' : ''} mb-4`}>
      <div className="glass-dark p-4 rounded-xl">
        <div className="flex items-start justify-between mb-3">
          <div className="flex items-center space-x-3">
            <div className="w-8 h-8 bg-red-500/20 rounded-full flex items-center justify-center">
              <User className="w-4 h-4 text-red-400" />
            </div>
            <div>
              <div className="font-medium text-white">{comment.author || '익명'}</div>
              <div className="flex items-center space-x-2 text-gray-400 text-sm">
                <Clock className="w-3 h-3" />
                <span>{formatDate(comment.createdAt)}</span>
              </div>
            </div>
          </div>
          
          {user && (comment.author === user.username || user.role === 'ADMIN') && (
            <div className="flex items-center space-x-2">
              <button
                onClick={() => handleDeleteComment(comment.id)}
                className="p-1 text-gray-400 hover:text-red-400 transition-colors duration-200"
                title="삭제"
              >
                <Trash2 className="w-4 h-4" />
              </button>
            </div>
          )}
        </div>
        
        <div className="text-gray-300 mb-3">
          {comment.content}
        </div>
        
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-4">
            <button className="flex items-center space-x-1 text-gray-400 hover:text-red-400 transition-colors duration-200">
              <ThumbsUp className="w-4 h-4" />
              <span className="text-sm">{comment.likeCount || 0}</span>
            </button>
            <button className="flex items-center space-x-1 text-gray-400 hover:text-blue-400 transition-colors duration-200">
              <ThumbsDown className="w-4 h-4" />
              <span className="text-sm">{comment.dislikeCount || 0}</span>
            </button>
          </div>
          
          {!isReply && (
            <button
              onClick={() => setReplyingTo(replyingTo === comment.id ? null : comment.id)}
              className="flex items-center space-x-1 text-gray-400 hover:text-blue-400 transition-colors duration-200"
            >
              <Reply className="w-4 h-4" />
              <span className="text-sm">답글</span>
            </button>
          )}
        </div>
        
        {/* 답글 작성 폼 */}
        {replyingTo === comment.id && (
          <form onSubmit={(e) => handleReplySubmit(e, comment.id)} className="mt-4">
            <div className="flex space-x-2">
              <input
                type="text"
                value={replyContent}
                onChange={(e) => setReplyContent(e.target.value)}
                placeholder="답글을 입력하세요..."
                className="flex-1 input-field"
                disabled={submitting}
              />
              <button
                type="submit"
                disabled={submitting || !replyContent.trim()}
                className="btn-primary px-4 py-2 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <Send className="w-4 h-4" />
              </button>
            </div>
          </form>
        )}
        
        {/* 답글들 */}
        {comment.replies && comment.replies.length > 0 && (
          <div className="mt-4 space-y-3">
            {comment.replies.map(reply => renderComment(reply, true))}
          </div>
        )}
      </div>
    </div>
  );

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-red-400"></div>
      </div>
    );
  }

  if (!post) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 flex items-center justify-center">
        <div className="glass-dark p-8 rounded-2xl text-center">
          <h2 className="text-2xl font-bold text-white mb-4">게시글을 찾을 수 없습니다</h2>
          <Link to="/" className="btn-primary">
            홈으로 돌아가기
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 py-8">
      <div className="max-w-4xl mx-auto px-4">
        {/* 뒤로가기 버튼 */}
        <div className="mb-6">
          <button
            onClick={() => navigate(-1)}
            className="flex items-center space-x-2 text-gray-300 hover:text-white transition-colors duration-200"
          >
            <ArrowLeft className="w-5 h-5" />
            <span>뒤로가기</span>
          </button>
        </div>

        {/* 게시글 내용 */}
        <div className="glass-dark p-8 rounded-2xl mb-8">
          <div className="flex items-start justify-between mb-6">
            <div className="flex-1">
              <div className="flex items-center space-x-3 mb-4">
                <span className="px-3 py-1 bg-red-500/20 text-red-400 rounded-full text-sm font-medium">
                  {post.category}
                </span>
                <span className="text-gray-400 text-sm">
                  {formatDate(post.createdAt)}
                </span>
              </div>
              
              <h1 className="text-3xl font-bold text-white mb-4">{post.title}</h1>
              
              <div className="flex items-center space-x-6 text-gray-400 mb-6">
                <div className="flex items-center space-x-2">
                  <User className="w-4 h-4" />
                  <span>{post.author || '익명'}</span>
                </div>
                <div className="flex items-center space-x-2">
                  <Eye className="w-4 h-4" />
                  <span>{post.viewCount || 0}</span>
                </div>
                <div className="flex items-center space-x-2">
                  <MessageSquare className="w-4 h-4" />
                  <span>{comments.length}</span>
                </div>
              </div>
            </div>
            
            {user && (post.author === user.username || user.role === 'ADMIN') && (
              <div className="flex items-center space-x-2">
                <Link
                  to={`/posts/${id}/edit`}
                  className="p-2 text-gray-400 hover:text-blue-400 transition-colors duration-200"
                  title="수정"
                >
                  <Edit className="w-5 h-5" />
                </Link>
                <button
                  onClick={handleDeletePost}
                  className="p-2 text-gray-400 hover:text-red-400 transition-colors duration-200"
                  title="삭제"
                >
                  <Trash2 className="w-5 h-5" />
                </button>
              </div>
            )}
          </div>
          
          <div className="prose prose-invert max-w-none mb-8">
            <div className="text-gray-300 leading-relaxed whitespace-pre-wrap">
              {post.content}
            </div>
          </div>

          {/* 첨부 파일 */}
          {post.files && post.files.length > 0 && (
            <div className="mb-8 p-4 bg-gray-800/50 rounded-xl">
              <h3 className="text-lg font-semibold text-white mb-4 flex items-center">
                <File className="w-5 h-5 mr-2 text-red-400" />
                첨부 파일 ({post.files.length}개)
              </h3>
              <div className="space-y-2">
                {post.files.map((file) => (
                  <div
                    key={file.id}
                    className="flex items-center justify-between p-3 bg-gray-700/50 rounded-lg"
                  >
                    <div className="flex items-center space-x-3">
                      <File className="w-5 h-5 text-gray-400" />
                      <div>
                        <p className="text-white text-sm">{file.name}</p>
                        <p className="text-gray-400 text-xs">{formatFileSize(file.size)}</p>
                      </div>
                    </div>
                    <div className="flex items-center space-x-2">
                      <button
                        onClick={() => handleFileDownload(file)}
                        className="p-2 text-gray-400 hover:text-blue-400 transition-colors duration-200"
                        title="다운로드"
                      >
                        <Download className="w-4 h-4" />
                      </button>
                      <a
                        href={file.url}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="p-2 text-gray-400 hover:text-green-400 transition-colors duration-200"
                        title="새 창에서 열기"
                      >
                        <ExternalLink className="w-4 h-4" />
                      </a>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
          
          {/* 반응 버튼 */}
          <div className="flex items-center space-x-4 pt-6 border-t border-gray-700">
            <button
              onClick={() => handleReaction('LIKE')}
              className="flex items-center space-x-2 px-4 py-2 bg-gray-800/50 hover:bg-gray-700/50 rounded-xl transition-colors duration-200 text-gray-300 hover:text-red-400"
            >
              <ThumbsUp className="w-5 h-5" />
              <span>{post.likeCount || 0}</span>
            </button>
            <button
              onClick={() => handleReaction('DISLIKE')}
              className="flex items-center space-x-2 px-4 py-2 bg-gray-800/50 hover:bg-gray-700/50 rounded-xl transition-colors duration-200 text-gray-300 hover:text-blue-400"
            >
              <ThumbsDown className="w-5 h-5" />
              <span>{post.dislikeCount || 0}</span>
            </button>
          </div>
        </div>

        {/* 댓글 섹션 */}
        <div className="glass-dark p-6 rounded-2xl">
          <h2 className="text-2xl font-bold text-white mb-6 flex items-center">
            <MessageSquare className="w-6 h-6 mr-3 text-red-400" />
            댓글 ({comments.length})
          </h2>
          
          {/* 댓글 작성 폼 */}
          {user ? (
            <form onSubmit={handleCommentSubmit} className="mb-8">
              <div className="flex space-x-3">
                <div className="w-10 h-10 bg-red-500/20 rounded-full flex items-center justify-center flex-shrink-0">
                  <User className="w-5 h-5 text-red-400" />
                </div>
                <div className="flex-1">
                  <textarea
                    value={commentContent}
                    onChange={(e) => setCommentContent(e.target.value)}
                    placeholder="댓글을 입력하세요..."
                    rows="3"
                    className="w-full input-field resize-none"
                    disabled={submitting}
                  />
                  <div className="flex justify-between items-center mt-2">
                    <span className="text-gray-400 text-sm">
                      {commentContent.length}/500자
                    </span>
                    <button
                      type="submit"
                      disabled={submitting || !commentContent.trim()}
                      className="btn-primary px-6 py-2 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      {submitting ? '작성 중...' : '댓글 작성'}
                    </button>
                  </div>
                </div>
              </div>
            </form>
          ) : (
            <div className="mb-8 p-4 bg-gray-800/50 rounded-xl text-center">
              <p className="text-gray-300 mb-3">댓글을 작성하려면 로그인이 필요합니다.</p>
              <Link to="/login" className="btn-primary">
                로그인하기
              </Link>
            </div>
          )}
          
          {/* 댓글 목록 */}
          <div className="space-y-4">
            {comments.length === 0 ? (
              <div className="text-center py-8">
                <MessageSquare className="w-12 h-12 text-gray-500 mx-auto mb-3" />
                <p className="text-gray-400">아직 댓글이 없습니다. 첫 번째 댓글을 작성해보세요!</p>
              </div>
            ) : (
              comments.map(comment => renderComment(comment))
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default PostDetail;
