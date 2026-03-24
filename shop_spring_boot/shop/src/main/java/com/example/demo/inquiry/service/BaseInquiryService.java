package com.example.demo.inquiry.service;

import com.example.demo.inquiry.entity.Inquiry;
import com.example.demo.inquiry.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BaseInquiryService implements InquiryService {

    private final InquiryRepository inquiryRepository;

    @Override
    public Inquiry save(String type, String title, String content, String loginId, String name) {
        Inquiry inquiry = new Inquiry(type, title, content, loginId, name);
        return inquiryRepository.save(inquiry);
    }

    @Override
    public List<Inquiry> findByLoginId(String loginId) {
        return inquiryRepository.findByLoginIdOrderByCreatedDesc(loginId);
    }

    @Override
    public List<Inquiry> findAll() {
        return inquiryRepository.findAllByOrderByCreatedDesc();
    }

    @Override
    public void reply(Integer id, String reply) {
        Inquiry inquiry = inquiryRepository.findById(id).orElseThrow();
        inquiry.setReply(reply);
        inquiryRepository.save(inquiry);
    }

    @Override
    public void delete(Integer id) {
        inquiryRepository.deleteById(id);
    }
    @Override
    public void update(Integer id, String content) {
        Inquiry inquiry = inquiryRepository.findById(id).orElseThrow();
        inquiry.update(content);
        inquiryRepository.save(inquiry);
    }

}