package com.fa.api.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fa.api.entity.Article;
import com.fa.api.model.ArticleDetailsDTO;
import com.fa.api.model.ArticleInstructionDTO;
import com.fa.api.model.ArticleInstructionWithFullParentDTO;
import com.fa.api.model.ArticleInstructionWithParentIdDTO;
import com.fa.api.model.ArticleRequestDTO;
import com.fa.api.repository.ArticleRepository;
import com.fa.api.service.ArticleService;
import com.fa.api.utils.ObjectMapperUtils;

@Service
public class ArticleServiceImpl implements ArticleService {

	@Autowired
	private ArticleRepository articleRepository;

	
	@Override
	public List<ArticleDetailsDTO> findMainArticles() {
		// Get main articles with null parent
		List<Article> mainArticles = articleRepository.findByParentArticleIsNull();

		if (!mainArticles.isEmpty()) {

			List<ArticleDetailsDTO> articleJsons = ObjectMapperUtils.mapAll(mainArticles, 
					ArticleDetailsDTO.class);
			
			return articleJsons;
		}
		return null;
	}
	
	
	@Override
	public List<ArticleInstructionDTO> findSimpleMainArticles() {

		List<Article> mainArticles = articleRepository.findByParentArticleIsNull();

		if (!mainArticles.isEmpty()) {

			List<ArticleInstructionDTO> articleJsons = ObjectMapperUtils.mapAll(mainArticles,
					ArticleInstructionDTO.class);
			
			return articleJsons;
		}
		return null;
	}

	@Override
	public ArticleDetailsDTO findById(Long id) {
		Optional<Article> theArticle = articleRepository.findById(id);

		if (theArticle.isPresent()) {

			ArticleDetailsDTO articleJson = ObjectMapperUtils.map(theArticle.get(), ArticleDetailsDTO.class);

			return articleJson;
		}
		return null;
	}
	
	@Override
	public List<ArticleInstructionWithFullParentDTO> findAllArticles() {
		
		List<Article> listArticle = articleRepository.findAll();
		
		List<ArticleInstructionWithFullParentDTO> articles = ObjectMapperUtils.mapAll(listArticle, ArticleInstructionWithFullParentDTO.class);
		return articles;
	}
	
	
	@Override
	public List<ArticleInstructionWithFullParentDTO> findAllChildsById(Long articleId,String keyword) {
		if(keyword == null) {
			keyword = "";
		}
		List<ArticleInstructionWithFullParentDTO> listChild = new ArrayList<>();
		ArticleDetailsDTO article = findById(articleId);
		
		listChild = getAllChilds(article, listChild,keyword);
		return listChild;
	}

	private List<ArticleInstructionWithFullParentDTO> getAllChilds(ArticleDetailsDTO article,
					List<ArticleInstructionWithFullParentDTO> listArticle,String keyword) {
		
		List<ArticleDetailsDTO> listArticlesDetail = article.getChildArticles();
		if (listArticlesDetail != null) {
			for (ArticleDetailsDTO articleDetails : listArticlesDetail) {
				ArticleInstructionWithFullParentDTO articleInstruction = ObjectMapperUtils.map(articleDetails,
						ArticleInstructionWithFullParentDTO.class);
				if(articleInstruction.getName().toUpperCase().contains(keyword.toUpperCase())) {
					listArticle.add(articleInstruction);
				}
				listArticle = getAllChilds(articleDetails, listArticle,keyword);
			}
		}
		return listArticle;
	}

	
	@Override
	public ArticleInstructionWithParentIdDTO findByIdForInstruction(Long id) {
		Optional<Article> theArticle = articleRepository.findById(id);

		ArticleInstructionWithParentIdDTO articleJson = ObjectMapperUtils.map(theArticle.get(),
				ArticleInstructionWithParentIdDTO.class);
		
		if(theArticle.get().getParentArticle() != null) {
			articleJson.setParentId(theArticle.get().getParentArticle().getId());
		}
		
		return articleJson;

	}
	
	
	@Override
	public void save(ArticleRequestDTO articleRequest) {
		Article parentArticle = null;
		
		if (articleRequest.getParentId() != null) {	
			parentArticle = new Article();
			parentArticle.setId(articleRequest.getParentId());
		}
		
		Article theArticle = new Article();
		theArticle.setId(articleRequest.getId());
		theArticle.setName(articleRequest.getName());
		theArticle.setRoot(articleRequest.isRoot());
		theArticle.setParentArticle(parentArticle);
		
		articleRepository.save(theArticle);
	}

	
	@Override
	public void delete(Long id) {
		
		Optional<Article> theArticle = articleRepository.findById(id);
		
		if (theArticle.isPresent()) {
			articleRepository.deleteById(id);
		}

	}	
	
}