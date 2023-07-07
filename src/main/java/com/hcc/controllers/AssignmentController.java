package com.hcc.controllers;

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

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createAssignment(@RequestBody Assignment assignment, @AuthenticationPrincipal User user) {

        assignment.setUser(user);
        assignment.setCodeReviewer(user);
        Assignment createdAssignment = assignmentService.save(assignment);

        return ResponseEntity.ok(createdAssignment);
    }

    @GetMapping
    public ResponseEntity<?> getAssignments(@AuthenticationPrincipal User user) {
        Set<Assignment> assignmentsByUser = assignmentService.findByUser(user);
        return ResponseEntity.ok(assignmentsByUser);
    }

    @GetMapping("{assignmentId}")
    public ResponseEntity<?> getAssignments(@AuthenticationPrincipal User user, @PathVariable Long assignmentId) {
        Optional<Assignment> assignment = assignmentService.findById(assignmentId);
        return ResponseEntity.ok(assignment);
    }


    @PutMapping("{assignmentId}")
    public ResponseEntity<?> updateAssignment(
            @PathVariable Long assignmentId,
            @RequestBody Assignment updatedAssignment,
            @AuthenticationPrincipal User user) {

        Optional<Assignment> assignment = assignmentService.findById(assignmentId);

        assignment.get().setStatus(updatedAssignment.getStatus());
        assignment.get().setNumber(updatedAssignment.getNumber());
        assignment.get().setBranch(updatedAssignment.getBranch());
        assignment.get().setReviewVideoUrl(updatedAssignment.getReviewVideoUrl());
        assignment.get().setGithubUrl(updatedAssignment.getGithubUrl());
        assignment.get().setCodeReviewer(user);

        Assignment createAssignment = assignmentService.save(assignment.get());

        return ResponseEntity.ok(createAssignment);
    }


    @GetMapping("/validate")
    public ResponseEntity<?> validateToken() {
        return ResponseEntity.ok("Token is valid");
    }
}
