import React, { useState, useEffect, Suspense } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { ArrowLeft, MessageSquare, Eye, ThumbsUp, ThumbsDown, Edit, Trash2, Reply, Send, User, Clock, File, Download, ExternalLink } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { toast } from 'react-hot-toast';
import api from '../services/api';

// 에러 바운더리 컴포넌트
class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null, errorInfo: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    console.error('PostDetail ErrorBoundary caught an error:', error, errorInfo);
    this.setState({ errorInfo });
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 flex items-center justify-center">
          <div className="glass-dark p-8 rounded-2xl text-center max-w-md">
            <h2 className="text-2xl font-bold text-white mb-4">오류가 발생했습니다</h2>
            <p className="text-gray-400 mb-4">페이지를 새로고침해주세요.</p>
            <div className="space-y-2">
              <button
                onClick={() => window.location.reload()}
                className="btn-primary w-full"
              >
                새로고침
              </button>
              <Link to="/" className="btn-secondary w-full block">
                홈으로 돌아가기
              </Link>
            </div>
            {process.env.NODE_ENV === 'development' && this.state.error && (
              <details className="mt-4 text-left">
                <summary className="text-red-400 cursor-pointer">오류 상세 정보</summary>
                <pre className="text-xs text-gray-400 mt-2 overflow-auto">
                  {this.state.error.toString()}
                  {this.state.errorInfo && this.state.errorInfo.componentStack}
                </pre>
              </details>
            )}
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}

// 안전한 컴포넌트 래퍼
const SafeComponent = ({ children, fallback = null }) => {
  try {
    return children;
  } catch (error) {
    console.error('SafeComponent caught error:', error);
    return fallback;
  }
};

// 안전한 렌더링을 위한 헬퍼 함수
const safeRender = (value, fallback = '') => {
  try {
    if (value === null || value === undefined) return fallback;
    if (typeof value === 'string') return value;
    if (typeof value === 'number') return value.toString();
    if (typeof value === 'boolean') return value.toString();
    if (typeof value === 'object') {
      // 객체인 경우 name 속성이 있으면 사용, 없으면 fallback
      if (value.name && typeof value.name === 'string') return value.name;
      if (value.title && typeof value.title === 'string') return value.title;
      if (value.id && (typeof value.id === 'string' || typeof value.id === 'number')) return value.id.toString();
      // 객체의 모든 속성을 문자열로 변환 시도
      const objStr = JSON.stringify(value);
      if (objStr && objStr !== '{}' && objStr !== '[]') {
        return objStr.length > 50 ? objStr.substring(0, 50) + '...' : objStr;
      }
      return fallback;
    }
    // 함수나 다른 타입의 경우 fallback 반환
    return fallback;
  } catch (error) {
    console.error('safeRender error:', error, 'value:', value);
    return fallback;
  }
};

// 안전한 텍스트 렌더링 컴포넌트
const SafeText = ({ value, fallback = '', className = '' }) => {
  try {
    const safeValue = safeRender(value, fallback);
    return <span className={className}>{safeValue}</span>;
  } catch (error) {
    console.error('SafeText error:', error);
    return <span className={className}>{fallback}</span>;
  }
};

const PostDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  
  // 모든 상태를 안전하게 초기화
  const [post, setPost] = useState(null);
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [commentContent, setCommentContent] = useState('');
  const [replyContent, setReplyContent] = useState('');
  const [replyingTo, setReplyingTo] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  // 디버깅을 위한 로그
  console.log('PostDetail render - id:', id, 'user:', user);

  useEffect(() => {
    console.log('PostDetail useEffect triggered with id:', id);
    
    const loadData = async () => {
      console.log('loadData called with id:', id);
      
      if (!id || isNaN(parseInt(id))) {
        console.error('Invalid post ID:', id);
        setError('잘못된 게시글 ID입니다.');
        setLoading(false);
        return;
      }

      setLoading(true);
      setError(null);
      
      try {
        console.log('Fetching post and comments for ID:', id);
        await Promise.all([fetchPost(), fetchComments()]);
      } catch (error) {
        console.error('Data load failed:', error);
        setError('데이터를 불러오는데 실패했습니다.');
      } finally {
        setLoading(false);
      }
    };
    
    loadData();
  }, [id]);

  const fetchPost = async () => {
    try {
      console.log('fetchPost called for ID:', id);
      const response = await api.get(`/posts/${id}`);
      console.log('Post API response:', response.data);
      
      // 응답 데이터의 모든 속성을 자세히 로깅
      if (response.data) {
        console.log('Post data keys:', Object.keys(response.data));
        console.log('Post data values:', response.data);
        
        // 문제가 될 수 있는 속성들 확인
        const problematicKeys = ['id', 'name', 'disp', 'display', 'type', 'value'];
        problematicKeys.forEach(key => {
          if (key in response.data) {
            console.log(`Found potentially problematic key '${key}':`, response.data[key]);
          }
        });
      }
      
      // 응답 데이터 검증 및 정제
      if (!response.data) {
        throw new Error('No data received from API');
      }
      
      if (typeof response.data !== 'object') {
        throw new Error('Invalid data type received');
      }
      
      if (!response.data.id) {
        throw new Error('Post data missing ID');
      }
      
      // 데이터 정제 - 문제가 될 수 있는 속성들 제거
      const cleanPostData = { ...response.data };
      
      // React에서 문제가 될 수 있는 속성들 제거
      const dangerousKeys = ['disp', 'display', 'type', 'value'];
      dangerousKeys.forEach(key => {
        if (key in cleanPostData) {
          console.log(`Removing potentially dangerous key '${key}':`, cleanPostData[key]);
          delete cleanPostData[key];
        }
      });
      
      // null/undefined 값들을 안전한 기본값으로 대체
      const safePostData = {
        id: cleanPostData.id || 0,
        title: cleanPostData.title || '',
        content: cleanPostData.content || '',
        author: cleanPostData.author || '',
        authorId: cleanPostData.authorId || 0,
        category: cleanPostData.category || null,
        createdAt: cleanPostData.createdAt || null,
        updatedAt: cleanPostData.updatedAt || null,
        viewCount: cleanPostData.viewCount || 0,
        likeCount: cleanPostData.likeCount || 0,
        dislikeCount: cleanPostData.dislikeCount || 0,
        files: Array.isArray(cleanPostData.files) ? cleanPostData.files : [],
        tags: Array.isArray(cleanPostData.tags) ? cleanPostData.tags : []
      };
      
      console.log('Setting safe post data:', safePostData);
      setPost(safePostData);
    } catch (error) {
      console.error('fetchPost error:', error);
      if (error.response?.status === 404) {
        setError('게시글을 찾을 수 없습니다.');
      } else {
        setError('게시글을 불러오는데 실패했습니다.');
      }
      throw error;
    }
  };

  const fetchComments = async () => {
    try {
      console.log('fetchComments called for ID:', id);
      const response = await api.get(`/comments/post/${id}/all`);
      console.log('Comments API response:', response.data);
      
      // 댓글 데이터의 구조 확인
      if (Array.isArray(response.data)) {
        console.log('Comments array length:', response.data.length);
        if (response.data.length > 0) {
          console.log('First comment structure:', response.data[0]);
          console.log('First comment keys:', Object.keys(response.data[0]));
        }
        
        // 댓글 데이터 정제
        const safeComments = response.data
          .filter(comment => comment && typeof comment === 'object')
          .map(comment => {
            // 문제가 될 수 있는 속성들 제거
            const cleanComment = { ...comment };
            const dangerousKeys = ['disp', 'display', 'type', 'value'];
            dangerousKeys.forEach(key => {
              if (key in cleanComment) {
                console.log(`Removing dangerous key '${key}' from comment:`, cleanComment[key]);
                delete cleanComment[key];
              }
            });
            
            // 안전한 기본값으로 대체
            return {
              id: cleanComment.id || 0,
              content: cleanComment.content || '',
              author: cleanComment.author || '',
              authorId: cleanComment.authorId || 0,
              createdAt: cleanComment.createdAt || null,
              updatedAt: cleanComment.updatedAt || null,
              likeCount: cleanComment.likeCount || 0,
              dislikeCount: cleanComment.dislikeCount || 0,
              replies: Array.isArray(cleanComment.replies) ? cleanComment.replies : []
            };
          });
        
        console.log('Setting safe comments data:', safeComments);
        setComments(safeComments);
      } else {
        console.error('Invalid comments data:', response.data);
        setComments([]);
      }
    } catch (error) {
      console.error('fetchComments error:', error);
      setComments([]);
    }
  };

  const handleReaction = async (type) => {
    if (!user) {
      toast.error('로그인이 필요합니다.');
      return;
    }

    try {
      const response = await api.post(`/posts/${id}/reaction`, { 
        postId: parseInt(id),
        type: type 
      });
      setPost(prev => ({
        ...prev,
        likeCount: response.data.likeCount,
        dislikeCount: response.data.dislikeCount
      }));
      toast.success(type === 'LIKE' ? '좋아요를 눌렀습니다!' : '싫어요를 눌렀습니다!');
    } catch (error) {
      console.error('반응 처리 실패:', error);
      if (error.response?.data?.message) {
        toast.error(error.response.data.message);
      } else {
        toast.error('반응 처리에 실패했습니다.');
      }
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

  const handleReplySubmit = async (e, parentCommentId, mentionUsername = null) => {
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
        parentId: parentCommentId,
        mentionUsername: mentionUsername
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
    if (!dateString) return '';
    try {
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
    } catch (error) {
      console.error('Date formatting error:', error);
      return '';
    }
  };

  const formatFileSize = (bytes) => {
    if (!bytes || bytes === 0) return '0 Bytes';
    try {
      const k = 1024;
      const sizes = ['Bytes', 'KB', 'MB', 'GB'];
      const i = Math.floor(Math.log(bytes) / Math.log(k));
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    } catch (error) {
      console.error('File size formatting error:', error);
      return '0 Bytes';
    }
  };

  const handleFileDownload = (file) => {
    try {
      const link = document.createElement('a');
      link.href = file.url;
      link.download = file.name;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    } catch (error) {
      console.error('File download error:', error);
      toast.error('파일 다운로드에 실패했습니다.');
    }
  };

  const renderComment = (comment, isReply = false) => {
    if (!comment || !comment.id) {
      console.error('Invalid comment data:', comment);
      return null;
    }
    
    return (
      <SafeComponent key={comment.id} fallback={<div>댓글을 불러올 수 없습니다.</div>}>
        <div className={`${isReply ? 'ml-8' : ''} mb-4`}>
          <div className="glass-dark p-4 rounded-xl">
            <div className="flex items-start justify-between mb-3">
              <div className="flex items-center space-x-3">
                <div className="w-8 h-8 bg-red-500/20 rounded-full flex items-center justify-center">
                  <User className="w-4 h-4 text-red-400" />
                </div>
                <div>
                  <div className="font-medium text-white">
                    <SafeText value={comment.author} fallback="익명" />
                  </div>
                  <div className="flex items-center space-x-2 text-gray-400 text-sm">
                    <Clock className="w-3 h-3" />
                    <span>{formatDate(comment.createdAt)}</span>
                  </div>
                </div>
              </div>
              
              {user && (safeRender(comment.author) === user.username || user.role === 'ADMIN') && (
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
              <SafeText value={comment.content} fallback="" />
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
                <div className="flex items-center space-x-2">
                  <button
                    onClick={() => setReplyingTo(replyingTo === comment.id ? null : comment.id)}
                    className="flex items-center space-x-1 text-gray-400 hover:text-blue-400 transition-colors duration-200"
                  >
                    <Reply className="w-4 h-4" />
                    <span className="text-sm">답글</span>
                  </button>
                  <button
                    onClick={() => {
                      setReplyingTo(comment.id);
                      setReplyContent(`@${safeRender(comment.author, '사용자')} `);
                    }}
                    className="flex items-center space-x-1 text-gray-400 hover:text-green-400 transition-colors duration-200"
                    title={`${safeRender(comment.author, '사용자')}님에게 답글`}
                  >
                    <Reply className="w-4 h-4" />
                    <span className="text-sm">멘션</span>
                  </button>
                </div>
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
            {comment.replies && Array.isArray(comment.replies) && comment.replies.length > 0 && (
              <div className="mt-4 space-y-3">
                {comment.replies.filter(reply => reply && reply.id).map(reply => renderComment(reply, true))}
              </div>
            )}
          </div>
        </div>
      </SafeComponent>
    );
  };

  // 로딩 상태
  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-red-400"></div>
      </div>
    );
  }

  // 오류 상태
  if (error) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 flex items-center justify-center">
        <div className="glass-dark p-8 rounded-2xl text-center">
          <h2 className="text-2xl font-bold text-white mb-4">오류</h2>
          <p className="text-gray-400 mb-4">{error}</p>
          <p className="text-gray-400 mb-4">게시글 ID: {id}</p>
          <Link to="/" className="btn-primary">
            홈으로 돌아가기
          </Link>
        </div>
      </div>
    );
  }

  // 게시글이 없는 상태
  if (!post || !post.id) {
    console.log('Post not found or invalid - post:', post, 'id:', id);
    return (
      <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 flex items-center justify-center">
        <div className="glass-dark p-8 rounded-2xl text-center">
          <h2 className="text-2xl font-bold text-white mb-4">게시글을 찾을 수 없습니다</h2>
          <p className="text-gray-400 mb-4">게시글 ID: {id}</p>
          <Link to="/" className="btn-primary">
            홈으로 돌아가기
          </Link>
        </div>
      </div>
    );
  }

  // 메인 렌더링
  return (
    <ErrorBoundary>
      <Suspense fallback={
        <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 flex items-center justify-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-red-400"></div>
        </div>
      }>
        <SafeComponent fallback={
          <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 flex items-center justify-center">
            <div className="glass-dark p-8 rounded-2xl text-center">
              <h2 className="text-2xl font-bold text-white mb-4">렌더링 오류</h2>
              <p className="text-gray-400 mb-4">페이지를 새로고침해주세요.</p>
              <button onClick={() => window.location.reload()} className="btn-primary">
                새로고침
              </button>
            </div>
          </div>
        }>
          {(() => {
            try {
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
                              <SafeText value={post?.category} fallback="기타" />
                            </span>
                            <span className="text-gray-400 text-sm">
                              {formatDate(post?.createdAt)}
                            </span>
                          </div>
                          
                          <h1 className="text-3xl font-bold text-white mb-4">
                            <SafeText value={post?.title} fallback="제목 없음" />
                          </h1>
                          
                          <div className="flex items-center space-x-6 text-gray-400 mb-6">
                            <div className="flex items-center space-x-2">
                              <User className="w-4 h-4" />
                              <SafeText value={post?.author} fallback="익명" />
                            </div>
                            <div className="flex items-center space-x-2">
                              <Eye className="w-4 h-4" />
                              <span>{post?.viewCount || 0}</span>
                            </div>
                            <div className="flex items-center space-x-2">
                              <MessageSquare className="w-4 h-4" />
                              <span>{Array.isArray(comments) ? comments.length : 0}</span>
                            </div>
                          </div>
                        </div>
                        
                        {user && (safeRender(post?.author) === user.username || post?.authorId === user.id || user.role === 'ADMIN') && (
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
                          <SafeText value={post?.content} fallback="내용이 없습니다." />
                        </div>
                      </div>

                      {/* 첨부 파일 */}
                      {post?.files && Array.isArray(post.files) && post.files.length > 0 && (
                        <div className="mb-8 p-4 bg-gray-800/50 rounded-xl">
                          <h3 className="text-lg font-semibold text-white mb-4 flex items-center">
                            <File className="w-5 h-5 mr-2 text-red-400" />
                            첨부 파일 ({post.files.length}개)
                          </h3>
                          <div className="space-y-2">
                            {post.files.map((file) => (
                              <div
                                key={file?.id || Math.random()}
                                className="flex items-center justify-between p-3 bg-gray-700/50 rounded-lg"
                              >
                                <div className="flex items-center space-x-3">
                                  <File className="w-5 h-5 text-gray-400" />
                                  <div>
                                    <p className="text-white text-sm">
                                      <SafeText value={file?.name} fallback="알 수 없는 파일" />
                                    </p>
                                    <p className="text-gray-400 text-xs">{formatFileSize(file?.size)}</p>
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
                                    href={file?.url}
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
                          <span>{post?.likeCount || 0}</span>
                        </button>
                        <button
                          onClick={() => handleReaction('DISLIKE')}
                          className="flex items-center space-x-2 px-4 py-2 bg-gray-800/50 hover:bg-gray-700/50 rounded-xl transition-colors duration-200 text-gray-300 hover:text-blue-400"
                        >
                          <ThumbsDown className="w-5 h-5" />
                          <span>{post?.dislikeCount || 0}</span>
                        </button>
                      </div>
                    </div>

                    {/* 댓글 섹션 */}
                    <div className="glass-dark p-6 rounded-2xl">
                      <h2 className="text-2xl font-bold text-white mb-6 flex items-center">
                        <MessageSquare className="w-6 h-6 mr-3 text-red-400" />
                        댓글 ({Array.isArray(comments) ? comments.length : 0})
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
                        {!Array.isArray(comments) || comments.length === 0 ? (
                          <div className="text-center py-8">
                            <MessageSquare className="w-12 h-12 text-gray-500 mx-auto mb-3" />
                            <p className="text-gray-400">아직 댓글이 없습니다. 첫 번째 댓글을 작성해보세요!</p>
                          </div>
                        ) : (
                          comments.filter(comment => comment && comment.id).map(comment => renderComment(comment))
                        )}
                      </div>
                    </div>
                  </div>
                </div>
              );
            } catch (error) {
              console.error('Main render error:', error);
              return (
                <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 flex items-center justify-center">
                  <div className="glass-dark p-8 rounded-2xl text-center">
                    <h2 className="text-2xl font-bold text-white mb-4">렌더링 오류</h2>
                    <p className="text-gray-400 mb-4">페이지를 새로고침해주세요.</p>
                    <button onClick={() => window.location.reload()} className="btn-primary">
                      새로고침
                    </button>
                  </div>
                </div>
              );
            }
          })()}
        </SafeComponent>
      </Suspense>
    </ErrorBoundary>
  );
};

export default PostDetail;
