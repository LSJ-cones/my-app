package com.blog.toy.dto;

import com.blog.toy.domain.ReactionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentReactionDto {

    private Long commentId;

    @JsonProperty("type")
    private String typeString; // JSON에서 받을 때는 String으로 받음

    // commentId setter (수동으로 작성)
    public void setCommentId(Long commentId) {
        System.out.println("🔍 CommentReactionDto.setCommentId 호출됨: " + commentId);
        this.commentId = commentId;
    }

    // ReactionType으로 변환하는 메서드
    public ReactionType getType() {
        System.out.println("🔍 CommentReactionDto.getType() 호출됨 - typeString: " + typeString);
        if (typeString == null) {
            System.out.println("⚠️ typeString이 null입니다!");
            return null;
        }
        try {
            ReactionType type = ReactionType.valueOf(typeString.toUpperCase());
            System.out.println("🔍 CommentReactionDto.getType() - 변환된 타입: " + type);
            return type;
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ 잘못된 반응 타입: " + typeString);
            System.out.println("⚠️ 사용 가능한 타입들: " + java.util.Arrays.toString(ReactionType.values()));
            return null;
        }
    }

    // String setter (Jackson이 이 메서드를 사용할 것)
    public void setType(String type) {
        System.out.println("🔍 CommentReactionDto.setType(String) 호출됨: " + type);
        this.typeString = type;
    }

    // ReactionType setter (직접 설정할 때 사용)
    public void setType(ReactionType type) {
        System.out.println("🔍 CommentReactionDto.setType(ReactionType) 호출됨: " + type);
        this.typeString = type != null ? type.name() : null;
    }

    @Override
    public String toString() {
        return "CommentReactionDto{" +
                "commentId=" + commentId +
                ", typeString='" + typeString + '\'' +
                ", getType()=" + getType() +
                '}';
    }
}
