import React from 'react';
import { Link } from 'react-router-dom';
import { User, Eye, MessageSquare, Heart, Clock, File, Tag } from 'lucide-react';
import { formatDate, formatNumber, generateExcerpt, calculateReadTime } from '../utils/format';

export default function PostCard({ post }) {
  const excerpt = post.excerpt || generateExcerpt(post.content, 120);
  const readTime = post.readMinutes || calculateReadTime(post.content);

  return (
    <article className="group rounded-2xl border border-gray-700 hover:border-gray-500 transition-all duration-300 p-6 bg-gray-800/30 hover:bg-gray-800/50 hover:shadow-xl hover:shadow-red-500/5">
      <Link to={`/posts/${post.id}`} className="block">
        {/* 헤더 */}
        <header className="flex items-start justify-between gap-4 mb-4">
          <div className="flex-1 min-w-0">
            <h2 className="text-xl font-bold text-white group-hover:text-red-300 transition-colors line-clamp-2 mb-2">
              {post.title}
            </h2>
            
            {/* 카테고리 및 메타 정보 */}
            <div className="flex items-center gap-3 text-sm text-gray-400 mb-3">
              {post.category && (
                <span className="px-3 py-1 bg-red-500/20 text-red-400 rounded-full text-xs font-medium border border-red-500/30">
                  {post.category.name}
                </span>
              )}
              <div className="flex items-center gap-1">
                <Clock className="w-3 h-3" />
                <span>{formatDate(post.createdAt)}</span>
              </div>
              <div className="flex items-center gap-1">
                <span>·</span>
                <span>{readTime} min read</span>
              </div>
            </div>
          </div>
          
          {/* 내 글 표시 */}
          {post.mine && (
            <span className="text-xs px-2 py-1 rounded-full bg-red-500/20 text-red-400 border border-red-500/30 flex-shrink-0">
              내 글
            </span>
          )}
        </header>

        {/* 본문 미리보기 */}
        {excerpt && (
          <p className="text-gray-300 line-clamp-2 mb-4 leading-relaxed">
            {excerpt}
          </p>
        )}

        {/* 태그 */}
        {post.tags && post.tags.length > 0 && (
          <div className="flex items-center gap-2 mb-4">
            <Tag className="w-4 h-4 text-gray-500" />
            <div className="flex flex-wrap gap-1">
              {post.tags.slice(0, 3).map((tag) => (
                <span
                  key={tag.id}
                  className="px-2 py-1 bg-gray-700/50 text-gray-300 rounded-md text-xs"
                >
                  #{tag.name}
                </span>
              ))}
              {post.tags.length > 3 && (
                <span className="px-2 py-1 text-gray-500 text-xs">
                  +{post.tags.length - 3}
                </span>
              )}
            </div>
          </div>
        )}

        {/* 첨부파일 표시 */}
        {post.files && post.files.length > 0 && (
          <div className="flex items-center gap-2 mb-4 text-sm text-gray-400">
            <File className="w-4 h-4" />
            <span>첨부파일 {post.files.length}개</span>
          </div>
        )}

        {/* 푸터 - 메타 정보 */}
        <footer className="flex items-center justify-between text-sm text-gray-400">
          <div className="flex items-center gap-4">
            <div className="flex items-center gap-1">
              <User className="w-4 h-4" />
              <span>{post.authorName || post.author}</span>
            </div>
            <div className="flex items-center gap-1">
              <Eye className="w-4 h-4" />
              <span>{formatNumber(post.viewCount)}</span>
            </div>
            <div className="flex items-center gap-1">
              <MessageSquare className="w-4 h-4" />
              <span>{formatNumber(post.commentCount)}</span>
            </div>
            {post.likeCount !== undefined && (
              <div className="flex items-center gap-1">
                <Heart className="w-4 h-4" />
                <span>{formatNumber(post.likeCount)}</span>
              </div>
            )}
          </div>
          
          {/* 날짜 */}
          <time dateTime={post.createdAt} className="text-gray-500">
            {formatDate(post.createdAt)}
          </time>
        </footer>
      </Link>
    </article>
  );
}
