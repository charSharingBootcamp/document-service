package com.charsharing.bootcamp.document.domain;

import lombok.Data;

@Data
public class BlockUpdateRequest {

    private String content;
    private String username;
}
