package org.flow_manager.controller;

import lombok.RequiredArgsConstructor;
import org.flow_manager.model.dto.FlowManagerResponse;
import org.flow_manager.service.FlowManagerService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/flow_manager")
public class FlowManagerController {
    private final FlowManagerService flowManagerService;

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FlowManagerResponse sendFile(@RequestParam("file") MultipartFile file) {
        return flowManagerService.sendFile(file);
    }

    @GetMapping("/status")
    public FlowManagerResponse getStatus(@RequestParam Long id) {
        return flowManagerService.getStatus(id);
    }

    @GetMapping("/download")
    public FlowManagerResponse getPdfById(@RequestParam Long id) {
        return flowManagerService.getPdfById(id);
    }
}
