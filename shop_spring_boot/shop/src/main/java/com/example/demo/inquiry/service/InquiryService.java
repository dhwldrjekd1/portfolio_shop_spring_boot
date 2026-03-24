package com.example.demo.inquiry.service;

import com.example.demo.inquiry.entity.Inquiry;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InquiryService {


    Inquiry save(String type, String title, String content, String loginId, String name);
    List<Inquiry> findByLoginId(String loginId);
    List<Inquiry> findAll();
    void reply(Integer id, String reply);
    void delete(Integer id);
    void update(Integer id, String content);
}