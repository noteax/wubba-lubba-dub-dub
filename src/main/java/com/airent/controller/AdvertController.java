package com.airent.controller;

import com.airent.model.rest.SearchRequest;
import com.airent.service.AdvertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdvertController {

    @Autowired
    private AdvertService advertService;

    @RequestMapping(method = RequestMethod.GET, path = "/")
    public String showMainPage(Model model) {
        model.addAttribute("adverts", advertService.getAdvertsForMainPage());
        return "main";
    }

    @RequestMapping(method = RequestMethod.GET, path = "/loadMore")
    public String loadMoreAdverts(@RequestParam long timestampUntil, Model model) {
        model.addAttribute("adverts", advertService.getAdvertsForMainPageFrom(timestampUntil));
        return "fragments/advert :: advertsForm";
    }

    @RequestMapping(method = RequestMethod.GET, path = "/search")
    public String searchAdverts(SearchRequest searchRequest, Model model) {
        model.addAttribute("adverts", advertService.searchAdvertsUntilTime(searchRequest, System.currentTimeMillis()));
        return "search";
    }

    @RequestMapping(method = RequestMethod.POST, path = "/search/loadMore")
    public String searchLoadMoreAdverts(@RequestBody SearchRequest searchRequest, Model model) {
        model.addAttribute("adverts", advertService.searchAdvertsUntilTime(searchRequest, System.currentTimeMillis()));
        return "fragments/advert :: advertsForm";
    }

}
