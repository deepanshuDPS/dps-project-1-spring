package com.indower.indtest.information.restControllers;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.indower.indtest.models.documentModels.Profession;
import com.indower.indtest.utils.AppConstants;
import com.indower.indtest.utils.MyResponseUtils;

@RestController
@RequestMapping("/info")
public class InfoController {

    @Autowired
    private RedisTemplate<String, Object> template;

    @GetMapping("/terms-and-conditions")
    public ResponseEntity<Map<String, Object>> termsAndConditions() {
        return MyResponseUtils.successWithData("Here are terms and conditions");
    }

    @GetMapping("/faq/{description}")
    public ResponseEntity<Map<String, Object>> faq(@PathVariable("description") String description) {
        template.opsForValue().set("faq", description);
        return MyResponseUtils.successWithData(template.opsForValue().get("faq"));
    }

    @GetMapping("/professions")
    public ResponseEntity<Map<String, Object>> getProfessions() {
        ArrayList<Profession> professions = new ArrayList<>();
        for (String[] i : AppConstants.PROFESSIONS) {
            professions.add(new Profession(i[0], i[1]));
        }
        return MyResponseUtils.successWithDataAndCache(professions, 5 * 60);
    }
}
