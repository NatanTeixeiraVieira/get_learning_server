package com.example.get_learning_server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Table(name = "post")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class Post extends Auditable implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private Boolean allowComments;

  private String content;

  private String title;

  private String subtitle;

  private LocalDateTime postTime;

  @ManyToOne
  @JoinColumn(name = "author_id")
  private Author author;

  @OneToOne
  @JoinColumn(name = "cover_image_id")
  private CoverImage coverImage;

  @ManyToMany(fetch = FetchType.EAGER
//    ,cascade = CascadeType.ALL
  )
  @JoinTable(name = "post_category",
      joinColumns = {@JoinColumn(name = "post_id")},
      inverseJoinColumns = {@JoinColumn(name = "category_id")})
  private List<Category> categories;

  @ManyToMany(fetch = FetchType.EAGER
//      , cascade = {CascadeType.PERSIST, CascadeType.MERGE}
  )
  @JoinTable(name = "post_tag",
      joinColumns = {@JoinColumn(name = "post_id")},
      inverseJoinColumns = {@JoinColumn(name = "tag_id")})
  private List<Tag> tags;
}
