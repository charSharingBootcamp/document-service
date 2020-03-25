package com.charsharing.bootcamp.document.repository;

import com.charsharing.bootcamp.document.domain.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends MongoRepository<Document, String> {
    Document findDocumentByTitle(String id);

    void deleteByTitleIn(List<String> titles);

    @Override
    @Query(value="{}", fields = "{content: 0}")
    List<Document> findAll();

    @Query(value="{archived:false}", fields = "{content:0}")
    List<Document> findAllExcludingArchived();
}
