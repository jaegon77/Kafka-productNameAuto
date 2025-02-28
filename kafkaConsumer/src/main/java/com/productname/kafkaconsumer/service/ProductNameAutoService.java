package com.productname.kafkaconsumer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.productname.kafkaconsumer.dto.ProductDto;
import com.productname.kafkaconsumer.entity.AttrVal;
import com.productname.kafkaconsumer.entity.TgtPdNmAuto;
import com.productname.kafkaconsumer.repository.AttrValRepository;

import com.productname.kafkaconsumer.repository.TgtPdNmAutoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductNameAutoService {
	private final AttrValRepository attrValRepository;
	private final TgtPdNmAutoRepository tgtPdNmAutoRepository;

	public void process(ProductDto productDto) {
		List<List<AttrVal>> attrValListofList = new ArrayList<>();
		String[] pdNm = productDto.getName().split(" ");
		long categoryId = productDto.getCategory().getId();

		for (String pdNmSplit : pdNm) {
			if (attrValRepository.existsAttrValByPdUnitNmAndCategoryId(pdNmSplit, categoryId)) {
				attrValListofList.add(attrValRepository.searchAttrValByPdUnitNmAndCategoryId(pdNmSplit, categoryId));
			}
		}

		for (List<AttrVal> attrValList : attrValListofList) {
			for (AttrVal attrVal : attrValList) {
				Optional<TgtPdNmAuto> existingOpt = tgtPdNmAutoRepository.findByPdNoAndAttrBaseIdAndAttrValId(
						productDto.getId(), attrVal.getAttrBaseId(), attrVal.getAttrValId()
				);

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
}
