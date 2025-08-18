import React, { useState, useEffect } from 'react';
import { Plus, Edit, Trash2, ChevronDown, ChevronRight, Folder, FolderOpen, Save, X } from 'lucide-react';
import { toast } from 'react-hot-toast';
import api from '../services/api';

const CategoryManagement = () => {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [editingId, setEditingId] = useState(null);
  const [expandedCategories, setExpandedCategories] = useState(new Set());
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    categoryType: 'SUB',
    parentId: null,
    displayOrder: 1
  });

  // 카테고리 데이터 로드
  const fetchCategories = async () => {
    try {
      setLoading(true);
      const response = await api.get('/categories/hierarchy');
      setCategories(response.data || []);
    } catch (error) {
      console.error('카테고리 로드 실패:', error);
      toast.error('카테고리를 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  // 대분류와 소분류 분리
  const mainCategories = categories.filter(cat => cat.categoryType === 'MAIN');
  const subCategories = categories.filter(cat => cat.categoryType === 'SUB');

  // 카테고리 확장/축소 토글
  const toggleExpanded = (categoryId) => {
    setExpandedCategories(prev => {
      const newSet = new Set(prev);
      if (newSet.has(categoryId)) {
        newSet.delete(categoryId);
      } else {
        newSet.add(categoryId);
      }
      return newSet;
    });
  };

  // 대분류별 소분류 그룹핑
  const getSubCategories = (mainCategoryId) => {
    return subCategories.filter(cat => cat.parentId === mainCategoryId);
  };

  // 폼 데이터 초기화
  const resetForm = () => {
    setFormData({
      name: '',
      description: '',
      categoryType: 'SUB',
      parentId: null,
      displayOrder: 1
    });
  };

  // 카테고리 생성
  const handleCreate = async () => {
    try {
      if (!formData.name.trim()) {
        toast.error('카테고리명을 입력해주세요.');
        return;
      }

      await api.post('/categories', formData);
      toast.success('카테고리가 생성되었습니다.');
      setShowCreateForm(false);
      resetForm();
      fetchCategories();
    } catch (error) {
      console.error('카테고리 생성 실패:', error);
      toast.error('카테고리 생성에 실패했습니다.');
    }
  };

  // 카테고리 수정
  const handleUpdate = async (id, updatedData) => {
    try {
      await api.put(`/categories/${id}`, updatedData);
      toast.success('카테고리가 수정되었습니다.');
      setEditingId(null);
      fetchCategories();
    } catch (error) {
      console.error('카테고리 수정 실패:', error);
      toast.error('카테고리 수정에 실패했습니다.');
    }
  };

  // 카테고리 삭제
  const handleDelete = async (id) => {
    if (!window.confirm('정말로 이 카테고리를 삭제하시겠습니까?')) {
      return;
    }

    try {
      await api.delete(`/categories/${id}`);
      toast.success('카테고리가 삭제되었습니다.');
      fetchCategories();
    } catch (error) {
      console.error('카테고리 삭제 실패:', error);
      toast.error('카테고리 삭제에 실패했습니다.');
    }
  };

  // 편집 모드 시작
  const startEditing = (category) => {
    setEditingId(category.id);
    setFormData({
      name: category.name,
      description: category.description || '',
      categoryType: category.categoryType,
      parentId: category.parentId,
      displayOrder: category.displayOrder || 1
    });
  };

  // 편집 모드 취소
  const cancelEditing = () => {
    setEditingId(null);
    resetForm();
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
      <div className="container mx-auto px-4 py-8">
        {/* 헤더 */}
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-3xl font-bold text-white mb-2">카테고리 관리</h1>
            <p className="text-gray-400">블로그의 카테고리를 관리합니다.</p>
          </div>
          <button
            onClick={() => setShowCreateForm(true)}
            className="inline-flex items-center px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
          >
            <Plus className="w-4 h-4 mr-2" />
            새 카테고리
          </button>
        </div>

        {/* 카테고리 목록 */}
        <div className="bg-gray-800/50 rounded-xl p-6">
          <h2 className="text-xl font-semibold text-white mb-4">카테고리 구조</h2>
          
          <div className="space-y-2">
            {mainCategories.map((mainCategory) => {
              const isExpanded = expandedCategories.has(mainCategory.id);
              const subCats = getSubCategories(mainCategory.id);
              const isEditing = editingId === mainCategory.id;
              
              return (
                <div key={mainCategory.id} className="border border-gray-700 rounded-lg">
                  {/* 대분류 */}
                  <div className="flex items-center justify-between p-4 bg-gray-700/30">
                    <div className="flex items-center space-x-3">
                      <button
                        onClick={() => toggleExpanded(mainCategory.id)}
                        className="p-1 hover:bg-gray-600/50 rounded"
                      >
                        {isExpanded ? (
                          <ChevronDown className="w-4 h-4 text-gray-400" />
                        ) : (
                          <ChevronRight className="w-4 h-4 text-gray-400" />
                        )}
                      </button>
                      <div className={`p-2 rounded-lg ${isExpanded ? 'bg-red-500/20' : 'bg-gray-600/50'}`}>
                        {isExpanded ? (
                          <FolderOpen className="w-4 h-4 text-red-400" />
                        ) : (
                          <Folder className="w-4 h-4 text-gray-400" />
                        )}
                      </div>
                      <div>
                        <div className="font-medium text-white">{mainCategory.name}</div>
                        <div className="text-sm text-gray-400">{subCats.length}개 하위 카테고리</div>
                      </div>
                    </div>
                    
                    <div className="flex items-center space-x-2">
                      {isEditing ? (
                        <>
                          <button
                            onClick={() => handleUpdate(mainCategory.id, formData)}
                            className="p-2 text-green-400 hover:bg-green-400/10 rounded"
                          >
                            <Save className="w-4 h-4" />
                          </button>
                          <button
                            onClick={cancelEditing}
                            className="p-2 text-gray-400 hover:bg-gray-600/50 rounded"
                          >
                            <X className="w-4 h-4" />
                          </button>
                        </>
                      ) : (
                        <>
                          <button
                            onClick={() => startEditing(mainCategory)}
                            className="p-2 text-blue-400 hover:bg-blue-400/10 rounded"
                          >
                            <Edit className="w-4 h-4" />
                          </button>
                          <button
                            onClick={() => handleDelete(mainCategory.id)}
                            className="p-2 text-red-400 hover:bg-red-400/10 rounded"
                          >
                            <Trash2 className="w-4 h-4" />
                          </button>
                        </>
                      )}
                    </div>
                  </div>

                  {/* 편집 폼 */}
                  {isEditing && (
                    <div className="p-4 bg-gray-700/20 border-t border-gray-700">
                      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <input
                          type="text"
                          value={formData.name}
                          onChange={(e) => setFormData({...formData, name: e.target.value})}
                          placeholder="카테고리명"
                          className="bg-gray-700 border border-gray-600 rounded-lg px-3 py-2 text-white"
                        />
                        <input
                          type="text"
                          value={formData.description}
                          onChange={(e) => setFormData({...formData, description: e.target.value})}
                          placeholder="설명"
                          className="bg-gray-700 border border-gray-600 rounded-lg px-3 py-2 text-white"
                        />
                        <input
                          type="number"
                          value={formData.displayOrder}
                          onChange={(e) => setFormData({...formData, displayOrder: parseInt(e.target.value)})}
                          placeholder="표시 순서"
                          className="bg-gray-700 border border-gray-600 rounded-lg px-3 py-2 text-white"
                        />
                      </div>
                    </div>
                  )}

                  {/* 소분류 목록 */}
                  {isExpanded && (
                    <div className="border-t border-gray-700">
                      {subCats.map((subCategory) => {
                        const isSubEditing = editingId === subCategory.id;
                        
                        return (
                          <div key={subCategory.id} className="flex flex-col p-4 bg-gray-800/30 border-b border-gray-700 last:border-b-0">
                            <div className="flex items-center justify-between">
                              <div className="flex items-center space-x-3 ml-8">
                                <div className="p-1.5 rounded bg-gray-600/50">
                                  <div className="w-2 h-2 bg-gray-400 rounded-full"></div>
                                </div>
                                <div>
                                  <div className="font-medium text-white">{subCategory.name}</div>
                                  <div className="text-sm text-gray-400">{subCategory.postCount || 0}개 글</div>
                                </div>
                              </div>
                              
                              <div className="flex items-center space-x-2">
                                {isSubEditing ? (
                                  <>
                                    <button
                                      onClick={() => handleUpdate(subCategory.id, formData)}
                                      className="p-2 text-green-400 hover:bg-green-400/10 rounded"
                                    >
                                      <Save className="w-4 h-4" />
                                    </button>
                                    <button
                                      onClick={cancelEditing}
                                      className="p-2 text-gray-400 hover:bg-gray-600/50 rounded"
                                    >
                                      <X className="w-4 h-4" />
                                    </button>
                                  </>
                                ) : (
                                  <>
                                    <button
                                      onClick={() => startEditing(subCategory)}
                                      className="p-2 text-blue-400 hover:bg-blue-400/10 rounded"
                                    >
                                      <Edit className="w-4 h-4" />
                                    </button>
                                    <button
                                      onClick={() => handleDelete(subCategory.id)}
                                      className="p-2 text-red-400 hover:bg-red-400/10 rounded"
                                    >
                                      <Trash2 className="w-4 h-4" />
                                    </button>
                                  </>
                                )}
                              </div>
                            </div>

                            {isSubEditing && (
                              <div className="mt-4 grid grid-cols-1 md:grid-cols-3 gap-4 ml-8">
                                <input
                                  type="text"
                                  value={formData.name}
                                  onChange={(e) => setFormData({...formData, name: e.target.value})}
                                  placeholder="카테고리명"
                                  className="bg-gray-700 border border-gray-600 rounded-lg px-3 py-2 text-white"
                                />
                                <input
                                  type="text"
                                  value={formData.description}
                                  onChange={(e) => setFormData({...formData, description: e.target.value})}
                                  placeholder="설명"
                                  className="bg-gray-700 border border-gray-600 rounded-lg px-3 py-2 text-white"
                                />
                                <input
                                  type="number"
                                  value={formData.displayOrder}
                                  onChange={(e) => setFormData({...formData, displayOrder: parseInt(e.target.value)})}
                                  placeholder="표시 순서"
                                  className="bg-gray-700 border border-gray-600 rounded-lg px-3 py-2 text-white"
                                />
                              </div>
                            )}
                          </div>
                        );
                      })}
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        </div>

        {/* 새 카테고리 생성 모달 */}
        {showCreateForm && (
          <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
            <div className="bg-gray-800 rounded-xl p-6 w-full max-w-md">
              <h3 className="text-xl font-semibold text-white mb-4">새 카테고리 생성</h3>
              
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-300 mb-2">카테고리 타입</label>
                  <select
                    value={formData.categoryType}
                    onChange={(e) => setFormData({...formData, categoryType: e.target.value})}
                    className="w-full bg-gray-700 border border-gray-600 rounded-lg px-3 py-2 text-white"
                  >
                    <option value="MAIN">대분류</option>
                    <option value="SUB">소분류</option>
                  </select>
                </div>

                {formData.categoryType === 'SUB' && (
                  <div>
                    <label className="block text-sm font-medium text-gray-300 mb-2">상위 카테고리</label>
                    <select
                      value={formData.parentId || ''}
                      onChange={(e) => setFormData({...formData, parentId: e.target.value ? parseInt(e.target.value) : null})}
                      className="w-full bg-gray-700 border border-gray-600 rounded-lg px-3 py-2 text-white"
                    >
                      <option value="">선택하세요</option>
                      {mainCategories.map(cat => (
                        <option key={cat.id} value={cat.id}>{cat.name}</option>
                      ))}
                    </select>
                  </div>
                )}

                <div>
                  <label className="block text-sm font-medium text-gray-300 mb-2">카테고리명</label>
                  <input
                    type="text"
                    value={formData.name}
                    onChange={(e) => setFormData({...formData, name: e.target.value})}
                    placeholder="카테고리명을 입력하세요"
                    className="w-full bg-gray-700 border border-gray-600 rounded-lg px-3 py-2 text-white"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-300 mb-2">설명</label>
                  <input
                    type="text"
                    value={formData.description}
                    onChange={(e) => setFormData({...formData, description: e.target.value})}
                    placeholder="카테고리 설명 (선택사항)"
                    className="w-full bg-gray-700 border border-gray-600 rounded-lg px-3 py-2 text-white"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-300 mb-2">표시 순서</label>
                  <input
                    type="number"
                    value={formData.displayOrder}
                    onChange={(e) => setFormData({...formData, displayOrder: parseInt(e.target.value)})}
                    className="w-full bg-gray-700 border border-gray-600 rounded-lg px-3 py-2 text-white"
                  />
                </div>
              </div>

              <div className="flex justify-end space-x-3 mt-6">
                <button
                  onClick={() => {
                    setShowCreateForm(false);
                    resetForm();
                  }}
                  className="px-4 py-2 text-gray-400 hover:text-white transition-colors"
                >
                  취소
                </button>
                <button
                  onClick={handleCreate}
                  className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
                >
                  생성
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default CategoryManagement;
