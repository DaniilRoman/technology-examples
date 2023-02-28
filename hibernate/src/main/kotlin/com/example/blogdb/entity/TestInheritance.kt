package com.example.blogdb.entity

import jakarta.annotation.PostConstruct
import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@MappedSuperclass
abstract class ArticleMetadata(
    @Column(nullable = false, unique = false)
    val viewCount: Int,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int?=null
)

@Entity
@Table(name="free_article")
class FreeArticle(
    viewCount: Int = 0,
    id: Int?=null,
): ArticleMetadata(viewCount, id)

@Entity
@Table(name="paid_article")
class PaidArticle(
    var tags: String,
    viewCount: Int = 0,
    id: Int?=null,
): ArticleMetadata(viewCount, id)

@Repository
interface ArticleMetadataRepository: JpaRepository<ArticleMetadata, Int> {
//    fun findByViewCount(viewCount: Int): List<FreeArticle1> // Throwing `Not an entity` exception
}

@Repository
interface PaidArticleRepository: JpaRepository<PaidArticle, Int>

@Repository
interface FreeArticleRepository: JpaRepository<FreeArticle, Int>

@Service
class ArticleMetadataService(val articleMetadataRepository: ArticleMetadataRepository) {
    @PostConstruct
    fun setup() {
        val metadata = mutableListOf<ArticleMetadata>()
        metadata.add(FreeArticle(viewCount = 0))
        metadata.add(PaidArticle(tags = "sport,football"))
        articleMetadataRepository.saveAll(metadata)
    }
}