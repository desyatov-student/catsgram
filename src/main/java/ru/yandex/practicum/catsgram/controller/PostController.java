package ru.yandex.practicum.catsgram.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.exception.PostNotFoundException;
import ru.yandex.practicum.catsgram.model.Pagination;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;
import ru.yandex.practicum.catsgram.model.SortOrder;

import java.util.Collection;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Collection<Post> findAll(
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size
    ) {

        if (SortOrder.from(sort) == null) {
            throw new ParameterNotValidException("sort", "параметр sort должен содержать корректное значение");
        }

        if (size <= 0) {
            throw new ParameterNotValidException("size", "параметр size должен быть больше нуля");
        }

        if (from < 0) {
            throw new ParameterNotValidException("from", "параметр from не может быть меньше нуля");
        }



        SortOrder sortOrder = SortOrder.from(sort) == null ? SortOrder.DESCENDING : SortOrder.from(sort);
        Integer fromResult = from >= 0 ? from : 0;
        Integer sizeResult = size > 0 ? size : 10;
        Pagination pagination = new Pagination(sortOrder, fromResult, sizeResult);
        return postService.findAll(pagination);
    }

    @GetMapping("/{postId}")
    public Post findById(@PathVariable Long postId) {
        return postService.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(String.format("Пост № %d не найден", postId)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }
}
