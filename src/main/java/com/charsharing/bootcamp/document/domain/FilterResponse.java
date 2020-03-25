package com.charsharing.bootcamp.document.domain;

import lombok.Data;

@Data
public final class FilterResponse {
    private boolean valid = false;
    private String filteredText;
}
