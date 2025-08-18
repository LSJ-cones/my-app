import React, { useState } from 'react';
import { Filter } from 'lucide-react';
import SidebarCategories from '../SidebarCategories';
import SlideInCategories from '../SlideInCategories';

export default function AppLayout({
  left,
  children,
  categories = [],
  selectedCategories = [],
  onToggleCategory,
  onSelectCategory
}) {
  const [mobilePanelOpen, setMobilePanelOpen] = useState(false);

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900">
      {/* 메인 컨테이너 */}
      <main className="container mx-auto px-4 py-6">
        <div className="grid grid-cols-1 lg:grid-cols-[16rem_1fr] gap-8">
          {/* 좌측 sticky 사이드바 */}
          {left}

          {/* 메인 영역 */}
          <section className="min-w-0">
            {/* 모바일에서 우측 패널 토글 버튼 */}
            <div className="lg:hidden mb-6 flex justify-end">
              <button 
                onClick={() => setMobilePanelOpen(true)}
                className="flex items-center gap-2 px-4 py-2 bg-gray-800/50 border border-gray-700 rounded-xl text-gray-300 hover:bg-gray-700/50 transition-colors"
              >
                <Filter className="w-4 h-4" />
                <span>카테고리</span>
                {selectedCategories && selectedCategories.length > 0 && (
                  <span className="px-2 py-0.5 bg-red-500 text-white text-xs rounded-full">
                    {selectedCategories.length}
                  </span>
                )}
              </button>
            </div>
            
            {children}
          </section>
        </div>
      </main>

      {/* 모바일 slide-in 카테고리 패널 */}
      {categories && selectedCategories && onToggleCategory && onSelectCategory && (
        <SlideInCategories 
          open={mobilePanelOpen} 
          onClose={() => setMobilePanelOpen(false)}
          categories={categories}
          selected={selectedCategories}
          onToggle={onToggleCategory}
          onSelect={onSelectCategory}
        />
      )}
    </div>
  );
}
