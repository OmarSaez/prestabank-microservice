package com.example.m5_follow.Controller;

import com.example.m5_follow.Service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/follow")
public class FollowController {
    @Autowired
    FollowService followService;

    @GetMapping("/{status}")
    public ResponseEntity<Integer> getFollowers(@PathVariable int status) {
        return ResponseEntity.ok(followService.followUp(status));
    }

}
