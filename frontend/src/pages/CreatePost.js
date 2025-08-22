import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, Save, X, BookOpen, Upload, ChevronDown, Shield } from 'lucide-react';
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
  const [categories, setCategories] = useState([]);
  const [mainCategories, setMainCategories] = useState([]);
  const [subCategories, setSubCategories] = useState([]);
  const [selectedMainCategory, setSelectedMainCategory] = useState(null);

  // 카테고리 목록 가져오기
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await api.get('/categories/hierarchy');
        setCategories(response.data);
        
        // 대분류와 소분류 분리 (parentId 기반)
        const mainCats = response.data.filter(cat => !cat.parentId);
        const subCats = response.data.filter(cat => cat.parentId);
        
        setMainCategories(mainCats);
        setSubCategories(subCats);
        
        // 첫 번째 대분류를 기본 선택
        if (mainCats.length > 0) {
          setSelectedMainCategory(mainCats[0]);
          // 해당 대분류의 첫 번째 소분류를 기본값으로 설정
          const firstSubCat = subCats.find(cat => cat.parentId === mainCats[0].id);
          if (firstSubCat && !formData.categoryId) {
            setFormData(prev => ({ ...prev, categoryId: firstSubCat.id }));
          }
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



  const handleFileUpload = async (e) => {
    const files = Array.from(e.target.files);
    if (files.length === 0) return;

          // setUploadingFiles(true);
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
    } catch (error) {
      console.error('파일 업로드 실패:', error);
      toast.error('파일 업로드에 실패했습니다.');
    } finally {
      // setUploadingFiles(false);
    }
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
        author: user?.username || '익명'
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
              <label className="block text-white font-medium mb-3">카테고리</label>
              
              {/* 카테고리 선택 */}
              <div className="mb-4">
                <label className="block text-gray-300 text-sm mb-2">카테고리</label>
                <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                  {mainCategories.map((category) => {
                    const subCats = subCategories.filter(cat => cat.parentId === category.id);
                    const isSelected = selectedMainCategory?.id === category.id;
                    const selectedSubCat = subCats.find(cat => cat.id === formData.categoryId);
                    
                    return (
                      <div key={category.id} className="relative">
                        <button
                          type="button"
                          onClick={() => {
                            if (isSelected) {
                              setSelectedMainCategory(null);
                              setFormData(prev => ({ ...prev, categoryId: '' }));
                            } else {
                              setSelectedMainCategory(category);
                              // 해당 대분류의 첫 번째 소분류를 자동 선택
                              const firstSubCat = subCats[0];
                              if (firstSubCat) {
                                setFormData(prev => ({ ...prev, categoryId: firstSubCat.id }));
                              }
                            }
                          }}
                          className={`w-full flex items-center justify-between p-3 rounded-xl border transition-all duration-200 ${
                            isSelected
                              ? 'bg-blue-600 text-white border-blue-600 shadow-lg'
                              : 'bg-gray-800 text-gray-300 border-gray-600 hover:bg-gray-700 hover:border-blue-500'
                          }`}
                        >
                          <div className="flex items-center space-x-2">
                            <BookOpen className="w-4 h-4" />
                            <span className="font-medium">{category.name}</span>
                          </div>
                          {isSelected && subCats.length > 0 && (
                            <ChevronDown className="w-4 h-4" />
                          )}
                        </button>
                        
                        {/* 소분류 드롭다운 */}
                        {isSelected && subCats.length > 0 && (
                          <div className="absolute top-full left-0 right-0 mt-1 bg-gray-800 border border-gray-600 rounded-lg shadow-lg z-10">
                            {subCats.map((subCat) => (
                              <button
                                key={subCat.id}
                                type="button"
                                onClick={() => {
                                  setFormData(prev => ({ ...prev, categoryId: subCat.id }));
                                  // 소분류 선택 후 드롭다운 닫기
                                  setSelectedMainCategory(null);
                                }}
                                className={`w-full text-left px-3 py-2 hover:bg-gray-700 transition-colors ${
                                  formData.categoryId === subCat.id
                                    ? 'bg-red-600 text-white'
                                    : 'text-gray-300'
                                }`}
                              >
                                {subCat.name}
                              </button>
                            ))}
                          </div>
                        )}
                        
                        {/* 선택된 소분류 표시 */}
                        {selectedSubCat && (
                          <div className="mt-2 px-2 py-1 bg-red-600/20 border border-red-600/30 rounded text-sm text-red-400">
                            선택: {selectedSubCat.name}
                          </div>
                        )}
                      </div>
                    );
                  })}
                </div>
              </div>
            </div>

            {/* 파일 업로드 - 비활성화 */}
            <div className="opacity-50 pointer-events-none">
              <label className="block text-white font-medium mb-3">파일 첨부 (비활성화)</label>
              <div className="space-y-4">
                {/* 파일 업로드 영역 */}
                <div className="border-2 border-dashed border-gray-600 rounded-xl p-6 text-center">
                  <Upload className="w-12 h-12 text-gray-400 mx-auto mb-3" />
                  <p className="text-gray-300 mb-2">
                    파일 업로드 기능이 일시적으로 비활성화되었습니다
                  </p>
                  <p className="text-gray-400 text-sm">
                    추후 업데이트 예정
                  </p>
                </div>
              </div>
            </div>

            {/* 내용 */}
            <div>
              <label className="block text-white font-medium mb-2">내용</label>
              <textarea
                value={formData.content}
                onChange={(e) => setFormData(prev => ({ ...prev, content: e.target.value }))}
                className="input-field w-full h-full p-4 rounded-lg bg-gray-700 text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="게시글 내용을 입력하세요"
                style={{ height: '300px' }}
              />
              <div className="text-right text-gray-400 text-sm mt-1">
                {formData.content.replace(/<[^>]*>/g, '').length}/5000
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


    </div>
  );
};

export default CreatePost;
