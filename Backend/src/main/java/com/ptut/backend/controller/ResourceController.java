package com.ptut.backend.controller;

import com.ptut.backend.dto.ResourceSummaryResponse;
import com.ptut.backend.entity.DocumentResource;
import com.ptut.backend.service.DocumentResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    private final DocumentResourceService documentResourceService;

    public ResourceController(DocumentResourceService documentResourceService) {
        this.documentResourceService = documentResourceService;
    }

    @Operation(summary = "Upload d'une ressource (ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResourceSummaryResponse> upload(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        ResourceSummaryResponse response = documentResourceService.upload(file, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lister les ressources (AMBASSADEUR/ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<List<ResourceSummaryResponse>> list() {
        return ResponseEntity.ok(documentResourceService.listAll());
    }

    @Operation(summary = "Télécharger une ressource (AMBASSADEUR/ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}/download")
    public ResponseEntity<ByteArrayResource> download(@PathVariable Long id) {
        DocumentResource resource = documentResourceService.getById(id);

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (resource.getContentType() != null && !resource.getContentType().isBlank()) {
            mediaType = MediaType.parseMediaType(resource.getContentType());
        }

        ByteArrayResource body = new ByteArrayResource(resource.getContent());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentLength(resource.getSize());
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(resource.getFileName(), StandardCharsets.UTF_8)
                        .build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(body);
    }

    @Operation(summary = "Supprimer une ressource (ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        documentResourceService.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Ressource supprimée"));
    }
}
