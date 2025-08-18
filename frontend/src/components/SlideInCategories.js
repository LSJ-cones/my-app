import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, BookOpen, Code, Database, Zap, Cloud, Layers } from 'lucide-react';

const getCategoryIcon = (categoryName) => {
  switch (categoryName.toLowerCase()) {
    case 'java':
      return <Code className="w-4 h-4" />;
    case 'spring boot':
      return <Database className="w-4 h-4" />;
    case 'web development':
      return <Zap className="w-4 h-4" />;
    case 'cloud':
      return <Cloud className="w-4 h-4" />;
    case 'database':
      return <Layers className="w-4 h-4" />;
    default:
      return <BookOpen className="w-4 h-4" />;
  }
};

export default function SlideInCategories({ 
  open, 
  onClose, 
  categories, 
  selected, 
  onToggle, 
  onSelect 
}) {
  return (
    <AnimatePresence>
      {open && (
        <>
          <motion.div
            className="fixed inset-0 bg-black/60 backdrop-blur-sm z-40"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={onClose}
          />
          <motion.aside
            className="fixed right-0 top-0 bottom-0 w-80 bg-gray-900 border-l border-gray-700 z-50 shadow-2xl"
            initial={{ x: 320 }}
            animate={{ x: 0 }}
            exit={{ x: 320 }}
            transition={{ type: "spring", damping: 25, stiffness: 200 }}
          >
            <div className="flex flex-col h-full">
              {/* 헤더 */}
              <div className="flex items-center justify-between p-6 border-b border-gray-700">
                <div>
                  <h3 className="text-lg font-semibold text-white">카테고리</h3>
                  <p className="text-sm text-gray-400">관심 있는 주제를 선택하세요</p>
                </div>
                <button 
                  onClick={onClose}
                  className="p-2 text-gray-400 hover:text-white transition-colors"
                  aria-label="닫기"
                >
                  <X className="w-5 h-5" />
                </button>
              </div>

              {/* 카테고리 목록 */}
              <div className="flex-1 overflow-y-auto p-4">
                <ul className="space-y-2">
                  {categories.map((category) => {
                    const active = selected.includes(category.id);
                    const isAll = category.id === 'all';
                    
                    return (
                      <li key={category.id}>
                        <button
                          onClick={() => isAll ? onSelect(category.id) : onToggle(category.id)}
                          className={`w-full text-left px-4 py-3 rounded-xl border transition-all duration-200 group
                            ${active 
                              ? 'border-red-500 bg-red-500/10 text-red-300 shadow-lg shadow-red-500/20' 
                              : 'border-gray-700 text-gray-300 hover:border-gray-500 hover:bg-gray-800/50'
                            }`}
                          aria-pressed={active}
                        >
                          <div className="flex items-center justify-between">
                            <div className="flex items-center space-x-3">
                              <div className={`p-2 rounded-lg ${active ? 'bg-red-500/20' : 'bg-gray-700/50 group-hover:bg-gray-600/50'}`}>
                                {getCategoryIcon(category.name)}
                              </div>
                              <div className="text-left">
                                <div className="font-medium">{category.name}</div>
                                {typeof category.count === 'number' && (
                                  <div className="text-xs text-gray-400">{category.count}개의 글</div>
                                )}
                              </div>
                            </div>
                            
                            {active && (
                              <div className="w-2 h-2 bg-red-400 rounded-full"></div>
                            )}
                          </div>
                        </button>
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

              {/* 푸터 */}
              <div className="p-4 border-t border-gray-700">
                <button
                  onClick={onClose}
                  className="w-full bg-red-600 hover:bg-red-700 text-white py-3 px-4 rounded-xl font-medium transition-colors"
                >
                  선택 완료
                </button>
              </div>
            </div>
          </motion.aside>
        </>
      )}
    </AnimatePresence>
  );
}
