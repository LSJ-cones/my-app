import { useMemo, useState, useEffect } from 'react';

export function useCategories(initial) {
  const [selected, setSelected] = useState(['all']); // 초기값을 전체 선택으로 설정

  // 카테고리 데이터가 로드되면 전체 선택 유지
  useEffect(() => {
    if (initial.length > 0 && selected.length === 0) {
      setSelected(['all']);
    }
  }, [initial, selected]);

  const toggle = (id) => {
    setSelected((prev) => {
      if (id === 'all') {
        // 전체 선택인 경우: 다른 모든 선택 해제하고 전체만 선택
        return ['all'];
      } else {
        // 개별 카테고리 선택인 경우: 전체 선택 해제하고 해당 카테고리 토글
        const newSelected = prev.filter(x => x !== 'all'); // 전체 선택 제거
        if (newSelected.includes(id)) {
          return newSelected.filter(x => x !== id);
        } else {
          return [...newSelected, id];
        }
      }
    });
  };

  const select = (id) => {
    if (id === 'all') {
      setSelected(['all']);
    } else {
      setSelected([id]);
    }
  };

  const clear = () => setSelected(['all']); // 초기화 시에도 전체 선택

  const map = useMemo(() => new Map(initial.map((c) => [c.id, c])), [initial]);

  const selectedCategories = useMemo(() => 
    selected.map(id => map.get(id)).filter(Boolean), 
    [selected, map]
  );

  return { 
    selected, 
    selectedCategories,
    toggle, 
    select,
    clear, 
    map,
    hasSelection: selected.length > 0 && selected[0] !== 'all'
  };
}
