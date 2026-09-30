package com.ejemplo.franquicias.service;

import com.ejemplo.franquicias.api.dto.BranchProductStock;
import com.ejemplo.franquicias.domain.Franchise;
import com.ejemplo.franquicias.domain.Franchise.Branch;
import com.ejemplo.franquicias.domain.Franchise.Product;
import com.ejemplo.franquicias.repository.FranchiseRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;

@Service
public class FranchiseService {
    private final FranchiseRepository repository;

    public FranchiseService(FranchiseRepository repository) {
        this.repository = repository;
    }

    public Mono<Franchise> createFranchise(String name) {
        return repository.save(new Franchise(name));
    }

    public Flux<Franchise> listFranchises() {
        return repository.findAll();
    }

    public Mono<Franchise> getFranchise(String franchiseId) {
        return requireFranchise(franchiseId);
    }

    public Mono<Franchise> renameFranchise(String franchiseId, String name) {
        return requireFranchise(franchiseId).flatMap(franchise -> {
            franchise.setName(name);
            return repository.save(franchise);
        });
    }

    public Mono<Franchise> addBranch(String franchiseId, String name) {
        return requireFranchise(franchiseId).flatMap(franchise -> {
            franchise.getBranches().add(new Branch(name));
            return repository.save(franchise);
        });
    }

    public Mono<Franchise> renameBranch(String franchiseId, String branchId, String name) {
        return requireBranch(franchiseId, branchId).flatMap(pair -> {
            pair.branch().setName(name);
            return repository.save(pair.franchise());
        });
    }

    public Mono<Franchise> addProduct(String franchiseId, String branchId, String name, int stock) {
        return requireBranch(franchiseId, branchId).flatMap(pair -> {
            pair.branch().getProducts().add(new Product(name, stock));
            return repository.save(pair.franchise());
        });
    }

    public Mono<Void> deleteProduct(String franchiseId, String branchId, String productId) {
        return requireBranch(franchiseId, branchId).flatMap(pair -> {
            boolean removed = pair.branch().getProducts().removeIf(product -> product.getId().equals(productId));
            if (!removed) return Mono.error(new ResourceNotFoundException("No existe el producto " + productId));
            return repository.save(pair.franchise()).then();
        });
    }

    public Mono<Franchise> updateStock(String franchiseId, String branchId, String productId, int stock) {
        return requireProduct(franchiseId, branchId, productId).flatMap(pair -> {
            pair.product().setStock(stock);
            return repository.save(pair.franchise());
        });
    }

    public Mono<Franchise> renameProduct(String franchiseId, String branchId, String productId, String name) {
        return requireProduct(franchiseId, branchId, productId).flatMap(pair -> {
            pair.product().setName(name);
            return repository.save(pair.franchise());
        });
    }

    public Mono<List<BranchProductStock>> topProductPerBranch(String franchiseId) {
        return requireFranchise(franchiseId).map(franchise -> franchise.getBranches().stream()
                .filter(branch -> !branch.getProducts().isEmpty())
                .map(branch -> branch.getProducts().stream()
                        .max(Comparator.comparingInt(Product::getStock))
                        .map(product -> new BranchProductStock(branch.getId(), branch.getName(),
                                product.getId(), product.getName(), product.getStock())))
                .flatMap(java.util.Optional::stream)
                .toList());
    }

    private Mono<Franchise> requireFranchise(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("No existe la franquicia " + id)));
    }

    private Mono<BranchPair> requireBranch(String franchiseId, String branchId) {
        return requireFranchise(franchiseId).flatMap(franchise -> franchise.getBranches().stream()
                .filter(branch -> branch.getId().equals(branchId))
                .findFirst()
                .map(branch -> Mono.just(new BranchPair(franchise, branch)))
                .orElseGet(() -> Mono.error(new ResourceNotFoundException("No existe la sucursal " + branchId))));
    }

    private Mono<ProductPair> requireProduct(String franchiseId, String branchId, String productId) {
        return requireBranch(franchiseId, branchId).flatMap(pair -> pair.branch().getProducts().stream()
                .filter(product -> product.getId().equals(productId))
                .findFirst()
                .map(product -> Mono.just(new ProductPair(pair.franchise(), pair.branch(), product)))
                .orElseGet(() -> Mono.error(new ResourceNotFoundException("No existe el producto " + productId))));
    }

    private record BranchPair(Franchise franchise, Branch branch) { }
    private record ProductPair(Franchise franchise, Branch branch, Product product) { }
}
