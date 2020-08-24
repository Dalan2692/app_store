package com.jm.online_store.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false, nullable = false)
    private Long id;
    @NonNull
    @Column(name = "product", nullable = false)
    private String product;
    @NonNull
    @Column(name = "price", nullable = false)
    private Double price;
    @NonNull
    private Integer amount;
    @NonNull
    private Double rating;
    @OneToOne(cascade = CascadeType.ALL)
    private Description descriptions;
    @NonNull
    private String productType;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    @JsonBackReference
    private List<ProductInOrder> productInOrders;

    public Product(@NonNull String product, @NonNull Double price, @NonNull int amount, @NonNull Double rating) {
        this.product = product;
        this.price = price;
        this.amount = amount;
        this.rating = rating;
    }
}
