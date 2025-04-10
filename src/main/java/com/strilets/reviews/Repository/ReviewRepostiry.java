package com.strilets.reviews.Repository;

import com.strilets.reviews.Model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepostiry extends JpaRepository<Review, Long> {
}
