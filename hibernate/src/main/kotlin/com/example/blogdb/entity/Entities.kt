package com.example.blogdb.entity

import jakarta.persistence.*


@NamedEntityGraph(
    name = "user-with-blog-with-articles",
    attributeNodes = [NamedAttributeNode(value = "blog", subgraph = "blog-articles")],
    subgraphs = [NamedSubgraph(name = "blog-articles", attributeNodes = [NamedAttributeNode("articles")])]
)
@Entity
@Table(name="users")
data class User(
    @Column(nullable = false, unique = true)
    val name: String,
    @OneToOne(cascade = [(CascadeType.REMOVE)], orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name="blog_id")
    var blog: Blog?=null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int?=null,
)

@Entity
@Table(name="blogs")
data class Blog(
    @Column(nullable = false, unique = true)
    val name: String,
    @OneToMany(mappedBy = "blog", cascade = [(CascadeType.REMOVE)], orphanRemoval = true, fetch = FetchType.LAZY)
    val articles: List<Article> = listOf(),
//    @OneToOne(cascade=[(CascadeType.PERSIST)],fetch = FetchType.LAZY, mappedBy="blog")
//    val user: User,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int?=null,
)

@Entity
@Table(name="articles")
data class Article(
    @Column(nullable = false, unique = true)
    val name: String,
    @Column(nullable = false, unique = false)
    val content: String,
    @JoinColumn(name="blog_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val blog: Blog,
    @OneToMany(cascade = [(CascadeType.REMOVE)], fetch = FetchType.LAZY)
    var comments: List<Comment> = listOf(),
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int?=null
) {
    override fun toString() = "id: $id, name: $name"
}

@Entity
@Table(name="comments")
data class Comment(
    @Column(nullable = false, unique = false)
    val content: String,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    val user: User,
    @OneToMany(cascade = [CascadeType.REMOVE, CascadeType.MERGE], fetch = FetchType.LAZY)
    var internalComments: List<Comment> = listOf(),
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int?=null
) {
    override fun toString() = "id: $id, name: ${user.id}"
}
