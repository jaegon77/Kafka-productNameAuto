package com.productname.kafkaconsumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.productname.kafkaconsumer.entity.AttrVal;

public interface AttrValRepository extends JpaRepository<AttrVal, Long> {
	boolean existsAttrValByPdUnitNmAndCategoryId(String pdUnitNm, Long categoryId);

	List<AttrVal> searchAttrValByPdUnitNmAndCategoryId(String pdNmSplit, long categoryId);
}
