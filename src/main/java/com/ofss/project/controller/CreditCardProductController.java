package com.ofss.project.controller;

import com.ofss.project.dto.response.CreditCardProductResponse;
import com.ofss.project.service.CreditCardProductService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CreditCardProductController {

	private final CreditCardProductService productService;

	@GetMapping
	public ResponseEntity<List<CreditCardProductResponse>> getActiveProducts() {

		return ResponseEntity.ok(productService.getActiveProducts());
	}

	@GetMapping("/{id}")
	public ResponseEntity<CreditCardProductResponse> getActiveProduct(@PathVariable Long id) {

		return ResponseEntity.ok(productService.getActiveProduct(id));
	}
}