package com.blog.toy.service;

import com.blog.toy.domain.Tag;
import com.blog.toy.dto.tag.TagRequestDto;
import com.blog.toy.dto.tag.TagResponseDto;
import com.blog.toy.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public TagResponseDto createTag(TagRequestDto requestDto) {
        if (tagRepository.existsByName(requestDto.getName())) {
            throw new RuntimeException("이미 존재하는 태그명입니다: " + requestDto.getName());
        }

        Tag tag = Tag.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .active(requestDto.isActive())
                .build();

        Tag savedTag = tagRepository.save(tag);
        return convertToResponseDto(savedTag);
    }

    @Transactional(readOnly = true)
    public List<TagResponseDto> getAllTags() {
        return tagRepository.findAllActiveOrdered()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TagResponseDto getTagById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("태그를 찾을 수 없습니다: " + id));
        return convertToResponseDto(tag);
    }

    public TagResponseDto updateTag(Long id, TagRequestDto requestDto) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("태그를 찾을 수 없습니다: " + id));

        // 이름이 변경된 경우 중복 체크
        if (!tag.getName().equals(requestDto.getName()) && 
            tagRepository.existsByName(requestDto.getName())) {
            throw new RuntimeException("이미 존재하는 태그명입니다: " + requestDto.getName());
        }

        tag.setName(requestDto.getName());
        tag.setDescription(requestDto.getDescription());
        tag.setActive(requestDto.isActive());

        Tag updatedTag = tagRepository.save(tag);
        return convertToResponseDto(updatedTag);
    }

    public void deleteTag(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("태그를 찾을 수 없습니다: " + id));

        // 해당 태그의 게시글 수 확인
        long postCount = tag.getPosts().size();
        if (postCount > 0) {
            throw new RuntimeException("게시글이 있는 태그는 삭제할 수 없습니다. 게시글 수: " + postCount);
        }

        tagRepository.delete(tag);
    }

    public List<Tag> findOrCreateTags(List<String> tagNames) {
        return tagNames.stream()
                .map(this::findOrCreateTag)
                .collect(Collectors.toList());
    }

    private Tag findOrCreateTag(String tagName) {
        return tagRepository.findByName(tagName)
                .orElseGet(() -> {
                    Tag newTag = Tag.builder()
                            .name(tagName)
                            .active(true)
                            .build();
                    return tagRepository.save(newTag);
                });
    }

    private TagResponseDto convertToResponseDto(Tag tag) {
        long postCount = tag.getPosts().size();
        
        return TagResponseDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .description(tag.getDescription())
                .active(tag.isActive())
                .createdAt(tag.getCreatedAt())
                .updatedAt(tag.getUpdatedAt())
                .postCount(postCount)
                .build();
    }
}
