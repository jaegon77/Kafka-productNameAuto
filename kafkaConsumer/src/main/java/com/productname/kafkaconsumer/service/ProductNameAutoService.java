package com.productname.kafkaconsumer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.productname.kafkaconsumer.dto.ProductDto;
import com.productname.kafkaconsumer.entity.AttrVal;
import com.productname.kafkaconsumer.entity.TgtPdNmAuto;
import com.productname.kafkaconsumer.entity.TgtPdNmAutoDlt;
import com.productname.kafkaconsumer.repository.AttrValRepository;

import com.productname.kafkaconsumer.repository.TgtPdNmAutoDltRepository;
import com.productname.kafkaconsumer.repository.TgtPdNmAutoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductNameAutoService {
	private final AttrValRepository attrValRepository;
	private final TgtPdNmAutoRepository tgtPdNmAutoRepository;
	private final TgtPdNmAutoDltRepository tgtPdNmAutoDltRepository;

	public void process(ProductDto productDto) {
		List<List<AttrVal>> attrValListofList = new ArrayList<>();
		String[] pdNm = productDto.getName().split(" ");
		long categoryId = productDto.getCategory().getId();

		for (String pdNmSplit : pdNm) {
			if (attrValRepository.existsAttrValByPdUnitNmAndCategoryId(pdNmSplit, categoryId)) {
				// 카테고리, 속성기본, 속성값 ID는 UQ(필수)
				  // 1-무늬-꽃무늬-꽃(pdUnit)
				  // 1-무늬-방울-꽃(pdUnit)
				attrValListofList.add(attrValRepository.searchAttrValByPdUnitNmAndCategoryId(pdNmSplit, categoryId));
			}
		}

		for (List<AttrVal> attrValList : attrValListofList) {
			for (AttrVal attrVal : attrValList) {
				Optional<TgtPdNmAuto> existingOpt = tgtPdNmAutoRepository.findByPdNoAndAttrBaseIdAndAttrValId(
						productDto.getId(), attrVal.getAttrBaseId(), attrVal.getAttrValId()
				);

				// upsert 구현
				if (existingOpt.isPresent()) {
					TgtPdNmAuto existing = existingOpt.get();
					existing.setAttrBaseNm(attrVal.getAttrBaseNm());
					existing.setAttrValNm(attrVal.getAttrValNm());

					tgtPdNmAutoRepository.save(existing);
				} else {
					TgtPdNmAuto tgtPdNmAuto = new TgtPdNmAuto();

					tgtPdNmAuto.setPdNo(productDto.getId());
					tgtPdNmAuto.setAttrBaseId(attrVal.getAttrBaseId());
					tgtPdNmAuto.setAttrBaseNm(attrVal.getAttrBaseNm());
					tgtPdNmAuto.setAttrValId(attrVal.getAttrValId());
					tgtPdNmAuto.setAttrValNm(attrVal.getAttrValNm());

					tgtPdNmAutoRepository.save(tgtPdNmAuto);
				}
			}
		}
	}

	public void dltProcess(ProductDto productDto) {
		TgtPdNmAutoDlt tgtPdNmAutoDlt = new TgtPdNmAutoDlt();
		tgtPdNmAutoDlt.setPdNo(productDto.getId());
		tgtPdNmAutoDlt.setPdNm(productDto.getName());
		tgtPdNmAutoDlt.setCategoryId(productDto.getCategory().getId());

		tgtPdNmAutoDltRepository.save(tgtPdNmAutoDlt);
	}
}
