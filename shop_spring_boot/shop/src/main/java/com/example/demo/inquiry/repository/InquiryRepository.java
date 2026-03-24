package com.example.demo.inquiry.repository;

import com.example.demo.inquiry.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Integer> {
    List<Inquiry> findByLoginIdOrderByCreatedDesc(String loginId);
    List<Inquiry> findAllByOrderByCreatedDesc();
}