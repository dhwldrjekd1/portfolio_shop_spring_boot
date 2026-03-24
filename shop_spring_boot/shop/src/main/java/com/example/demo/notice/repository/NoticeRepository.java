package com.example.demo.notice.repository;

import com.example.demo.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {
    List<Notice> findAllByOrderByImportantDescCreatedDesc();
}