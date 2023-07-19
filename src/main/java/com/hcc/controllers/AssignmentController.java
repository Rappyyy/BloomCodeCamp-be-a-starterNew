package com.hcc.controllers;

import com.hcc.DTOs.AssignmentResponseDto;
import com.hcc.entities.Assignment;
import com.hcc.entities.User;
import com.hcc.services.AssignmentService;
import com.hcc.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/assignments")
@CrossOrigin(origins = "http://localhost:3000")
public class AssignmentController {
    @Autowired
    private AssignmentService assignmentService;
    @Autowired
    private UserService userService;

//    @PostMapping
//    public ResponseEntity<?> createAssignment(@RequestBody Assignment assignment, @AuthenticationPrincipal User user) {
//        assignment.setUser(user);
//        assignment.setCodeReviewer(user);
//        Assignment createdAssignment = assignmentService.save(assignment);
//        return ResponseEntity.ok(createdAssignment);
//    }
    @PostMapping
    public ResponseEntity<?> createAssignment(@RequestBody Assignment assignment, @AuthenticationPrincipal User user) {
        assignment.setUser(user);
        assignment.setCodeReviewer(user);
        Assignment createdAssignment = assignmentService.save(assignment);
        AssignmentResponseDto assignmentDto = mapToDto(createdAssignment);
        return ResponseEntity.ok(assignmentDto);
    }

    @GetMapping
    public ResponseEntity<?> getAssignments(@AuthenticationPrincipal User user) {
        Set<Assignment> assignmentsByUser = assignmentService.findByUser(user);
        Set<AssignmentResponseDto> assignmentDtos = assignmentsByUser.stream()
                .map(this::mapToDto)
                .collect(Collectors.toSet());
        return ResponseEntity.ok(assignmentDtos);
    }

    @GetMapping("{assignmentId}")
    public ResponseEntity<?> getAssignment(@AuthenticationPrincipal User user, @PathVariable Long assignmentId) {
        Optional<Assignment> assignment = assignmentService.findById(assignmentId);
        if (assignment.isPresent()) {
            AssignmentResponseDto assignmentDto = mapToDto(assignment.get());
            return ResponseEntity.ok(assignmentDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/{assignmentId}")
    public ResponseEntity<?> updateAssignment(
            @PathVariable Long assignmentId,
            @RequestBody Assignment updatedAssignment,
            @AuthenticationPrincipal User user) {

        Optional<Assignment> assignment = assignmentService.findById(assignmentId);

        if (assignment.isPresent()) {
            Assignment existingAssignment = assignment.get();

            existingAssignment.setStatus(updatedAssignment.getStatus());
            existingAssignment.setNumber(updatedAssignment.getNumber());
            existingAssignment.setBranch(updatedAssignment.getBranch());
            existingAssignment.setReviewVideoUrl(updatedAssignment.getReviewVideoUrl());
            existingAssignment.setGithubUrl(updatedAssignment.getGithubUrl());
            existingAssignment.setCodeReviewer(user);

            Assignment updated = assignmentService.save(existingAssignment);

            return ResponseEntity.ok(mapToDto(updated));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken() {
        return ResponseEntity.ok("Token is valid");
    }

    private AssignmentResponseDto mapToDto(Assignment assignment) {
        AssignmentResponseDto dto = new AssignmentResponseDto();
        dto.setId(assignment.getId());
        dto.setStatus(assignment.getStatus());
        dto.setNumber(assignment.getNumber());
        dto.setGithubUrl(assignment.getGithubUrl());
        dto.setBranch(assignment.getBranch());
        dto.setReviewVideoUrl(assignment.getReviewVideoUrl());
        dto.setUserId(assignment.getUser().getId());
        dto.setCodeReviewerId(assignment.getCodeReviewer().getId());
        return dto;
    }
}
