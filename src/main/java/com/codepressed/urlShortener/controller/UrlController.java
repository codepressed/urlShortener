package com.codepressed.urlShortener.controller;

import com.codepressed.urlShortener.model.Advertisement;
import com.codepressed.urlShortener.model.ShortUrl;
import com.codepressed.urlShortener.service.AdvertisementServiceImpl;
import com.codepressed.urlShortener.service.MongoUtilsServiceImpl;
import com.codepressed.urlShortener.service.ShortUrlServiceImpl;
import com.codepressed.urlShortener.util.UrlConversions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Controller
public class UrlController {
    @Autowired
    MongoUtilsServiceImpl mongoUtilsService;

    @Autowired
    ShortUrlServiceImpl shortUrlService;

    @Autowired
    AdvertisementServiceImpl advertisementService;

    @GetMapping(value = "/")
    public String index(@RequestParam(name="createdLink", required = false) String createdLink, Model model){
        model.addAttribute("links", shortUrlService.findLast10Links());
        model.addAttribute("createdLink", createdLink);
        return "index";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public void redirectToUrl (@PathVariable String id, HttpServletResponse resp) throws Exception{
        String url;
        if (shortUrlService.findUrlById(UrlConversions.shortURLtoID(id)) != null){
            url = shortUrlService.findUrlById(UrlConversions.shortURLtoID(id));
        }else if (shortUrlService.findUrlByCustom(UrlConversions.shortURLtoID(id)) != null){
            url = shortUrlService.findUrlByCustom(UrlConversions.shortURLtoID(id));
        }else {
            url = "error404.html";
        }
        resp.addHeader("Location", url);
        resp.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
    }

    @GetMapping(value = "/go/{id}")
    public String randomAd(@PathVariable("id") String id, Model model) {
        Advertisement ad = advertisementService.randomAd();
        if (ad != null) {
            model.addAttribute("advertisement", ad);
            model.addAttribute("shortenUrl", shortUrlService.findUrlById(UrlConversions.shortURLtoID(id)));
            return "go";
        }
        if (shortUrlService.findUrlById(UrlConversions.shortURLtoID(id)) != null)
        return shortUrlService.findUrlById(UrlConversions.shortURLtoID(id));
        else return shortUrlService.findUrlByCustom(UrlConversions.shortURLtoID(id));

    }

    @PostMapping("/shorten/new")
    public String submitNewUrl(ShortUrl shortUrl, Model model) {
        shortUrlService.insert(shortUrl);
        return "redirect:/index";

    }


}

