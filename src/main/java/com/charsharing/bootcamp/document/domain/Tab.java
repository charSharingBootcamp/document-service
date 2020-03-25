package com.charsharing.bootcamp.document.domain;

import lombok.Data;

import java.util.List;

@Data
public class Tab {

    private List<Block> textBlocks;

    String title;

    public Tab(String title) {
        this.title = title;
    }

    public Tab(){};
}
