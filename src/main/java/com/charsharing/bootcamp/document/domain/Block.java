package com.charsharing.bootcamp.document.domain;

import lombok.Data;

import java.util.Date;

@Data
public class Block {

    private String id;

    private String content;

    private String creator;

    private Date createdAt;

    private boolean code;

    private Date updatedAt;

    private String updatedBy;
}
