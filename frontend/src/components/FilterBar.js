import React from 'react';
import { Search, Filter, X, ChevronDown } from 'lucide-react';

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
  const hasFilters = keyword || selected.length > 0 || sort !== 'latest';

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

      {/* 결과 카운트 */}
      <div className="flex items-center justify-between text-sm text-gray-400">
        <div className="flex items-center space-x-2">
          <Filter className="w-4 h-4" />
          <span>
            {hasFilters ? `${filteredCount}개 결과` : `${totalCount}개 게시글`}
          </span>
          {hasFilters && (
            <span className="text-gray-500">(전체 {totalCount}개)</span>
          )}
        </div>
        
        {hasFilters && (
          <button
            onClick={() => {
              onKeyword('');
              onClearCategories();
              onSort('latest');
            }}
            className="text-red-400 hover:text-red-300 transition-colors"
          >
            필터 초기화
          </button>
        )}
      </div>

      {/* 카테고리 chips */}
      {categories.length > 0 && (
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
