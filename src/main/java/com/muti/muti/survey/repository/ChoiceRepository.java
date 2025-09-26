package com.muti.muti.survey.repository;

import com.muti.muti.survey.domain.Choice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {
}
