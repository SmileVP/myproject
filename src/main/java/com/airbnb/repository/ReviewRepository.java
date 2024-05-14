package com.airbnb.repository;

import com.airbnb.entity.Property;
import com.airbnb.entity.PropertyUser;
import com.airbnb.entity.Review;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    //instead of giving value like this propery id and user id give the full object itself
//    @Query("select r from Review r where r.property=:propertyId and r.propertyUser=:userId")
//   Review findReviewByUserIdAndPropertyId(@Param("propertyId")long propertyId, @Param("userId")long userId);


    //here I m giving the object address as property and property user are foreign key
    //r.property and propertyUser are the reference variable in review entitiy as they are foreign keyu
    @Query("select r from Review r where r.property=:property and r.propertyUser=:user")
    Review findReviewByUser(@Param("property") Property property,@Param("user") PropertyUser user);

    //to get all reviews for the current user logged in
   List<Review> findByPropertyUser(PropertyUser user);

}