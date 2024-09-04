package com.junmooo.lingji.serivce;

import com.junmooo.lingji.mapper.common.DictMapper;
import com.junmooo.lingji.model.Dict;
import com.junmooo.lingji.model.TextToImg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DictService {

    @Autowired
    private DictMapper dictMapper;

    public Integer save(Dict dict) {
        return dictMapper.insert(dict);
    }

}
