package com.example.demo.notice.service;

import com.example.demo.notice.entity.Notice;
import com.example.demo.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BaseNoticeService implements NoticeService {

    private final NoticeRepository noticeRepository;

    @Override
    public Notice save(String title, String content, boolean important) {
        Notice notice = new Notice(title, content, important);
        return noticeRepository.save(notice);
    }

    @Override
    public List<Notice> findAll() {
        return noticeRepository.findAllByOrderByImportantDescCreatedDesc();
    }

    @Override
    public void update(Integer id, String title, String content, boolean important) {
        Notice notice = noticeRepository.findById(id).orElseThrow();
        notice.update(title, content, important);
        noticeRepository.save(notice);
    }

    @Override
    public void delete(Integer id) {
        noticeRepository.deleteById(id);
    }
}