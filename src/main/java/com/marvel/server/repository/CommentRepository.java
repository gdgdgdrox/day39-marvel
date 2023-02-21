package com.marvel.server.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;


import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class CommentRepository {
    private static final String COLLECTION_COMMENTS = "comments";
    @Autowired
    private MongoTemplate mongoTemp; 

    public void saveComment(Integer id, Map<String,String> comment){
        Document doc = new Document();
        doc.put("characterId", id);
        doc.put("comment", comment.get("comment"));
        doc.put("posted", new Timestamp(System.currentTimeMillis()));
        System.out.println("DOC FOR INSERTION : " + doc);
        mongoTemp.insert(doc, COLLECTION_COMMENTS);
    }

    public List<Document> getComments(Integer id){
        Criteria criteria = Criteria.where("characterId").is(id);
        Query query = new Query().addCriteria(criteria);
        query.fields().exclude("_id").include("comment");
        //get the 10 most recent comment
        query.limit(10).with(Sort.by(Direction.DESC, "posted"));
        System.out.println("SEARCHING FOR LATEST 10 COMMENTS FOR CHARACTER %d".formatted(id));
        List<Document> docs = mongoTemp.find(query, Document.class, COLLECTION_COMMENTS);
        return docs;
    }
}
