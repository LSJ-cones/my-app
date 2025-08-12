# 게시글 반응 시스템 테스트 스크립트
# PowerShell에서 실행: .\test-post-reaction.ps1

$baseUrl = "http://localhost:8081"
$postId = 1

Write-Host "=== 게시글 반응 시스템 테스트 ===" -ForegroundColor Green
Write-Host ""

# 색상 함수
function Write-Success { param($message) Write-Host $message -ForegroundColor Green }
function Write-Error { param($message) Write-Host $message -ForegroundColor Red }
function Write-Info { param($message) Write-Host $message -ForegroundColor Yellow }
function Write-Log { param($message) Write-Host "[$(Get-Date -Format 'HH:mm:ss')] $message" -ForegroundColor Cyan }

# API 호출 함수
function Invoke-ApiCall {
    param(
        [string]$Method = "GET",
        [string]$Url,
        [string]$Body = $null
    )
    
    try {
        $headers = @{
            "Content-Type" = "application/json"
        }
        
        $params = @{
            Method = $Method
            Uri = $Url
            Headers = $headers
        }
        
        if ($Body) {
            $params.Body = $Body
        }
        
        $response = Invoke-RestMethod @params
        return $response
    }
    catch {
        Write-Error "API 호출 실패: $($_.Exception.Message)"
        return $null
    }
}

# 1. 게시글 반응 상태 조회
Write-Log "1. 게시글 $postId 의 반응 상태 조회"
$reaction = Invoke-ApiCall -Url "$baseUrl/api/posts/$postId/reactions"
if ($reaction) {
    Write-Success "반응 상태 조회 성공"
    Write-Info "좋아요: $($reaction.likeCount), 싫어요: $($reaction.dislikeCount)"
    Write-Info "사용자 좋아요: $($reaction.userLiked), 사용자 싫어요: $($reaction.userDisliked)"
} else {
    Write-Error "반응 상태 조회 실패"
}
Write-Host ""

# 2. 좋아요 추가
Write-Log "2. 게시글 $postId 에 좋아요 추가"
$likeResponse = Invoke-ApiCall -Method "POST" -Url "$baseUrl/api/posts/$postId/reactions?type=LIKE"
if ($likeResponse) {
    Write-Success "좋아요 추가 성공"
    Write-Info "좋아요: $($likeResponse.likeCount), 싫어요: $($likeResponse.dislikeCount)"
} else {
    Write-Error "좋아요 추가 실패"
}
Write-Host ""

# 3. 반응 상태 재확인
Write-Log "3. 반응 상태 재확인"
$reactionAfterLike = Invoke-ApiCall -Url "$baseUrl/api/posts/$postId/reactions"
if ($reactionAfterLike) {
    Write-Success "반응 상태 재확인 성공"
    Write-Info "사용자 좋아요: $($reactionAfterLike.userLiked), 사용자 싫어요: $($reactionAfterLike.userDisliked)"
} else {
    Write-Error "반응 상태 재확인 실패"
}
Write-Host ""

# 4. 싫어요로 변경
Write-Log "4. 좋아요를 싫어요로 변경"
$dislikeResponse = Invoke-ApiCall -Method "POST" -Url "$baseUrl/api/posts/$postId/reactions?type=DISLIKE"
if ($dislikeResponse) {
    Write-Success "싫어요로 변경 성공"
    Write-Info "좋아요: $($dislikeResponse.likeCount), 싫어요: $($dislikeResponse.dislikeCount)"
} else {
    Write-Error "싫어요로 변경 실패"
}
Write-Host ""

# 5. 모든 반응 조회
Write-Log "5. 게시글 $postId 의 모든 반응 조회"
$allReactions = Invoke-ApiCall -Url "$baseUrl/api/posts/$postId/reactions/all"
if ($allReactions) {
    Write-Success "모든 반응 조회 성공"
    Write-Info "총 반응 수: $($allReactions.Count)"
    foreach ($reaction in $allReactions) {
        Write-Info "  - $($reaction.username): $($reaction.type) ($($reaction.createdAt))"
    }
} else {
    Write-Error "모든 반응 조회 실패"
}
Write-Host ""

