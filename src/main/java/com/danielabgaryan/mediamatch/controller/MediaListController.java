package com.danielabgaryan.mediamatch.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.danielabgaryan.mediamatch.model.MediaList;
import com.danielabgaryan.mediamatch.repository.MediaListRepository;

@RestController
@RequestMapping("/media_lists")
public class MediaListController {

    private final MediaListRepository repository;

    public MediaListController(MediaListRepository repository) {
        this.repository = repository;
    }

    //CREATE (POST)
    @PostMapping
    public MediaList addMediaList(@RequestBody MediaList mediaList) {
        return repository.save(mediaList);
    }

    //READ (GET ALL)
    @GetMapping
    public List<MediaList> getAllMediaLists() {
        return repository.findAll();
    }
}
