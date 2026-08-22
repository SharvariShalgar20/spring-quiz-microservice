package com.sharvari.analytics_service.repository;

import com.sharvari.analytics_service.model.QuizStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizStatsRepository extends JpaRepository<QuizStats, Integer> {
}