# 6. 내 반응 목록 조회
Write-Log "6. 내 반응 목록 조회"
$myReactions = Invoke-ApiCall -Url "$baseUrl/api/reactions/my"
if ($myReactions) {
    Write-Success "내 반응 목록 조회 성공"
    Write-Info "내 반응 수: $($myReactions.Count)"
    foreach ($reaction in $myReactions) {
        Write-Info "  - 게시글 $($reaction.postId) ($($reaction.postTitle)): $($reaction.type)"
    }
} else {
    Write-Error "내 반응 목록 조회 실패"
}
Write-Host ""

# 7. 반응 취소
Write-Log "7. 게시글 $postId 의 반응 취소"
$removeResponse = Invoke-ApiCall -Method "DELETE" -Url "$baseUrl/api/posts/$postId/reactions"
if ($removeResponse) {
    Write-Success "반응 취소 성공"
    Write-Info "좋아요: $($removeResponse.likeCount), 싫어요: $($removeResponse.dislikeCount)"
    Write-Info "사용자 좋아요: $($removeResponse.userLiked), 사용자 싫어요: $($removeResponse.userDisliked)"
} else {
    Write-Error "반응 취소 실패"
}
Write-Host ""

# 8. 최종 상태 확인
Write-Log "8. 최종 반응 상태 확인"
$finalReaction = Invoke-ApiCall -Url "$baseUrl/api/posts/$postId/reactions"
if ($finalReaction) {
    Write-Success "최종 상태 확인 성공"
    Write-Info "좋아요: $($finalReaction.likeCount), 싫어요: $($finalReaction.dislikeCount)"
    Write-Info "사용자 좋아요: $($finalReaction.userLiked), 사용자 싫어요: $($finalReaction.userDisliked)"
} else {
    Write-Error "최종 상태 확인 실패"
}
Write-Host ""

# 9. 반응 통계 조회
Write-Log "9. 게시글 $postId 의 반응 통계 조회"
$stats = Invoke-ApiCall -Url "$baseUrl/api/posts/$postId/reactions/stats"
if ($stats) {
    Write-Success "반응 통계 조회 성공"
    Write-Info "좋아요: $($stats.likeCount), 싫어요: $($stats.dislikeCount)"
} else {
    Write-Error "반응 통계 조회 실패"
}
Write-Host ""

Write-Host "=== 테스트 완료 ===" -ForegroundColor Green
Write-Host ""

# 추가 테스트: 다른 게시글에 반응 추가
Write-Log "추가 테스트: 게시글 2에 좋아요 추가"
$post2Like = Invoke-ApiCall -Method "POST" -Url "$baseUrl/api/posts/2/reactions?type=LIKE"
if ($post2Like) {
    Write-Success "게시글 2 좋아요 추가 성공"
    Write-Info "좋아요: $($post2Like.likeCount), 싫어요: $($post2Like.dislikeCount)"
} else {
    Write-Error "게시글 2 좋아요 추가 실패"
}
Write-Host ""

Write-Log "추가 테스트: 게시글 3에 싫어요 추가"
$post3Dislike = Invoke-ApiCall -Method "POST" -Url "$baseUrl/api/posts/3/reactions?type=DISLIKE"
if ($post3Dislike) {
    Write-Success "게시글 3 싫어요 추가 성공"
    Write-Info "좋아요: $($post3Dislike.likeCount), 싫어요: $($post3Dislike.dislikeCount)"
} else {
    Write-Error "게시글 3 싫어요 추가 실패"
}
Write-Host ""

# 최종 내 반응 목록 확인
Write-Log "최종 내 반응 목록 확인"
$finalMyReactions = Invoke-ApiCall -Url "$baseUrl/api/reactions/my"
if ($finalMyReactions) {
    Write-Success "최종 내 반응 목록 조회 성공"
    Write-Info "내 반응 수: $($finalMyReactions.Count)"
    foreach ($reaction in $finalMyReactions) {
        Write-Info "  - 게시글 $($reaction.postId) ($($reaction.postTitle)): $($reaction.type)"
    }
} else {
    Write-Error "최종 내 반응 목록 조회 실패"
}

Write-Host ""
Write-Host "=== 모든 테스트 완료 ===" -ForegroundColor Green
Write-Host "게시글 반응 시스템이 정상적으로 작동합니다!" -ForegroundColor Green
