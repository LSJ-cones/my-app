import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, Save, X, FileText, Code, Database, Zap, Cloud, BookOpen, Upload, File, X as XIcon } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { toast } from 'react-hot-toast';
import api from '../services/api';

const CreatePost = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [formData, setFormData] = useState({
    title: '',
    content: '',
    category: 'JAVA'
  });
  const [submitting, setSubmitting] = useState(false);
  const [uploadedFiles, setUploadedFiles] = useState([]);
  const [uploadingFiles, setUploadingFiles] = useState(false);

  const categories = [
    { id: 'JAVA', name: 'Java', icon: <Code className="w-4 h-4" /> },
    { id: 'SPRING', name: 'Spring', icon: <Database className="w-4 h-4" /> },
    { id: 'JAVASCRIPT', name: 'JavaScript', icon: <Zap className="w-4 h-4" /> },
    { id: 'REACT', name: 'React', icon: <Cloud className="w-4 h-4" /> }
  ];

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

  if (!user) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 flex items-center justify-center">
        <div className="glass-dark p-8 rounded-2xl text-center">
          <h2 className="text-2xl font-bold text-white mb-4">로그인이 필요합니다</h2>
          <p className="text-gray-300 mb-6">게시글을 작성하려면 로그인해주세요.</p>
          <button 
            onClick={() => navigate('/login')}
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
      <div className="max-w-4xl mx-auto px-4">
        {/* 헤더 */}
        <div className="flex items-center justify-between mb-8">
          <button
            onClick={handleCancel}
            className="flex items-center space-x-2 text-gray-300 hover:text-white transition-colors duration-200"
          >
            <ArrowLeft className="w-5 h-5" />
            <span>뒤로가기</span>
          </button>
          
          <div className="flex space-x-3">
            <button
              onClick={handleSubmit}
              disabled={submitting}
              className="btn-primary flex items-center space-x-2 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <Save className="w-4 h-4" />
              <span>{submitting ? '작성 중...' : '게시글 작성'}</span>
            </button>
            <button
              onClick={handleCancel}
              className="btn-secondary flex items-center space-x-2"
            >
              <X className="w-4 h-4" />
              <span>취소</span>
            </button>
          </div>
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
              <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                {categories.map((category) => (
                  <button
                    key={category.id}
                    type="button"
                    onClick={() => setFormData(prev => ({ ...prev, category: category.id }))}
                    className={`flex items-center space-x-2 p-3 rounded-xl border transition-all duration-200 ${
                      formData.category === category.id
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
