import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Plus, BookOpen } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { toast } from 'react-hot-toast';
import api from '../services/api';
import AppLayout from '../components/Layout/AppLayout.js';
import HierarchicalCategories from '../components/HierarchicalCategories.js';
import FilterBar from '../components/FilterBar.js';
import PostCard from '../components/PostCard.js';
import { useCategories } from '../hooks/useCategories.js';

const Home = () => {
  const { user } = useAuth();
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [keyword, setKeyword] = useState('');
  const [sort, setSort] = useState('latest');
  const [categories, setCategories] = useState([]);
  const [showAdvancedSearch, setShowAdvancedSearch] = useState(false);
  const [advancedSearchData, setAdvancedSearchData] = useState({
    keyword: '',
    categoryId: '',
    tagNames: '',
    status: 'DRAFT'
  });

  const { selected, selectedCategories, toggle, select, clear, hasSelection } = useCategories(categories);

  // 계층 구조 카테고리 데이터 가져오기
  const fetchCategories = async () => {
    try {
      const response = await api.get('/categories/hierarchy');
      const categoryData = response.data || [];
      
      // 전체 카테고리 추가
      const allCategories = [
        { id: 'all', name: '전체', parentId: null, count: totalElements },
        ...categoryData.map(cat => ({
          id: cat.id, // 숫자 ID 유지
          name: cat.name,
          parentId: cat.parentId,
          parentName: cat.parentName,
          fullPath: cat.fullPath,
          count: cat.postCount || 0
        }))
      ];
      
      setCategories(allCategories);
    } catch (error) {
      console.error('카테고리 로드 실패:', error);
      // 기본 카테고리 설정
      setCategories([
        { id: 'all', name: '전체', parentId: null, count: totalElements },
        { id: '1', name: 'Development', parentId: null, count: 0 },
        { id: '2', name: 'Infrastructure', parentId: null, count: 0 },
        { id: '3', name: 'Data', parentId: null, count: 0 },
        { id: '4', name: 'Others', parentId: null, count: 0 },
      ]);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  useEffect(() => {
    fetchPosts();
  }, [currentPage, selected, keyword, sort]);

  // totalElements가 변경될 때 전체 카테고리의 count 업데이트
  useEffect(() => {
    setCategories(prev => 
      prev.map(cat => 
        cat.id === 'all' ? { ...cat, count: totalElements } : cat
      )
    );
  }, [totalElements]);

  const fetchPosts = async () => {
    try {
      setLoading(true);
      let response;
      
      // 정렬 파라미터 매핑
      const getSortParams = (sortValue) => {
        switch (sortValue) {
          case 'latest':
            return { sortBy: 'createdAt', sortDirection: 'desc' };
          case 'oldest':
            return { sortBy: 'createdAt', sortDirection: 'asc' };
          case 'views':
            return { sortBy: 'viewCount', sortDirection: 'desc' };
          case 'likes':
            return { sortBy: 'likeCount', sortDirection: 'desc' };
          case 'comments':
            return { sortBy: 'commentCount', sortDirection: 'desc' };
          default:
            return { sortBy: 'createdAt', sortDirection: 'desc' };
        }
      };

      const sortParams = getSortParams(sort);
      
      // 선택된 카테고리 필터링 (전체가 아닌 경우만)
      const filteredCategories = selected.filter(catId => catId !== 'all');
      
      if (filteredCategories.length === 0) {
        // 전체 선택인 경우
        response = await api.get('/posts', {
          params: {
            page: currentPage,
            size: 10,
            ...sortParams
          }
        });
      } else {
        // 특정 카테고리 선택인 경우
        const categoryNames = filteredCategories.join(',');
        response = await api.get('/posts', {
          params: {
            page: currentPage,
            size: 10,
            categories: categoryNames,
            ...sortParams
          }
        });
      }

      if (response.data) {
        setPosts(response.data.content || []);
        setTotalPages(response.data.totalPages || 0);
        setTotalElements(response.data.totalElements || 0);
      }
    } catch (error) {
      console.error('게시글 로드 실패:', error);
      toast.error('게시글을 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleCategoryToggle = (categoryId) => {
    toggle(categoryId);
  };

  const handleCategorySelect = (categoryId) => {
    select(categoryId);
  };

  const handleSearch = (searchKeyword) => {
    setKeyword(searchKeyword);
    setCurrentPage(0);
  };

  const handleSortChange = (newSort) => {
    setSort(newSort);
    setCurrentPage(0);
  };

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
  };

  // 카테고리 변경 시 자동 새로고침
  const handleCategoryChange = () => {
    fetchCategories();
    fetchPosts();
  };

  return (
    <AppLayout
      left={
        <HierarchicalCategories
          categories={categories}
          selected={selected}
          onToggle={handleCategoryToggle}
          onSelect={handleCategorySelect}
        />
      }
    >
      <div className="space-y-6">
        {/* 헤더 */}
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-white">게시글 목록</h1>
            <p className="text-gray-400 mt-1">
              총 {totalElements}개의 게시글
            </p>
          </div>
          {user && user.role === 'ADMIN' && (
            <Link
              to="/posts/create"
              className="inline-flex items-center px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
            >
              <Plus className="w-4 h-4 mr-2" />
              새 글 작성
            </Link>
          )}
        </div>

        {/* 필터바 */}
        <FilterBar
          keyword={keyword}
          onKeyword={handleSearch}
          categories={categories}
          selected={selected}
          onToggle={handleCategoryToggle}
          onClearCategories={clear}
          sort={sort}
          onSort={handleSortChange}
          totalCount={totalElements}
          filteredCount={posts.length}
        />

        {/* 게시글 목록 */}
        {loading ? (
          <div className="space-y-4">
            {[...Array(5)].map((_, index) => (
              <div key={index} className="animate-pulse">
                <div className="h-32 bg-gray-800 rounded-lg"></div>
              </div>
            ))}
          </div>
        ) : posts.length > 0 ? (
          <div className="space-y-4">
            {posts.map((post) => (
              <PostCard key={post.id} post={post} />
            ))}
          </div>
        ) : (
          <div className="text-center py-12">
            <BookOpen className="w-16 h-16 text-gray-600 mx-auto mb-4" />
            <h3 className="text-lg font-medium text-gray-300 mb-2">게시글이 없습니다</h3>
            <p className="text-gray-500">조건에 맞는 게시글이 없습니다.</p>
          </div>
        )}

        {/* 페이지네이션 */}
        {totalPages > 1 && (
          <div className="flex justify-center space-x-2">
            <button
              onClick={() => handlePageChange(currentPage - 1)}
              disabled={currentPage === 0}
              className="px-3 py-2 text-sm border border-gray-600 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:border-gray-500"
            >
              이전
            </button>
            <span className="px-3 py-2 text-sm text-gray-400">
              {currentPage + 1} / {totalPages}
            </span>
            <button
              onClick={() => handlePageChange(currentPage + 1)}
              disabled={currentPage === totalPages - 1}
              className="px-3 py-2 text-sm border border-gray-600 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:border-gray-500"
            >
              다음
            </button>
          </div>
        )}
      </div>
    </AppLayout>
  );
};

export default Home;
