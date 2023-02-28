package com.example.blogdb.entity

import jakarta.annotation.PostConstruct
import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.util.UUID

@Entity
@Table(name="free_article_3")
@Inheritance(strategy = InheritanceType.JOINED)
open class FreeArticle3(
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
@Table(name="paid_article_3")
class PaidArticle3(
    var tags: String,
    viewCount: Int = 10,
    id: UUID?=null,
): FreeArticle3(viewCount, id) {
    override fun toString(): String {
        return id.toString()
    }
}

@Repository
interface PaidArticleRepository3: JpaRepository<PaidArticle3, Int>

@Repository
interface FreeArticleRepository3: JpaRepository<FreeArticle3, Int> {
    fun findByViewCountGreaterThan(viewCount: Int): List<FreeArticle3>
}

@Service
class ArticleMetadataService3(val articleMetadataRepository: FreeArticleRepository3) {
    @PostConstruct
    fun setup() {
        println("====================== JOINED TABLE ==========================")
        val metadata = mutableListOf<FreeArticle3>()
        metadata.add(FreeArticle3(viewCount = 1))
        metadata.add(PaidArticle3("sport,football"))
        articleMetadataRepository.saveAll(metadata)
        println("=========== INHERITANCE QUERY (JOINED TABLE) ==================")
        println(articleMetadataRepository.findByViewCountGreaterThan(0))
    }
}