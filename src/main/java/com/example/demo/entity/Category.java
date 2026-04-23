package com.example.demo.entity;

import lombok.Setter;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@Setter

public class Category {
    private int categoryId;
    private String categoryCode;
    private String categoryName;
    private String description;
    private Timestamp createdAt;
    private Timestamp modifiedAt;
}
