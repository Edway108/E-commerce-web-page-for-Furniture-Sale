package com.furnituree.furnituree.Controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.furnituree.furnituree.dto.TagRequest;
import com.furnituree.furnituree.model.Product;
import com.furnituree.furnituree.model.Tag;
import com.furnituree.furnituree.service.TagService;

@RestController
@RequestMapping("/tags")
public class tag_controller {

    private final TagService tagService;

    public tag_controller(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public List<Tag> getAllTags() {
        return tagService.getAllTags();
    }

    @GetMapping("/active")
    public List<Tag> getActiveTags() {
        return tagService.getActiveTags();
    }

    @GetMapping("/{id}")
    public Tag getTagById(@PathVariable Long id) {
        return tagService.getTagById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Tag createTag(@RequestBody TagRequest request) {
        return tagService.createTag(request);
    }

    @PutMapping("/{id}")
    public Tag updateTag(@PathVariable Long id, @RequestBody TagRequest request) {
        return tagService.updateTag(id, request);
    }

    @PatchMapping("/{id}/deactivate")
    public Tag deactivateTag(@PathVariable Long id) {
        return tagService.deactivateTag(id);
    }

    @PostMapping("/products/{productId}/tags/{tagId}")
    public Product assignTagToProduct(@PathVariable Long productId, @PathVariable Long tagId) {
        return tagService.assignTagToProduct(productId, tagId);
    }

    @DeleteMapping("/products/{productId}/tags/{tagId}")
    public Product removeTagFromProduct(@PathVariable Long productId, @PathVariable Long tagId) {
        return tagService.removeTagFromProduct(productId, tagId);
    }

    @GetMapping("/products/{productId}")
    public Set<Tag> getTagsForProduct(@PathVariable Long productId) {
        return tagService.getTagsForProduct(productId);
    }
}
