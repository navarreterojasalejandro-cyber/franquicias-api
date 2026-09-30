package com.ejemplo.franquicias.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document("franchises")
public class Franchise {
    @Id
    private String id;
    private String name;
    private List<Branch> branches = new ArrayList<>();
    @Version
    private Long version;

    public Franchise() { }

    public Franchise(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Branch> getBranches() { return branches; }
    public void setBranches(List<Branch> branches) { this.branches = branches; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public static class Branch {
        private String id;
        private String name;
        private List<Product> products = new ArrayList<>();

        public Branch() { }
        public Branch(String name) {
            this.id = UUID.randomUUID().toString();
            this.name = name;
        }
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<Product> getProducts() { return products; }
        public void setProducts(List<Product> products) { this.products = products; }
    }

    public static class Product {
        private String id;
        private String name;
        private int stock;

        public Product() { }
        public Product(String name, int stock) {
            this.id = UUID.randomUUID().toString();
            this.name = name;
            this.stock = stock;
        }
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getStock() { return stock; }
        public void setStock(int stock) { this.stock = stock; }
    }
}
