package com.example.database.api.controller;


import com.example.data.models.entity.mysticschool.ArticleCategory;
import com.example.data.models.entity.response.ApiResponseWithDataList;
import com.example.database.api.client.MysticSchoolClient;
import com.example.data.models.service.ModelMapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class MysticSchoolDataController {

    private final MysticSchoolClient client;
    private final ModelMapperService mapperService;

    @GetMapping("/articles/category/")
    public Mono<ResponseEntity<ApiResponseWithDataList>> getArticleCategories(
            @RequestParam("id") int id
    ) {
        return client.getArticleByArticleCategoryId(id)
                .map(resp ->  {

                    ApiResponseWithDataList<ArticleCategory> articleCategoryApiResponse =
                            new ApiResponseWithDataList<>(HttpStatus.OK, "Success!", resp);

                    return ResponseEntity.status(HttpStatus.OK).body(articleCategoryApiResponse);
                });
    }

    @GetMapping("/articles/category/getAll")
    public Mono<ResponseEntity<ApiResponseWithDataList<ArticleCategory>>> getArticleCategories() {
        return client.getArticleCategories()
                .map(resp ->  {

                    ApiResponseWithDataList<ArticleCategory> articleCategoryApiResponseWithDataList =
                            new ApiResponseWithDataList<>(HttpStatus.OK, "Success!", resp);

                    return ResponseEntity.status(HttpStatus.OK).body(articleCategoryApiResponseWithDataList);
                });
    }

}
