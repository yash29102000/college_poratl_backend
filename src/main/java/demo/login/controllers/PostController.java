package demo.login.controllers;

import demo.login.payload.response.PostResponse;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import demo.login.data.Post;
import demo.login.data.User;
import demo.login.repository.BlobStorageRepository;
import demo.login.repository.PostRepository;
import demo.login.repository.UserRepository;

@RestController
@RequestMapping(path = "/api")
@CrossOrigin(origins = "*")
public class PostController {
    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BlobStorageRepository blobStorageRepository;

    @Autowired
    CommonService commonService;

    @PostMapping("/post")
    public ResponseEntity<String> testUpload(@RequestParam("userId") Long userId,
             @RequestParam("postContent") String postContent,
            @RequestParam(name = "file", required = false) MultipartFile file) {
        try {
            String fileType = file == null ? null : file.getContentType();
            String fileUrl = blobStorageRepository.uploadFile("postdocuments", file);
            User user = userRepository.findById(userId).get();
            Post post = new Post(user, postContent, fileType, fileUrl);
            postRepository.save(post);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IOException | InvalidKeyException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/users/{id}/posts")
    public List<PostResponse> getPostsByUserId(@PathVariable(name = "id") Long id) {
        User user = userRepository.findById(id).get();
        List<Post> posts = postRepository.findAllByUser(user);
        return posts.stream().map(commonService::mapPostToPostResponse).collect(Collectors.toList());
    }

    @GetMapping("/posts")
    public List<PostResponse> getPosts() {
        List<Post> posts = postRepository.findAll();
        posts.sort((a, b) -> b.getPostDate().compareTo(a.getPostDate()));
        return posts.stream().map(commonService::mapPostToPostResponse).collect(Collectors.toList());
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isPresent()) {
            return ResponseEntity.ok(commonService.mapPostToPostResponse(postOptional.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        try {
            postRepository.deleteById(id);
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
