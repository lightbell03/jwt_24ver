package com.example.jwt.controller;

import com.example.jwt.dto.response.CommonResponse;
import com.example.jwt.dto.response.DummyResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dummy")
public class DummyController {
    @GetMapping("/{dummyId}")
    public ResponseEntity<DummyResponse> getDummy(@PathVariable("dummyId") Long dummyId) {
        return ResponseEntity.status(HttpStatus.OK).body(new DummyResponse("dummy id = " + dummyId));
    }
}
