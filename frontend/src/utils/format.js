export const formatDate = (dateString) => {
  if (!dateString) return '';
  
  try {
    const localDate = new Date(dateString);
    
    if (isNaN(localDate.getTime())) {
      console.error('Invalid date:', dateString);
      return '';
    }
    
    const now = new Date();
    const diffInMs = now.getTime() - localDate.getTime();
    
    // 음수인 경우 (미래 시간) 처리
    if (diffInMs < 0) {
      return '방금 전';
    }
    
    const diffInMinutes = Math.floor(diffInMs / (1000 * 60));
    const diffInHours = Math.floor(diffInMs / (1000 * 60 * 60));
    const diffInDays = Math.floor(diffInMs / (1000 * 60 * 60 * 24));

    if (diffInMinutes < 1) return '방금 전';
    if (diffInMinutes < 60) return `${diffInMinutes}분 전`;
    if (diffInHours < 24) return `${diffInHours}시간 전`;
    if (diffInDays < 7) return `${diffInDays}일 전`;
    
    // 7일 이상 지난 경우 날짜 형식으로 표시
    return localDate.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (error) {
    console.error('Date formatting error:', error, 'dateString:', dateString);
    return '';
  }
};

export const formatDateOnly = (dateString) => {
  if (!dateString) return '';
  
  try {
    const localDate = new Date(dateString);
    
    if (isNaN(localDate.getTime())) {
      return '';
    }
    
    return localDate.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  } catch (error) {
    return '';
  }
};

export const readMinutesLabel = (min) =>
  min ? `${min} min read` : undefined;

export const formatNumber = (num) => {
  // undefined, null, NaN 체크
  if (num === undefined || num === null || isNaN(num)) {
    return '0';
  }
  
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + 'M';
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K';
  }
  return num.toString();
};

export const formatFileSize = (bytes) => {
  if (!bytes || bytes === 0) return '0 Bytes';
  
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

export const generateExcerpt = (content, maxLength = 150) => {
  if (!content) return '';
  
  // HTML 태그 제거
  const plainText = content.replace(/<[^>]*>/g, '');
  
  if (plainText.length <= maxLength) {
    return plainText;
  }
  
  return plainText.substring(0, maxLength) + '...';
};

export const calculateReadTime = (content) => {
  if (!content) return 1;
  
  // HTML 태그 제거
  const plainText = content.replace(/<[^>]*>/g, '');
  
  // 한국어 기준으로 읽기 시간 계산 (분당 약 300자)
  const words = plainText.length;
  const readTime = Math.ceil(words / 300);
  
  return Math.max(1, readTime);
};
