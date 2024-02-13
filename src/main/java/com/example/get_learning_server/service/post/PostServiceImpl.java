package com.example.get_learning_server.service.post;

import com.example.get_learning_server.controller.PostController;
import com.example.get_learning_server.dto.request.savePost.SavePostRequestDTO;
import com.example.get_learning_server.dto.request.updatePost.UpdatePostRequestDTO;
import com.example.get_learning_server.dto.response.getAllPosts.Posts;
import com.example.get_learning_server.dto.response.getPostById.GetPostByIdResponseDTO;
import com.example.get_learning_server.dto.response.savePost.SavePostResponseDTO;
import com.example.get_learning_server.dto.response.updatePost.UpdatePostResponseDTO;
import com.example.get_learning_server.entity.*;
import com.example.get_learning_server.exception.NoPermissionException;
import com.example.get_learning_server.exception.NoPostFoundException;
import com.example.get_learning_server.repository.*;
import com.example.get_learning_server.util.Constants;
import com.example.get_learning_server.util.MethodsUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
  private final Environment environment;
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
  public SavePostResponseDTO savePost(MultipartFile coverImageFile, String dto) throws IOException {
    logger.info("saving one post");

    final SavePostRequestDTO postData = objectMapper.readValue(dto, SavePostRequestDTO.class);

    final User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    final BlobInfo blobInfo = uploadCoverImage(coverImageFile);

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

  @Override
  public UpdatePostResponseDTO updatePost(@Nullable MultipartFile coverImageFile, String dto) throws IOException {
    logger.info("Updating one post");

    final UpdatePostRequestDTO postData = objectMapper.readValue(dto, UpdatePostRequestDTO.class);
    final Post post = postRepository
        .findById(postData.getId())
        .orElseThrow(() -> new NoPostFoundException("Invalid data"));

    final User loggedUser = MethodsUtil.getLoggedUser();

    final Author loggedAuthor = authorRepository.findByUser(loggedUser).get();

    if(!loggedAuthor.getId().equals(post.getAuthor().getId()))
      throw new NoPermissionException("You don't have permission to execute this action");

    if(coverImageFile != null && postData.getCoverImageId() != null) {
      final CoverImage coverImage = coverImageRepository.findById(postData.getCoverImageId()).get();
      final BlobInfo blobInfo = uploadCoverImage(coverImageFile);

      // Delete previous image from FireStorage
      Blob blobToDelete = StorageClient.getInstance().bucket().get(coverImage.getName());
      blobToDelete.delete();

      coverImage.setName(blobInfo.getName());
      coverImage.setUrl(blobInfo.getMediaLink());

      final CoverImage savedCoverImage = coverImageRepository.save(coverImage);
      post.setCoverImage(savedCoverImage);
    }

    final List<Category> postCategoriesList = postData
        .getCategories()
        .stream()
        .map(categoryRepository::findByName)
        .toList();

    final List<Tag> postTagsList = postData
        .getTags()
        .stream()
        .map(tag -> {
          final Tag newTag = tagRepository.findById(tag.getId()).get();
          newTag.setName(tag.getName());
          newTag.setSlug(MethodsUtil.generateSlug(tag.getName()));
          return newTag;
        })
        .toList();

    post.setAllowComments(postData.getAllowComments());
    post.setTitle(postData.getTitle());
    post.setSubtitle(postData.getSubtitle());
    post.setContent(postData.getContent());
    post.setCategories(postCategoriesList);
    post.setTags(postTagsList);

    final Post postUpdated = postRepository.save(post);

    final UpdatePostResponseDTO updatePostResponseDTO = mapper.map(postUpdated, UpdatePostResponseDTO.class);
    updatePostResponseDTO
        .add(linkTo(methodOn(PostController.class).findPostById(updatePostResponseDTO.getId())).withSelfRel());

    return updatePostResponseDTO;
  }

  public void deletePost(UUID postId) {
    final Post post = postRepository
        .findById(postId)
        .orElseThrow(() -> new NoPostFoundException("No post found for this id"));

    postRepository.delete(post);
  }

  private BlobInfo uploadCoverImage(MultipartFile coverImageFile) throws IOException {
    final String coverImagePath = Constants.coverImageBasePath + UUID.randomUUID();

    return StorageClient
        .getInstance()
        .bucket()
        .create(coverImagePath, coverImageFile.getInputStream(), coverImageFile.getContentType());
  }
}
