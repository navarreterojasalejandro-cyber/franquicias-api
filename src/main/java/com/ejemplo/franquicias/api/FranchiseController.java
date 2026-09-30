package com.ejemplo.franquicias.api;

import com.ejemplo.franquicias.api.dto.BranchProductStock;
import com.ejemplo.franquicias.api.dto.Requests.NameRequest;
import com.ejemplo.franquicias.api.dto.Requests.ProductRequest;
import com.ejemplo.franquicias.api.dto.Requests.StockRequest;
import com.ejemplo.franquicias.domain.Franchise;
import com.ejemplo.franquicias.service.FranchiseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/franquicias")
public class FranchiseController {
    private final FranchiseService service;

    public FranchiseController(FranchiseService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Franchise> create(@Valid @RequestBody NameRequest request) {
        return service.createFranchise(request.name());
    }

    @GetMapping
    public Flux<Franchise> list() { return service.listFranchises(); }

    @GetMapping("/{franchiseId}")
    public Mono<Franchise> get(@PathVariable String franchiseId) { return service.getFranchise(franchiseId); }

    @PatchMapping("/{franchiseId}")
    public Mono<Franchise> rename(@PathVariable String franchiseId, @Valid @RequestBody NameRequest request) {
        return service.renameFranchise(franchiseId, request.name());
    }

    @PostMapping("/{franchiseId}/sucursales")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Franchise> addBranch(@PathVariable String franchiseId, @Valid @RequestBody NameRequest request) {
        return service.addBranch(franchiseId, request.name());
    }

    @PatchMapping("/{franchiseId}/sucursales/{branchId}")
    public Mono<Franchise> renameBranch(@PathVariable String franchiseId, @PathVariable String branchId,
                                        @Valid @RequestBody NameRequest request) {
        return service.renameBranch(franchiseId, branchId, request.name());
    }

    @PostMapping("/{franchiseId}/sucursales/{branchId}/productos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Franchise> addProduct(@PathVariable String franchiseId, @PathVariable String branchId,
                                      @Valid @RequestBody ProductRequest request) {
        return service.addProduct(franchiseId, branchId, request.name(), request.stock());
    }

    @DeleteMapping("/{franchiseId}/sucursales/{branchId}/productos/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteProduct(@PathVariable String franchiseId, @PathVariable String branchId,
                                    @PathVariable String productId) {
        return service.deleteProduct(franchiseId, branchId, productId);
    }

    @PatchMapping("/{franchiseId}/sucursales/{branchId}/productos/{productId}/stock")
    public Mono<Franchise> updateStock(@PathVariable String franchiseId, @PathVariable String branchId,
                                       @PathVariable String productId, @Valid @RequestBody StockRequest request) {
        return service.updateStock(franchiseId, branchId, productId, request.stock());
    }

    @PatchMapping("/{franchiseId}/sucursales/{branchId}/productos/{productId}")
    public Mono<Franchise> renameProduct(@PathVariable String franchiseId, @PathVariable String branchId,
                                         @PathVariable String productId, @Valid @RequestBody NameRequest request) {
        return service.renameProduct(franchiseId, branchId, productId, request.name());
    }

    @GetMapping("/{franchiseId}/productos-mayor-stock")
    public Mono<List<BranchProductStock>> topProductPerBranch(@PathVariable String franchiseId) {
        return service.topProductPerBranch(franchiseId);
    }
}
