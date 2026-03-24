package com.example.demo.board.service;

import com.example.demo.board.entity.Board;
import com.example.demo.board.entity.Comment;
import com.example.demo.board.repository.BoardRepository;
import com.example.demo.board.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BaseBoardService implements BoardService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    @Override
    public Board save(String title, String content, String loginId, String name) {
        Board board = new Board(title, content, loginId, name);
        return boardRepository.save(board);
    }

    @Override
    public List<Board> findAll() {
        return boardRepository.findAllByOrderByCreatedDesc();
    }

    @Override
    public void update(Integer id, String title, String content) {
        Board board = boardRepository.findById(id).orElseThrow();
        board.update(title, content);
        boardRepository.save(board);
    }

    @Override
    public void delete(Integer id) {
        boardRepository.deleteById(id);
    }

    @Override
    public Comment saveComment(Integer boardId, String content, String loginId, String name) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        Comment comment = new Comment(content, loginId, name, board);
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> findComments(Integer boardId) {
        return commentRepository.findByBoardIdOrderByCreatedAsc(boardId);
    }

    @Override
    public void deleteComment(Integer id) {
        commentRepository.deleteById(id);
    }
}