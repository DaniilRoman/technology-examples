package com.example.blogdb.entity

import jakarta.annotation.PostConstruct
import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.util.UUID

@Entity
@Table(name="free_article_2")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
open class FreeArticle2(
    @Column(nullable = false, unique = false)
    open val viewCount: Int = 0,
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    open val id: UUID?=null,
) {
    override fun toString(): String {
        return id.toString()
    }
}

@Entity
@Table(name="paid_article_2")
class PaidArticle2(
    var tags: String,
    viewCount: Int = 10,
    id: UUID?=null,
): FreeArticle2(viewCount, id) {
    override fun toString(): String {
        return id.toString()
    }
}

@Repository
interface PaidArticleRepository2: JpaRepository<PaidArticle2, Int>

@Repository
interface FreeArticleRepository2: JpaRepository<FreeArticle2, Int> {
    fun findByViewCountGreaterThan(viewCount: Int): List<FreeArticle2>
}

@Service
class ArticleMetadataService2(val articleMetadataRepository: FreeArticleRepository2) {
    @PostConstruct
    fun setup() {
        val metadata = mutableListOf<FreeArticle2>()
        metadata.add(FreeArticle2(viewCount = 1))
        metadata.add(PaidArticle2("sport,football"))
        articleMetadataRepository.saveAll(metadata)
        println("=========== INHERITANCE QUERY (SINGLE TABLE) ==================")
        println(articleMetadataRepository.findByViewCountGreaterThan(0))
    }
}