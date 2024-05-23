package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.exception.PostNotFoundException;
import ru.yandex.practicum.catsgram.model.Pagination;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Указываем, что класс PostService - является бином и его
// нужно добавить в контекст приложения
@Service
public class PostService {
    private final Map<Long, Post> posts = new HashMap<>();
    private final UserService userService;

    public PostService(UserService userService) {
        this.userService = userService;
    }

    public Collection<Post> findAll(Pagination pagination) {
        Comparator<Post> comparator = pagination.getSortOrder().isAscending() ?
                Comparator.comparing(Post::getPostDate) : Comparator.comparing(Post::getPostDate).reversed();

        return sublist(posts.values().stream().sorted(comparator).toList(), pagination);
    }

    private List<Post> sublist(List<Post> list, Pagination pagination) {
        if (pagination.getFrom() < 0) {
            throw new IllegalArgumentException("from could not be negative");
        }
        if (pagination.getFrom() >= list.size()) {
            return List.of();
        }
        int endIndex = Integer.min(list.size(), pagination.getFrom() + pagination.getSize());
        return list.subList(pagination.getFrom(), endIndex);
    }

    public Optional<Post> findById(Long postId) {
        return posts.values().stream()
                .filter(x -> x.getId().equals(postId))
                .findFirst();
    }

    public Post create(Post post) {
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        Optional<User> userOpt = userService.findById(post.getAuthorId());
        if (userOpt.isEmpty()) {
            throw new ConditionsNotMetException("Автор с id = " + post.getAuthorId() + " не найден");
        }

        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (!posts.containsKey(newPost.getId())) {
            throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
        }

        Post oldPost = posts.get(newPost.getId());
        if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }
        oldPost.setDescription(newPost.getDescription());
        return oldPost;
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}