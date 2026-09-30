package com.ejemplo.franquicias.api.dto;

public record BranchProductStock(String branchId, String branchName, String productId,
                                 String productName, int stock) { }
