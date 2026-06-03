package com.furnituree.furnituree.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.furnituree.furnituree.dto.TagRequest;
import com.furnituree.furnituree.exception.BadRequestException;
import com.furnituree.furnituree.exception.ResourceNotFoundException;
import com.furnituree.furnituree.model.Product;
import com.furnituree.furnituree.model.Tag;
import com.furnituree.furnituree.repo.product_repo;
import com.furnituree.furnituree.repo.tag_repo;

@Service
public class TagService {

    private final tag_repo tagRepo;
    private final product_repo productRepo;

    public TagService(tag_repo tagRepo, product_repo productRepo) {
        this.tagRepo = tagRepo;
        this.productRepo = productRepo;
    }

    public List<Tag> getAllTags() {
        return tagRepo.findAll();
    }

    public List<Tag> getActiveTags() {
        return tagRepo.findByStatusIgnoreCaseOrderByNameAsc("ACTIVE");
    }

    public Tag getTagById(Long id) {
        return tagRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id " + id));
    }

    public Tag createTag(TagRequest request) {
        validateTagRequest(request);

        String name = request.getName().trim();

        if (tagRepo.existsByNameIgnoreCase(name)) {
            throw new BadRequestException("Tag name already exists");
        }

        Tag tag = new Tag();
        tag.setName(name);
        tag.setDescription(request.getDescription());
        tag.setStatus(request.getStatus() == null ? "ACTIVE" : request.getStatus());

        return tagRepo.save(tag);
    }

    public Tag updateTag(Long id, TagRequest request) {
        validateTagRequest(request);

        Tag tag = getTagById(id);
        String newName = request.getName().trim();

        tagRepo.findByNameIgnoreCase(newName).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new BadRequestException("Tag name already exists");
            }
        });

        tag.setName(newName);
        tag.setDescription(request.getDescription());

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            validateStatus(request.getStatus());
            tag.setStatus(request.getStatus());
        }

        return tagRepo.save(tag);
    }

    public Tag deactivateTag(Long id) {
        Tag tag = getTagById(id);
        tag.setStatus("INACTIVE");
        return tagRepo.save(tag);
    }

    @Transactional
    public Product assignTagToProduct(Long productId, Long tagId) {
        Product product = getProduct(productId);
        Tag tag = getTagById(tagId);

        if (!"ACTIVE".equalsIgnoreCase(tag.getStatus())) {
            throw new BadRequestException("Cannot assign an inactive tag to a product");
        }

        product.getTags().add(tag);
        return productRepo.save(product);
    }

    @Transactional
    public Product removeTagFromProduct(Long productId, Long tagId) {
        Product product = getProduct(productId);
        Tag tag = getTagById(tagId);

        product.getTags().removeIf(existingTag -> existingTag.getId().equals(tag.getId()));
        return productRepo.save(product);
    }

    public Set<Tag> getTagsForProduct(Long productId) {
        return getProduct(productId).getTags();
    }

    private Product getProduct(Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
    }

    private void validateTagRequest(TagRequest request) {
        if (request == null) {
            throw new BadRequestException("Tag information is required");
        }

        if (request.getName() == null || !request.getName().trim().matches("^[A-Za-zÀ-ỹ0-9\\s-]{2,80}$")) {
            throw new BadRequestException("Tag name must be 2-80 characters and cannot contain invalid symbols");
        }

        if (request.getDescription() != null && request.getDescription().length() > 255) {
            throw new BadRequestException("Tag description must be at most 255 characters");
        }

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            validateStatus(request.getStatus());
        }
    }

    private void validateStatus(String status) {
        String normalized = status.trim().toUpperCase();

        if (!normalized.equals("ACTIVE") && !normalized.equals("INACTIVE")) {
            throw new BadRequestException("Tag status must be ACTIVE or INACTIVE");
        }
    }
}
