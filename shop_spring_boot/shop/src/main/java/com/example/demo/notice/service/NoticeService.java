package com.example.demo.notice.service;

import com.example.demo.notice.entity.Notice;
import java.util.List;

public interface NoticeService {

    Notice save(String title, String content, boolean important);

    List<Notice> findAll();
    void update(Integer id, String title, String content, boolean important);
    void delete(Integer id);
}