import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, Save, X, FileText, Code, Database, Zap, Cloud, BookOpen, Upload, File, X as XIcon, Plus, Shield } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { toast } from 'react-hot-toast';
import api from '../services/api';

const CreatePost = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const isAdmin = user && user.role === 'ADMIN';
  const [formData, setFormData] = useState({
    title: '',
    content: '',
    categoryId: ''
  });
  const [submitting, setSubmitting] = useState(false);
  const [uploadedFiles, setUploadedFiles] = useState([]);
  const [uploadingFiles, setUploadingFiles] = useState(false);
  const [categories, setCategories] = useState([]);
  const [showCategoryModal, setShowCategoryModal] = useState(false);
  const [newCategory, setNewCategory] = useState({ name: '', description: '' });

  // 카테고리 목록 가져오기
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await api.get('/categories');
        setCategories(response.data);
        // 첫 번째 카테고리를 기본값으로 설정
        if (response.data.length > 0 && !formData.categoryId) {
          setFormData(prev => ({ ...prev, categoryId: response.data[0].id }));
        }
      } catch (error) {
        console.error('카테고리 목록 로드 실패:', error);
        toast.error('카테고리 목록을 불러오는데 실패했습니다.');
      }
    };
    fetchCategories();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  // 카테고리 생성
  const handleCreateCategory = async () => {
    if (!newCategory.name.trim()) {
      toast.error('카테고리명을 입력해주세요.');
      return;
    }

    try {
      const response = await api.post('/categories', {
        name: newCategory.name,
        description: newCategory.description
      });
      
      toast.success('카테고리가 생성되었습니다.');
      setCategories(prev => [...prev, response.data]);
      setNewCategory({ name: '', description: '' });
      setShowCategoryModal(false);
    } catch (error) {
      console.error('카테고리 생성 실패:', error);
      toast.error('카테고리 생성에 실패했습니다.');
    }
  };

  const handleFileUpload = async (e) => {
    const files = Array.from(e.target.files);
    if (files.length === 0) return;

    setUploadingFiles(true);
    const newFiles = [];

    try {
      for (const file of files) {
        // 파일 크기 체크 (10MB 제한)
        if (file.size > 10 * 1024 * 1024) {
          toast.error(`${file.name} 파일이 너무 큽니다. (최대 10MB)`);
          continue;
        }

        const formData = new FormData();
        formData.append('file', file);

        const response = await api.post('/files/upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        });

        newFiles.push({
          id: response.data.id,
          name: file.name,
          url: response.data.url,
          size: file.size
        });

        toast.success(`${file.name} 파일이 업로드되었습니다.`);
      }

      setUploadedFiles(prev => [...prev, ...newFiles]);
    } catch (error) {
      console.error('파일 업로드 실패:', error);
      toast.error('파일 업로드에 실패했습니다.');
    } finally {
      setUploadingFiles(false);
    }
  };

  const removeFile = (fileId) => {
    setUploadedFiles(prev => prev.filter(file => file.id !== fileId));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.title.trim()) {
      toast.error('제목을 입력해주세요.');
      return;
    }

    if (!formData.content.trim()) {
      toast.error('내용을 입력해주세요.');
      return;
    }

    setSubmitting(true);
    try {
      const postData = {
        ...formData,
        author: user?.username || '익명',
        fileIds: uploadedFiles.map(file => file.id)
      };

      const response = await api.post('/posts', postData);
      toast.success('게시글이 작성되었습니다!');
      navigate(`/posts/${response.data.id}`);
    } catch (error) {
      console.error('게시글 작성 실패:', error);
      toast.error('게시글 작성에 실패했습니다.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleCancel = () => {
    if (formData.title || formData.content) {
      if (window.confirm('작성 중인 내용이 있습니다. 정말로 나가시겠습니까?')) {
        navigate(-1);
      }
    } else {
      navigate(-1);
    }
  };

  const formatFileSize = (bytes) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  if (!isAdmin) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 flex items-center justify-center">
        <div className="glass-dark p-8 rounded-2xl text-center max-w-md">
          <Shield className="w-10 h-10 text-red-400 mx-auto mb-3" />
          <h2 className="text-2xl font-bold text-white mb-2">접근 권한이 없습니다</h2>
          <p className="text-gray-300 mb-6">게시글 작성은 관리자만 가능합니다.</p>
          <button 
            onClick={() => navigate('/')}
            className="btn-primary"
          >
            홈으로 이동
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 py-8">
      <div className="max-w-4xl mx-auto px-4">
        {/* 헤더 */}
        <div className="flex items-center mb-8">
          <button
            onClick={handleCancel}
            className="flex items-center space-x-2 text-gray-300 hover:text-white transition-colors duration-200"
          >
            <ArrowLeft className="w-5 h-5" />
            <span>뒤로가기</span>
          </button>
        </div>

        {/* 폼 */}
        <div className="glass-dark p-8 rounded-2xl">
          <h1 className="text-3xl font-bold text-white mb-8">새 게시글 작성</h1>
          
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* 제목 */}
            <div>
              <label className="block text-white font-medium mb-2">제목</label>
              <input
                type="text"
                name="title"
                value={formData.title}
                onChange={handleChange}
                placeholder="게시글 제목을 입력하세요"
                className="input-field w-full"
                maxLength={100}
              />
              <div className="text-right text-gray-400 text-sm mt-1">
                {formData.title.length}/100
              </div>
            </div>

            {/* 카테고리 선택 */}
            <div>
              <div className="flex items-center justify-between mb-3">
                <label className="block text-white font-medium">카테고리</label>
                {user?.role === 'ADMIN' && (
                  <button
                    type="button"
                    onClick={() => setShowCategoryModal(true)}
                    className="flex items-center space-x-1 text-red-400 hover:text-red-300 transition-colors duration-200"
                  >
                    <Plus className="w-4 h-4" />
                    <span className="text-sm">카테고리 추가</span>
                  </button>
                )}
              </div>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                {categories.map((category) => (
                  <button
                    key={category.id}
                    type="button"
                    onClick={() => setFormData(prev => ({ ...prev, categoryId: category.id }))}
                    className={`flex items-center space-x-2 p-3 rounded-xl border transition-all duration-200 ${
                      formData.categoryId === category.id
                        ? 'bg-red-600 text-white border-red-600 shadow-lg'
                        : 'bg-gray-800 text-gray-300 border-gray-600 hover:bg-gray-700 hover:border-red-500'
                    }`}
                  >
                    <BookOpen className="w-4 h-4" />
                    <span className="font-medium">{category.name}</span>
                  </button>
                ))}
              </div>
            </div>

            {/* 파일 업로드 */}
            <div>
              <label className="block text-white font-medium mb-3">파일 첨부</label>
              <div className="space-y-4">
                {/* 파일 업로드 영역 */}
                <div className="border-2 border-dashed border-gray-600 rounded-xl p-6 text-center hover:border-red-500 transition-colors duration-200">
                  <input
                    type="file"
                    multiple
                    onChange={handleFileUpload}
                    className="hidden"
                    id="file-upload"
                    accept="image/*,.pdf,.doc,.docx,.txt,.zip,.rar"
                    disabled={uploadingFiles}
                  />
                  <label htmlFor="file-upload" className="cursor-pointer">
                    <Upload className="w-12 h-12 text-gray-400 mx-auto mb-3" />
                    <p className="text-gray-300 mb-2">
                      {uploadingFiles ? '업로드 중...' : '클릭하여 파일을 선택하거나 여기로 드래그하세요'}
                    </p>
                    <p className="text-gray-400 text-sm">
                      최대 10MB, 이미지, PDF, 문서 파일 지원
                    </p>
                  </label>
                </div>

                {/* 업로드된 파일 목록 */}
                {uploadedFiles.length > 0 && (
                  <div className="space-y-2">
                    <h4 className="text-white font-medium">첨부된 파일</h4>
                    {uploadedFiles.map((file) => (
                      <div
                        key={file.id}
                        className="flex items-center justify-between p-3 bg-gray-800/50 rounded-lg"
                      >
                        <div className="flex items-center space-x-3">
                          <File className="w-5 h-5 text-gray-400" />
                          <div>
                            <p className="text-white text-sm">{file.name}</p>
                            <p className="text-gray-400 text-xs">{formatFileSize(file.size)}</p>
                          </div>
                        </div>
                        <button
                          type="button"
                          onClick={() => removeFile(file.id)}
                          className="p-1 text-gray-400 hover:text-red-400 transition-colors duration-200"
                        >
                          <XIcon className="w-4 h-4" />
                        </button>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>

            {/* 내용 */}
            <div>
              <label className="block text-white font-medium mb-2">내용</label>
              <textarea
                name="content"
                value={formData.content}
                onChange={handleChange}
                placeholder="게시글 내용을 입력하세요"
                rows="12"
                className="input-field w-full resize-none"
                maxLength={5000}
              />
              <div className="text-right text-gray-400 text-sm mt-1">
                {formData.content.length}/5000
              </div>
            </div>

            {/* 작성 버튼 */}
            <div className="flex justify-end space-x-3 pt-6 border-t border-gray-700">
              <button
                onClick={handleCancel}
                className="btn-secondary flex items-center space-x-2"
              >
                <X className="w-4 h-4" />
                <span>취소</span>
              </button>
              <button
                onClick={handleSubmit}
                disabled={submitting}
                className="btn-primary flex items-center space-x-2 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <Save className="w-4 h-4" />
                <span>{submitting ? '작성 중...' : '게시글 작성'}</span>
              </button>
            </div>
          </form>

          {/* 작성 팁 */}
          <div className="mt-8 p-4 bg-gray-800/50 rounded-xl">
            <h3 className="text-white font-medium mb-2">작성 팁</h3>
            <ul className="text-gray-300 text-sm space-y-1">
              <li>• 명확하고 구체적인 제목을 사용하세요</li>
              <li>• 코드 블록은 ```로 감싸서 작성하세요</li>
              <li>• 이미지는 파일 첨부 기능을 활용하세요</li>
              <li>• 적절한 카테고리를 선택하여 분류하세요</li>
            </ul>
          </div>
        </div>
      </div>

      {/* 카테고리 생성 모달 */}
      {showCategoryModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-gray-800 rounded-xl p-6 w-full max-w-md">
            <h3 className="text-xl font-bold text-white mb-4">새 카테고리 생성</h3>
            <div className="space-y-4">
              <div>
                <label className="block text-white font-medium mb-2">카테고리명</label>
                <input
                  type="text"
                  value={newCategory.name}
                  onChange={(e) => setNewCategory(prev => ({ ...prev, name: e.target.value }))}
                  placeholder="카테고리명을 입력하세요"
                  className="input-field w-full"
                />
              </div>
              <div>
                <label className="block text-white font-medium mb-2">설명 (선택사항)</label>
                <textarea
                  value={newCategory.description}
                  onChange={(e) => setNewCategory(prev => ({ ...prev, description: e.target.value }))}
                  placeholder="카테고리 설명을 입력하세요"
                  className="input-field w-full h-20 resize-none"
                />
              </div>
            </div>
            <div className="flex space-x-3 mt-6">
              <button
                onClick={handleCreateCategory}
                className="flex-1 btn-primary"
              >
                생성
              </button>
              <button
                onClick={() => {
                  setShowCategoryModal(false);
                  setNewCategory({ name: '', description: '' });
                }}
                className="flex-1 btn-secondary"
              >
                취소
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CreatePost;
