import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { 
  ArrowLeft, 
  Save, 
  X, 
  FileText, 
  Code, 
  Database, 
  Zap, 
  Cloud,
  BookOpen
} from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { toast } from 'react-hot-toast';
import api from '../services/api';
import ReactQuill from 'react-quill';
import '../quill-styles.css';

const EditPost = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [post, setPost] = useState(null);
  const [formData, setFormData] = useState({
    title: '',
    content: '',
    category: 'JAVA'
  });
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  // Quill 에디터 설정
  const quillModules = {
    toolbar: [
      [{ 'header': [1, 2, 3, false] }],
      ['bold', 'italic', 'underline', 'strike'],
      [{ 'list': 'ordered'}, { 'list': 'bullet' }],
      [{ 'color': [] }, { 'background': [] }],
      [{ 'align': [] }],
      ['link', 'image', 'code-block'],
      ['clean']
    ],
  };

  const quillFormats = [
    'header',
    'bold', 'italic', 'underline', 'strike',
    'list', 'bullet',
    'color', 'background',
    'align',
    'link', 'image', 'code-block'
  ];

  const categories = [
    { id: 'JAVA', name: 'Java', icon: <Code className="w-4 h-4" /> },
    { id: 'SPRING', name: 'Spring', icon: <Database className="w-4 h-4" /> },
    { id: 'JAVASCRIPT', name: 'JavaScript', icon: <Zap className="w-4 h-4" /> },
    { id: 'REACT', name: 'React', icon: <Cloud className="w-4 h-4" /> }
  ];

  useEffect(() => {
    fetchPost();
  }, [id]);

  const fetchPost = async () => {
    try {
      const response = await api.get(`/posts/${id}`);
      const postData = response.data;
      
      // 작성자가 아니면 접근 불가
      if (user && postData.authorId !== user.id) {
        toast.error('게시글을 수정할 권한이 없습니다.');
        navigate(`/posts/${id}`);
        return;
      }
      
      setPost(postData);
      setFormData({
        title: postData.title,
        content: postData.content,
        category: postData.category
      });
    } catch (error) {
      console.error('게시글 로드 실패:', error);
      toast.error('게시글을 불러오는데 실패했습니다.');
      navigate('/');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!user) {
      toast.error('로그인이 필요합니다.');
      return;
    }

    if (!formData.title.trim()) {
      toast.error('제목을 입력해주세요.');
      return;
    }

    if (!formData.content.trim()) {
      toast.error('내용을 입력해주세요.');
      return;
    }

    try {
      setSubmitting(true);
      await api.put(`/posts/${id}`, formData);
      toast.success('게시글이 수정되었습니다!');
      navigate(`/posts/${id}`);
    } catch (error) {
      console.error('게시글 수정 실패:', error);
      toast.error('게시글 수정에 실패했습니다.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleCancel = () => {
    navigate(`/posts/${id}`);
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 flex items-center justify-center">
        <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-red-500"></div>
      </div>
    );
  }

  if (!post) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-white mb-4">게시글을 찾을 수 없습니다</h2>
          <button
            onClick={() => navigate('/')}
            className="text-red-400 hover:text-red-300"
          >
            홈으로 돌아가기
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* 헤더 */}
        <div className="mb-8">
          <div className="flex items-center justify-between">
            <button
              onClick={handleCancel}
              className="flex items-center space-x-2 text-gray-400 hover:text-red-400 transition-colors duration-200"
            >
              <ArrowLeft size={20} />
              <span>뒤로가기</span>
            </button>
            
            <h1 className="text-2xl font-bold text-white flex items-center">
              <FileText className="w-6 h-6 mr-2" />
              게시글 수정
            </h1>
            
            <div className="w-20"></div> {/* 균형을 위한 빈 공간 */}
          </div>
        </div>

        {/* 수정 폼 */}
        <div className="bg-gray-800/50 backdrop-blur-sm border border-gray-700 rounded-xl p-6">
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* 카테고리 선택 */}
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-3">
                카테고리
              </label>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                {categories.map((category) => (
                  <button
                    key={category.id}
                    type="button"
                    onClick={() => setFormData(prev => ({ ...prev, category: category.id }))}
                    className={`flex items-center space-x-2 p-3 rounded-lg border transition-all duration-200 ${
                      formData.category === category.id
                        ? 'bg-red-600 text-white border-red-600 shadow-lg'
                        : 'bg-gray-700 text-gray-300 border-gray-600 hover:bg-gray-600 hover:border-red-500'
                    }`}
                  >
                    {category.icon}
                    <span className="font-medium">{category.name}</span>
                  </button>
                ))}
              </div>
            </div>

            {/* 제목 입력 */}
            <div>
              <label htmlFor="title" className="block text-sm font-medium text-gray-300 mb-2">
                제목
              </label>
              <input
                type="text"
                id="title"
                name="title"
                value={formData.title}
                onChange={handleChange}
                placeholder="게시글 제목을 입력하세요..."
                className="w-full px-4 py-3 bg-gray-700 border border-gray-600 rounded-lg text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-red-500 focus:border-transparent transition-all duration-200"
                maxLength={100}
              />
              <div className="flex justify-between items-center mt-1">
                <span className="text-xs text-gray-400">
                  명확하고 구체적인 제목을 작성해주세요
                </span>
                <span className="text-xs text-gray-400">
                  {formData.title.length}/100
                </span>
              </div>
            </div>

            {/* 내용 입력 */}
            <div>
              <label htmlFor="content" className="block text-sm font-medium text-gray-300 mb-2">
                내용
              </label>
              <div className="quill-editor-container">
                <ReactQuill
                  theme="snow"
                  value={formData.content}
                  onChange={(content) => setFormData(prev => ({ ...prev, content }))}
                  modules={quillModules}
                  formats={quillFormats}
                  placeholder="게시글 내용을 입력하세요..."
                  style={{ height: '300px' }}
                />
              </div>
              <div className="flex justify-between items-center mt-1">
                <span className="text-xs text-gray-400">
                  리치 텍스트 에디터를 사용하여 서식을 적용할 수 있습니다
                </span>
                <span className="text-xs text-gray-400">
                  {formData.content.replace(/<[^>]*>/g, '').length}자
                </span>
              </div>
            </div>

            {/* 액션 버튼 */}
            <div className="flex justify-end space-x-3 pt-6 border-t border-gray-700">
              <button
                type="button"
                onClick={handleCancel}
                className="flex items-center space-x-2 px-6 py-3 bg-gray-700 text-gray-300 rounded-lg hover:bg-gray-600 transition-colors duration-200"
              >
                <X size={16} />
                <span>취소</span>
              </button>
              
              <button
                type="submit"
                disabled={submitting || !formData.title.trim() || !formData.content.trim()}
                className="flex items-center space-x-2 px-6 py-3 bg-gradient-to-r from-red-600 to-red-700 text-white rounded-lg hover:from-red-700 hover:to-red-800 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200"
              >
                <Save size={16} />
                <span>{submitting ? '수정 중...' : '게시글 수정'}</span>
              </button>
            </div>
          </form>
        </div>

        {/* 수정 정보 */}
        <div className="mt-6 bg-gray-800/30 border border-gray-700 rounded-lg p-4">
          <h3 className="text-lg font-semibold text-white mb-3 flex items-center">
            <BookOpen className="w-5 h-5 mr-2" />
            수정 정보
          </h3>
          <div className="text-sm text-gray-300 space-y-1">
            <p>• 원본 작성일: {new Date(post.createdAt).toLocaleDateString('ko-KR')}</p>
            <p>• 마지막 수정: {new Date().toLocaleDateString('ko-KR')}</p>
            <p>• 작성자: {post.authorName}</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default EditPost;
