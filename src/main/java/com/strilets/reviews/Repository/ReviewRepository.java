package com.strilets.reviews.Repository;

import com.strilets.reviews.Model.Product;
import com.strilets.reviews.Model.Review;
import com.strilets.reviews.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r WHERE r.product = :prod AND r.confirmed = :conf ORDER BY r.rating DESC ")
    Iterable<Review> findAllByProductAndConfirmed(Product prod, Boolean conf);

    Iterable<Review> findAllByUser(User user);
}
