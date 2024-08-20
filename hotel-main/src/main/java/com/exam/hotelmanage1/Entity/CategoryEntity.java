package com.exam.hotelmanage1.Entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = {"storeEntity", "children", "parent","category1","category2"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "category")
@SequenceGenerator(name = "category_sql", sequenceName = "category_sql",
        initialValue = 1,allocationSize = 1)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "categoryNo")
public class CategoryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "category_sql")
    @Column(name="category_no")
    private Long categoryNo;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "category_no")
    private CategoryEntity parent;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="store_no")
    private StoreEntity storeEntity;
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<CategoryEntity> children = new ArrayList<>();
    @OneToMany(mappedBy = "category1", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuEntity> category1 = new ArrayList<>();
    @OneToMany(mappedBy = "category2", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuEntity> category2 = new ArrayList<>();

}
