package com.taenan.reduz.domain.service;

import org.springframework.stereotype.Service;

import com.taenan.reduz.api.ResourceUriHelper;
import com.taenan.reduz.api.assembler.CategoryInputDisassembler;
import com.taenan.reduz.api.assembler.CategoryModelAssembler;
import com.taenan.reduz.api.model.CategoryModel;
import com.taenan.reduz.api.model.input.CategoryInput;
import com.taenan.reduz.domain.enums.Status;
import com.taenan.reduz.domain.exception.CategoryNotFoundException;
import com.taenan.reduz.domain.exception.DomainException;
import com.taenan.reduz.domain.model.Category;
import com.taenan.reduz.domain.model.repository.CategoryRepository;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoryService {

	private CategoryRepository categoryRepository;
	private CategoryModelAssembler categoryModelAssembler;
	private CategoryInputDisassembler categoryInputDisassembler;

	public CategoryModel create(@Valid CategoryInput categoryInput) {

		categoryRepository.findByNameAndStatus(categoryInput.getName(), Status.ACTIVE).stream().findAny()
				.ifPresent(c -> {
					throw new DomainException("Uma categoria com o nome " + categoryInput.getName() + " já existe.");
				});

		Category category = categoryInputDisassembler.toDomainObject(categoryInput);
		category.setStatus(Status.ACTIVE);
		category = categoryRepository.save(category);
		CategoryModel categoryModel = categoryModelAssembler.toModel(category);

		ResourceUriHelper.addUriInResponseHeader(categoryModel.getId());

		return categoryModel;
	}

	public Category buscarOuFalhar(Long categoryId) {
		return categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));
	}
}