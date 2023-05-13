package com.indower.indtest.information.restControllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Type;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.indower.indtest.models.documentModels.Profession;
import com.indower.indtest.utils.AppConstants;
import com.indower.indtest.utils.MyResponseUtils;

@RestController
@RequestMapping("/info")
public class InfoController {

    // @Autowired
    // private RedisTemplate<String, Object> template;

    // @GetMapping("/privacy-policy")
    // public ResponseEntity<Map<String, Object>> privacyPolicy() {
    // return MyResponseUtils.successWithDataAndCache("Privacy...",
    // AppConstants.INFO_CACHE);
    // }

    // @GetMapping("/terms-conditions")
    // public ResponseEntity<Map<String, Object>> termsCondtions() {
    // return MyResponseUtils.successWithDataAndCache("T&C...",
    // AppConstants.INFO_CACHE);
    // }

    // @GetMapping("/faqs")
    // public ResponseEntity<Map<String, Object>> faq() {
    // ArrayList<FAQs> faqs = new ArrayList<>();
    // for (int i = 1; i <= 10; i++) {
    // faqs.add(new FAQs("Question" + i, "Answer" + i));
    // }
    // return MyResponseUtils.successWithDataAndCache(faqs,
    // AppConstants.INFO_CACHE);
    // }

    @GetMapping("/professions")
    public ResponseEntity<Map<String, Object>> getProfessions() {
        ArrayList<Profession> professions = new ArrayList<>();
        Type type = new TypeToken<HashMap<String, List<String>>>() {
        }.getType();
        HashMap<String, List<String>> professionMap = new Gson().fromJson(AppConstants.PROFESSIONS_JSON_STRING, type);
        professionMap.forEach((key, value) -> {
            for (String i : value) {
                professions.add(new Profession(i, key));
            }
        });
        professions.add(new Profession("Others", "others"));
        Collections.sort(professions, Comparator.comparing(Profession::getProfession));
        return MyResponseUtils.successWithDataAndCache(professions, AppConstants.INFO_CACHE);
    }
}
