package com.marvel.server.service;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marvel.server.repository.CommentRepository;

@Service
public class CommentService {
    
    @Autowired
    private CommentRepository commentRepo;

    public void saveComment(Integer postId, Map<String,String> comment){
        commentRepo.saveComment(postId, comment);
    }

    public List<String> getComments(Integer id){
        List<Document> docs = commentRepo.getComments(id);
        List<String> comments = docs.stream().map(d -> d.getString("comment")).toList();
        return comments;
        
    }
}
