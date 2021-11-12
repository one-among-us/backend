package org.hydev.back.db

import org.hydev.back.int
import org.hydev.back.str
import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Flower(
    @Id @GeneratedValue
    var pk: int = 0,
    var id: str = "",
    var flowers: int = 0
)

interface FlowerRepo: JpaRepository<Flower, int>
