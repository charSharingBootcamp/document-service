package com.charsharing.bootcamp.document.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

// Creating entity model document
@org.springframework.data.mongodb.core.mapping.Document(collection = "document")
@Data
@AllArgsConstructor
public class Document {
    @Id
    private String title;
    private String creator;
    private Date createdAt;
    private Date updatedAt;
    private List<Tab> content;
    private boolean archived;

}
