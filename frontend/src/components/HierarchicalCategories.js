import React, { useState } from 'react';
import { ChevronDown, ChevronRight, Folder, FolderOpen } from 'lucide-react';

export default function HierarchicalCategories({ categories, selected, onToggle, onSelect }) {
  const [expandedCategories, setExpandedCategories] = useState(new Set());

  // 대분류와 소분류로 분리
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

  // 카테고리 클릭 핸들러
  const handleCategoryClick = (category) => {
    if (category.categoryType === 'MAIN') {
      // 대분류 클릭 시 확장/축소
      toggleExpanded(category.id);
    } else {
      // 소분류 클릭 시 선택
      onToggle(category.id);
    }
  };

  return (
    <aside className="hidden lg:block w-64 pr-6">
      <div className="sticky top-20 h-[calc(100vh-96px)] overflow-y-auto">
        <div className="mb-6">
          <h3 className="text-lg font-semibold text-white mb-3">카테고리</h3>
          <p className="text-sm text-gray-400">관심 있는 주제를 선택하세요</p>
        </div>
        
        <ul className="flex flex-col gap-1">
          {/* 전체 카테고리 */}
          <li>
            <button
              onClick={() => onSelect('all')}
              className={`w-full text-left px-4 py-3 rounded-xl border transition-all duration-200 group
                ${selected.includes('all') 
                  ? 'border-red-500 bg-red-500/10 text-red-300 shadow-lg shadow-red-500/20' 
                  : 'border-gray-700 text-gray-300 hover:border-gray-500 hover:bg-gray-800/50'
                }`}
              aria-pressed={selected.includes('all')}
            >
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-3">
                  <div className={`p-2 rounded-lg ${selected.includes('all') ? 'bg-red-500/20' : 'bg-gray-700/50 group-hover:bg-gray-600/50'}`}>
                    <Folder className="w-4 h-4" />
                  </div>
                  <div className="text-left">
                    <div className="font-medium">전체</div>
                    <div className="text-xs text-gray-400">모든 게시글</div>
                  </div>
                </div>
                
                {selected.includes('all') && (
                  <div className="w-2 h-2 bg-red-400 rounded-full"></div>
                )}
              </div>
            </button>
          </li>

          {/* 대분류 카테고리들 */}
          {mainCategories.map((mainCategory) => {
            const isExpanded = expandedCategories.has(mainCategory.id);
            const subCats = getSubCategories(mainCategory.id);
            const hasSelectedSub = subCats.some(cat => selected.includes(cat.id));
            
            return (
              <li key={mainCategory.id}>
                {/* 대분류 버튼 */}
                <button
                  onClick={() => handleCategoryClick(mainCategory)}
                  className={`w-full text-left px-4 py-3 rounded-xl border transition-all duration-200 group
                    ${hasSelectedSub 
                      ? 'border-red-500 bg-red-500/10 text-red-300 shadow-lg shadow-red-500/20' 
                      : 'border-gray-700 text-gray-300 hover:border-gray-500 hover:bg-gray-800/50'
                    }`}
                >
                  <div className="flex items-center justify-between">
                    <div className="flex items-center space-x-3">
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          toggleExpanded(mainCategory.id);
                        }}
                        className="p-1 hover:bg-gray-600/50 rounded"
                      >
                        {isExpanded ? (
                          <ChevronDown className="w-4 h-4" />
                        ) : (
                          <ChevronRight className="w-4 h-4" />
                        )}
                      </button>
                      <div className={`p-2 rounded-lg ${hasSelectedSub ? 'bg-red-500/20' : 'bg-gray-700/50 group-hover:bg-gray-600/50'}`}>
                        {isExpanded ? (
                          <FolderOpen className="w-4 h-4" />
                        ) : (
                          <Folder className="w-4 h-4" />
                        )}
                      </div>
                      <div className="text-left">
                        <div className="font-medium">{mainCategory.name}</div>
                        <div className="text-xs text-gray-400">{subCats.length}개 하위 카테고리</div>
                      </div>
                    </div>
                    
                    {hasSelectedSub && (
                      <div className="w-2 h-2 bg-red-400 rounded-full"></div>
                    )}
                  </div>
                </button>

                {/* 소분류 카테고리들 */}
                {isExpanded && (
                  <ul className="ml-6 mt-2 space-y-1">
                    {subCats.map((subCategory) => {
                      const isSelected = selected.includes(subCategory.id);
                      
                      return (
                        <li key={subCategory.id}>
                          <button
                            onClick={() => handleCategoryClick(subCategory)}
                            className={`w-full text-left px-3 py-2 rounded-lg border transition-all duration-200
                              ${isSelected 
                                ? 'border-red-500 bg-red-500/10 text-red-300 shadow-lg shadow-red-500/20' 
                                : 'border-gray-600 text-gray-300 hover:border-gray-500 hover:bg-gray-800/50'
                              }`}
                            aria-pressed={isSelected}
                          >
                            <div className="flex items-center justify-between">
                              <div className="flex items-center space-x-2">
                                <div className={`p-1.5 rounded ${isSelected ? 'bg-red-500/20' : 'bg-gray-600/50'}`}>
                                  <div className="w-2 h-2 bg-gray-400 rounded-full"></div>
                                </div>
                                <div className="text-left">
                                  <div className="font-medium text-sm">{subCategory.name}</div>
                                  {subCategory.postCount > 0 && (
                                    <div className="text-xs text-gray-400">{subCategory.postCount}개 글</div>
                                  )}
                                </div>
                              </div>
                              
                              {isSelected && (
                                <div className="w-1.5 h-1.5 bg-red-400 rounded-full"></div>
                              )}
                            </div>
                          </button>
                        </li>
                      );
                    })}
                  </ul>
                )}
              </li>
            );
          })}
        </ul>
        
        {selected.length > 0 && selected[0] !== 'all' && (
          <div className="mt-4 pt-4 border-t border-gray-700">
            <button
              onClick={() => onSelect('all')}
              className="w-full text-center px-4 py-2 text-sm text-gray-400 hover:text-white transition-colors"
            >
              전체 보기
            </button>
          </div>
        )}
      </div>
    </aside>
  );
}
