import React, { useState, useMemo, useRef, useEffect } from 'react';
import { Search, Filter, X, ChevronDown, ChevronRight, Folder, FolderOpen } from 'lucide-react';

const sortOptions = [
  { value: 'latest', label: '최신순' },
  { value: 'oldest', label: '오래된순' },
  { value: 'views', label: '조회순' },
  { value: 'likes', label: '좋아요순' },
  { value: 'comments', label: '댓글순' },
];

export default function FilterBar({ 
  keyword, 
  onKeyword, 
  categories, 
  selected, 
  onToggle, 
  onClearCategories,
  sort, 
  onSort, 
  totalCount,
  filteredCount 
}) {
  const [isCategoryDropdownOpen, setIsCategoryDropdownOpen] = useState(false);
  const [selectedMainCategory, setSelectedMainCategory] = useState(null);
  const dropdownRef = useRef(null);

  // 드롭다운 외부 클릭 시 닫기
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsCategoryDropdownOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const hasFilters = keyword || selected.length > 0 || sort !== 'latest';

  // 계층형 카테고리 구조로 변환 (parentId 기반)
  const hierarchicalCategories = useMemo(() => {
    const mainCategories = categories.filter(cat => !cat.parentId && cat.id !== 'all');
    const subCategories = categories.filter(cat => cat.parentId);
    
    // 디버깅용 로그
    console.log('🔍 FilterBar - 전체 카테고리:', categories);
    console.log('🔍 FilterBar - 대분류:', mainCategories);
    console.log('🔍 FilterBar - 소분류:', subCategories);
    
    return mainCategories.map(mainCat => ({
      ...mainCat,
      subCategories: subCategories.filter(subCat => subCat.parentId === mainCat.id)
    }));
  }, [categories]);

  // 선택된 카테고리들의 이름 가져오기
  const selectedCategoryNames = useMemo(() => {
    return selected.map(id => {
      const category = categories.find(cat => cat.id === id);
      return category ? category.name : '';
    }).filter(name => name);
  }, [selected, categories]);

  // 카테고리 선택 처리
  const handleCategorySelect = (categoryId) => {
    onToggle(categoryId);
  };

  // 대분류 선택 시 해당 소분류들 표시
  const handleMainCategoryClick = (mainCategory) => {
    setSelectedMainCategory(selectedMainCategory?.id === mainCategory.id ? null : mainCategory);
  };

  return (
    <div className="space-y-4">
      {/* 검색 및 정렬 */}
      <div className="flex flex-col sm:flex-row gap-3">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
          <input
            value={keyword}
            onChange={(e) => onKeyword(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && (e.currentTarget.blur())}
            placeholder="제목·태그·내용 검색..."
            aria-label="검색"
            className="w-full pl-10 pr-4 py-3 bg-gray-800/50 border border-gray-700 rounded-xl text-gray-200 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-red-500/60 focus:border-transparent"
          />
          {keyword && (
            <button
              onClick={() => onKeyword('')}
              className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-white"
              aria-label="검색어 지우기"
            >
              <X className="w-4 h-4" />
            </button>
          )}
        </div>
        
        <div className="relative">
          <select
            value={sort}
            onChange={(e) => onSort(e.target.value)}
            className="appearance-none bg-gray-800/50 border border-gray-700 rounded-xl px-4 py-3 pr-10 text-gray-200 focus:outline-none focus:ring-2 focus:ring-red-500/60 focus:border-transparent"
            aria-label="정렬"
          >
            {sortOptions.map(option => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
          <ChevronDown className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4 pointer-events-none" />
        </div>
      </div>

      {/* 계층형 카테고리 필터 */}
      {hierarchicalCategories.length > 0 && (
        <div className="space-y-3">
          {/* 카테고리 드롭다운 버튼 */}
          <div className="relative" ref={dropdownRef}>
            <button
              onClick={() => setIsCategoryDropdownOpen(!isCategoryDropdownOpen)}
              className="flex items-center justify-between w-full px-4 py-3 bg-gray-800/50 border border-gray-700 rounded-xl text-gray-200 hover:bg-gray-800/70 focus:outline-none focus:ring-2 focus:ring-red-500/60 focus:border-transparent transition-colors"
            >
        <div className="flex items-center space-x-2">
          <Filter className="w-4 h-4" />
                <span className="text-sm">
                  {selectedCategoryNames.length > 0 
                    ? `${selectedCategoryNames.join(', ')} 선택됨`
                    : '카테고리 선택'
                  }
          </span>
              </div>
              <ChevronDown className={`w-4 h-4 transition-transform ${isCategoryDropdownOpen ? 'rotate-180' : ''}`} />
            </button>

            {/* 카테고리 드롭다운 메뉴 */}
            {isCategoryDropdownOpen && (
              <div className="absolute top-full left-0 right-0 mt-2 bg-gray-900 border border-gray-700 rounded-xl shadow-xl z-50 max-h-96 overflow-y-auto">
                <div className="p-4 space-y-2">
                  {/* 전체 선택 버튼 */}
                  <button
                    onClick={() => {
                      onClearCategories();
                      setSelectedMainCategory(null);
                    }}
                    className={`w-full text-left px-3 py-2 rounded-lg text-sm transition-colors ${
                      selected.length === 0
                        ? 'bg-red-600 text-white'
                        : 'text-gray-300 hover:bg-gray-800'
                    }`}
                  >
                    <div className="flex items-center space-x-2">
                      <span>전체</span>
                      <span className="text-xs text-gray-400">({totalCount})</span>
                    </div>
                  </button>

                  {/* 대분류 및 소분류 */}
                  {hierarchicalCategories.map((mainCategory) => (
                    <div key={mainCategory.id} className="space-y-1">
                      {/* 대분류 */}
                      <button
                        onClick={() => handleMainCategoryClick(mainCategory)}
                        className={`w-full text-left px-3 py-2 rounded-lg text-sm transition-colors ${
                          selected.includes(mainCategory.id)
                            ? 'bg-red-600 text-white'
                            : 'text-gray-300 hover:bg-gray-800'
                        }`}
                      >
                        <div className="flex items-center justify-between">
                          <div className="flex items-center space-x-2">
                            {selectedMainCategory?.id === mainCategory.id ? (
                              <FolderOpen className="w-4 h-4" />
                            ) : (
                              <Folder className="w-4 h-4" />
                            )}
                            <span>{mainCategory.name}</span>
                            {typeof mainCategory.count === 'number' && (
                              <span className="text-xs text-gray-400">({mainCategory.count})</span>
                            )}
                          </div>
                          {mainCategory.subCategories.length > 0 && (
                            <ChevronRight 
                              className={`w-4 h-4 transition-transform ${
                                selectedMainCategory?.id === mainCategory.id ? 'rotate-90' : ''
                              }`} 
                            />
                          )}
                        </div>
                      </button>

                      {/* 소분류 */}
                      {selectedMainCategory?.id === mainCategory.id && mainCategory.subCategories.length > 0 && (
                        <div className="ml-6 space-y-1">
                          {mainCategory.subCategories.map((subCategory) => (
                            <button
                              key={subCategory.id}
                              onClick={() => handleCategorySelect(subCategory.id)}
                              className={`w-full text-left px-3 py-2 rounded-lg text-sm transition-colors ${
                                selected.includes(subCategory.id)
                                  ? 'bg-red-500 text-white'
                                  : 'text-gray-400 hover:bg-gray-800 hover:text-gray-300'
                              }`}
                            >
                              <div className="flex items-center space-x-2">
                                <span>└ {subCategory.name}</span>
                                {typeof subCategory.count === 'number' && (
                                  <span className="text-xs text-gray-400">({subCategory.count})</span>
                                )}
                              </div>
                            </button>
                          ))}
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              </div>
          )}
        </div>
        
          {/* 선택된 카테고리 칩들 */}
          {selectedCategoryNames.length > 0 && (
            <div className="flex flex-wrap gap-2">
              {selectedCategoryNames.map((name, index) => (
                <div
                  key={index}
                  className="flex items-center space-x-2 px-3 py-1 bg-red-600/20 border border-red-600/30 rounded-full text-sm text-red-300"
                >
                  <span>{name}</span>
          <button
            onClick={() => {
                      const category = categories.find(cat => cat.name === name);
                      if (category) {
                        onToggle(category.id);
                      }
                    }}
                    className="hover:text-red-200"
                  >
                    <X className="w-3 h-3" />
          </button>
                </div>
              ))}
            </div>
        )}
      </div>
      )}

      {/* 기존 카테고리 칩들 (하위 호환성을 위해 유지) */}
      {categories.length > 0 && selected.length === 0 && (
        <div className="flex flex-wrap gap-2">
          {categories.map((category) => {
            const active = selected.includes(category.id);
            
            return (
              <button
                key={category.id}
                onClick={() => onToggle(category.id)}
                className={`px-4 py-2 rounded-full text-sm font-medium border transition-all duration-200
                  ${active
                    ? 'bg-red-600 border-red-600 text-white shadow-lg shadow-red-500/20'
                    : 'border-gray-600 text-gray-300 hover:border-gray-400 hover:bg-gray-800/50'
                  }`}
                aria-pressed={active}
              >
                <div className="flex items-center space-x-2">
                  <span>{category.name}</span>
                  {typeof category.count === 'number' && (
                    <span className={`text-xs ${active ? 'text-red-200' : 'text-gray-400'}`}>
                      ({category.count})
                    </span>
                  )}
                </div>
              </button>
            );
          })}
        </div>
      )}
    </div>
  );
}
