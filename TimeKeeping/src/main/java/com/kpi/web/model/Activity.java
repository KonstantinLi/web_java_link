package com.kpi.web.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;

@Entity
@Table(name = "activities")
@Getter
@Setter
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL)
    private Category category;

    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

    private int duration;

    @Column(name = "confirmed")
    private boolean isConfirmed;

    @Override
    public String toString() {
        return "Activity(" +
                "id=" + id +
                ", category_id=" + category.getId() +
                ", user_id=" + user.getId() +
                ", duration=" + duration +
                ", isConfirmed=" + isConfirmed + ")";
    }
}
