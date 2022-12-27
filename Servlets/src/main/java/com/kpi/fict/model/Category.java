package com.kpi.fict.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@ToString
public class Category implements Comparable<Category> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String type;

    @Override
    public int compareTo(Category o) {
        return type.compareTo(o.type);
    }
}
