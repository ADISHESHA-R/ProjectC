package com.attendance.system.service;

import com.attendance.system.dto.request.CreateNoticeRequest;
import com.attendance.system.dto.request.UpdateNoticeRequest;
import com.attendance.system.dto.response.NoticeResponse;
import com.attendance.system.entity.Notice;
import com.attendance.system.exception.ResourceNotFoundException;
import com.attendance.system.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private static final int ADMIN_NOTICE_LIST_MAX = 10_000;

    private final NoticeRepository noticeRepository;

    @Transactional
    public NoticeResponse create(CreateNoticeRequest request) {
        Notice notice = new Notice();
        notice.setMessage(request.getMessage().trim());
        notice = noticeRepository.save(notice);
        return mapToResponse(notice);
    }

    @Transactional
    public NoticeResponse update(Long id, UpdateNoticeRequest request) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notice", id));
        if (request.getMessage() != null) {
            notice.setMessage(request.getMessage().trim());
        }
        notice = noticeRepository.save(notice);
        return mapToResponse(notice);
    }

    @Transactional
    public void delete(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notice", id));
        noticeRepository.delete(notice);
    }

    public NoticeResponse getById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notice", id));
        return mapToResponse(notice);
    }

    public List<NoticeResponse> getAll() {
        return noticeRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Page<NoticeResponse> searchNotices(String search, Pageable pageable) {
        String q = (search != null && !search.isBlank()) ? search.trim() : null;
        if (q == null) {
            return noticeRepository.filterNotices(pageable).map(this::mapToResponse);
        }
        return noticeRepository.searchNotices(q, pageable).map(this::mapToResponse);
    }

    public List<NoticeResponse> listNoticesForAdmin(String search) {
        Pageable pageable = PageRequest.of(0, ADMIN_NOTICE_LIST_MAX,
                Sort.by(Sort.Direction.DESC, "updatedAt"));
        return searchNotices(search, pageable).getContent();
    }

    private NoticeResponse mapToResponse(Notice notice) {
        return new NoticeResponse(
                notice.getId(),
                notice.getMessage(),
                notice.getCreatedAt(),
                notice.getUpdatedAt()
        );
    }
}
