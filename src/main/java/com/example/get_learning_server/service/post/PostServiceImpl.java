package com.example.get_learning_server.service.post;

import com.example.get_learning_server.controller.PostController;
import com.example.get_learning_server.dto.request.savePost.SavePostRequestDTO;
import com.example.get_learning_server.dto.response.getAllPosts.Posts;
import com.example.get_learning_server.dto.response.getPostById.GetPostByIdResponseDTO;
import com.example.get_learning_server.dto.response.savePost.SavePostResponseDTO;
import com.example.get_learning_server.entity.*;
import com.example.get_learning_server.exception.NoPostFoundException;
import com.example.get_learning_server.repository.*;
import com.example.get_learning_server.util.MethodsUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.BlobInfo;
import com.google.firebase.cloud.StorageClient;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {
  private final Logger logger = Logger.getLogger(PostServiceImpl.class.getName());

  private final PostRepository postRepository;
  private final CoverImageRepository coverImageRepository;
  private final AuthorRepository authorRepository;
  private final CategoryRepository categoryRepository;
  private final TagRepository tagRepository;
  private final ModelMapper mapper;
  private final ObjectMapper objectMapper;
  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  private final PagedResourcesAssembler<Posts> assembler;

  @Override
  public PagedModel<EntityModel<Posts>> findAllPosts(Pageable pageable) {
    logger.info("Finding all posts");

    final Page<Post> postsEntity = postRepository.findAll(pageable);

    final Page<Posts> postsPageDTO = postsEntity
        .map((post -> mapper.map(post, Posts.class)));
    
    postsPageDTO.forEach(postPage ->
        postPage.add(linkTo(methodOn(PostController.class).findPostById(postPage.getId())).withSelfRel()));

    final Link link = linkTo(
        methodOn(PostController.class).findAllPosts(pageable.getPageNumber(), pageable.getPageSize(), "asc")
    ).withSelfRel();

    return assembler.toModel(postsPageDTO, link);
  }

  @Override
  public GetPostByIdResponseDTO findPostById(UUID postId) {
    final Post post = postRepository
        .findById(postId)
        .orElseThrow(() -> new NoPostFoundException("No post found for id: " + postId));
    final GetPostByIdResponseDTO postDto = mapper.map(post, GetPostByIdResponseDTO.class);
    postDto.add(
        linkTo(methodOn(PostController.class).findAllPosts(0, 12, "asc")).withRel("postsList")
    );
    return postDto;
  }

  @Override
  public SavePostResponseDTO savePost(
      MultipartFile coverImageFile,
      String title,
      String subtitle,
      String content,
      String allowComments,
      String categories,
      String tags) throws IOException {
    logger.info("saving one post");

    final ArrayList<String> categoriesList = objectMapper.readValue(categories, ArrayList.class);
    final ArrayList<String> tagsList = objectMapper.readValue(tags, ArrayList.class);

    final SavePostRequestDTO postData = new SavePostRequestDTO(
        title,
        subtitle,
        content,
        allowComments.equals("true"),
        categoriesList,
        tagsList
    );

    final User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    // Create a temporary file in server
    Path tempFile = Files.createTempFile("temp", coverImageFile.getOriginalFilename());
    Files.copy(coverImageFile.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

    // Upload image
    final BlobInfo blobInfo = StorageClient
        .getInstance()
        .bucket()
        .create("coverImage/" + coverImageFile.getOriginalFilename(), Files.readAllBytes(tempFile));

    // Remove temporary file
    Files.deleteIfExists(tempFile);

    final CoverImage coverImage = new CoverImage();
    coverImage.setName(blobInfo.getName());
    coverImage.setUrl(blobInfo.getMediaLink());

    final CoverImage savedCoverImage = coverImageRepository.save(coverImage);

    final Author loggedAuthor = authorRepository.findByUser(loggedUser).get();
    final Post post = mapper.map(postData, Post.class);
    post.setPostTime(LocalDateTime.now());
    post.setAuthor(loggedAuthor);
    post.setCoverImage(savedCoverImage);

    final ArrayList<Category> postCategoriesList = new ArrayList<>();
    postData.getCategories().forEach(categoryName ->
        postCategoriesList.add(categoryRepository.findByName(categoryName)));
    post.setCategories(postCategoriesList);

    final ArrayList<Tag> postTagsList = new ArrayList<>();
    postData.getTags().forEach(tagName -> {
      Tag tag = new Tag();
      tag.setName(tagName);
      tag.setSlug(MethodsUtil.generateSlug(tagName));
      tag = tagRepository.save(tag);
      postTagsList.add(tag);
    });
    post.setTags(postTagsList);

    final Post savedPost = postRepository.save(post);

    final SavePostResponseDTO savePostResponseDTO = mapper.map(savedPost, SavePostResponseDTO.class);
    savePostResponseDTO
        .add(linkTo(methodOn(PostController.class).findPostById(savePostResponseDTO.getId())).withSelfRel());

    return savePostResponseDTO;
  }
}
