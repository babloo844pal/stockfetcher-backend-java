package com.stockfetcher.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stockfetcher.model.MetaInfoEntity;
import com.stockfetcher.service.MetaInfoService;

@RestController
@RequestMapping("/meta-info")
public class MetaInfoController {

    @Autowired
    private MetaInfoService metaInfoService;

    @PostMapping
    public MetaInfoEntity saveOrUpdateMetaInfo(@RequestBody MetaInfoEntity metaInfo) {
        return metaInfoService.getOrSaveMetaInfo(metaInfo);
    }
}
