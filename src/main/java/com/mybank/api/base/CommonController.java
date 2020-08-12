package com.mybank.api.base;

import com.mybank.api.response.dto.ResponseEntity;
import com.mybank.aspect.annotation.KfApi;
import com.mybank.base.entity.ComplexOrderMapping;
import com.mybank.base.repository.ComplexOrderMappingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/common")
public class CommonController {

    private final static Logger logger = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    private ComplexOrderMappingRepository repository;

    @PostMapping(value = "/complexOrder")
    @KfApi(encryptable = false)
    public ResponseEntity<ComplexOrderMapping> complexId(@RequestBody @Valid ComplexOrderMapping mapping){

        ResponseEntity<ComplexOrderMapping> entity = new ResponseEntity<>();

        ComplexOrderMapping temp;

        if(mapping.getPreAuthOrderNo() > 0L){
            temp = repository.findBypreAuthOrderNo(mapping.getPreAuthOrderNo());
            mapping.setTradeOrderNo(0L);
        } else if(mapping.getTradeOrderNo() > 0){
            temp = repository.findByTradeOrderNo(mapping.getTradeOrderNo());
            mapping.setPreAuthOrderNo(0L);
        }else{
            entity.setResponseCode("10001");
            entity.setMsg("参数异常！");
            return entity;
        }

        if(temp != null && !temp.getBiz().equals(mapping.getBiz())){
            entity.setResponseCode("10001");
            entity.setMsg("参数异常！");
            return entity;
        }

        if(temp != null){
            mapping.setId(temp.getId());
        }else{
            mapping.setId(ComplexOrderMapping.getNextId());
            repository.save(mapping);
        }

        entity.setDto(mapping);
        return entity;
    }

}
